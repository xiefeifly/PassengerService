package com.passenger.bean

import androidx.annotation.Keep

@Keep
data class Data(
    val data: ArrayList<DataItem>
)

@Keep
data class DataItem(
    val AuditStatus: String?,
    val DeviceCode: String?,
    val Timeoftakingeffect: String?,
    val Passenger: Passenger?,
    val PassengerAttachments: List<PassengerAttachment?>?,
    val RegisterTypeId: String?,
    val Source: String?,
    val Failuretime: String?
)
@Keep
data class DataItems(
    val AuditStatus: String?,
    val DeviceCode: String?,
    val Passenger: Passenger?,
    val PassengerAttachments: List<PassengerAttachment?>?,
    val RegisterTypeId: String?,
    val Source: String?,
)

@Keep
data class Passenger(
    val CustomCode: String?,
    val DisplayName: String?,
    val IDcard: String?,
    val Name: String?,
    val Nickname: String?,
    val PhoneNumber: String?,
    val Sex: String?
)

@Keep
data class PassengerAttachment(
    val SubType: Int?,
    val FileFormat: Int?,
    val FileState: Int?,
    val FileType: Int?,
    val Name: String?,
    val Url: String?
)