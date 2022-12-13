package com.example.weatherthreepointo.services;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.weatherthreepointo.exceptions.LocationException;
import com.example.weatherthreepointo.model.AsyncHttpQueue;
import com.example.weatherthreepointo.model.json.JsonParseHelpers;
import com.example.weatherthreepointo.model.json.LocationData;
import com.example.weatherthreepointo.model.viewModels.Location;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LocationService {
    private static final String url = "https://www.smhi.se/wpt-a/backend_solr/autocomplete/search/%s";

    public static void fetch(String searchCity, Consumer<List<Location>> onSuccess, Consumer<LocationException> onFail) {
        String formatUrl = String.format(url, searchCity);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, formatUrl, null, json -> onJsonSuccess(json, onSuccess, onFail), e -> onFailedJson(e, onFail));
        AsyncHttpQueue.getInstance().addRequest(request);
    }

    public static boolean isLocationFavorite(Context context, Location location) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String city = sharedPref.getString(location.getCity(), "");
        return !city.isEmpty();
    }

    public static void addFavorite(Context context, Location location) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String currentFavorites = sharedPref.getString("favorites", "");
        sharedPref.edit()
                .putString(location.getCity(), location.getCity())
                .putFloat(location.getCity() + "-lat", location.getLatitude())
                .putFloat(location.getCity() + "-lon", location.getLongitude())
                .putString("favorites", currentFavorites + (currentFavorites.isEmpty() ? location.getCity() : "|" + location.getCity()))
                .apply();
    }

    public static void removeFavorite(Context context, Location location) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String[] favList = sharedPref.getString("favorites", "").split("\\|");
        List<String> newList = Arrays.stream(favList).filter(s -> !s.equals(location.getCity())).collect(Collectors.toList());
        String newListString = String.join("|", newList);
        sharedPref.edit()
                .remove(location.getCity())
                .remove(location.getCity() + "-lat")
                .remove(location.getCity() + "-lon")
                .putString("favorites", newListString)
                .apply();
    }

    public static List<String> getAllFavorites(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> result = Arrays.asList(sharedPref.getString("favorites", "").split("\\|"));
        if (result.size() == 1 && result.get(0).isEmpty()) {
            return new ArrayList<>();
        }
        return result;
    }

    private static void onJsonSuccess(JSONArray rootArray, Consumer<List<Location>> onSuccess, Consumer<LocationException> onFail) {
        try {
            onSuccess.accept(toLocations(JsonParseHelpers.parseLocationData(rootArray)));
        } catch (JSONException e) {
            onFailedJson(e, onFail);
        }
    }

    private static void onFailedJson(Exception error, Consumer<LocationException> onFail) {
        onFail.accept(new LocationException(error.getMessage()));
    }

    private static List<Location> toLocations(List<LocationData> locationData) {
        return locationData.stream().map(d -> new Location(d.getPlace(), d.getCounty(), d.getLon(), d.getLat())).collect(Collectors.toList());
    }
}
