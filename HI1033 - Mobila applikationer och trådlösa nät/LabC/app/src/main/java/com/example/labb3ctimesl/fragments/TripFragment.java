package com.example.labb3ctimesl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.Utils;
import com.example.labb3ctimesl.adapter.TripAdapter;
import com.example.labb3ctimesl.databinding.FragmentTravelBinding;
import com.example.labb3ctimesl.dataservices.ScheduleDataService;
import com.example.labb3ctimesl.dataservices.TripDataService;
import com.example.labb3ctimesl.model.Schedule;
import com.example.labb3ctimesl.model.Trip;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import java.util.Optional;

public class TripFragment extends Fragment {

    private FragmentTravelBinding binding;

    private final Trip trip = new Trip();
    private Skeleton skeleton;
    private TripAdapter tripAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTravelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Init recyclerview
        RecyclerView recyclerView = root.findViewById(R.id.travelList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        //Cards
        tripAdapter = new TripAdapter(trip);
        recyclerView.setAdapter(tripAdapter);

        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.travel_card, 5);
        skeleton.setShowShimmer(true);
        skeleton.setMaskColor(Utils.getColor(R.attr.cardBackgroundColor, root.getContext()));
        skeleton.setShimmerColor(Utils.getColor(R.attr.cardBackgroundColorAlt, root.getContext()));
        skeleton.setMaskCornerRadius(24);

        reload();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void reload() {
        skeleton.showSkeleton();
        ScheduleDataService.getSchedule(requireContext(), schedule -> {
            Optional<Schedule.Entry> earliestOpt = schedule.getEarliestLesson();
            if (!earliestOpt.isPresent()) {
                skeleton.showOriginal();
                return;
            }
            Schedule.Entry earliest = earliestOpt.get();
            TripDataService.getTrip(earliest.getLatitude(), earliest.getLongitude(), requireContext(), trip -> {
                skeleton.showOriginal();
                this.trip.setSteps(trip.getSteps());
                tripAdapter.notifyItemRangeChanged(0, this.trip.getSteps().size());
            });
        });
    }

}