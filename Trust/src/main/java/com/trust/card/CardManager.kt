package com.trust.card

import android.content.Context
import android.util.Log
import cn.com.aratek.idcard.IDCard
import com.kaer.sdk.utils.LogUtils
import com.trust.data.CardIDData
import com.trustpass.api.sdk.bean.CpuCardResult
import com.trustpass.api.sdk.common.ErrorCode
import com.trustpass.api.sdk.enums.CardType
import com.trustpass.api.sdk.helper.ARADeviceHelper
import com.trustpass.api.sdk.inter.ICard
import com.trustpass.api.sdk.listener.CardResultListener
import java.text.SimpleDateFormat
import java.util.*

object CardManager {
    lateinit var mCardHelper: ICard
    fun initCardHelper(context: Context) {
        mCardHelper = ARADeviceHelper.getInstance(context).cardHelper
    }


    fun openDevice(): String {
        val error: Int = mCardHelper.openDevice(CardType.ID_CARD)
        if (error == ErrorCode.RESULT_OK || error == ErrorCode.DEVICE_REOPEN) {
            return mCardHelper.sn
//            runOnUiThread {
//                mViewBind.tvModuleSerial.setText(
//                    getString(R.string.idcard_module_version)
//                            + (sn ?: getString(R.string.idcard_value_none))
//                )
//                mViewBind.btnRead.setEnabled(true)
//                if (DeviceUtil.isP80ev()) {
//                    mViewBind.btnLight.setEnabled(true)
//                }
//            }
        } else {
            return mCardHelper.getIDCardErrorString(error)
//            showResultMessage(
//                getString(R.string.idcard_open_failed) + mCardHelper.getIDCardErrorString(
//                    error
//                ), false
//            )
        }
    }

    fun closeDevice() {
        mCardHelper.closeDevice(CardType.ID_CARD)
    }

    fun startIDRead(startIDReadListener: StartIDReadListener) {
        var error = mCardHelper.startIDRead(200, object : CardResultListener {
            override fun detectCard() {
                startIDReadListener.detectCard()
            }

            override fun error(errorCode: Int, errorMessage: String?) {
                startIDReadListener.Readerror(errorCode, errorMessage)
            }

            override fun mifareCardResult(p0: String?) {
            }

            override fun cpuCardResult(p0: CpuCardResult?) {
            }

            override fun idCardResult(idCard: IDCard, physicalID: String) {
                LogUtils.d("idCardResult：" + idCard.name)
                LogUtils.d("idCardResult：" + idCard.sex)
                LogUtils.d("idCardResult：" + idCard.address)
                Log.e("TAG", "idCardResult: $idCard.address")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
                var data = CardIDData(
                    idCard.type.toString(),
                    idCard.name,
                    idCard.englishName,
                    idCard.sex.toString(),
                    idCard.nationality?.toString(),
                    idCard.country?.toString(),
                    dateFormat.format(idCard.birthday),
                    idCard.address,
                    idCard.number,
                    idCard.authority,
                    idCard.authorityCode,
                    dateFormat.format(idCard.validFrom),
                    dateFormat.format(idCard.validTo),
                    idCard.latestAddress?.toString(),
                    idCard.passportNumber,
                    idCard.numberOfIssuances,
                    idCard.photo
                )
                startIDReadListener.idCardResult(data, physicalID)
            }
        })
        if (error == ErrorCode.RESULT_OK) {
            startIDReadListener.Enderror(error)
//            showResultMessage(getString(R.string.idcard_is_reading), true)
//            isStartRead = true
//            mViewBind.etInterval.setEnabled(false)
//            mViewBind.btnRead.setText(R.string.idcard_stop_read)
//            mViewBind.btnRead.setBackgroundResource(R.drawable.selector_stop_button_bg)
        } else {
            startIDReadListener.Failed(mCardHelper.getIDCardErrorString(error))
//            showResultMessage(
//                getString(R.string.idcard_read_failed) + mCardHelper.getIDCardErrorString(
//                    kotlin.error
//                ), false
//            )
        }
    }
}
