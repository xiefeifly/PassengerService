package com.passenger.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.com.aratek.idcard.IDCard
import com.passenger.api.toLiveData
import com.passenger.bean.DataItems
import com.passenger.bean.Passenger
import com.passenger.bean.PassengerAttachment
import com.passenger.contents.Config
import com.passenger.utils.InviteUtils
import com.passenger.utils.ToastUtil
import com.trust.face.utils.BitmapUtil
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import updata.api.KtRetrofit
import updata.api.KtService
import java.io.File
import java.util.*

class FaceIdMoudel : ViewModel() {
    private var TAG = "CardIdModel"
    var job: CoroutineScope = MainScope()
    var mCardIdActivity: FaceIdActivity? = null
    var mLifecycleOwner: LifecycleOwner? = null
    var dataList = ObservableArrayList<DataItems>()
    var mPassengerAttachmentList: MutableList<PassengerAttachment> = mutableListOf()
    var mPassenger: Passenger? = null
    var mIdCard: IDCard? = null
    var mRegisterType: String? = null
    private lateinit var mContext: Context
    val etLiveData: MutableLiveData<String> = MutableLiveData()
    var dataliveData = MutableLiveData<String>()
    lateinit var timer: Timer

    fun setdata(activity: FaceIdActivity, context: Context, lifecycleOwner: LifecycleOwner) {
        mCardIdActivity = activity
        mLifecycleOwner = lifecycleOwner
        mContext = context
    }

    fun setRegisterType(registerType: String) {
        mRegisterType = registerType
    }

    fun startTimer() {
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                viewModelScope.launch(Dispatchers.Main) {
                    dataliveData.value = InviteUtils.getCurrentTime()
                }
            }
        }
        timer.schedule(timerTask, 1000, 1000)
    }

    fun faceIdSubmit() {
        if (TextUtils.isEmpty(etLiveData.value)) {
            ToastUtil.showBottom(mContext, "电话不可为空")
            return
        }
        Log.e(TAG, "faceIdSubmit: ${etLiveData.value}=====================")
        mIdCard?.let {
            setPassenger(it, etLiveData.value.toString())
        }
        dataList.add(
            DataItems(
                "Unaudited",
                "jydai136",
//                    sn.value,
                mPassenger,
                mPassengerAttachmentList,
//                mRegisterType,
                "3fa85f64-5717-4562-b3fc-2c963f66afa9",
                "WorkstationComputer"
            )
        )

        val toLiveData = KtRetrofit.initConfig(Config.ApiURL).getService(KtService::class.java)
            .getSideDoorRegists(dataList).toLiveData()
        toLiveData.observe(mLifecycleOwner!!) { value ->
            Log.e(TAG, "submit: $value")
            if (value == true) {
                mCardIdActivity?.let { it.finish() }
            }
        }
    }

    fun UpdataBitmips(bitmap: Bitmap, idCard: IDCard) {
        mIdCard = idCard
        val deepCopyBitmap = BitmapUtil.deepCopyBitmap(bitmap)
        if (!idCard.number.toString().contains("860")) {
            setIDCardBitmap(deepCopyBitmap, "IDCardHead")
        }
    }

    fun setPassenger(idCard: IDCard, phone: String) {
        val name = idCard.sex.toString()

        var sex = if (name == "男") {
            "Man"
        } else {
            "Women"
        }
        if (idCard.number.toString().contains("860")) {
            mPassenger =
                Passenger(
                    "Cd01",
                    "王涛",
                    "422302198801239876",
                    "王涛",
                    "王涛",
                    phone,
                    sex
                )
        } else {
            mPassenger =
                Passenger(
                    "Cd01",
                    idCard.name,
                    idCard.number,
                    idCard.name,
                    idCard.name,
                    phone,
                    sex
                )
        }
    }

    fun setIDCardBitmap(IdCardBitmap: Bitmap, name: String) {
        job.launch(Dispatchers.IO) {
            var path = "${Environment.getExternalStorageDirectory()}/TrustFaceDemo"
            BitmapUtil.saveBitmap2File(IdCardBitmap, path, name)
            var paths = "$path/$name.png"
            Log.e(TAG, "setIDCardBitmap: ======$paths")
            var file = File(paths)

            val requestBody: RequestBody = RequestBody.create("image/png".toMediaType(), file)
            val liveCoverImage: MultipartBody.Part =
                MultipartBody.Part.createFormData(name, file.path, requestBody)
            val retrofitCall = KtRetrofit.initConfig(Config.ApiUploadURL)
                .getService(KtService::class.java)
                .geturl(liveCoverImage)
            val toLiveData = retrofitCall.toLiveData()
            withContext(Dispatchers.Main) {
                toLiveData.observe(mLifecycleOwner!!) {
                    Log.e(TAG, "setIDCardBitmap: $it")
                    mPassengerAttachmentList.add(
                        PassengerAttachment(
                            1,
                            1,
                            2,
                            2,
                            name,
                            it?.data?.path
                        )
                    )
                }
            }

        }
    }
}