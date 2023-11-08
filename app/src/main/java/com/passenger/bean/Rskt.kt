package com.passenger.bean

import androidx.annotation.Keep

@Keep
data class Rskt(val Rskts: RsktItem)

@Keep
data class RsktItem(
    val AreaId: String?,
    val AreaName: String?,
    val AuditStatus: String?,
    val AuditTime: String?,
    val CreateTime: String?,
    val DeviceCode: String?,
    val DeviceId: String?,
    val DeviceName: String?,
    val Enable: String?,
    val Failuretime: String?,
    val ID: String?,
    val Iseffective: String?,
    val LineAreaId: String?,
    val LineName: String?,
    val Passenger: Passengers?,
    val PassengerAttachments: List<PassengerAttachments?>?,
    val PassengerID: String?,
    val RegisterTypeId: String?,
    val Registertime: String?,
    val Source: String?,
    val StationAreaId: String?,
    val StationName: String?,
    val Timeoftakingeffect: String?
)


@Keep
data class PassengerAttachments(
    val FileFormat: String?,
    val FileState: String?,
    val FileType: String?,
    val ID: String?,
    val Name: String?,
    val PassengerId: String?,
    val SubType: String?,
    val Url: String?
)

@Keep
data class Passengers(
    val CustomCode: String?,
    val DisplayName: String?,
    val ID: String?,
    val IDcard: String?,
    val LastLoginTime: String?,
    val Locked: Int?,
    val LoginCount: String?,
    val Name: String?,
    val Nickname: String?,
    val PhoneNumber: String?,
    val Sex: String?
)