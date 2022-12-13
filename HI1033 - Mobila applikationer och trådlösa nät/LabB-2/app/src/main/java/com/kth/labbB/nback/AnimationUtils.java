package com.kth.labbB.nback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class AnimationUtils {

    public static void fadeInImageView(View viewToFadeIn) {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        alphaAnimator.setDuration(500);
        alphaAnimator.setInterpolator(new LinearInterpolator());

        alphaAnimator.addUpdateListener(valueAnimator -> {
            float newAlpha = (float) valueAnimator.getAnimatedValue();
            viewToFadeIn.setAlpha(newAlpha);
        });
        alphaAnimator.start();
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                ValueAnimator alphaAnimator = ValueAnimator.ofFloat(1.0F, 0.0F);
                alphaAnimator.setDuration(500);
                alphaAnimator.setInterpolator(new LinearInterpolator());

                alphaAnimator.addUpdateListener(valueAnimator -> {
                    float newAlpha = (float) valueAnimator.getAnimatedValue();
                    viewToFadeIn.setAlpha( newAlpha);
                });
                alphaAnimator.start();

            }
        });
    }

    public static void fadeInColorSuccess(View viewToFadeIn) {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
        alphaAnimator.setDuration(1000);
        alphaAnimator.setInterpolator(new LinearInterpolator());

        alphaAnimator.addUpdateListener(valueAnimator -> {
            float newAlpha = (float) valueAnimator.getAnimatedValue();
            viewToFadeIn.setAlpha(newAlpha);
        });
        alphaAnimator.start();
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewToFadeIn.setAlpha(0.0f);
            }
        });
    }

    private static int fetchAccentColor(int id, Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { id });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static void fadeFromFailColor(View view){
        ObjectAnimator colorFade = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), Color.argb(255,125, 0, 0), fetchAccentColor(R.attr.colorOnPrimary,view.getContext()));
            colorFade.setDuration(500);
            colorFade.start();
        }
    }

    public static void fadeFromSuccessfulColor(View view){
        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), Color.argb(255,0, 125, 0), fetchAccentColor(R.attr.colorOnPrimary,view.getContext()));
        colorFade.setDuration(500);
        colorFade.start();
    }
}
