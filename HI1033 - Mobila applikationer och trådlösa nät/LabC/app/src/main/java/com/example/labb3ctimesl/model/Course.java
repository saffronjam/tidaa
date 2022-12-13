package com.example.labb3ctimesl.model;

import java.util.Objects;

public class Course {
    private final String code;
    private final String title;
    private final float credits;
    private final String creditUnitLabel;
    private final String creditUnitAbber;
    private final EducationLevel educationalLevel;

    public Course(String code, String title, float credits, String creditUnitLabel, String creditUnitAbber, EducationLevel educationalLevel) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.creditUnitLabel = creditUnitLabel;
        this.creditUnitAbber = creditUnitAbber;
        this.educationalLevel = educationalLevel;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public float getCredits() {
        return credits;
    }

    public String getCreditUnitLabel() {
        return creditUnitLabel;
    }

    public String getCreditUnitAbber() {
        return creditUnitAbber;
    }

    public EducationLevel getEducationalLevel() {
        return educationalLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}

