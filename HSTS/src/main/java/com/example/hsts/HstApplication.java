package com.example.hsts;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HstApplication implements X509TrustManager {
    static {
        System.loadLibrary("fsp_sdk");
        //下面两个so 依赖 JNI_OnLoad, 所以需要load调JNI_OnLoad
        System.loadLibrary("avdevice");
        System.loadLibrary("vncmp");
        System.loadLibrary("framecore");
    }

    private static TrustManager[] trustManagers;
    private static final X509Certificate[] _AcceptedIssuers = new
            X509Certificate[]{};
    private static HstApplication s_instance = null;

    public static HstApplication getInstance() {
        if (s_instance == null) {
            synchronized (HstApplication.class) {
                if (s_instance == null) {
                    s_instance = new HstApplication();
                }
            }
        }
        return s_instance;
    }

    public void init() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                // TODO Auto-generated method stub
                return true;
            }

        });

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{this};
        }

        try {
            context = SSLContext.getInstance("SSL");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());


    }


    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
