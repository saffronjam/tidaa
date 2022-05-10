package com.example.weatherthreepointo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherthreepointo.R;
import com.example.weatherthreepointo.Utils;
import com.example.weatherthreepointo.exceptions.LocationException;
import com.example.weatherthreepointo.model.viewModels.Location;
import com.example.weatherthreepointo.services.LocationService;
import com.example.weatherthreepointo.adapters.FavoriteListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListFavoritesActivity extends AppCompatActivity {

    private final ArrayList<String> favoriteList = new ArrayList<>();
    private final FavoriteListAdapter favoriteRvAdapter = new FavoriteListAdapter(this::onClickFavorite, favoriteList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_favorites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView favoriteListRV = findViewById(R.id.idFavoriteRecyclerView);
        favoriteListRV.setAdapter(favoriteRvAdapter);
        favoriteListRV.setLayoutManager(new LinearLayoutManager(this));

        syncFavoriteList();
    }

    private void onClickFavorite(String cityName) {
        if (Utils.hasInternetConnection(this)) {
            LocationService.fetch(cityName, this::onSuccessfulLocationFetch, this::onFailedLocationFetch);
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void intentBackToMain(Location location) {
        Intent intent = new Intent(this, WeatherDisplayActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    private void syncFavoriteList() {
        List<String> favoriteCities = LocationService.getAllFavorites(this);
        favoriteList.clear();
        favoriteList.addAll(favoriteCities);
        favoriteRvAdapter.notifyDataSetChanged();
    }

    private void onSuccessfulLocationFetch(List<Location> locationData) {
        if (locationData.isEmpty()) {
            onFailedLocationFetch(new LocationException("Failed to fetch resource"));
        } else {
            intentBackToMain(locationData.get(0));
        }
    }

    private void onFailedLocationFetch(LocationException exception) {
        Toast.makeText(getApplicationContext(), "Failed to load location: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}