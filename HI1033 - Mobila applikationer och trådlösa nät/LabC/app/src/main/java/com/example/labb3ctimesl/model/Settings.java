package com.example.labb3ctimesl.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

public class Settings {
    private transient Context context;
    private int morningBufferTime;
    private int arrivalBufferTime;
    private int academicBufferTime;
    private float latitude;
    private float longitude;

    private Settings(Context context) {
        this.context = context;
        this.morningBufferTime = 45;
        this.arrivalBufferTime = 10;
        this.academicBufferTime = 0;
        this.latitude = 59.2966017f;
        this.longitude = 18.0541255f;
    }

    public int getMorningBufferTime() {
        return morningBufferTime;
    }

    public int getArrivalBufferTime() {
        return arrivalBufferTime;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getAcademicBufferTime() {
        return academicBufferTime;
    }

    public Settings setMorningBufferTime(int morningBufferTime) {
        this.morningBufferTime = morningBufferTime;
        return this;
    }

    public Settings setArrivalBufferTime(int arrivalBufferTime) {
        this.arrivalBufferTime = arrivalBufferTime;
        return this;
    }

    public Settings setLatitude(float latitude) {
        this.latitude = latitude;
        return this;
    }

    public Settings setLongitude(float longitude) {
        this.longitude = longitude;
        return this;
    }

    public Settings setAcademicBufferTime(int academicBufferTime) {
        this.academicBufferTime = academicBufferTime;
        return this;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public void save() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        String jsonThis = new Gson().toJson(this);
        prefs.edit().putString("settings", jsonThis).apply();
    }

    public static Settings get(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String tryGetThisString = prefs.getString("settings", null);
        if (tryGetThisString == null) {
            return new Settings(context);
        }
        Settings settings = new Gson().fromJson(tryGetThisString, Settings.class);
        settings.setContext(context);
        return settings;
    }
}
