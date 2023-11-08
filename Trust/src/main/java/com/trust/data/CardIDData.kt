package com.trust.data

import android.graphics.Bitmap

data class CardIDData(
    val mType: String?,
    val mName: String?,
    val mEngName: String?,
    val mSex: String?,
    val mNationality: String?,
    val mCountry: String?,
    val mBirthday: String?,
    val mAddress: String?,
    val mNumber: String?,
    val mAuthority: String?,
    val mAuthorityCode: String?,
    val mValidFrom: String?,
    val mValidTo: String?,
    val mLatestAddress: String?,
    val mPassportNumber: String?,
    val mNumberOfIssuances: Int?,
    val mPhoto: Bitmap?,
//    mFingerprint: Array<>()
)
