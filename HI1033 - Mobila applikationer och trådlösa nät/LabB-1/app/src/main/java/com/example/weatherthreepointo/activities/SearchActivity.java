package com.example.weatherthreepointo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.weatherthreepointo.adapters.SearchResultAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private final ArrayList<Location> searchResultList = new ArrayList<>();
    private final SearchResultAdapter searchResultAdapter = new SearchResultAdapter(this::intentBackToMain, searchResultList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView searchResultRV = findViewById(R.id.idSearchResult);
        searchResultRV.setAdapter(searchResultAdapter);
        searchResultRV.setLayoutManager(new LinearLayoutManager(this));

        syncResultList("");

        TextInputEditText searchBarTIET = findViewById(R.id.idSearchBar);
        searchBarTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                syncResultList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void syncResultList(String search) {
        if (Utils.hasInternetConnection(this)) {
            LocationService.fetch(search, this::onSuccessfulLocationFetch, this::onFailedLocationFetch);
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void intentBackToMain(Location location) {
        Intent intent = new Intent(this, WeatherDisplayActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    private void onSuccessfulLocationFetch(List<Location> locationData) {
        searchResultList.clear();
        searchResultList.addAll(locationData);
        searchResultAdapter.notifyDataSetChanged();
    }

    private void onFailedLocationFetch(LocationException exception) {
        Toast.makeText(getApplicationContext(), "Failed to load location: " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}