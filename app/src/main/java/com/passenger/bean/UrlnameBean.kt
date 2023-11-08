package com.passenger.bean

import androidx.annotation.Keep

@Keep
data class UrlnameBean(val code: Int, val message: Any?, val data: DataDTO)

@Keep
data class DataDTO(
    val id: String,
    val fileName: String,
    val fileExt: String,
    val path: String,
    val length: Int,
    val uploadTime: String,
    val saveMode: String,
    val fileData: String,
    val extraInfo: Any?,
    val handlerInfo: Any?
)
