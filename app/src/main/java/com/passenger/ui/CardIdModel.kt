package com.passenger.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.com.aratek.idcard.IDCard
import cn.com.aratek.trustface.sdk.ErrorInfo
import com.kaer.sdk.utils.LogUtils
import com.lvfq.pickerview.TimePickerView
import com.passenger.R
import com.passenger.api.toLiveData
import com.passenger.bean.*
import com.passenger.contents.Config
import com.passenger.contents.init
import com.passenger.contents.openDevice
import com.passenger.utils.ContentUtil
import com.passenger.utils.InviteUtils
import com.passenger.utils.ToastUtil
import com.passenger.widget.AlertTimerPickerWidget
import com.trust.face.help.FaceHelper
import com.trust.face.utils.BitmapUtil
import com.trustpass.api.sdk.bean.CpuCardResult
import com.trustpass.api.sdk.common.ErrorCode
import com.trustpass.api.sdk.helper.ARADeviceHelper
import com.trustpass.api.sdk.inter.ICard
import com.trustpass.api.sdk.listener.CardResultListener
import com.trustpass.api.sdk.util.DeviceUtil
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import updata.api.KtRetrofit
import updata.api.KtService
import java.io.File
import java.util.*

class CardIdModel : ViewModel() {
    var job: CoroutineScope = MainScope()
    private lateinit var mCardHelper: ICard
    private var TAG = "CardIdModel"
    var flageSubmit = MutableLiveData<Boolean>()
    var flageEnter = MutableLiveData<Boolean>()
    var startTime = MutableLiveData<String>()
    var endTime = MutableLiveData<String>()
    var flagecardidTitleOne = MutableLiveData<Boolean>()
    var flageCameraContainer = MutableLiveData<Boolean>()
    var flageCardBtnYes = MutableLiveData<Boolean>()
    var tvMsg = MutableLiveData<String>()
    var tvTitle = MutableLiveData<String>().apply {
        this.postValue("第一步")
    }
    var sn = MutableLiveData<String>().apply {
        this.postValue(ContentUtil.getDeviceSN())
    }
    var mFacePhoto = MutableLiveData<Bitmap>()
    var isDetectingID = MutableLiveData<Boolean>()
    var isDetectingFace = MutableLiveData<Boolean>()
    var IDCardBtPath: String? = null
    var IDCardHeadPath: String? = null

    var dataList = ObservableArrayList<DataItem>()
    var dataLists = MutableLiveData<MutableList<DataItem>>()
    var mPassengerAttachmentList: MutableList<PassengerAttachment> = mutableListOf()

    var mPassenger: Passenger? = null
    private lateinit var mContext: Context
    var mCardIdActivity: CardIdActivity? = null
    var mLifecycleOwner: LifecycleOwner? = null
    var count: Int = 0
    var countss: Int = 0
    var dataliveData = MutableLiveData<String>()
    lateinit var timer: Timer
    var mRegisterType: String? = null

    fun setActivity(activity: CardIdActivity, context: Context, lifecycleOwner: LifecycleOwner) {
        mCardIdActivity = activity
        mLifecycleOwner = lifecycleOwner
        mContext = context
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

    fun setRegisterType(registerType: String) {
        mRegisterType = registerType
    }

    fun submit() {
        countss = 0
        if (TextUtils.isEmpty(startTime.value) || TextUtils.isEmpty(endTime.value)) {
            Log.e(TAG, "submit: 时间为null")
            return
        }
        val toLiveData = KtRetrofit.initConfig(Config.ApiURL).getService(KtService::class.java)
            .getSideDoorRegist(dataList).toLiveData()
        toLiveData.observe(mLifecycleOwner!!) {
            Log.e(TAG, "submit: $it")
            if (it == true) {
                dataList.clear()
            }
//            val asJsonArray = JsonParser().parse(it).asJsonArray
//            for (item in asJsonArray) {
//                val fromJson = Gson().fromJson(item, RsktItem::class.java)
//                Log.e(TAG, "submit: $fromJson")
//            }
        }
    }

    fun delete(item: DataItem) {
        countss = 0
        dataList.remove(item)
        Log.e(TAG, "delete: ${dataList.size}", )
    }

    fun add() {
        if (TextUtils.isEmpty(startTime.value) && TextUtils.isEmpty(endTime.value)) {
            ToastUtil.showBottom(mContext, "时间不能为空")
            return
        }
        mCardIdActivity?.setUI(1)
        flageEnter.postValue(false)
        flageSubmit.postValue(false)
        mPassenger = null
        mPassengerAttachmentList.clear()
        count++
        flagecardidTitleOne.postValue(true)
        tvMsg.postValue(mContext.getString(R.string.tvMsg1))
        openDevice()

//        flageSubmit.postValue(true)
//        mPassengerAttachmentList.add(
//            PassengerAttachment(
//                1,
//                2,
//                1,
//                2,
//                "IDCardBt",
//                "http://10.243.1.6:9000/jydbucket/IDCardBt_1698419396915t5h2G.png"
//            )
//        )
//        dataList.add(
//            DataItem(
//                "Unaudited",
//                "jydai136",
//                startTime.value,
//                Passenger("Cd$count", "小明", "122304911939285739", "小明", "小明", "", "man"),
//                mPassengerAttachmentList,
//                "3fa85f64-5717-4562-b3fc-2c963f66afa9",
//                "WorkstationComputer",
//                endTime.value
//            )
//        )
//        dataLists.postValue(dataList)

    }

    fun startTime() {
//        flageCameraContainer.postValue(true)
//        mCardIdActivity?.openCamera("2")

        AlertTimerPickerWidget.alertTimerPicker(
            mContext,
            TimePickerView.Type.ALL,
            "yyyy-MM-dd HH:mm:ss"
        ) { date -> startTime.postValue(date) }
    }

    fun endTime() {
        AlertTimerPickerWidget.alertTimerPicker(
            mContext,
            TimePickerView.Type.ALL,
            "yyyy-MM-dd HH:mm:ss"
        ) { date -> endTime.postValue(date) }
    }

    fun enter() {
        mCardIdActivity?.setUI(2)
        isDetectingFace.postValue(false)
        mCardIdActivity?.let { it.openCame0() }
        flageCameraContainer.postValue(true)
    }

    fun cardBtnYes() {
        isDetectingID.postValue(true)
        tvTitle.postValue("第二步")
        flageCardBtnYes.postValue(false)
        tvMsg.postValue(mContext.getString(R.string.tvMsg3))
        flageEnter.postValue(true)
        flageCameraContainer.postValue(false)

    }

    private fun openDevice() {
        job.launch(Dispatchers.IO) {
            mCardHelper = ARADeviceHelper.getInstance(mContext).cardHelper
            when (val error = mCardHelper.openDevice()) {
                ErrorCode.RESULT_OK, ErrorCode.DEVICE_REOPEN -> {
                    Log.e(TAG, mContext.getString(R.string.idcard_open_success))
                    when (val error = startReadCard()) {
                        ErrorCode.RESULT_OK -> {
                            Log.e(TAG, mContext.getString(R.string.idcard_is_reading))
                        }
                        else -> {
                            Log.e(
                                TAG,
                                mContext.getString(R.string.idcard_read_failed) + mCardHelper.getIDCardErrorString(
                                    error
                                )
                            )
                        }
                    }
                }
                else -> {
                    Log.e(
                        TAG,
                        mContext.getString(R.string.idcard_open_failed) + mCardHelper.getIDCardErrorString(
                            error
                        )
                    )
                }
            }

            when (val ret = FaceHelper.init(mContext)) {
                ErrorInfo.CV_OK -> {
                    when (val ret = FaceHelper.initEngine(mContext)) {
                        ErrorInfo.CV_OK -> {
                            Log.e(TAG, "人脸引擎初始化成功")
                        }
                        else -> {
                            Log.e(TAG, "人脸引擎初始化失败：$ret")
                        }
                    }
                }
                else -> {
                    Log.e(TAG, "人脸引擎激活失败：$ret")
                }
            }
        }
    }

    private var isCompareFace = false
    suspend fun startReadCard(): Int {
        LogUtils.d("startReadCard()")
        return mCardHelper.startIDRead(200, object : CardResultListener {
            override fun detectCard() {
                Log.d(TAG, "detectCard")
                //showResultMessage(getString(R.string.idcard_detect_card),MessageType.NORMAL);
                if (isCompareFace) return
                if (DeviceUtil.isP80ev() || DeviceUtil.isM50Dev()) {
                    //显示读卡对话框，其实只需要在部分读卡比较久的设备显示一下，避免因为移动身份证导致读卡失败
//                    mCardDialog.showLoading()
//                    mTextToSpeechHelper.speak(getString(R.string.idcard_tts_reading))
                }
            }

            override fun error(errorCode: Int, errorMessage: String) {
                LogUtils.e("error:$errorCode,$errorMessage")
                if (isCompareFace) return
                if (errorCode != ErrorCode.RESULT_OK) {
                    Log.e(TAG, mContext.getString(R.string.idcard_read_failed) + errorMessage)
//                    if (mCardDialog != null && DeviceUtil.isP80ev() || DeviceUtil.isM50Dev()) {
//                        //显示错误对话框
//                        mCardDialog.dismiss()
//                        mCardDialog.showError(0)
//                        mTextToSpeechHelper.speak(getString(R.string.idcard_tts_read_failed))
//                    }
                }
            }

            override fun mifareCardResult(result: String) {}
            override fun cpuCardResult(result: CpuCardResult) {}
            override fun idCardResult(idCard: IDCard, physicalID: String) {
                stopReadCard(mContext)
                Log.e(TAG, mContext.getString(R.string.idcard_read_success));
                val name = idCard.sex.toString()

                var sex = if (name == "男") {
                    "Man"
                } else {
                    "Women"
                }
                if (idCard.number.toString().contains("860")) {
                    countss = 2
                    mPassenger =
                        Passenger(
                            "Cd$count",
                            "王涛",
                            "422302198801239876",
                            "王涛",
                            "王涛",
                            "",
                            sex
                        )
                } else {
                    mPassenger =
                        Passenger(
                            "Cd$count",
                            idCard.name,
                            idCard.number,
                            idCard.name,
                            idCard.name,
                            "",
                            sex
                        )
                }

                mFacePhoto.postValue(idCard.photo)
                //Bitmap test = BitmapFactory.decodeResource(getResources(),R.mipmap.test_photo);
                mCardIdActivity?.let { it.openCamera("2") }
                flageCameraContainer.postValue(true)
                flageCardBtnYes.postValue(true)
                flageEnter.postValue(false)
            }
        })
    }

    fun setUiFour() {
        tvTitle.postValue("第一步")
        flageCameraContainer.postValue(false)
        flagecardidTitleOne.postValue(false)
        if (countss != 2) {
            Log.e(TAG, "setUiFour: ========================================================")
            setIDCardBitmap(mFacePhoto!!.value!!, "IDCardHead")
        }
        job.launch(Dispatchers.Main) {
            dataList.add(
                DataItem(
                    "Unaudited",
                    "jydai136",
//                    sn.value,
                    startTime.value,
                    mPassenger,
                    mPassengerAttachmentList,
//                    mRegisterType,
                    "3fa85f64-5717-4562-b3fc-2c963f66afa9",
                    "WorkstationComputer",
                    endTime.value
                )
            )
            dataLists.postValue(dataList)
            flageSubmit.postValue(true)
            mCardIdActivity?.setUI(1)
        }
        countss == 0
    }

    private fun stopReadCard(context: Context) {
        mCardHelper.stopIDRead()
        Log.e(TAG, context.getString(R.string.idcard_is_stop))
    }

    fun setErrorUiFour() {
        flageCameraContainer.postValue(false)

    }

    fun setIDCardBitmap(IdCardBitmap: Bitmap, name: String) {
        job.launch(Dispatchers.IO) {
            var path = "${Environment.getExternalStorageDirectory()}/TrustFaceDemo"
            BitmapUtil.saveBitmap2File(IdCardBitmap, path, name)
            var paths = "$path/$name.png"
            Log.e(TAG, "setIDCardBitmap: ======$paths")
            var file = File(paths)

            val requestBody: RequestBody = RequestBody.create("image/png".toMediaType(), file)
//                RequestBody.create("application/octet-stream".toMediaType()
//                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
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