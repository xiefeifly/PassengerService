package com.passenger.utils

import java.text.SimpleDateFormat
import java.util.*

object InviteUtils {
    fun getMsg(userName: String, name: String): String? {
        val builder = StringBuilder()
        builder.append("~!!~$userName~!!~")
        builder.append(name)
        builder.append("~!!~")
        return builder.toString()
    }

    fun getDate(): String {
        val instance: Calendar = Calendar.getInstance()
        return "${instance.get(Calendar.YEAR)}年${instance.get(Calendar.MONTH)+1}月${
            instance.get(
                Calendar.DAY_OF_MONTH
            )
        }日"
    }

    fun getWeek(): String {
        val instance: Calendar = Calendar.getInstance()
        return when (instance.get(Calendar.DAY_OF_WEEK)) { // 星期 (1:星期日,2:星期一,...)
            1 -> return "星期日"
            2 -> return "星期一"
            3 -> return "星期二"
            4 -> return "星期三"
            5 -> return "星期四"
            6 -> return "星期五"
            7 -> return "星期六"
            else -> {
                "null"
            }
        }
    }
    /**
     * 获取当前时间
     */
    fun getCurrentTime() :String{
//        val current = Date().time
        val formatter = SimpleDateFormat("HH:mm")
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = formatter.format(Date())
        return formatted.toString()
    }
}