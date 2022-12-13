package com.example.labb3ctimesl.activites;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.Utils;
import com.example.labb3ctimesl.adapter.LocationAdapter;
import com.example.labb3ctimesl.dataservices.LocationDataService;
import com.example.labb3ctimesl.dataservices.ScheduleDataService;
import com.example.labb3ctimesl.dataservices.TripDataService;
import com.example.labb3ctimesl.model.Location;
import com.example.labb3ctimesl.model.Settings;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private LocationAdapter locationAdapter;

    private final List<Location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Utils.getColor(R.attr.appBackground, this)));
        }

        //Init recyclerview
        RecyclerView locationRV = findViewById(R.id.locations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        locationRV.setLayoutManager(layoutManager);

        locationAdapter = new LocationAdapter(locations);
        locationAdapter.setLocationChoiceListener(position -> {
            Location location = locations.get(position);
            Settings.get(getApplicationContext())
                    .setLatitude(location.getLatitude())
                    .setLongitude(location.getLongitude())
                    .save();

            Settings settings = Settings.get(getApplicationContext());

            ScheduleDataService.nullifySchedule(getApplicationContext());
            TripDataService.nullifyCurrentTrip(getApplicationContext());
            finish();
        });

        locationRV.setAdapter(locationAdapter);

        EditText searchFieldET = findViewById(R.id.searchFieldLocation);
        searchFieldET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    LocationDataService.getLocation(getApplicationContext(), s.toString(), locs -> {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            locations.clear();
                            locations.addAll(locs);
                            locationAdapter.notifyDataSetChanged();
                        });
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}