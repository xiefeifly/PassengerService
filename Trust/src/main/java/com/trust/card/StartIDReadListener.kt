package com.trust.card

import cn.com.aratek.idcard.IDCard
import com.trust.data.CardIDData

interface StartIDReadListener {

    fun detectCard()
    fun Readerror(errorCode: Int, errorMessage: String?)
    fun idCardResult(idCard: CardIDData, physicalID: String)
    fun Enderror(errorCode: Int)
    fun Failed(errorCode: String)
}