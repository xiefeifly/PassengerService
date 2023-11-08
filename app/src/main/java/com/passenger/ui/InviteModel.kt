package com.passenger.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hst.business.FspManager
import com.passenger.utils.InviteUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class InviteModel : ViewModel() {
    val TAG = "InviteModel"
    var isSelected = true
    lateinit var timer: Timer
    var liveData = MutableLiveData<Boolean>()
    var dataliveData = MutableLiveData<String>()
    fun microClick() {
        viewModelScope.launch(Dispatchers.Main) {
            if (isSelected) {
                isSelected = false
                FspManager.stopPublishAudio()
                liveData.value = false
            } else {
                isSelected = true
                FspManager.startPublishAudio()
                liveData.value = true
            }
        }
    }

    fun startTimer() {
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                viewModelScope.launch(Dispatchers.Main) {
                    dataliveData.value = InviteUtils.getCurrentTime()
                }
            }
        }
        timer.schedule(timerTask, 1000, 1000)
    }
}