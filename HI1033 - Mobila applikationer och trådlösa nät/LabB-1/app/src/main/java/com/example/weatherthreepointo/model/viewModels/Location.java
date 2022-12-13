package com.example.weatherthreepointo.model.viewModels;

import java.io.Serializable;

public class Location implements Serializable {
    private final String city;
    private final String county;
    private final float longitude;
    private final float latitude;

    public Location(String city, String county, float longitude, float latitude) {
        this.city = city;
        this.county = county;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }
}
