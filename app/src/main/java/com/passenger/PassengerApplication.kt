package com.passenger

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.hst.HstApplications
//import com.passenger.widget.MeetingDemoService

class PassengerApplication :Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        HstApplications.init(this)
    }
}