package com.example.labb3ctimesl;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static int getColor(int id, Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{id});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static void fadeInImageView(View viewToFadeIn) {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        alphaAnimator.setDuration(2000);
        alphaAnimator.setInterpolator(new AccelerateInterpolator());

        alphaAnimator.addUpdateListener(valueAnimator -> {
            float newAlpha = (float) valueAnimator.getAnimatedValue();
            viewToFadeIn.setAlpha(newAlpha);
        });
        alphaAnimator.start();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }



    public static boolean isSameDay(Date date1, Date date2) {
        if(date1 == null || date2 == null)
            return false;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
