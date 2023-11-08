package com.hst.business

import com.hst.fsp.FspEngine

object FspFailMsgUtils {
    fun getFspEventDesc(errCode: Int): String {
        return when (errCode) {
            FspEngine.FSP_EVENT_CONNECT_LOST -> "重连失败，fsp连接断开"
            FspEngine.FSP_EVENT_RECONNECT_START -> "网络断开过，开始重连"
            else -> ""
        }
    }

    fun getErrorDesc(errCode: Int): String {
        return when (errCode) {
            FspEngine.ERR_INVALID_ARG -> "非法参数"
            FspEngine.ERR_INVALID_STATE -> "非法状态"
            FspEngine.ERR_OUTOF_MEMORY -> "内存不足"
            FspEngine.ERR_DEVICE_FAIL -> "访问设备失败"
            FspEngine.ERR_CONNECT_FAIL -> "网络连接失败"
            FspEngine.ERR_NO_GROUP -> "没加入组"
            FspEngine.ERR_TOKEN_INVALID -> "认证失败"
            FspEngine.ERR_APP_NOT_EXIST -> "应用不存在"
            FspEngine.ERR_USERID_CONFLICT -> "用户重复登录"
            FspEngine.ERR_NOT_LOGIN -> "没有登录"
            FspEngine.ERR_NO_BALANCE -> "账户余额不足"
            FspEngine.ERR_NO_VIDEO_PRIVILEGE -> "没有视频权限"
            FspEngine.ERR_NO_AUDIO_PRIVILEGE -> "没有音频权限"
            FspEngine.ERR_SERVER_ERROR -> "服务内部错误"
            FspEngine.ERR_FAIL -> "操作失败"
            else -> "系统错误"
        }
    }
}