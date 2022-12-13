package com.example.weatherthreepointo.model.viewModels;

import java.io.Serializable;

public class WeatherCard implements Serializable {
    private final String date;
    private final String degrees; // In celsius
    private final String cloudCoverage;
    private final String windSpeed;
    private final int drawableId;

    public WeatherCard(String date, String degrees, String cloudCoverage, String windSpeed, int drawableId) {
        this.date = date;
        this.degrees = degrees;
        this.cloudCoverage = cloudCoverage;
        this.windSpeed = windSpeed;
        this.drawableId = drawableId;
    }

    public String getDate() {
        return date;
    }

    public String getDegrees() {
        return degrees;
    }

    public String getCloudCoverage() {
        return cloudCoverage;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public int getDrawableId() {
        return drawableId;
    }
}
