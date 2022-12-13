package com.example.labb3ctimesl.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.adapter.CourseAdapter;
import com.example.labb3ctimesl.databinding.FragmentCoursesBinding;
import com.example.labb3ctimesl.model.Course;
import com.example.labb3ctimesl.dataservices.CourseDataService;
import com.example.labb3ctimesl.dataservices.ScheduleDataService;
import com.example.labb3ctimesl.model.SearchParams;
import com.example.labb3ctimesl.dataservices.TripDataService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseFragment extends Fragment {
    private FragmentCoursesBinding binding;

    private final List<Course> courses = new ArrayList<>();
    private final Set<String> selectedCourses = new HashSet<>();

    private EditText searchFieldET;
    private RecyclerView coursesRV;
    private CourseAdapter courseAdapter;

    private final Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        syncSettings();

        //Init recyclerview
        coursesRV = root.findViewById(R.id.courses);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        coursesRV.setLayoutManager(layoutManager);

        //Cards
        courseAdapter = new CourseAdapter(courses, selectedCourses);
        coursesRV.setAdapter(courseAdapter);
        courseAdapter.setChoseCourseListener(this::onCourseChoice);

        searchFieldET = root.findViewById(R.id.searchField);
        searchFieldET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    CourseDataService.fetchCourses(new SearchParams(s.toString()),
                            result -> {
                                courses.clear();
                                courses.addAll(result);
                                saveSearchResult(result);
                                handler.post(() -> courseAdapter.notifyDataSetChanged());
                            },
                            err -> {}
                    );
                } else {
                    courses.clear();
                    saveSearchResult(new ArrayList<>());
                    handler.post(() -> courseAdapter.notifyDataSetChanged());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onCourseChoice(Course course, boolean add) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String selectedCourses = sharedPref.getString("selectedCourses", "[]");
        Type type = new TypeToken<HashSet<Course>>() {
        }.getType();
        HashSet<Course> list = new Gson().fromJson(selectedCourses, type);
        if (add == (list.contains(course))) {
            return;
        }

        // Since we don't know when the changed course have any lessons we need to refetch trip
        TripDataService.nullifyCurrentTrip(requireContext());

        // We always need to re-fetch the schedule since we do not have support for partial edits
        ScheduleDataService.nullifySchedule(requireContext());

        if (add) {
            list.add(course);
            this.selectedCourses.add(course.getCode());
        } else {
            list.remove(course);
            this.selectedCourses.remove(course.getCode());
        }

        sharedPref.edit()
                .putString("selectedCourses", new Gson().toJson(list))
                .apply();
    }

    private void saveSearchResult(List<Course> courses) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPref.edit()
                .putString("searchResult", new Gson().toJson(courses))
                .apply();
    }


    private void syncSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String selectedCoursesString = sharedPref.getString("selectedCourses", "[]");
        String searchResultString = sharedPref.getString("searchResult", "[]");
        Type hashSetType = new TypeToken<HashSet<Course>>() {
        }.getType();
        Type listType = new TypeToken<List<Course>>() {
        }.getType();
        HashSet<Course> selectedCourses = new Gson().fromJson(selectedCoursesString, hashSetType);
        List<Course> searchResult = new Gson().fromJson(searchResultString, listType);

        this.selectedCourses.clear();
        this.selectedCourses.addAll(selectedCourses.stream().map(Course::getCode).collect(Collectors.toList()));

        this.courses.clear();
        this.courses.addAll(searchResult);
    }

}