package com.example.labb3ctimesl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {


    public interface LocationChoiceListener {
        void onChoice(int position);
    }

    private LocationChoiceListener locationChoiceListener;
    private final List<Location> locations;

    public LocationAdapter(List<Location> courses) {
        this.locations = courses;
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_card, parent, false);
        final LocationAdapter.ViewHolder vh = new LocationAdapter.ViewHolder(itemView);

        vh.title = itemView.findViewById(R.id.locationName);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        Location entry = locations.get(position);
        holder.title.setText(entry.getAddress());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void setLocationChoiceListener(LocationChoiceListener locationChoiceListener) {
        this.locationChoiceListener = locationChoiceListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public int position;

        @Override
        public void onClick(View view) {
            if (locationChoiceListener != null) {
                locationChoiceListener.onChoice(position);
            }
        }

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }
    }
}
