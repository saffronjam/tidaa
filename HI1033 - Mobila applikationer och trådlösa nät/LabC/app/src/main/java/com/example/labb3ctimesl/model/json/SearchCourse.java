package com.example.labb3ctimesl.model.json;

import com.example.labb3ctimesl.model.EducationLevel;

import java.util.HashMap;
import java.util.List;

public class SearchCourse {
    public List<HashMap<String, Course>> searchHits;

    public static class Course {
        public String courseCode;
        public String title;
        public float credits;
        public String creditUnitLabel;
        public String creditUnitAbbr;
        public EducationLevel educationalLevel;
    }

}
