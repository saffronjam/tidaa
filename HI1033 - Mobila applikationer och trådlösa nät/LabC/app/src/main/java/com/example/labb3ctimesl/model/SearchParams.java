package com.example.labb3ctimesl.model;

import java.util.ArrayList;

public class SearchParams {
    private final String textPattern;
    private final boolean onlyMhu;
    private final boolean inEnglishOnly;
    private final boolean includeNonActive;
    private int termPeriod;
    private ArrayList<EducationLevel> educationLevels;
    private String departmentPrefix;

    public SearchParams(String textPattern, boolean onlyMhu, boolean inEnglishOnly, boolean includeNonActive, int termPeriod, ArrayList<EducationLevel> educationLevels, String departmentPrefix) {
        this.textPattern = textPattern;
        this.onlyMhu = onlyMhu;
        this.inEnglishOnly = inEnglishOnly;
        this.includeNonActive = includeNonActive;
        this.termPeriod = termPeriod;
        this.educationLevels = educationLevels;
        this.departmentPrefix = departmentPrefix;
    }

    public SearchParams(String textPattern) {
        this.textPattern = textPattern;
        onlyMhu = false;
        inEnglishOnly = false;
        includeNonActive = false;
    }
    
    public String getTextPattern() {
        return textPattern;
    }

    public boolean isOnlyMhu() {
        return onlyMhu;
    }

    public boolean isInEnglishOnly() {
        return inEnglishOnly;
    }

    public boolean isIncludeNonActive() {
        return includeNonActive;
    }

    public int getTermPeriod() {
        return termPeriod;
    }

    public ArrayList<EducationLevel> getEducationLevels() {
        return educationLevels;
    }

    public String getDepartmentPrefix() {
        return departmentPrefix;
    }
}