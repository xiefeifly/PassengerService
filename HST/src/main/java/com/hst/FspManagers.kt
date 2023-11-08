package com.hst

import android.content.Context
import com.hst.business.FspManager

object FspManagers {
    fun init(
        context: Context,
        appId: String,
        appSecret: String,
        serverAddr: String
    ) {
        FspManager.init(context, appId, appSecret, serverAddr)
    }
    fun login(
        id: String,
        userName: String
    ) {
        FspManager.login(id,userName)
    }
}