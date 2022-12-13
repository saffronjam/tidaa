package com.example.weatherthreepointo.model.json;

import com.example.weatherthreepointo.model.json.WeatherData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JsonParseHelpers {

    public static List<WeatherData> parseWeatherData(JSONObject root) throws JSONException {
        JSONArray timeSeriesObject = root.getJSONArray("timeSeries");
        Type listType = new TypeToken<ArrayList<WeatherData>>() {
        }.getType();
        List<WeatherData> list = new Gson().fromJson(timeSeriesObject.toString(), listType);
        list.sort(Comparator.comparing(WeatherData::getValidTime));
        return list;
    }

    public static List<LocationData> parseLocationData(JSONArray root) throws JSONException {
        Type listType = new TypeToken<ArrayList<LocationData>>() {
        }.getType();
        return new Gson().fromJson(root.toString(), listType);
    }
}
