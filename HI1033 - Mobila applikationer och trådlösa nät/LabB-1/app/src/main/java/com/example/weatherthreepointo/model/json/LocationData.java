package com.example.weatherthreepointo.model.json;

import java.util.List;

public class LocationData {
    private final int geonameid;
    private final String place;
    private final int population;
    private final float lon; // Longitude
    private final float lat; // latitude
    private final List<String> type;
    private final String municipality;
    private final String county;
    private final String country;
    private final String district;

    public LocationData(int geonameid, String place, int population, float lon, float lat, List<String> type, String municipality, String county, String country, String district) {
        this.geonameid = geonameid;
        this.place = place;
        this.population = population;
        this.lon = lon;
        this.lat = lat;
        this.type = type;
        this.municipality = municipality;
        this.county = county;
        this.country = country;
        this.district = district;
    }

    public int getGeonameid() {
        return geonameid;
    }

    public String getPlace() {
        return place;
    }

    public int getPopulation() {
        return population;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    public List<String> getType() {
        return type;
    }

    public String getMunicipality() {
        return municipality;
    }

    public String getCounty() {
        return county;
    }

    public String getCountry() {
        return country;
    }

    public String getDistrict() {
        return district;
    }
}
