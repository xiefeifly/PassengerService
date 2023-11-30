package com.passenger.web

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.passenger.ui.InviteIncomeActivity
import com.passenger.MainActivity
import com.passenger.ui.CardIdActivity
import com.passenger.ui.FaceIdActivity

class JsJavaBridge(val mainActivity: MainActivity, webview: WebView) {
    val TAG = "JsJavaBridge"

    @SuppressLint("SuspiciousIndentation")
    @JavascriptInterface
    fun callVideo(type: Int) {
        Log.e(TAG, "lon: callVideo$type")
        if (type == 0) {
            val intent = Intent(mainActivity, InviteIncomeActivity::class.java)
            intent.putExtra("Incometype", 0)
            intent.putExtra("IM_INFO", 0)
            mainActivity.startActivity(intent)
        }
    }


    @JavascriptInterface
    fun callVideos(type: String) {
        if (type == "0")
            Log.e(TAG, "lon: callVideos$type")
    }

    @JavascriptInterface
    fun callAudio(type: Int) {
        Log.e(TAG, "lon: callAudio$type")
        if (type == 1) {
            val intent = Intent(mainActivity, InviteIncomeActivity::class.java)
            intent.putExtra("Incometype", 1)
            intent.putExtra("IM_INFO", 1)
            mainActivity.startActivity(intent)
        }
    }

    @JavascriptInterface
    fun openActivityCard(type: String) {
        Log.e(TAG, "lon: callAudio$type")
        val intent = Intent(mainActivity, CardIdActivity::class.java)
        intent.putExtra("RegisterType", type)
        mainActivity.startActivity(intent)
    }

    @JavascriptInterface
    fun openActivityFaceId(type: String) {
        Log.e(TAG, "lon: callAudio$type")
        val intent = Intent(mainActivity, FaceIdActivity::class.java)
        intent.putExtra("RegisterType", type)
        mainActivity.startActivity(intent)
    }
}
