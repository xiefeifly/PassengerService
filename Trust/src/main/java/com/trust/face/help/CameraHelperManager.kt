package com.trust.face.help

import android.content.Context
import android.util.Log
import com.trust.face.help.CameraHelperManager

/**
 * 用来管理 Camera1OpenHelper 和 Camera2OpenHelper 的类，
 * 根据当前设备的摄像头和系统规格，自动切换使用的摄像头框架版本
 */
object CameraHelperManager {
    private const val TAG = "CameraHelperManager"
    fun initCameraHelper(context: Context?): CameraOpenInterface {
        //默认情况按系统配置选择
        return if (Camera2OpenHelper.isSupportCamera2(context, "0")) {
            Log.e(TAG, "use Camera2 api")
            Camera2OpenHelper(context)
        } else {
            Log.e(TAG, "use Camera1 api")
            Camera1OpenHelper(context)
        }
//        return  Camera2OpenHelper(context)
    }
}