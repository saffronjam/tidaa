package com.example.labb3ctimesl.dataservices;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.example.labb3ctimesl.model.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LocationDataService {

    private static final double lowerLeftLatitude = 55.00;
    private static final double lowerLeftLongitude = 10.00;
    private static final double upperRightLatitude = 70.00;
    private static final double upperRightLongitude = 25.00;

    public static void getLocation(Context context, String address, Consumer<List<Location>> then) {
        Runnable task = () -> {
            try {
                Geocoder coder = new Geocoder(context, Locale.getDefault());
                List<Address> result = coder.getFromLocationName(address, 10, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRightLongitude);
                {
                    then.accept(result
                            .stream()
                            .map(a -> new Location(a.getAddressLine(0), (float) a.getLatitude(), (float) a.getLongitude()))
                            .collect(Collectors.toList()));
                }
            } catch (IOException e) {
                Log.w("Fetch error", "Failed to get geolocation for: " + address + " Reason: " + e.getMessage());
            }
        };

        AsyncTask.execute(task);
    }

}

