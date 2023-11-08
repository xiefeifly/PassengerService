package com.trust.face

import android.content.Context
import cn.com.aratek.trustface.sdk.FaceEngine

object FaceEngineManager {
    fun FaceEngineActiveOnline(context: Context, ACCOUNT: String, PASSWORD: String): Int =
        FaceEngine.activeOnline(
            context,
            ACCOUNT,
            PASSWORD
        )

}