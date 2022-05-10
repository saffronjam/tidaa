package com.example.labb3ctimesl.dataservices;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.labb3ctimesl.AsyncHttpQueue;
import com.example.labb3ctimesl.Utils;
import com.example.labb3ctimesl.model.Alarm;
import com.example.labb3ctimesl.model.Course;
import com.example.labb3ctimesl.model.Schedule;
import com.example.labb3ctimesl.model.json.CourseData;
import com.example.labb3ctimesl.model.json.CourseLocationData;
import com.example.labb3ctimesl.model.json.CourseMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ScheduleDataService {
    private static final String SchedulePrefKey = "schedule";

    //TODO: fix search of sechdule depending on time
    private static final String url = "https://www.kth.se/api/schema/v2/course/%s?startTime=%s&endTime=%s";
    private static final String metaurl = "https://www.kth.se/api/kopps/v2/course/%s";

    private static final HashMap<UUID, HashMap<String, Schedule>> cache = new HashMap<>();

    private static Schedule scheduleCache;

    // fetch -> skapa id
    // för varje kurs kod gör request och skicka med id
    // när request är klar, lägg in i temporär hashmap
    //      kolla även om efter request lagt in i hashmap, ifall


    public static void fetch(List<String> courseCodes, Consumer<Schedule> onResult) {
        if (courseCodes.isEmpty()) {
            onResult.accept(new Schedule());
            return;
        }

        UUID uuid = UUID.randomUUID();
        int requests = courseCodes.size();
        cache.put(uuid, new HashMap<>());
        HashMap<String, Schedule> requestCache = cache.get(uuid);

        for (String courseCode : courseCodes) {
            doMetaRequest(courseCode, meta -> {
                doDataRequest(meta, courseData -> {
                    if (courseData.entries.length > 0) {
                        for (CourseData.Entries entry : courseData.entries) {
                            doLongLatRequest(entry.locations[0].url, location -> {
                                Schedule schedule = new Schedule(
                                        Arrays.stream(courseData.entries).map(e -> createEntry(e, meta, location)).collect(Collectors.toList())
                                );
                                requestCache.put(courseCode, schedule);
                                if (requestCache.size() == requests) {
                                    Schedule entries = collectEntries(requestCache);
                                    onResult.accept(entries);
                                    cache.remove(uuid);
                                }
                            });
                        }
                    } else {
                        Schedule schedule = new Schedule();
                        requestCache.put(courseCode, schedule);
                        if (requestCache.size() == requests) {
                            Schedule entries = collectEntries(requestCache);
                            onResult.accept(entries);
                            cache.remove(uuid);
                        }
                    }
                });
            });
        }
    }

    public static void getSchedule(Context context, Consumer<Schedule> then) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Strategy:
        // 1. Fetch local variable
        // 2. Fetch local storage with JSON parse
        // 3. Fetch externally with http

        if (scheduleCache == null) {
            load(context);
            String tryGetScheduleString = sharedPref.getString(SchedulePrefKey, null);
            if (tryGetScheduleString != null) {
                // Trip was already fetched for given cours configuration
                scheduleCache = new Gson().fromJson(tryGetScheduleString, Schedule.class);
                then.accept(new Schedule(new ArrayList<>(scheduleCache.getEntries())));
            } else {
                List<String> selectedCourseCodes = getSelectedCourseCodes(context);
                fetch(selectedCourseCodes, schedule -> {
                    sharedPref.edit().putString(SchedulePrefKey, new Gson().toJson(schedule)).apply();
                    scheduleCache = schedule;
                    then.accept(new Schedule(new ArrayList<>(scheduleCache.getEntries())));
                });
            }
        } else {
            then.accept(new Schedule(new ArrayList<>(scheduleCache.getEntries())));
        }
    }

    public static Optional<Schedule> getCachedSchedule(Context context) {
        if (scheduleCache == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String tryGetScheduleString = sharedPref.getString(SchedulePrefKey, null);
            if (tryGetScheduleString != null) {
                scheduleCache = new Gson().fromJson(tryGetScheduleString, Schedule.class);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(new Schedule(new ArrayList<>(scheduleCache.getEntries())));
    }

    public static void save(Context context) {
        if (scheduleCache != null) {
            overwrite(context, scheduleCache);
        }
    }

    public static void load(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String tryGetScheduleString = sharedPref.getString(SchedulePrefKey, null);
        if (tryGetScheduleString != null) {
            scheduleCache = new Gson().fromJson(tryGetScheduleString, Schedule.class);
        }
    }

    public static void nullifySchedule(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().remove(SchedulePrefKey).apply();
        scheduleCache = null;
        Alarm.cancel(context);
    }

    public static List<String> getSelectedCourseCodes(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedCourses = sharedPref.getString("selectedCourses", "[]");
        Type type = new TypeToken<HashSet<Course>>() {
        }.getType();
        HashSet<Course> list = new Gson().fromJson(selectedCourses, type);
        return list.stream().map(Course::getCode).collect(Collectors.toList());
    }

    private static void doMetaRequest(String courseCode, Consumer<CourseMetadata> then) {
        String formatUrl = String.format(metaurl, courseCode);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, formatUrl, null,
                json -> then.accept(parseMeta(courseCode, json)),
                e -> {
                    Log.w("Fetch fail", "Failed to fetch metadata for course: " + courseCode + " Error: " + e.getMessage());
                    then.accept(CourseMetadata.getFailed(courseCode));
                }
        );
        AsyncHttpQueue.addRequest(request);
    }

    private static void doLongLatRequest(String url, Consumer<CourseLocationData> then) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    int latIndex = response.indexOf("<meta name=\"latitude\" content=\"");
                    int longIndex = response.indexOf("longitude");
                    if (latIndex == -1 || longIndex == -1) {
                        Log.w("Fetch fail", "Failed to fetch location for course. URL: " + url + " Error: No latitude or longitude found in html");
                        return;
                    }

                    int latStartIndex = response.indexOf("content=\"", latIndex) + "content=\"".length();
                    int longStartIndex = response.indexOf("content=\"", longIndex) + "content=\"".length();
                    int latEndIndex = response.indexOf("\"", latStartIndex + 1);
                    int longEndIndex = response.indexOf("\"", longStartIndex + 1);

                    String latSub = response.substring(latStartIndex, latEndIndex);
                    String longSub = response.substring(longStartIndex, longEndIndex);

                    float latitude = Float.parseFloat(latSub);
                    float longitude = Float.parseFloat(longSub);

                    then.accept(new CourseLocationData(latitude, longitude));
                },
                error -> Log.w("Fetch fail", "Failed to fetch location for course. URL: " + url + " Error: " + error.getMessage())
        );
        AsyncHttpQueue.addRequest(request);
    }

    private static void doDataRequest(CourseMetadata meta, Consumer<CourseData> then) {
        Date now = new Date();
        Date chosen;

        if (now.getHours() > 18) {
            chosen = Utils.toDate(LocalDateTime.now().plusDays(1));
        } else {
            chosen = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String startDateStr = sdf.format(chosen);
        String endDateStr = sdf.format(Utils.toDate(Utils.toLocalDateTime(chosen).plusDays(1)));

        String formatUrl = String.format(url, meta.code, "2022-01-18", "2022-01-19");
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, formatUrl, null,
                json -> {
                    CourseData data = new Gson().fromJson(json.toString(), CourseData.class);
                    then.accept(data);
                },
                e -> {
                    Log.w("Fetch fail", "Failed to fetch data for course: " + meta.code + " Error: " + e.getMessage());
                    then.accept(new CourseData());
                }
        );
        AsyncHttpQueue.addRequest(request);
    }

    private static Schedule collectEntries(HashMap<String, Schedule> requestCache) {
        List<Schedule.Entry> finalEntries = new ArrayList<>();
        for (Schedule schedules : requestCache.values()) {
            finalEntries.addAll(schedules.getEntries());
        }
        finalEntries.sort(Comparator.comparing(Schedule.Entry::getStart));
        return new Schedule(finalEntries);
    }

    private static CourseMetadata parseMeta(String courseCode, JSONObject jsonObject) {
        CourseMetadata meta;
        try {
            meta = new Gson().fromJson(jsonObject.toString(), CourseMetadata.class);
        } catch (Exception exception) {
            meta = CourseMetadata.getFailed(courseCode);
        }
        return meta;
    }

    private static Schedule.Entry createEntry(CourseData.Entries entry, CourseMetadata meta, CourseLocationData location) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date start;
        try {
            start = sdf.parse(entry.start);
        } catch (ParseException ex) {
            start = new Date();
        }

        return new Schedule.Entry(meta.title.en, meta.code, start, entry.locations[0].name, entry.type_name.en, location.getLatitude(), location.getLongitude());
    }

    private static void overwrite(Context context, Schedule schedule) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putString(SchedulePrefKey, new Gson().toJson(schedule)).apply();
    }

    public static void setEntryEnabled(int position, boolean enabled) {
        scheduleCache.getEntries().get(position).setEnabled(enabled);
    }
}
