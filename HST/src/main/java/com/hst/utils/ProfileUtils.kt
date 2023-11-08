package com.hst.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.R
import androidx.appcompat.app.AlertDialog
import com.hst.fsp.VideoProfile

object ProfileUtils {
    private val s_profile_list = arrayOf("340x240", "640x480", "1280x720", "1920x1080")
    fun getProfileRecently(width: Int): String {
        if (width <= 320) {
            return s_profile_list[0]
        } else if (width <= 640) {
            return s_profile_list[1]
        } else if (width <= 1280) {
            return s_profile_list[2]
        } else if (width <= 1920) {
            return s_profile_list[3]
        }
        return ""
    }

    fun showProfileDialog(context: Context?, onClickListener: DialogInterface.OnClickListener?) {
        val builder = AlertDialog.Builder(
            context!!, R.style.Theme_AppCompat_Dialog
        )
        builder.setTitle("分辨率")
        builder.setItems(s_profile_list, onClickListener)
        val r_dialog = builder.create()
        r_dialog.show()
    }

    fun getProfileList(position: Int): String {
        return s_profile_list[position]
    }

    fun setProfile(profile: VideoProfile, position: Int): VideoProfile {
        if (position == 0) {
            profile.width = 320
            profile.height = 240
        } else if (position == 1) {
            profile.width = 640
            profile.height = 480
        } else if (position == 2) {
            profile.width = 1280
            profile.height = 720
        } else if (position == 3) {
            profile.width = 1920
            profile.height = 1080
        }
        return profile
    }
}