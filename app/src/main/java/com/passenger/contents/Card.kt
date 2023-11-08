package com.passenger.contents

import android.content.Context
import android.os.Environment
import android.util.Log
import com.trust.face.help.FaceHelper
import com.trustpass.api.sdk.enums.CardType
import com.trustpass.api.sdk.inter.ICard

var TAG = "Card"
fun ICard.openDevice(): Int {
    return openDevice(CardType.ID_CARD)
}

fun FaceHelper.init(context: Context): Int {
//    var ret: Int = FaceHelper.activeOnline(context)
    var path = "${Environment.getExternalStorageDirectory()}/TrustFaceDemo/License.lic"
    Log.e(TAG, "人脸引擎初始化成功path $path")
    return activeOffline(context, path)
}