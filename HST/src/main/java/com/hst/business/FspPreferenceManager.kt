package com.hst.business

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.hst.utils.FspUtils
import com.hst.utils.VoiceVariantUtils

@SuppressLint("StaticFieldLeak")
object FspPreferenceManager {
    private lateinit var m_editor: SharedPreferences.Editor
    private lateinit var m_sharedPreferences: SharedPreferences

    fun init(context: Context) {
        m_sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        m_editor = m_sharedPreferences.edit()
    }

    val appConfig: Boolean
        get() = m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_APPCONFIG, true)
    val appId: String
        get() = m_sharedPreferences.getString(FspConstants.PKEY_USER_APPID, "")!!
    val appSecret: String
        get() = m_sharedPreferences.getString(FspConstants.PKEY_USER_APPSECRET, "")!!
    val appServerAddr: String
        get() = m_sharedPreferences.getString(FspConstants.PKEY_USER_APPSERVERADDR, "")!!
    val defaultOpenCamera: Boolean
        get() = m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_OPENCAMERA, false)
    val defaultOpenMIC: Boolean
        get() = m_sharedPreferences.getBoolean(FspConstants.PKEY_USE_DEFAULT_OPENMIC, false)
    val isForceLogin: Boolean
        get() = m_sharedPreferences.getBoolean(FspConstants.PKEY_IS_FORCELOGIN, true)
    val isRecvVoiceVariant: Int
        get() {
            val strTmp: String = m_sharedPreferences.getString(
                FspConstants.PKEY_IS_RECVVOICEVARIANT,
                VoiceVariantUtils.s_voice_variant_list.get(0)
            )!!
            return if (FspUtils.isSameText(
                    strTmp,
                    VoiceVariantUtils.s_voice_variant_list.get(1)
                )
            ) 1 else if (FspUtils.isSameText(
                    strTmp,
                    VoiceVariantUtils.s_voice_variant_list.get(2)
                )
            ) 2 else 0
        }

    fun setAppConfig(config: Boolean): FspPreferenceManager {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_APPCONFIG, config)
        return this
    }

    fun setAppId(appId: String?): FspPreferenceManager {
        m_editor.putString(FspConstants.PKEY_USER_APPID, appId)
        return this
    }

    fun setAppSecret(appId: String?): FspPreferenceManager {
        m_editor.putString(FspConstants.PKEY_USER_APPSECRET, appId)
        return this
    }

    fun setAppServerAddr(appServerAddr: String?): FspPreferenceManager {
        m_editor.putString(FspConstants.PKEY_USER_APPSERVERADDR, appServerAddr)
        return this
    }

    fun setDefaultOpenCamera(open: Boolean): FspPreferenceManager {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_OPENCAMERA, open)
        return this
    }

    fun setDefaultOpenMIC(open: Boolean): FspPreferenceManager {
        m_editor.putBoolean(FspConstants.PKEY_USE_DEFAULT_OPENMIC, open)
        return this
    }

    fun setForceLogin(isForceLogin: Boolean): FspPreferenceManager {
        m_editor.putBoolean(FspConstants.PKEY_IS_FORCELOGIN, isForceLogin)
        return this
    }

    fun setRecvVoiceVariant(RecvVoiceVariant: String?): FspPreferenceManager {
        m_editor.putString(FspConstants.PKEY_IS_RECVVOICEVARIANT, RecvVoiceVariant)
        return this
    }

    fun apply() {
        m_editor.apply()
    }

    fun commit() {
        m_editor.commit()
    }


}