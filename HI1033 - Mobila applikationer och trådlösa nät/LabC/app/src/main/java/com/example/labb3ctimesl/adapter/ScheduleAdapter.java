package com.example.labb3ctimesl.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.Utils;
import com.example.labb3ctimesl.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    public interface RequestAlarmListener {
        void onRequestAlarm(int id, boolean create);
    }

    public interface EntryEnabledListener {
        void onEnableChange(int position, boolean enabled);
    }

    private RequestAlarmListener requestAlarmListener;
    private EntryEnabledListener entryEnabledListener;

    //Log
    private static final String TAG = ScheduleAdapter.class.getSimpleName();

    private final Schedule schedule;
    private boolean dismissedAlarmCache = true;
    private Date firstDate;

    public ScheduleAdapter(Schedule schedule) {
        this.schedule = schedule;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_card, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);

        vh.timeStamp = itemView.findViewById(R.id.timestamp);
        vh.eventType = itemView.findViewById(R.id.eventType);
        vh.eventTypeIcon = itemView.findViewById(R.id.eventTypeIcon);
        vh.eventDescription = itemView.findViewById(R.id.eventDescription);
        vh.eventDetailedDescription = itemView.findViewById(R.id.eventDetailedDescription);
        vh.scheduleAlarm = itemView.findViewById(R.id.scheduleAlarm);
        vh.scheduleAlarmLayout = itemView.findViewById(R.id.scheduleAlarmLayout);
        vh.enabledEntry = itemView.findViewById(R.id.enabledEntry);
        vh.enabledEntryLayout = itemView.findViewById(R.id.enabledEntryLayout);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule.Entry entry = schedule.getEntries().get(position);

        String dateString = getString(position, entry.getStart());

        holder.timeStamp.setText(dateString);
        if (entry.getType().equals(Schedule.Entry.TRIP) || entry.getType().equals(Schedule.Entry.WAKEUP)) {
            holder.eventType.setText("");
            holder.enabledEntryLayout.setVisibility(View.GONE);
        } else {
            holder.eventType.setText(entry.getType());
            holder.enabledEntryLayout.setVisibility(View.VISIBLE);
            holder.enabledEntry.setChecked(entry.isEnabled());
            holder.enabledEntry.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (entryEnabledListener != null) {
                    entryEnabledListener.onEnableChange(position, isChecked);
                }
            });
        }
        holder.eventTypeIcon.setImageResource(getImageOnType(entry.getType()));
        holder.eventDescription.setText(entry.getCourseName());
        holder.eventDetailedDescription.setText(entry.getLocation());

        // Check if this is a wakeup card in the schedule, so we can show the extra button
        // We check towards "Home" since the card might now have been fully initialized
        // (It says 'Loading...' during initialization)
        if (entry.getLocation().equals("Home")) {
            if (entry.getStart().before(new Date())) {
                Log.i(TAG, "onBindViewHolder: Alarm before now");
                holder.scheduleAlarm.setEnabled(false);
            } else {
                Log.i(TAG, "onBindViewHolder: Alarm after now");
                holder.scheduleAlarm.setEnabled(true);
            }
            holder.scheduleAlarmLayout.setVisibility(View.VISIBLE);
            holder.scheduleAlarm.setChecked(!dismissedAlarmCache);
            holder.scheduleAlarm.setOnCheckedChangeListener((v, checked) -> {
                dismissedAlarmCache = false;
                if (this.requestAlarmListener != null) {
                    this.requestAlarmListener.onRequestAlarm(new Random().nextInt(), checked);
                }
            });
        } else {
            holder.scheduleAlarmLayout.setVisibility(View.GONE);
        }

    }

    /**
     * Formats the string reperesenting the time somethin happens
     *
     * @param position - in the array to calculate the inital time
     * @param entry    - the date to be formatted
     * @return a formatted string to be displayed
     */
    @NonNull
    private String getString(int position, Date entry) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (position == 0)
            firstDate = entry;
        if (entry != null) {
            if (!Utils.isSameDay(firstDate, entry)) {
                sdf = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
            }
        }

        return entry == null ? "Loading..." : sdf.format(entry);
    }

    @Override
    public int getItemCount() {
        return schedule.getEntries().size();
    }

    public void setRequestAlarmListener(RequestAlarmListener listener) {
        this.requestAlarmListener = listener;
    }

    public void setEntryEnabledListener(EntryEnabledListener entryEnabledListener) {
        this.entryEnabledListener = entryEnabledListener;
    }

    public void onDismissedAlarm() {
        dismissedAlarmCache = true;
        assert !schedule.getEntries().isEmpty();
        this.notifyItemChanged(0);
    }

    private int getImageOnType(String type) {
        switch (type) {
            case Schedule.Entry.WAKEUP:
                return R.drawable.ic_baseline_wb_sunny_24;
            case Schedule.Entry.TRIP:
                return R.drawable.ic_baseline_train_24;
            case "Laboratory":
                return R.drawable.ic_baseline_science_24;
            case "Exercise":
            case "Lecture":
            case "Lesson":
            case "Seminar":
            case "Tutorial":
                return R.drawable.ic_baseline_school_24;
            case "Roll-call":
            case "Information":
                return R.drawable.ic_baseline_info_24;
            case "Test":
            case "Examination":
                return R.drawable.ic_baseline_menu_book_24;
            default:
                return R.drawable.ic_baseline_question_mark_24;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView timeStamp;
        public TextView eventType;
        public ImageView eventTypeIcon;
        public TextView eventDescription;
        public TextView eventDetailedDescription;
        public SwitchCompat scheduleAlarm;
        public LinearLayout scheduleAlarmLayout;
        public CheckBox enabledEntry;
        public LinearLayout enabledEntryLayout;

        public ViewHolder(View v) {
            super(v);
        }
    }
}
