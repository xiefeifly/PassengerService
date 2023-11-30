package com.example.hsts.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;

public class AudioManagerUtils {
    private static AudioManagerUtils mAudioManager = null;
    AudioManager audioManager;
    private int currVolume = 0;

    public static AudioManagerUtils getInstance() {
        if (mAudioManager == null) {
            synchronized (AudioManagerUtils.class) {
                mAudioManager = new AudioManagerUtils();
            }
        }
        return mAudioManager;
    }

    public void init(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }


    /**
     * 打开扬声器
     */
    @SuppressLint("WrongConstant")
    public void openSpeaker() {
        try {
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setbigStreamVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
    }

    public void setSmalStreamVolume() {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 12, AudioManager.FLAG_PLAY_SOUND);
    }
}
