package com.example.labb3ctimesl.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.labb3ctimesl.AsyncHttpQueue;
import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.databinding.ActivityAppBinding;
import com.example.labb3ctimesl.dataservices.ScheduleDataService;
import com.example.labb3ctimesl.dataservices.TripDataService;
import com.example.labb3ctimesl.model.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityAppBinding binding;
    private TextView titleBarTV;
    //Settings
    private ImageButton settingsIB;

    //Log
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CHANNEL_ID = "ALARM_SERVICE_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "-------");
        Log.d(TAG, "onCreate");

        Objects.requireNonNull(getSupportActionBar()).hide();

        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        titleBarTV = findViewById(R.id.titleBar);
        //Settings setup
        settingsIB = findViewById(R.id.settingsBtn);
        settingsIB.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // Setup
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_travel, R.id.navigation_schedule, R.id.navigation_course)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_app);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.navigation_schedule);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_course) {
                Log.i(TAG, "onCreate: Changed fragment to Course");
                Log.i(TAG, "Has a internet connection: " + isConnected());
                titleBarTV.setText("Courses");
            } else if (destination.getId() == R.id.navigation_schedule) {
                Log.i(TAG, "onCreate: Changed fragment to Schedule");
                Log.i(TAG, "Has a internet connection: " + isConnected());
                titleBarTV.setText("Schedule");
            } else if (destination.getId() == R.id.navigation_travel) {
                Log.i(TAG, "onCreate: Changed fragment to Travel");
                Log.i(TAG, "Has a internet connection: " + isConnected());
                titleBarTV.setText("Travel");
            }
        });
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        AsyncHttpQueue.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        ScheduleDataService.load(this);

        // Transfer prefs to settings singleton for easier access in fragments
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Settings settings = Settings.get(this);
        boolean tripChanged = false;
        
        String academicTimeString = sharedPref.getString("academicTime", null);
        if (academicTimeString != null) {
            int newVal = Integer.parseInt(academicTimeString);
            int oldVal = settings.getAcademicBufferTime();
            if (newVal != oldVal) {
                settings.setAcademicBufferTime(newVal);
                tripChanged = true;
            }
        }

        if (tripChanged) {
            TripDataService.nullifyCurrentTrip(this);
        }
        settings.save();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScheduleDataService.save(this);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = cm.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = cm.getNetworkCapabilities(nw);
        boolean res = cm.isDefaultNetworkActive();
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }
}