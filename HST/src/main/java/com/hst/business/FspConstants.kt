package com.hst.business

import com.hst.fsp.VideoProfile

object FspConstants {
    const val LOCAL_VIDEO_CLOSED = 0 ///<本地视频关闭状态
    const val LOCAL_VIDEO_BACK_PUBLISHED = 1 ///<广播了前置摄像头
    const val LOCAL_VIDEO_FRONT_PUBLISHED = 2 ///<广播了后置摄像头
    const val PKEY_USER_APPID = "userAppId"
    const val PKEY_USER_APPSECRET = "userAppSecret"
    const val PKEY_USER_APPSERVERADDR = "userServerAddr"
    const val PKEY_USE_DEFAULT_APPCONFIG = "useDefaultAppConfig"
    const val PKEY_USE_DEFAULT_OPENCAMERA = "useDefaultOpenCamera"
    const val PKEY_USE_DEFAULT_OPENMIC = "useDefaultOpenMic"
    const val PKEY_IS_FORCELOGIN = "isForceLogin"
    const val PKEY_IS_RECVVOICEVARIANT = "isRecvVoiceVariant"

    // 为安全起见，App Secret最好不要在客户端保存
    /*public static final String DEFAULT_APP_ID = "925aa51ebf829d49fc98b2fca5d963bc";
    public static final String DEFAULT_APP_SECRET = "d52be60bb810d17e";
    public static final String DEFAULT_APP_ADDRESS = "";*/
    //    public static final String DEFAULT_APP_ID = "3049291591cb6aed78e638c2aed53867";
    //    public static final String DEFAULT_APP_SECRET = "16753469423db000";
    //    public static final String DEFAULT_APP_ADDRESS = "http://10.243.1.15:20020/server/address";
    const val DEFAULT_APP_ID = "413b57c27a5c13cf9a8357f5f310154b"
    const val DEFAULT_APP_SECRET = "9823baafc938aa00"
    const val DEFAULT_APP_ADDRESS = "https://gjxsk.jyd.com.cn:21100/server/address"
    val DEFAULT_PROFILE = VideoProfile(640, 480, 15)
}