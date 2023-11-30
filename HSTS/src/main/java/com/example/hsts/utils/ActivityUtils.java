package com.example.hsts.utils;

import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityUtils {

    public static void finishActivity(AppCompatActivity activity, boolean isFinish) {
        if (!isFinish) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isDestroyed()) {
                activity.finish();
            }
        } else {
            activity.finish();
        }
    }
}
