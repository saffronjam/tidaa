package com.example.weatherthreepointo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherthreepointo.R;
import com.example.weatherthreepointo.Utils;
import com.example.weatherthreepointo.exceptions.WeatherException;
import com.example.weatherthreepointo.model.AsyncHttpQueue;
import com.example.weatherthreepointo.model.viewModels.Location;
import com.example.weatherthreepointo.model.viewModels.WeatherCard;
import com.example.weatherthreepointo.services.LocationService;
import com.example.weatherthreepointo.services.WeatherService;
import com.example.weatherthreepointo.adapters.WeatherListAdapter;
import com.google.gson.Gson;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherDisplayActivity extends AppCompatActivity {
    private ProgressBar jsonLoadPB;
    private RecyclerView mainViewRV;
    private TextView cityDisplayTV;
    private Button settingsB;
    private Button searchB;
    private Button listFavoritesB;
    private ImageButton favoriteIB;
    private TextView lastUpdateTV;

    private int refreshDelay;

    private final ArrayList<WeatherCard> weatherCards = new ArrayList<>();
    private Location currentLocation = null;
    private final WeatherListAdapter mainViewRVAdapter = new WeatherListAdapter(this, weatherCards);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        refreshSettings();
        refreshIfOldEnough();

        // Initialize singleton volley request queue
        AsyncHttpQueue.initialize(this);

        setupUiElements();
        setupListeners();

        mainViewRV.setAdapter(mainViewRVAdapter);
        mainViewRV.setLayoutManager(new LinearLayoutManager(this));

        setLoading(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCityNameCache();
        loadWeatherCardsCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSettings();

        // Recover bundles
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // If resumed by favorite list or search result, we could have a location sent to us
            Serializable tryGetLocation = extras.getSerializable("location");
            if (tryGetLocation != null) {
                Location location = (Location) tryGetLocation;
                setLocation(location);
            }
        }
        refreshIfOldEnough();
        refreshLastUpdateText(Utils.hasInternetConnection(this));
    }

    private void refreshIfOldEnough() {
        LocalDateTime lastUpdate = getLastUpdate();
        if (ChronoUnit.MINUTES.between(lastUpdate, LocalDateTime.now()) > refreshDelay) {
            // Only fetch if we have a valid city
            if (currentLocation != null) {
                boolean hasInternet = Utils.hasInternetConnection(this);
                if (hasInternet) {
                    fetchWeatherCards();
                    setLastUpdate(LocalDateTime.now());
                } else {
                    Toast.makeText(getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
                }
                refreshLastUpdateText(hasInternet);
            }
        }
    }

    private void setupUiElements() {
        mainViewRV = findViewById(R.id.recycler_view);
        jsonLoadPB = findViewById(R.id.idWeatherProgressBar);
        cityDisplayTV = findViewById(R.id.idCityDisplay);
        settingsB = findViewById(R.id.idSettingsActivityButton);
        favoriteIB = findViewById(R.id.idFavoriteButton);
        listFavoritesB = findViewById(R.id.idFavoriteListActivityButton);
        searchB = findViewById(R.id.idSearchActitvityButton);
        lastUpdateTV = findViewById(R.id.idLastUpdate);
    }

    private void setupListeners() {
        settingsB.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        listFavoritesB.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListFavoritesActivity.class);
            startActivity(intent);
        });
        searchB.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        favoriteIB.setOnClickListener(v -> {
            if (currentLocation != null) {
                if (LocationService.isLocationFavorite(this, currentLocation)) {
                    LocationService.removeFavorite(this, currentLocation);
                } else {
                    LocationService.addFavorite(this, currentLocation);
                }
                updateFavoriteImagebuttonResource();
            }
        });
    }

    private void fetchWeatherCards() {
        setLoading(true);
        WeatherService.fetch(currentLocation.getLongitude(), currentLocation.getLatitude(), this::onSuccessfulJsonFetch, this::onFailedJsonFetch);
    }

    private void clearWeatherCards() {
        weatherCards.clear();
        mainViewRVAdapter.notifyDataSetChanged();
        setLoading(false);
    }

    private void onSuccessfulJsonFetch(List<WeatherCard> weatherCards) {
        this.weatherCards.clear();
        this.weatherCards.addAll(weatherCards);
        mainViewRVAdapter.notifyDataSetChanged();
        // Put new data in storage
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String weathCardListString = new Gson().toJson(weatherCards);
        String locationString = new Gson().toJson(currentLocation);
        sharedPref.edit()
                .putString("weatherCardCache", weathCardListString)
                .putString("locationCache", locationString)
                .apply();


        setLoading(false);
    }

    private void loadCityNameCache() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String locationCacheString = sharedPref.getString("locationCache", null);
        if (locationCacheString != null) {
            setLocation(new Gson().fromJson(locationCacheString, Location.class));
        }
    }

    private void loadWeatherCardsCache() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String weathCardListString = sharedPref.getString("weatherCardCache", null);
        if (weathCardListString != null) {
            weatherCards.clear();
            weatherCards.addAll(Arrays.asList(new Gson().fromJson(weathCardListString, WeatherCard[].class)));
        }
    }

    private void onFailedJsonFetch(WeatherException exception) {
        clearWeatherCards();
        setLoading(false);
    }

    private void setLoading(boolean state) {
        if (state) {
            jsonLoadPB.setVisibility(View.VISIBLE);
            mainViewRV.setVisibility(View.GONE);
        } else {
            jsonLoadPB.setVisibility(View.GONE);
            mainViewRV.setVisibility(View.VISIBLE);
        }
    }

    private void refreshSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String refreshTimerString = sharedPref.getString("refresh_timer", "minutes_5");
        refreshDelay = Integer.parseInt(refreshTimerString.substring("minutes_".length()));
    }

    private void refreshLastUpdateText(boolean connected) {
        LocalDateTime lastUpdate = getLastUpdate();
        String formattedDate = Utils.getFormattedDayString(lastUpdate);
        lastUpdateTV.setText((connected ? "LIVE: " : "STALE: ") + formattedDate);
    }

    private void setLastUpdate(LocalDateTime time) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("lastWeatherFetch", time.toString()).apply();
    }

    private LocalDateTime getLastUpdate() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String dateTimeString = sharedPref.getString("lastWeatherFetch", "");
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return LocalDateTime.MIN;
        }
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void setLocation(Location location) {
        favoriteIB.setVisibility(View.VISIBLE);
        cityDisplayTV.setText(location.getCity());
        currentLocation = location;

        updateFavoriteImagebuttonResource();
    }

    private void updateFavoriteImagebuttonResource() {
        favoriteIB.setVisibility(View.VISIBLE);
        if (LocationService.isLocationFavorite(this, currentLocation)) {
            favoriteIB.setImageResource(R.drawable.star_filled);
        } else {
            favoriteIB.setImageResource(R.drawable.star_hollow);
        }
    }

}