package com.example.labb3ctimesl.dataservices;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.labb3ctimesl.AsyncHttpQueue;
import com.example.labb3ctimesl.Utils;
import com.example.labb3ctimesl.exception.FetchError;
import com.example.labb3ctimesl.model.Schedule;
import com.example.labb3ctimesl.model.Settings;
import com.example.labb3ctimesl.model.Trip;
import com.example.labb3ctimesl.model.json.TripData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TripDataService {
    private static final String TripPrefKey = "trip";

    private static final String apiKey = "023ce03f36b94bb7ad89b70123b3757d";
    private static final String url = "https://api.sl.se/api2/TravelplannerV3_1/trip.Json?key=%s&originCoordLat=%f&originCoordLong=%f&destCoordLat=%f&destCoordLong=%f&Date=%s&Time=%s&searchForArrival=1";

    private static final String TAG = TripDataService.class.getSimpleName();

    public static void fetch(float origLat, float origLong, float destLat, float destLong, Date arrivalLatest, Consumer<Trip> onSuccess, Consumer<FetchError> onFail) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String date = sdfDate.format(arrivalLatest);
        String time = sdfTime.format(arrivalLatest);

        String formatUrl = String.format(Locale.ENGLISH, url, apiKey, origLat, origLong, destLat, destLong, date, time);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, formatUrl, null,
                json -> onFetchSuccess(json, onSuccess, onFail),
                error -> onFetchFail(error, onFail)
        );
        AsyncHttpQueue.addRequest(request);
    }

    public static void getTrip(float destLat, float destLong, Context context, Consumer<Trip> then) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String tryGetTripString = sharedPref.getString(TripPrefKey, null);
        if (tryGetTripString != null) {
            // Trip was already fetched for given cours configuration
            then.accept(new Gson().fromJson(tryGetTripString, Trip.class));
        } else {
            // Trip must be fetched again since something changed
            Optional<Schedule> scheduleOpt = ScheduleDataService.getCachedSchedule(context);
            if (!scheduleOpt.isPresent() || !scheduleOpt.get().getEarliestLesson().isPresent()) {
                // No trip can be fetched if we don't have any lessons ;)
                then.accept(new Trip());
                return;
            }
            Schedule.Entry earliestEntry = scheduleOpt.get().getEarliestLesson().get();

            // Account for buffer time before lesson
            Settings settings = Settings.get(context);
            LocalDateTime earliestLessonStart = Utils.toLocalDateTime(earliestEntry.getStart());
            Date withOffset = Utils.toDate(earliestLessonStart
                    .plusMinutes(settings.getAcademicBufferTime())
                    .minusMinutes(settings.getArrivalBufferTime()));

            TripDataService.fetch(settings.getLatitude(), settings.getLongitude(), destLat, destLong, withOffset,
                    trip -> {
                        sharedPref.edit().putString(TripPrefKey, new Gson().toJson(trip)).apply();
                        then.accept(trip);
                    },
                    fetchError -> {
                        Log.w("Fetch fail", "Failed to fetch trip. Error: " + fetchError.getMessage());
                    });
        }
    }

    public static Optional<Trip> getCachedTrip(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String tryGetTripString = sharedPref.getString(TripPrefKey, null);
        if (tryGetTripString != null) {
            return Optional.of(new Gson().fromJson(tryGetTripString, Trip.class));
        }
        return Optional.empty();
    }


    public static void nullifyCurrentTrip(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().remove(TripPrefKey).apply();
    }

    private static void onFetchSuccess(JSONObject
                                               root, Consumer<Trip> onSuccess, Consumer<FetchError> onFail) {
        try {
            JSONArray tripJson = root.getJSONArray("Trip");
            // Get last trip since they are sorted acending and we want latest possible trip
            JSONObject lastTrip = (JSONObject) tripJson.get(tripJson.length() - 1);
            JSONObject legList = lastTrip.getJSONObject("LegList");
            JSONArray leg = legList.getJSONArray("Leg");
            Type listType = new TypeToken<List<TripData>>() {
            }.getType();
            List<TripData> tripData = new Gson().fromJson(leg.toString(), listType);

            List<Trip.Step> tripSteps = tripData
                    .stream()
                    .map(td -> {
                        LocalDate orginDateTmp;
                        LocalDate destinationDateTmp;
                        LocalTime orginTimeTmp;
                        LocalTime destinationTimeTmp;
                        Date originTime;
                        Date destinationTime;
                        try {
                            orginDateTmp = LocalDate.parse(td.origin.date);
                            orginTimeTmp = LocalTime.parse(td.origin.time);
                            originTime = Utils.toDate(LocalDateTime.of(orginDateTmp,orginTimeTmp));

                            destinationDateTmp = LocalDate.parse(td.destination.date);
                            destinationTimeTmp = LocalTime.parse(td.destination.time);
                            destinationTime = Utils.toDate(LocalDateTime.of(destinationDateTmp,destinationTimeTmp));
                        } catch (DateTimeParseException e) {
                            originTime = new Date();
                            destinationTime = new Date();
                        }

                        boolean walk = td.type.equals("WALK");

                        int transportNumber = walk ? -1 : Integer.parseInt(td.name.replaceAll("[^0-9]", ""));
                        return new Trip.Step(
                                new Trip.Step.Place(td.origin.name, originTime),
                                new Trip.Step.Place(td.destination.name, destinationTime),
                                td.name, td.category, td.type, td.direction, transportNumber);
                    })
                    .collect(Collectors.toList());

            for (int i = 1; i < tripSteps.size(); i++) {
                Trip.Step before = tripSteps.get(i - 1);
                Trip.Step after = tripSteps.get(i);

                if (before.getType().equals("WALK") && after.getType().equals("WALK")) {
                    Trip.Step merged = new Trip.Step(
                            before.getOrigin(),
                            after.getDestination(),
                            before.getName(),
                            before.getCategory(),
                            before.getType(),
                            after.getDirection(),
                            before.getTransportNumber());
                    tripSteps.set(i - 1, merged);
                    tripSteps.set(i, null);
                    i++;
                }
            }
            tripSteps = tripSteps.stream().filter(Objects::nonNull).collect(Collectors.toList());
            onSuccess.accept(new Trip(tripSteps));
        } catch (JSONException e) {
            onFail.accept(new FetchError(e.getMessage()));
        }
    }

    private static void onFetchFail(VolleyError error, Consumer<FetchError> onFail) {
        onFail.accept(new FetchError(error.getMessage()));
    }

}
