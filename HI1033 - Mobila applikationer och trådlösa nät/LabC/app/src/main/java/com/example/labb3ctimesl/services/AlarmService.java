package com.example.labb3ctimesl.services;

import static com.example.labb3ctimesl.activites.MainActivity.CHANNEL_ID;
import static com.example.labb3ctimesl.receivers.AlarmBroadcastReceiver.TEXT;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.activites.RingActivity;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String notificationText = intent.getStringExtra(TEXT);
        if (notificationText == null) {
            notificationText = "Time to wake up!";
        }

        Intent notificationIntent = new Intent(this, RingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("Time to wake up!")
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{1000})
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        mediaPlayer.start();

        long[] pattern = {0, 100, 1000};
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));

        int id = (int) System.currentTimeMillis() % 10000;
        startForeground(id, notification);

        Intent activityIntent = new Intent(this, RingActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(activityIntent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
