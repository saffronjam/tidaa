package com.example.weatherthreepointo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherthreepointo.R;

import java.util.List;
import java.util.function.Consumer;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final List<String> data;
    private Consumer<String> onFavoriteClick;

    public FavoriteListAdapter(Consumer<String> onFavoriteClick, List<String> data) {
        this.data = data;
        this.onFavoriteClick = onFavoriteClick;
    }

    @NonNull
    @Override
    public FavoriteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_card_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(FavoriteListAdapter.ViewHolder holder, int position) {
        holder.cityNameTV.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView cityNameTV;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.cityNameTV = view.findViewById(R.id.idCityName);
        }

        @Override
        public void onClick(View view) {
            // Return back with favorite selected
            onFavoriteClick.accept(cityNameTV.getText().toString());
        }
    }
}