package com.example.labb3ctimesl.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.labb3ctimesl.services.AlarmService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String TEXT = "TEXT";

    @Override
    public void onReceive(Context context, Intent intent) {
        startAlarmService(context, intent);
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TEXT, intent.getStringExtra(TEXT));
        context.startForegroundService(intentService);
    }

}
