package com.hst.utils

import android.media.AudioManager
import android.annotation.SuppressLint
import android.content.Context
import java.lang.Exception

object AudioManagerUtils {
    var audioManager: AudioManager? = null
    private var currVolume = 0
    fun init(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 打开扬声器
     */
    @SuppressLint("WrongConstant")
    fun openSpeaker() {
        try {
            audioManager!!.mode = AudioManager.ROUTE_SPEAKER
            currVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
            if (!audioManager!!.isSpeakerphoneOn) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager!!.mode = AudioManager.MODE_IN_CALL
                audioManager!!.isSpeakerphoneOn = true
                audioManager!!.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    audioManager!!.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭扬声器
     */
    fun closeSpeaker() {
        try {
            if (audioManager != null) {
                if (audioManager!!.isSpeakerphoneOn) {
                    audioManager!!.isSpeakerphoneOn = false
                    audioManager!!.setStreamVolume(
                        AudioManager.STREAM_VOICE_CALL, currVolume,
                        AudioManager.STREAM_VOICE_CALL
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setbigStreamVolume() {
        audioManager!!.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    fun setSmalStreamVolume() {
        audioManager!!.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 12,
            AudioManager.FLAG_PLAY_SOUND
        )
    }

//    companion object {
//        private var mAudioManager: AudioManagerUtils? = null
//        val instance: AudioManagerUtils?
//            get() {
//                if (mAudioManager == null) {
//                    synchronized(AudioManagerUtils::class.java) {
//                        mAudioManager = AudioManagerUtils()
//                    }
//                }
//                return mAudioManager
//            }
//    }
}