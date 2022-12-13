package com.example.weatherthreepointo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherthreepointo.R;
import com.example.weatherthreepointo.model.viewModels.WeatherCard;

import java.util.List;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {
    private final Context context;
    private final List<WeatherCard> data;

    public WeatherListAdapter(Context context, List<WeatherCard> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public WeatherListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(context).inflate(R.layout.weather_card_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(WeatherListAdapter.ViewHolder holder, int position) {
        WeatherCard card = data.get(position);

        holder.celsiusTV.setText(card.getDegrees() + " °C");
        holder.dateTV.setText(card.getDate());
        holder.windSpeedTV.setText(card.getWindSpeed() + " m/s");
        holder.cloudCoverageTV.setText(card.getCloudCoverage() + " oktas");
        holder.weatherIconIV.setBackgroundResource(data.get(position).getDrawableId());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTV, celsiusTV, windSpeedTV, cloudCoverageTV;
        private final ImageView weatherIconIV;

        public ViewHolder(View view) {
            super(view);
            this.dateTV = view.findViewById(R.id.idTime);
            this.celsiusTV = view.findViewById(R.id.idDegrees);
            this.windSpeedTV = view.findViewById(R.id.idWindSpeed);
            this.cloudCoverageTV = view.findViewById(R.id.idCloudCoverage);
            this.weatherIconIV = view.findViewById(R.id.idWeatherIcon);
        }
    }
}