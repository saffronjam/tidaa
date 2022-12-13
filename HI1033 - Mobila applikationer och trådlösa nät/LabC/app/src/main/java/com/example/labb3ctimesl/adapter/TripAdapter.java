package com.example.labb3ctimesl.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.model.Trip;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private final Trip trip;

    public TripAdapter(Trip trip) {
        this.trip = trip;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.travel_card, parent, false);
        final ViewHolder vh = new ViewHolder(itemView);

        vh.travelType = itemView.findViewById(R.id.travelType);
        vh.startTime = itemView.findViewById(R.id.startTime);
        vh.endTime = itemView.findViewById(R.id.endTime);
        vh.originName = itemView.findViewById(R.id.travelDescriptionFrom);
        vh.destName = itemView.findViewById(R.id.travelDescriptionTo);
        vh.details = itemView.findViewById(R.id.travelDetailedDescription);
        vh.travelTypeIcon = itemView.findViewById(R.id.travelTypeIcon);
        vh.cardTypeColor = itemView.findViewById(R.id.cardTypeColor);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip.Step step = this.trip.getSteps().get(position);

        boolean walk = step.getType().equals("WALK");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String start = sdf.format(step.getOrigin().getTime());
        String end = sdf.format(step.getDestination().getTime());

        holder.startTime.setText(start);
        holder.endTime.setText(end);

        Drawable drawable = holder.cardTypeColor.getBackground();

        if (walk) {
            holder.travelType.setText("Walk");
            holder.details.setText("");
            holder.travelTypeIcon.setImageResource(getImageOnType(step.getType()));
            DrawableCompat.setTint(drawable, getColor(step.getType()));
        } else {
            String category = step.getName().contains("blåbuss") ? "BUSBLUE" : step.getCategory();

            holder.travelType.setText("" + step.getTransportNumber());
            holder.details.setText("Towards " + step.getDirection());
            holder.travelTypeIcon.setImageResource(getImageOnType(step.getCategory()));
            DrawableCompat.setTint(drawable, getColor(category, step.getTransportNumber()));
        }

        holder.originName.setText(step.getOrigin().getName());
        holder.destName.setText(step.getDestination().getName());
    }


    private int getImageOnType(String type) {
        switch (type) {
            case "BUS":
                return R.drawable.ic_baseline_directions_bus_24;
            case "WALK":
                return R.drawable.ic_baseline_directions_walk_24;
            case "TRN":
                return R.drawable.ic_baseline_train_24;
            case "TRM":
                return R.drawable.ic_baseline_tram_24;
            case "MET":
                return R.drawable.ic_baseline_directions_subway_24;
            case "BOAT":
            case "SHP":
                return R.drawable.ic_baseline_directions_boat_24;
            default:
                return R.drawable.ic_baseline_question_mark_24;
        }
    }

    private int getColor(String type) {
        return getColor(type, 0);
    }

    private int getColor(String type, int number) {
        switch (type) {
            case "MET":
                switch (number) {
                    case 10:
                    case 11:
                        return Color.rgb(28, 47, 122);
                    case 13:
                    case 14:
                        return Color.rgb(213, 20, 39);
                    case 17:
                    case 18:
                    case 19:
                        return Color.rgb(96, 164, 43);
                    default:
                        return Color.rgb(0, 0, 0);
                }
            case "TRM":
                return Color.rgb(255, 153, 0);
            case "TRN":
                return Color.rgb(236, 97, 159);
            case "BUS":
                return Color.rgb(215, 29, 36);
            case "BUSBLUE":
                return Color.rgb(0, 151, 218);
            case "WALK":
                return Color.rgb(0, 0, 0);
            default:
                return Color.rgb(0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return trip.getSteps().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView travelType;
        public TextView startTime;
        public TextView endTime;
        public TextView originName;
        public TextView destName;
        public TextView details;
        public ImageView travelTypeIcon;
        private RelativeLayout cardTypeColor;

        public ViewHolder(View v) {
            super(v);
        }
    }

}
