package com.passenger.utils

import java.lang.reflect.Method

object ContentUtil {
    fun getDeviceSN(): String? {
        var serial: String? = null
        try {
            val c = Class.forName("android.os.SystemProperties")
            val get: Method = c.getMethod("get", String::class.java)
            serial = get.invoke(c, "ro.serialno") as String
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return serial
    }
}