package com.example.labb3ctimesl.dataservices;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.labb3ctimesl.AsyncHttpQueue;
import com.example.labb3ctimesl.exception.FetchError;
import com.example.labb3ctimesl.model.Course;
import com.example.labb3ctimesl.model.SearchParams;
import com.example.labb3ctimesl.model.json.SearchCourse;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CourseDataService {
    private static final String searchURL = "https://www.kth.se/api/kopps/v2/courses/search?text_pattern=%s";

    public static void fetchCourses(SearchParams searchParams, Consumer<List<Course>> onSuccess, Consumer<FetchError> onFail) {
        String formatUrl = String.format(Locale.ENGLISH, searchURL, searchParams.getTextPattern());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, formatUrl, null,
                json -> onFetchSuccess(json, onSuccess, onFail),
                error -> onFetchFail(error, onFail)
        );
        AsyncHttpQueue.addRequest(request);
    }

    private static void onFetchSuccess(JSONObject root, Consumer<List<Course>> onSuccess, Consumer<FetchError> onFail) {
        try {
            SearchCourse searchCourse = new Gson().fromJson(root.toString(), SearchCourse.class);
            List<Course> entries = searchCourse.searchHits.stream().map(hm -> {
                SearchCourse.Course in = hm.get("course");
                assert in != null;
                return new Course(in.courseCode, in.title, in.credits, in.creditUnitLabel, in.creditUnitAbbr, in.educationalLevel);
            }).collect(Collectors.toList());
            onSuccess.accept(entries);
        } catch (Exception e) {
            e.printStackTrace();
            onFail.accept(new FetchError("Parsing error"));
        }
    }

    private static void onFetchFail(VolleyError error, Consumer<FetchError> onFail) {
        onFail.accept(new FetchError(error.getMessage()));
    }


}
