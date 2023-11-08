package com.passenger.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import cn.com.aratek.idcard.IDCard
import cn.com.aratek.trustface.sdk.ErrorInfo
import cn.com.aratek.trustface.sdk.bean.*
import cn.com.aratek.trustface.sdk.enums.ImageFormat
import com.kaer.sdk.utils.LogUtils
import com.orhanobut.logger.Logger
import com.passenger.R
import com.passenger.databinding.ActivityFaceidBinding
import com.passenger.utils.InviteUtils
import com.trust.face.help.*
import com.trust.face.utils.BitmapUtil
import com.trust.face.utils.NV21RotateUtils
import com.trust.face.utils.NV21ToBitmap
import com.trustpass.api.sdk.bean.CpuCardResult
import com.trustpass.api.sdk.common.ErrorCode
import com.trustpass.api.sdk.enums.CardType
import com.trustpass.api.sdk.helper.ARADeviceHelper
import com.trustpass.api.sdk.inter.ICard
import com.trustpass.api.sdk.listener.CardResultListener
import com.trustpass.api.sdk.util.DeviceUtil
import kotlinx.coroutines.*
import java.util.ArrayList

class FaceIdActivity : AppCompatActivity(), CameraStateCallback {
    private val TAG = "FaceIdActivity"

    var job: CoroutineScope = MainScope()
    private lateinit var mCameraData: ByteArray
    private lateinit var mCardHelper: ICard

    var mCameraOpenInterface: CameraOpenInterface? = null

    private var isDetectingFace = false

    private var isDetectedFace = false

    private var mCameraDataWidth = 0

    private var mCameraDataHeight = 0
    var mScreenWidth: Int = 0
    var mScreenHeight: Int = 0
    lateinit var mBinding: ActivityFaceidBinding
    var mNV21ToBitmap: NV21ToBitmap? = null

    private var mFaceInfo: FaceInfo? = null
    private var mFacephoto: Bitmap? = null
    lateinit var mFaceIdMoudel: FaceIdMoudel
    var isDetectId = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView<ActivityFaceidBinding>(this, R.layout.activity_faceid)
        hideBottomMenu()
        //获取真实屏幕宽高
        val displayMetrics = DisplayMetrics()
        mFaceIdMoudel = ViewModelProvider(this)[FaceIdMoudel::class.java]
        mBinding.apply {
            faceIdVm = mFaceIdMoudel
            lifecycleOwner = this@FaceIdActivity
            mFaceIdMoudel.setdata(this@FaceIdActivity, this@FaceIdActivity, this@FaceIdActivity)
            faceIdTitle.weekTimes.text = InviteUtils.getWeek()
            faceIdTitle.dateTimes.text = InviteUtils.getDate()
        }
        mFaceIdMoudel.startTimer()
        mFaceIdMoudel.dataliveData.observe(this) { value ->
            mBinding.faceIdTitle.time.text = value

        }
        intent.getStringExtra("RegisterType")?.let { mFaceIdMoudel.setRegisterType(it) }
        window.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        mScreenWidth = displayMetrics.widthPixels
        mScreenHeight = displayMetrics.heightPixels

        mNV21ToBitmap = NV21ToBitmap.getInstance(this)

        openDevice(this)

//        setUiFour()

        mBinding.flCameraContainer.visibility = View.GONE
//        openCamera("2")
        mBinding.btnYes.setOnClickListener {
            isDetectId = true
//            mBinding.imageBitmap.setImageBitmap(nv21ToBitmap)
            mBinding.flCameraContainer.visibility = View.GONE
//            mCameraOpenInterface?.let { it.closeCamera() }
//            mBinding.flCameraContainer.visibility = View.VISIBLE
            mBinding.tvTitle.text = "第三步"
            mBinding.tvMessage.text = resources.getText(R.string.tvMsg3)
            mBinding.btnMessage.visibility = View.VISIBLE
            job.launch(Dispatchers.IO) {
                setUI(3)
            }
        }
        mBinding.btnMessage.setOnClickListener {
            mBinding.btnMessage.visibility = View.GONE
            job.launch(Dispatchers.IO) {
                openCamera("0")
//                delay(2000)
                withContext(Dispatchers.Main) {
                    mBinding.flCameraContainer.visibility = View.VISIBLE
                    mBinding.tvIDMsg.visibility = View.GONE
                    mBinding.btnYes.visibility = View.GONE
                }
            }
        }
    }

    private fun openDevice(context: Context) {
        //打开设备和初始化人脸算法都是耗时操作，要放到子线程处理
        job.launch(Dispatchers.IO) {
//            showLoadingDialog(getString(R.string.notice_is_opening))
            mCardHelper = ARADeviceHelper.getInstance(context).cardHelper
            val error: Int = mCardHelper.openDevice(CardType.ID_CARD)
            if (error == ErrorCode.RESULT_OK || error == ErrorCode.DEVICE_REOPEN) {
                Log.e(TAG, getString(R.string.idcard_open_success))
                startReadCard()
            } else {
                Log.e(
                    TAG,
                    getString(R.string.idcard_open_failed) + mCardHelper.getIDCardErrorString(
                        error
                    )
                )
            }
            //初始化人脸算法
//            var ret: Int = FaceHelper.activeOnline(context)
            val tm: TelephonyManager =
                context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val SimSerialNumber: String? = tm.getDeviceId()
            val mAndroidID: String? =
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            Log.e(TAG, "人脸引擎初始化成功mAndroidID $mAndroidID")
            var path = "${Environment.getExternalStorageDirectory()}/TrustFaceDemo/License.lic"
            Log.e(TAG, "人脸引擎初始化成功path $path")
            var ret: Int = FaceHelper.activeOffline(context, path)
            if (ret == ErrorInfo.CV_OK) {
                ret = FaceHelper.initEngine(context)
                if (ret == ErrorInfo.CV_OK) {
                    Log.e(TAG, "人脸引擎初始化成功")
                } else {
                    Log.e(TAG, "人脸引擎初始化失败：$ret")
                }
            } else {
                Log.e(TAG, "人脸引擎激活失败：$ret")
            }
            //初始化tts服务
//            mTextToSpeechHelper =
//                ARADeviceHelper.getInstance(MainApplication.getInstance()).textToSpeechHelper
//            mTextToSpeechHelper.init(Locale.getDefault())
//            mTextToSpeechHelper.setSpeechRate(1.5f)
//            dismissLoadingDialog()
        }
    }

    private var isCompareFace = false

    /**
     * 开始读去身份证
     */
    suspend fun startReadCard() {
        LogUtils.d("startReadCard()")
        val error = mCardHelper.startIDRead(200, object : CardResultListener {
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
                    Log.e(TAG, getString(R.string.idcard_read_failed) + errorMessage)
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
                stopReadCard()
                Log.e(TAG, getString(R.string.idcard_read_success));
                Log.e(TAG, idCard.name);
                mFacephoto = idCard.photo
                mFacephoto?.let { mFaceIdMoudel.UpdataBitmips(it, idCard) }

                //Bitmap test = BitmapFactory.decodeResource(getResources(),R.mipmap.test_photo);
                //隐藏读卡对话框
//                mCardDialog.dismiss()
                /**
                 * 对比人脸相册
                 */
//                tryFaceCompare(idCard.photo)
                openCamera("2")
                job.launch(Dispatchers.Main) {
                    delay(1000)
                    mBinding.flCameraContainer.visibility = View.VISIBLE
                }
            }
        })
        if (error == ErrorCode.RESULT_OK) {
            Log.e(TAG, getString(R.string.idcard_is_reading))
        } else {
            Log.e(
                TAG,
                getString(R.string.idcard_read_failed) + mCardHelper.getIDCardErrorString(
                    error
                )
            )
        }
    }


    private fun openCamera(cameraId: String) {
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
                .setPreviewView(mBinding.tvCameraView)
                .setPreviewImageFormat(PreviewImageFormat.PIX_FMT_NV21)
                .setCameraStateCallback(this)
                .openCamera()
        }

        LogUtils.d("outputSizes: " + mCameraOpenInterface?.getOutputSizes())

    }

    override fun onCameraOpen() {
    }

    override fun onCameraClose() {
    }

    override fun onCameraError(message: String?) {
    }


    override fun onCameraPreview(image: CameraPreviewImage?) {
        if (isDetectId) {
            isDetectId = false
            val data = NV21RotateUtils.rotateDegree180(image!!.data, image!!.width, image!!.height)
            var bitmap = mNV21ToBitmap!!.nv21ToBitmap(data, image.width, image.height)
            mFaceIdMoudel.setIDCardBitmap(bitmap!!, "IDCardBt")
            mCameraOpenInterface?.closeCamera()
        }

        if (isDetectingFace) return
        job.launch(Dispatchers.IO) {
            isDetectingFace = true
            val faceInfoList: List<FaceInfo> = ArrayList()
            //画面需旋转到人脸为正，否则检测不出人脸，不同设备的旋转角度不同
            val data: ByteArray = image!!.data
            val width: Int = image.width
            val height: Int = image.height
            val ret: Int = FaceHelper
                .detectFaceForVideo(data, width, height, ImageFormat.PIX_FMT_NV21, faceInfoList)

            if (ret == ErrorInfo.CV_OK && faceInfoList.size > 0) {
                Log.d(TAG, "faceInfoList: $faceInfoList")
                mCameraData = data
                mCameraDataWidth = width
                mCameraDataHeight = height
                mFaceInfo = faceInfoList[0]
                mFacephoto?.let { tryFaceCompare(it) }
            } else {
                LogUtils.d("detectFaceForVideo error:$ret")
//                mBinding.fvFaceView.clearFaceInfoList()
                isDetectedFace = false
            }
            isDetectingFace = false
        }

    }

    /**
     * 人证核验，1:1比对
     * @param idCardPhoto 身份证照片
     * return
     */
    private fun tryFaceCompare(idCardPhoto: Bitmap) {
        if (isCompareFace) return
        job.launch(Dispatchers.IO) {
            isCompareFace = true
            Log.e(TAG, "waite face: start")
            Log.e(TAG, "waite face: start verification_look_at_camera")
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
                            setUiFour()

                            Logger.d("${getString(R.string.verification_verify_success)}")
//                            showResultMessage(
//                                getString(R.string.verification_verify_success),
//                                VerificationActivity.MessageType.SUCCESS
//                            )
                        } else {
                            Logger.d("${getString(R.string.verification_verify_failed)},$score")

//                            showResultMessage(
//                                getString(R.string.verification_verify_failed, score),
//                                VerificationActivity.MessageType.FAILED
//                            )
                        }
                    } else {
                        Logger.d("${getString(R.string.verification_compare_failed)}")

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
        }
    }

    /**
     * 开始读去身份证
     */
    private fun stopReadCard() {
        mCardHelper.stopIDRead()
        Log.e(TAG, getString(R.string.idcard_is_stop))
    }

    private suspend fun setUI(type: Int) {
        withContext(Dispatchers.Main) {
            when (type) {
                1 -> {
                    mBinding.imgOne.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvOne.text = resources.getText(R.string.face_ones)
                    mBinding.tvOne.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgTwo.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvTwo.text = resources.getText(R.string.face_two)
                    mBinding.tvTwo.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                    mBinding.imgThree.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvThree.text = resources.getText(R.string.face_three)
                    mBinding.tvThree.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                    mBinding.imgFour.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvFour.text = resources.getText(R.string.face_four)
                    mBinding.tvFour.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                }
                2 -> {
                    mBinding.imgOne.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvOne.text = resources.getText(R.string.face_one)
                    mBinding.tvOne.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgTwo.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvTwo.text = resources.getText(R.string.face_twos)
                    mBinding.tvTwo.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgThree.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvThree.text = resources.getText(R.string.face_three)
                    mBinding.tvThree.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                    mBinding.imgFour.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvFour.text = resources.getText(R.string.face_four)
                    mBinding.tvFour.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                }
                3 -> {
                    mBinding.imgOne.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvOne.text = resources.getText(R.string.face_one)
                    mBinding.tvOne.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgTwo.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvTwo.text = resources.getText(R.string.face_two)
                    mBinding.tvTwo.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgThree.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvThree.text = resources.getText(R.string.face_threes)
                    mBinding.tvThree.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgFour.setBackgroundResource(R.mipmap.checkoff)
                    mBinding.tvFour.text = resources.getText(R.string.face_four)
                    mBinding.tvFour.setTextColor(resources.getColor(R.color.faceid_tvcolor_off))
                }
                4 -> {
                    mBinding.imgOne.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvOne.text = resources.getText(R.string.face_one)
                    mBinding.tvOne.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgTwo.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvTwo.text = resources.getText(R.string.face_two)
                    mBinding.tvTwo.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgThree.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvThree.text = resources.getText(R.string.face_three)
                    mBinding.tvThree.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                    mBinding.imgFour.setBackgroundResource(R.mipmap.checktrue)
                    mBinding.tvFour.text = resources.getText(R.string.face_fours)
                    mBinding.tvFour.setTextColor(resources.getColor(R.color.faceid_tvcolor_on))
                }
            }
        }

    }

    fun setUiFour() {
        job.launch(Dispatchers.Main) {
            mBinding.flCameraContainer.visibility = View.GONE
            mBinding.btnMessage.visibility = View.GONE
            mBinding.tvTitle.text = "第四步"
            mBinding.tvMessage.visibility = View.GONE
            mBinding.tvTelephoneTitle.visibility = View.VISIBLE
            mBinding.editTelephone.visibility = View.VISIBLE
            mBinding.btnTelephone.visibility = View.VISIBLE
            setUI(4)
            Toast.makeText(this@FaceIdActivity, "核验成功", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroy() {
        mCameraOpenInterface?.let {
            it.closeCamera()
        }
        stopReadCard()
//        closeDevice()
        super.onDestroy()
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