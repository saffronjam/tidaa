package com.example.labb3ctimesl.model.json;

public class CourseLocationData {
    private final float latitude;
    private final float longitude;

    public CourseLocationData(float latitude, float longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
