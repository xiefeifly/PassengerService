//package com.hst
//
//import android.app.Application
//import android.content.Context
//import android.content.res.Configuration
//import com.orhanobut.logger.AndroidLogAdapter
//import com.orhanobut.logger.Logger
//import java.security.KeyManagementException
//import java.security.NoSuchAlgorithmException
//import java.security.SecureRandom
//import java.security.cert.CertificateException
//import java.security.cert.X509Certificate
//import javax.net.ssl.HttpsURLConnection
//import javax.net.ssl.SSLContext
//import javax.net.ssl.TrustManager
//import javax.net.ssl.X509TrustManager
//
//open class HstApplication : Application(), X509TrustManager {
//    companion object {
//        init {
//            System.loadLibrary("fsp_sdk")
//            //下面两个so 依赖 JNI_OnLoad, 所以需要load调JNI_OnLoad
//            System.loadLibrary("avdevice")
//            System.loadLibrary("vncmp")
//            System.loadLibrary("framecore")
//        }
//
//        private var trustManagers: Array<TrustManager>? = null
//        private val _AcceptedIssuers = arrayOf<X509Certificate>()
//        var instance: HstApplication? = null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//        Logger.addLogAdapter(AndroidLogAdapter())
//        HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 ->
//            true
//        }
//        var context: SSLContext? = null
//        if (trustManagers == null) {
//            trustManagers = arrayOf(this)
//        }
//        try {
//            context = SSLContext.getInstance("SSL")
//            context.init(null, trustManagers, SecureRandom())
//        } catch (e: NoSuchAlgorithmException) {
//            e.printStackTrace()
//        } catch (e: KeyManagementException) {
//            e.printStackTrace()
//        }
//        HttpsURLConnection.setDefaultSSLSocketFactory(context!!.socketFactory)
//    }
//
//    @Throws(CertificateException::class)
//    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
//    }
//
//    @Throws(CertificateException::class)
//    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
//    }
//
//    override fun getAcceptedIssuers(): Array<X509Certificate?> {
//        return arrayOfNulls(0)
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//    }
//
//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(base)
//    }
//}