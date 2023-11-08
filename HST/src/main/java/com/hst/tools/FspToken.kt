package com.hst.tools

import android.util.Base64
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class FspToken {
    private var m_appid: String? = null
    private var m_secretkey: String? = null
    private var m_userid: String? = null
    private var m_expiretime: Long = 0
    private val m_version = "001"
    fun setAppId(appid: String?) {
        m_appid = appid
    }

    fun setSecretKey(secretKey: String?) {
        m_secretkey = secretKey
    }

    fun setUserId(userid: String?) {
        m_userid = userid
    }

    fun setExpireTime(expireTime: Long) {
        m_expiretime = expireTime
    }

    fun build(): String {
        if (m_secretkey!!.length != 16) {
            return ""
        }
        val rawJson = generateJsonRaw()
        val encodedContent = encode(rawJson)
        return m_version + encodedContent
    }

    private fun generateJsonRaw(): String {
        //simple string build, you can use your self json library
        val jsonString = StringBuilder("{")
        jsonString.append("\"aid\":\"").append(m_appid).append("\",")
        jsonString.append("\"uid\":\"").append(m_userid).append("\",")
        if (m_expiretime != 0L) {
            jsonString.append("\"et\":").append(m_expiretime).append(",")
        }
        jsonString.append("\"ts\":").append(System.currentTimeMillis()).append(",")
        val r = Random()
        jsonString.append("\"r\":").append(Math.abs(r.nextInt()))
        jsonString.append("}")
        return jsonString.toString()
    }

    private fun encode(jsonContent: String): String {
        var jsonContent = jsonContent
        try {
            val byteIv = byteArrayOf(
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
            )
            val iv = IvParameterSpec(byteIv)
            val skeySpec = SecretKeySpec(
                m_secretkey!!.toByteArray(charset("UTF-8")), "AES"
            )
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            while (jsonContent.toByteArray().size % 16 != 0) {
                jsonContent = "$jsonContent "
            }
            val encrypted = cipher.doFinal(jsonContent.toByteArray())

            //if in server side, use java.util.Base64
            return Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    companion object {
        fun build(appId: String?, appSecrectKey: String?, userId: String?): String {
            //生成token的代码应该在服务器， demo中直接生成token不是 正确的做法
            val token = FspToken()
            token.setAppId(appId)
            token.setSecretKey(appSecrectKey)
            token.setUserId(userId)
            return token.build()
        }
    }
}