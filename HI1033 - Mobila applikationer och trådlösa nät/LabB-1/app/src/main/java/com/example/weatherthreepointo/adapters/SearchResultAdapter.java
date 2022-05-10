package com.example.weatherthreepointo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherthreepointo.R;
import com.example.weatherthreepointo.model.viewModels.Location;

import java.util.List;
import java.util.function.Consumer;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private final List<Location> data;
    private final Consumer<Location> onFavoriteClick;

    public SearchResultAdapter(Consumer<Location> onFavoriteClick, List<Location> data) {
        this.data = data;
        this.onFavoriteClick = onFavoriteClick;
    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_card_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder holder, int position) {
        Location location = data.get(position);
        if (location.getCounty() == null) {
            holder.cityNameTV.setText(location.getCity());
        } else {
            holder.cityNameTV.setText(location.getCity() + ", " + location.getCounty());
        }
        holder.location = location;
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView cityNameTV;
        private Location location;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.cityNameTV = view.findViewById(R.id.idCityName);
        }

        @Override
        public void onClick(View view) {
            // Return back with favorite selected
            onFavoriteClick.accept(location);
        }
    }
}