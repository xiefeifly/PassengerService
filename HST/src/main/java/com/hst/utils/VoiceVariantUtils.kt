package com.hst.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.R
import androidx.appcompat.app.AlertDialog

object VoiceVariantUtils {
    val s_voice_variant_list = arrayOf("跟随发送端", "只接收原声", "只接收变声")
    private const val voice_variant_follow = 0
    private const val voice_variant_origin_only = 1
    private const val voice_variant_variant_only = 2
    fun getProfileRecently(value: Int): String {
        var value = value
        if (value < 0 || value > 2) value = 0
        return s_voice_variant_list[value]
    }

    fun showProfileDialog(context: Context?, onClickListener: DialogInterface.OnClickListener?) {
        val builder = AlertDialog.Builder(
            context!!, R.style.Theme_AppCompat_Dialog
        )
        builder.setTitle("变调")
        builder.setItems(s_voice_variant_list, onClickListener)
        val r_dialog = builder.create()
        r_dialog.show()
    }

    fun getVoiceModeList(position: Int): String {
        return s_voice_variant_list[position]
    }
}