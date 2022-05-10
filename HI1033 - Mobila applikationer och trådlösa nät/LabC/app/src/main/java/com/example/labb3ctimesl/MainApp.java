package com.example.labb3ctimesl;

import static com.example.labb3ctimesl.activites.MainActivity.CHANNEL_ID;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setupNotificationChannel();
    }

    private void setupNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}