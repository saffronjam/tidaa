package com.example.labb3ctimesl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.Utils;
import com.example.labb3ctimesl.adapter.ScheduleAdapter;
import com.example.labb3ctimesl.databinding.FragmentScheduleBinding;
import com.example.labb3ctimesl.dataservices.ScheduleDataService;
import com.example.labb3ctimesl.dataservices.TripDataService;
import com.example.labb3ctimesl.model.Alarm;
import com.example.labb3ctimesl.model.Schedule;
import com.example.labb3ctimesl.model.Settings;
import com.example.labb3ctimesl.model.Trip;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class ScheduleFragment extends Fragment {

    public interface TitleChangeRequestListener {
        void onWantNewTilte(String title);
    }

    private TitleChangeRequestListener titleChangeRequestListener;

    private FragmentScheduleBinding binding;

    private static final String TAG = ScheduleFragment.class.getSimpleName();

    private final Schedule schedule = new Schedule();

    private RecyclerView scheduleRV;
    private ScheduleAdapter scheduleAdapter;
    private SeekBar morningBufferTimeSB;
    private SeekBar arrivalBufferTimeSB;
    private TextView morningBufferTimeTV;
    private TextView arrivalBufferTimeTV;

    private Date wakeUpTimeCache;
    private Schedule.Entry earliestLesson;

    private Skeleton skeleton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Init recyclerview
        scheduleRV = root.findViewById(R.id.schedule);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        scheduleRV.setLayoutManager(layoutManager);

        //Cards
        scheduleAdapter = new ScheduleAdapter(schedule);
        scheduleRV.setAdapter(scheduleAdapter);
        scheduleAdapter.setRequestAlarmListener((id, create) -> {
            if (create) {
                Date date = earliestLesson.getStart();
                String text = String.format(Locale.getDefault(), "%s at %02d:%02d", earliestLesson.getCourseName(), date.getHours(), date.getMinutes());
                Alarm.schedule(requireContext(), id, wakeUpTimeCache.getHours(), wakeUpTimeCache.getMinutes(), text);
            } else {
                Alarm.cancel(requireContext());
            }
        });
        scheduleAdapter.setEntryEnabledListener((position, enabled) -> {
            TripDataService.nullifyCurrentTrip(requireContext());
            ScheduleDataService.setEntryEnabled(position - schedule.getBaseIndex(), enabled);
            schedule.getEntries().get(position).setEnabled(enabled);
            reload();
        });

        //Timers
        arrivalBufferTimeSB = root.findViewById(R.id.arrivalBufferTimeSeekBar);
        arrivalBufferTimeTV = root.findViewById(R.id.arrivalBufferTime);
        arrivalBufferTimeTV.setText(String.valueOf(arrivalBufferTimeSB.getProgress()));

        morningBufferTimeSB = root.findViewById(R.id.morningBufferTimeSeekBar);
        morningBufferTimeTV = root.findViewById(R.id.morningBufferTime);
        morningBufferTimeTV.setText(String.valueOf(morningBufferTimeSB.getProgress()));

        skeleton = SkeletonLayoutUtils.applySkeleton(scheduleRV, R.layout.time_card, 3);
        skeleton.setShowShimmer(true);
        skeleton.setMaskColor(Utils.getColor(R.attr.cardBackgroundColor, root.getContext()));
        skeleton.setShimmerColor(Utils.getColor(R.attr.cardBackgroundColorAlt, root.getContext()));
        skeleton.setMaskCornerRadius(24);
        skeleton.showSkeleton();

        setupListeners();

        reload();
        syncUi();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle extras = requireActivity().getIntent().getExtras();
        if (extras != null) {
            // If resumed by favorite list or search result, we could have a location sent to us
            boolean tryGetLocation = extras.getBoolean("dismissedAlarm");
            if (tryGetLocation) {
                scheduleAdapter.onDismissedAlarm();
            }
        }
        reload();
        syncUi();
    }

    private void setupListeners() {
        morningBufferTimeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                morningBufferTimeTV.setText(String.valueOf(progress));
                Optional<Trip> currentTripOpt = TripDataService.getCachedTrip(requireContext());

                if (currentTripOpt.isPresent() && !currentTripOpt.get().getSteps().isEmpty()) {
                    LocalDateTime depatureTime = Utils.toLocalDateTime(currentTripOpt.get().getDepartureTime());
                    LocalDateTime wakeUp = depatureTime.minusMinutes(progress);
                    schedule.setWakeUpEntry(Utils.toDate(wakeUp), "Home");
                    scheduleAdapter.notifyItemRangeChanged(0, 1);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Settings settings = Settings.get(requireContext());
                int oldVal = settings.getMorningBufferTime();
                int newVal = seekBar.getProgress();
                if (newVal != oldVal) {
                    settings.setMorningBufferTime(seekBar.getProgress()).save();
                    reloadTrip();
                }
            }
        });

        arrivalBufferTimeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                arrivalBufferTimeTV.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Settings settings = Settings.get(requireContext());
                int oldVal = settings.getArrivalBufferTime();
                int newVal = seekBar.getProgress();
                if (newVal != oldVal) {
                    TripDataService.nullifyCurrentTrip(requireContext());
                    settings.setArrivalBufferTime(seekBar.getProgress()).save();
                    reloadTrip();
                }
            }
        });
    }

    private void reload() {
        skeleton.showSkeleton();
        ScheduleDataService.getSchedule(requireContext(),
                schedule -> {
                    skeleton.showOriginal();

                    this.schedule.setEntries(schedule.getEntries());

                    // No entries means that we don't have any wake up and no trip
                    if (!schedule.getEntries().isEmpty()) {
                        Optional<Schedule.Entry> earliestLessonOpt = this.schedule.getEarliestLesson();
                        if (earliestLessonOpt.isPresent()) {
                            earliestLesson = earliestLessonOpt.get();
                            this.schedule.addWakeUpPlaceholderEntry();
                            this.schedule.addTripPlaceholderEntry();
                        } else {
                            scheduleAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                    scheduleAdapter.notifyDataSetChanged();
                    reloadTrip();
                }
        );
    }

    private void reloadTrip() {
        Optional<Schedule.Entry> earliestOpt = schedule.getEarliestLesson();
        if (!earliestOpt.isPresent()) {
            return;
        }
        Schedule.Entry earliest = earliestOpt.get();
        TripDataService.getTrip(earliest.getLatitude(), earliest.getLongitude(), requireContext(), trip -> {
            if (!trip.getSteps().isEmpty()) {
                reloadWakeUp(trip);
            }
        });
    }

    private void reloadWakeUp(Trip trip) {
        // Calcualte wake up time
        int morningBuffer = morningBufferTimeSB.getProgress();

        // Convert to LocalDateTime so we can do the date offests with ease
        LocalDateTime earliestTime = Utils.toLocalDateTime(trip.getDepartureTime());

        // Convert back to Date
        Date wakeUpTime = Utils.toDate(earliestTime.minusMinutes(morningBuffer)); // Account for time in the morning

        wakeUpTimeCache = wakeUpTime;
        schedule.setWakeUpEntry(wakeUpTime, "Home");
        schedule.setTripEntry(trip.getDepartureTime(), trip.getArrivalTime());

        scheduleAdapter.notifyItemRangeChanged(0, 2);
    }

    public void setTitleChangeRequestListener(TitleChangeRequestListener titleChangeRequestListener) {
        this.titleChangeRequestListener = titleChangeRequestListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void syncUi() {
        Settings settings = Settings.get(requireContext());
        morningBufferTimeSB.setProgress(settings.getMorningBufferTime());
        arrivalBufferTimeSB.setProgress(settings.getArrivalBufferTime());
    }
}