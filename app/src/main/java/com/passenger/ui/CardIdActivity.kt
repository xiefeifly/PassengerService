package com.passenger.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.os.FileUtils.copy
import android.util.Log
import android.util.Size
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.com.aratek.trustface.sdk.ErrorInfo
import cn.com.aratek.trustface.sdk.bean.*
import cn.com.aratek.trustface.sdk.enums.ImageFormat
import com.kaer.sdk.utils.LogUtils
import com.orhanobut.logger.Logger
import com.passenger.R
import com.passenger.adapter.CardIdRecycleAdapter
import com.passenger.bean.DataItem
import com.passenger.databinding.ActivityCardidBinding
import com.passenger.utils.InviteUtils
import com.trust.face.help.*
import com.trust.face.utils.NV21RotateUtils
import com.trust.face.utils.NV21ToBitmap
import com.trustpass.api.sdk.common.ErrorCode
import com.trustpass.api.sdk.util.DeviceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files.copy
import java.util.Collections.copy

class CardIdActivity : AppCompatActivity(), CameraStateCallback {
    var TAG = "CardIdActivity"
    lateinit var mBinding: ActivityCardidBinding
    lateinit var cardIdModel: CardIdModel
    private var mCameraOpenInterface: CameraOpenInterface? = null

    var job = MainScope()
    var mFacephoto: Bitmap? = null
    private var isDetectingFace = true
    private var isDetectingID = false
    private var isDetectedFace = false
    private var isCompareFace = false

    private var mCameraDataWidth = 0
    private var mCameraDataHeight = 0
    private lateinit var mCameraData: ByteArray
    var mScreenWidth: Int = 0
    var mScreenHeight: Int = 0
    var mNV21ToBitmap: NV21ToBitmap? = null
    var nv21ToBitmap: Bitmap? = null
    var data = mutableListOf<DataItem>()
    private var mFaceInfo: FaceInfo? = null
    var cardIdRecycleAdapter: CardIdRecycleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cardid)
        cardIdModel = ViewModelProvider(this)[CardIdModel::class.java]
        mNV21ToBitmap = NV21ToBitmap.getInstance(this)
        hideBottomMenu()
        mBinding.apply {
            cardidVm = cardIdModel
            lifecycleOwner = this@CardIdActivity
            cardIdModel.setActivity(this@CardIdActivity, this@CardIdActivity, this@CardIdActivity)
            cardTitle.weekTimes.text = InviteUtils.getWeek()
            cardTitle.dateTimes.text = InviteUtils.getDate()
            cardIdRecycleAdapter = CardIdRecycleAdapter()
            recyclerview.adapter = cardIdRecycleAdapter
            cardIdRecycleAdapter?.setData(object : CardIdRecycleAdapter.onClickListener {
                override fun OnitemListener(item: DataItem) {
                    Log.e(TAG, "OnitemListener: ${item.DeviceCode}")
                    cardIdModel.delete(item)
                    cardIdRecycleAdapter?.setDatas(data)
                }
            })
        }
        cardIdModel.startTimer()
        cardIdModel.dataliveData.observe(this) { value ->
            mBinding.cardTitle.time.text = value

        }
        cardIdModel.dataLists.observe(this) { value ->
            Log.e(TAG, "onCreate: ===============")
            data = value
            cardIdRecycleAdapter?.setDatas(data)
        }

        intent.getStringExtra("RegisterType")?.let { cardIdModel.setRegisterType(it) }
        cardIdModel.mFacePhoto.observe(
            this
        ) { value -> mFacephoto = value }
        cardIdModel.isDetectingID.observe(
            this
        ) { value -> isDetectingID = value }
        cardIdModel.isDetectingFace.observe(
            this
        ) { value -> isDetectingFace = value }
        mBinding.cardTitle.back.setOnClickListener {
            finish()
        }
    }

    fun openCamera(cameraId: String) {
        val previewSize: Size = if (DeviceUtil.isP80ev()) {
            Size(1024, 768)
        } else {
            //M50,BD9000,BD8500 等其他设备
            Size(1280, 720)
        }
        mCameraOpenInterface = CameraHelperManager.initCameraHelper(this)
        mCameraOpenInterface?.let {
            it.setCameraId(cameraId)
                .setPreviewSize(previewSize)
                .setPreviewView(mBinding.cardTvCameraView)
                .setPreviewImageFormat(PreviewImageFormat.PIX_FMT_NV21)
                .setCameraStateCallback(this)
                .openCamera()
        }
        LogUtils.d("outputSizes: " + mCameraOpenInterface?.outputSizes)
    }

    fun openCame0() {
        openCamera("0")
    }

    fun closeCamera() {
        mCameraOpenInterface?.let { it.closeCamera() }
    }

    override fun onCameraOpen() {
    }

    override fun onCameraClose() {
    }

    override fun onCameraError(message: String?) {
    }

    override fun onCameraPreview(image: CameraPreviewImage?) {
        if (isDetectingID) {
            val data = NV21RotateUtils.rotateDegree180(image!!.data, image!!.width, image!!.height)
            nv21ToBitmap = mNV21ToBitmap?.nv21ToBitmap(data, image.width, image.height)
            cardIdModel.setIDCardBitmap(nv21ToBitmap!!, "IDCardBt")
            closeCamera()
            isDetectingID = false
        }

        if (isDetectingFace) return
        job.launch(Dispatchers.IO) {
            isDetectingFace = true
            val faceInfoList: List<FaceInfo> = ArrayList()
            val data: ByteArray = image!!.data
            val width: Int = image.width
            val height: Int = image.height
            val ret: Int = FaceHelper
                .detectFaceForVideo(data, width, height, ImageFormat.PIX_FMT_NV21, faceInfoList)

            if (ret == ErrorInfo.CV_OK && faceInfoList.size > 0) {
//                Log.d(TAG,"faceInfoList: $faceInfoList")
                mCameraData = data
                mCameraDataWidth = width
                mCameraDataHeight = height
                mFaceInfo = faceInfoList[0]
                mFacephoto?.let { tryFaceCompare(it) }
            } else {
                LogUtils.d("detectFaceForVideo error:$ret")
                isDetectedFace = false
            }
            isDetectingFace = false
        }
    }

    private fun tryFaceCompare(idCardPhoto: Bitmap) {
        if (isCompareFace) return
        isCompareFace = true
        Log.e(TAG, "waite face: start")
//            showResultMessage(
//                getString(R.string.verification_look_at_camera),
//                VerificationActivity.MessageType.NORMAL
//            )
        var count = 5
        while (true) {
            if (isDetectedFace && mFaceInfo != null && mCameraData != null || count <= 0) {
                break
            } else {
                Log.e(TAG, "waite face:$count")
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                count--
            }
        }
        if (isDetectedFace && mFaceInfo != null && mCameraData != null) {
            Logger.d("${getString(R.string.verification_compare_face)}")
//                showResultMessage(
//                    getString(R.string.verification_compare_face),
//                    VerificationActivity.MessageType.NORMAL
//                )
            val cameraFeature = FaceFeature()
            val cameraCode: Int = FaceHelper.extractFaceFeature(
                mCameraData,
                mCameraDataWidth,
                mCameraDataHeight,
                ImageFormat.PIX_FMT_NV21,
                mFaceInfo,
                FeatureMode.RECOGNITION,
                MaskInfo.NOT_WORN,
                cameraFeature
            )
            val cardFeature = FaceFeature()
            val cardCode: Int =
                FaceHelper.extractFaceFeature(idCardPhoto, cardFeature)
            if (cameraCode == ErrorInfo.CV_OK && cardCode == ErrorInfo.CV_OK) {
                val faceSimilar = FaceSimilar()
                val compareCode: Int = FaceHelper
                    .compareFaceFeature(cameraFeature, cardFeature, faceSimilar)
                if (compareCode == ErrorCode.RESULT_OK) {
                    val score = (faceSimilar.score * 100).toInt()
                    LogUtils.d("compareScore：$score")
                    if (score >= 80) {
                        mCameraOpenInterface?.let { it.closeCamera() }
                        cardIdModel.setUiFour()

                        Logger.d("${getString(R.string.verification_verify_success)}")
//                            showResultMessage(
//                                getString(R.string.verification_verify_success),
//                                VerificationActivity.MessageType.SUCCESS
//                            )
                    } else {
                        Logger.d("${getString(R.string.verification_verify_failed)},$score")
                        mCameraOpenInterface?.let { it.closeCamera() }
                        cardIdModel.setErrorUiFour()
//                            showResultMessage(
//                                getString(R.string.verification_verify_failed, score),
//                                VerificationActivity.MessageType.FAILED
//                            )
                    }
                } else {
                    Logger.d("${getString(R.string.verification_compare_failed)}")
                    mCameraOpenInterface?.let { it.closeCamera() }
                    cardIdModel.setErrorUiFour()
//                        showResultMessage(
//                            getString(R.string.verification_compare_failed),
//                            VerificationActivity.MessageType.FAILED
//                        )
                }
            } else {
                LogUtils.e("cameraCode=$cameraCode,cardCode=$cardCode")
                Logger.d("${getString(R.string.verification_feature_failed)}")
//                    showResultMessage(
//                        getString(R.string.verification_feature_failed),
//                        VerificationActivity.MessageType.FAILED
//                    )
            }
        } else {
            LogUtils.e("no face, do nothing...")
            Logger.d("${getString(R.string.verification_no_face)}")
//                showResultMessage(
//                    getString(R.string.verification_no_face),
//                    VerificationActivity.MessageType.FAILED
//                )
        }
        //一定延时后恢复默认状态
        try {
            Thread.sleep(4000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Logger.d("${getString(R.string.verification_please_read_card)}")
//            showResultMessage(
//                getString(R.string.verification_please_read_card),
//                VerificationActivity.MessageType.NORMAL
//            )
        isDetectedFace = true
        isCompareFace = false
//        }
    }

    override fun onDestroy() {
        mCameraOpenInterface?.let { it.closeCamera() }
        super.onDestroy()
    }

    fun setUI(type: Int) {
        job.launch(Dispatchers.Main) {
            when (type) {
                1 -> {
                    mBinding.imgOne.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvOne.text = resources.getText(R.string.face_ones)
                    mBinding.tvOne.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgFour.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvFour.text = resources.getText(R.string.face_foures)
                    mBinding.tvFour.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                }
                2 -> {
                    mBinding.imgOne.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvOne.text = resources.getText(R.string.face_one)
                    mBinding.tvOne.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgFour.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvFour.text = resources.getText(R.string.face_foures)
                    mBinding.tvFour.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                }
            }
        }
    }

    fun hideBottomMenu() {
        val decorView = window.decorView
        val option =
            0x1613006 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        decorView.systemUiVisibility = option
        decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
            if (visibility and 4 == 0) {
                decorView.systemUiVisibility = option
            }
        }
    }
}