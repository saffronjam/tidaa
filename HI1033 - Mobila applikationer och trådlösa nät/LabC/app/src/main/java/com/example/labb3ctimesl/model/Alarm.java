package com.example.labb3ctimesl.model;

import static com.example.labb3ctimesl.receivers.AlarmBroadcastReceiver.TEXT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.labb3ctimesl.receivers.AlarmBroadcastReceiver;
import com.example.labb3ctimesl.services.AlarmService;

import java.time.LocalDateTime;
import java.util.Calendar;

public class Alarm {

    private static PendingIntent alarmPendingIntent;

    public static void schedule(Context context, int alarmId, int hour, int minute, String text) {

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(TEXT, text);

        if (alarmPendingIntent != null) {
            cancel(context);
        }
        alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, LocalDateTime.now().getHour());
        calendar.set(Calendar.MINUTE, LocalDateTime.now().getMinute());
        calendar.set(Calendar.SECOND, LocalDateTime.now().getSecond() + 5);
        calendar.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );
    }

    public static void cancel(Context context) {
        if (alarmPendingIntent == null) {
            return;
        }

        Intent intentService = new Intent(context, AlarmService.class);
        context.stopService(intentService);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmPendingIntent);
    }
}