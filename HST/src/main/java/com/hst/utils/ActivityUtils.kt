package com.hst.utils

import androidx.appcompat.app.AppCompatActivity
import android.os.Build

object ActivityUtils {
    fun finishActivity(activity: AppCompatActivity, isFinish: Boolean) {
        if (!isFinish) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isDestroyed) {
                activity.finish()
            }
        } else {
            activity.finish()
        }
    }
}