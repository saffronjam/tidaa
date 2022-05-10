package com.example.weatherthreepointo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    public static String getFormattedDayString(LocalDateTime dateTime) {
        if (dateTime == LocalDateTime.MIN) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        String prefix;
        int dayDiff = Math.abs(dateTime.getDayOfYear() - now.getDayOfYear());
        if (dayDiff == 0) {
            prefix = "";
        } else if (dayDiff == 1) {
            prefix = "Tomorrow ";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault());
            prefix = formatter.format(dateTime) + " ";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        String time = formatter.format(dateTime);

        return prefix + time;
    }


    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (capabilities != null) {
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        }
        return false;
    }
}
