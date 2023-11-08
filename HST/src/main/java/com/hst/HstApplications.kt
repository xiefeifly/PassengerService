package com.hst

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startForegroundService
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.lang.System.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

 @SuppressLint("CustomX509TrustManager")
 object HstApplications : X509TrustManager {
    init {
        loadLibrary("fsp_sdk")
        //下面两个so 依赖 JNI_OnLoad, 所以需要load调JNI_OnLoad
        loadLibrary("avdevice")
        loadLibrary("vncmp")
        loadLibrary("framecore")
    }

    private var trustManagers: Array<TrustManager>? = null
//    private val _AcceptedIssuers = arrayOf<X509Certificate>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(contexts: Context) {

        Logger.addLogAdapter(AndroidLogAdapter())
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ ->
            true
        }
        var context: SSLContext? = null
        if (trustManagers == null) {
            trustManagers = arrayOf(this)
        }
        try {
            context = SSLContext.getInstance("SSL")
            context.init(null, trustManagers, SecureRandom())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(context!!.socketFactory)
    }

    @SuppressLint("TrustAllX509TrustManager")
    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    @SuppressLint("TrustAllX509TrustManager")
    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }
}