package com.example.weatherthreepointo.services;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.weatherthreepointo.R;
import com.example.weatherthreepointo.Utils;
import com.example.weatherthreepointo.exceptions.WeatherException;
import com.example.weatherthreepointo.model.AsyncHttpQueue;
import com.example.weatherthreepointo.model.WeatherSymbol;
import com.example.weatherthreepointo.model.json.JsonParseHelpers;
import com.example.weatherthreepointo.model.json.WeatherData;
import com.example.weatherthreepointo.model.viewModels.Location;
import com.example.weatherthreepointo.model.viewModels.WeatherCard;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeatherService {

    private static final String url = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/%.3f/lat/%.3f/data.json";

    public static void fetch(float longitude, float latitude, Consumer<List<WeatherCard>> onSuccess, Consumer<WeatherException> onFail) {
        String formatUrl = String.format(Locale.ENGLISH, url, longitude, latitude);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, formatUrl, null, json -> parseJson(json, onSuccess, onFail), e ->
                onFailedFetch(e, onFail));
        AsyncHttpQueue.getInstance().addRequest(request);
    }

    private static void parseJson(JSONObject rootObject, Consumer<List<WeatherCard>> onSuccess, Consumer<WeatherException> onFail) {
        try {
            List<WeatherCard> cards = toWeatherCards(JsonParseHelpers.parseWeatherData(rootObject));
            onSuccess.accept(cards);
        } catch (JSONException e) {
            onFail.accept(new WeatherException(e.getMessage()));
        }
    }

    private static void onFailedFetch(VolleyError error, Consumer<WeatherException> onFail) {
        onFail.accept(new WeatherException(error.getMessage()));
    }

    private static List<WeatherCard> toWeatherCards(List<WeatherData> weatherData) {
        LocalDateTime now = LocalDateTime.now();
        return weatherData.stream().map(w -> {
            List<WeatherData.Parameters> params = w.getParameters();
            float celsius = params.stream().filter(p -> p.getName().equals("t")).findFirst().get().getValues().get(0);
            int symbol = (int) (float) params.stream().filter(p -> p.getName().equals("Wsymb2")).findFirst().get().getValues().get(0);
            float cloudCoverage = params.stream().filter(p -> p.getName().equals("tcc_mean")).findFirst().get().getValues().get(0);
            float windSpeed = params.stream().filter(p -> p.getName().equals("ws")).findFirst().get().getValues().get(0);

            Date date = w.getValidTime();
            LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            String formattedDate = Utils.getFormattedDayString(dateTime);

            WeatherSymbol weatherSymbol = WeatherSymbol.values()[symbol];
            int hour = dateTime.getHour();
            int drawableId = (hour > 6 && hour < 18) ? WeatherService.getDayDrawableId(weatherSymbol) : WeatherService.getNightDrawableId(weatherSymbol);

            return new WeatherCard(formattedDate, String.valueOf(celsius), String.valueOf(cloudCoverage), String.valueOf(windSpeed), drawableId);
        }).collect(Collectors.toList());
    }

    public static int getDayDrawableId(WeatherSymbol symbol) {
        switch (symbol) {
            case Unknown:
                break;
            case ClearSky:
                return R.drawable.wd1;
            case NearlyClearSky:
                return R.drawable.wd2;
            case VariableCloudiness:
                return R.drawable.wd3;
            case HalfclearSky:
                return R.drawable.wd4;
            case CloudySky:
                return R.drawable.wd5;
            case Overcast:
                return R.drawable.wd6;
            case Fog:
                return R.drawable.wd7;
            case LightRainShowers:
                return R.drawable.wd8;
            case ModerateRainShowers:
                return R.drawable.wd9;
            case HeavyRainShowers:
                return R.drawable.wd10;
            case Thunderstorm:
                return R.drawable.wd11;
            case LightSleetShowers:
                return R.drawable.wd12;
            case ModerateSleetShowers:
                return R.drawable.wd13;
            case HeavySleetShowers:
                return R.drawable.wd14;
            case LightSnowShowers:
                return R.drawable.wd15;
            case ModerateSnowShowers:
                return R.drawable.wd16;
            case HeavySnowShowers:
                return R.drawable.wd17;
            case LightRain:
                return R.drawable.wd18;
            case ModerateRain:
                return R.drawable.wd19;
            case HeavyRain:
                return R.drawable.wd20;
            case Thunder:
                return R.drawable.wd21;
            case LightSleet:
                return R.drawable.wd22;
            case ModerateSleet:
                return R.drawable.wd23;
            case HeavySleet:
                return R.drawable.wd24;
            case LightSnowfall:
                return R.drawable.wd25;
            case ModerateSnowfall:
                return R.drawable.wd26;
            case HeavySnowfall:
                return R.drawable.wd27;
        }
        return R.drawable.wd1;
    }

    public static int getNightDrawableId(WeatherSymbol symbol) {
        switch (symbol) {
            case Unknown:
                break;
            case ClearSky:
                return R.drawable.wn1;
            case NearlyClearSky:
                return R.drawable.wn2;
            case VariableCloudiness:
                return R.drawable.wn3;
            case HalfclearSky:
                return R.drawable.wn4;
            case CloudySky:
                return R.drawable.wn5;
            case Overcast:
                return R.drawable.wn6;
            case Fog:
                return R.drawable.wn7;
            case LightRainShowers:
                return R.drawable.wn8;
            case ModerateRainShowers:
                return R.drawable.wn9;
            case HeavyRainShowers:
                return R.drawable.wn10;
            case Thunderstorm:
                return R.drawable.wn11;
            case LightSleetShowers:
                return R.drawable.wn12;
            case ModerateSleetShowers:
                return R.drawable.wn13;
            case HeavySleetShowers:
                return R.drawable.wn14;
            case LightSnowShowers:
                return R.drawable.wn15;
            case ModerateSnowShowers:
                return R.drawable.wn16;
            case HeavySnowShowers:
                return R.drawable.wn17;
            case LightRain:
                return R.drawable.wn18;
            case ModerateRain:
                return R.drawable.wn19;
            case HeavyRain:
                return R.drawable.wn20;
            case Thunder:
                return R.drawable.wn21;
            case LightSleet:
                return R.drawable.wn22;
            case ModerateSleet:
                return R.drawable.wn23;
            case HeavySleet:
                return R.drawable.wn24;
            case LightSnowfall:
                return R.drawable.wn25;
            case ModerateSnowfall:
                return R.drawable.wn26;
            case HeavySnowfall:
                return R.drawable.wn27;
        }
        return R.drawable.wn1;
    }

}
