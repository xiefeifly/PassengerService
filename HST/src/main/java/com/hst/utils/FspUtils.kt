package com.hst.utils

import java.text.DecimalFormat

object FspUtils {
    fun isSameText(str1: String?, str2: String?): Boolean {
        if (str1 == null && str2 == null) {
            return true
        } else if (str1 == null || str2 == null) {
            return false
        }
        return str1 == str2
    }

    fun isEmptyText(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    const val sKB: Long = 1024
    const val sMB = sKB * sKB
    const val sGB = sMB * sKB
    private const val sUnitB = "B"
    private const val sUnitKB = "K"
    private const val sUnitMB = "M"
    private const val sUnitGB = "G"
    private val sFormat = DecimalFormat("#.0")

    /**
     * 字节大小转换成 K M之类可读大小
     * @param byteSize byte
     * @return human size
     */
    fun convertBytes2HumanSize(byteSize: Long): String {
        val sSize: String
        sSize = if (byteSize > sGB) {
            sFormat.format((byteSize.toFloat() / sGB).toDouble()) + sUnitGB
        } else if (byteSize > sMB) {
            sFormat.format((byteSize.toFloat() / sMB).toDouble()) + sUnitMB
        } else if (byteSize > sKB) {
            sFormat.format((byteSize.toFloat() / sKB).toDouble()) + sUnitKB
        } else {
            byteSize.toString() + sUnitB
        }
        return sSize
    }
}