package com.trust.face.help

import android.content.Context
import android.graphics.Bitmap
import cn.com.aratek.trustface.sdk.ErrorInfo
import cn.com.aratek.trustface.sdk.FaceEngine
import com.trust.face.help.FaceHelper
import cn.com.aratek.trustface.sdk.bean.FaceInfo
import cn.com.aratek.trustface.sdk.bean.FaceFeature
import cn.com.aratek.trustface.sdk.bean.FeatureMode
import cn.com.aratek.trustface.sdk.bean.MaskInfo
import cn.com.aratek.trustface.sdk.bean.FaceSimilar
import cn.com.aratek.trustface.sdk.enums.ImageFormat
import java.nio.ByteBuffer
import java.util.ArrayList

/**
 * 人脸算法引擎管理助手
 */
object FaceHelper {
    /**
     * 人脸算法引擎，用于摄像头画面的人脸检测
     */
    private var videoEngine: FaceEngine? = null

    /**
     * 人脸算法引擎，用于静态图片的人脸检测
     */
    private var imageEngine: FaceEngine? = null

    /**
     * 人脸算法引擎，用于提取和比对人脸特征
     */
    private var compareEngine: FaceEngine? = null

    /**
     * 算法激活测试账号
     */
    val ACTIVE_ACCOUNT = "1122"

    /**
     * 算法激活测试账号密码
     */
    val ACTIVE_PASSWORD = "232d90fd469d19ef"

    /**
     * 最大检测人脸数量
     */
    val MAX_FACE_NUM = 1

//    private object InstanceHolder {
//        val instance = FaceHelper()
//    }

    /**
     * 激活引擎，首次调用需要联网激活，此后每次应用启动也要调用一次，但设备可以离线
     * @param context 上下文
     * @return 错误码
     */
    fun activeOnline(context: Context?): Int {
        return FaceEngine.activeOnline(context, ACTIVE_ACCOUNT, ACTIVE_PASSWORD)
    }

    fun activeOffline(context: Context?, path: String?): Int {
        return FaceEngine.activeOffline(context, path)
    }

    /**
     * 初始化引擎，由于单个引擎不支持多线程调用，所以根据使用场景分成多个引擎实例，分开调用
     * @param context 上下文
     * @return 错误码
     */
    fun initEngine(context: Context?): Int {
        videoEngine = FaceEngine()
        imageEngine = FaceEngine()
        compareEngine = FaceEngine()
        var ret = videoEngine!!.init(context, MAX_FACE_NUM, FaceEngine.ARA_FACE_TRACK)
        if (ret == ErrorInfo.CV_OK) {
            ret = imageEngine!!.init(context, MAX_FACE_NUM, FaceEngine.ARA_FACE_DETECT)
        }
        if (ret == ErrorInfo.CV_OK) {
            ret = compareEngine!!.init(context, MAX_FACE_NUM, FaceEngine.ARA_FACE_RECOGNITION)
        }
        return ret
    }

    /**
     * 释放引擎
     * @return 错误码
     */
    fun uniInitEngine(): Int {
        var ret = 0
        if (videoEngine != null) {
            ret = videoEngine!!.unInit()
            videoEngine = null
        }
        if (imageEngine != null) {
            ret = imageEngine!!.unInit()
            imageEngine = null
        }
        if (compareEngine != null) {
            ret = compareEngine!!.unInit()
            compareEngine = null
        }
        return ret
    }

    /**
     * 使用[.videoEngine]，检测（追踪）摄像头画面中的人脸
     * @param data 图像数据
     * @param width 图像宽度
     * @param height 图像高度
     * @param imageFormat 图像数据的格式
     * @param faceInfoList 人脸检测结果列表
     * @return 错误码
     */
    fun detectFaceForVideo(
        data: ByteArray?,
        width: Int,
        height: Int,
        imageFormat: ImageFormat?,
        faceInfoList: List<FaceInfo?>?
    ): Int {
        return if (videoEngine == null) {
            ErrorInfo.CV_ERR_BAD_STATE
        } else videoEngine!!.detectFacesTrack(data, width, height, imageFormat, faceInfoList)
    }

    /**
     * 使用[.imageEngine]，检测静态图片中的人脸
     * @param data 图像数据
     * @param width 图像宽度
     * @param height 图像高度
     * @param imageFormat 图像数据的格式
     * @param faceInfoList 人脸检测结果列表
     * @return 错误码
     */
    fun detectFaceForImage(
        data: ByteArray?,
        width: Int,
        height: Int,
        imageFormat: ImageFormat?,
        faceInfoList: List<FaceInfo?>?
    ): Int {
        return if (imageEngine == null) {
            ErrorInfo.CV_ERR_BAD_STATE
        } else imageEngine!!.detectFaces(
            data,
            width,
            height,
            imageFormat,
            faceInfoList
        )
    }

    /**
     * 使用[.imageEngine]，检测静态图片中的人脸
     * @param bitmap 图片
     * @param faceInfoList 人脸检测结果列表
     * @return 错误码
     */
    fun detectFaceForImage(bitmap: Bitmap, faceInfoList: List<FaceInfo>?): Int {
        if (imageEngine == null) {
            return ErrorInfo.CV_ERR_BAD_STATE
        }
        val data = getDataFromBitmap(bitmap)
        return if (data != null) {
            imageEngine!!.detectFaces(
                data,
                bitmap.width,
                bitmap.height,
                ImageFormat.PIX_FMT_RGBA8888,
                faceInfoList
            )
        } else {
            ErrorInfo.CV_ERR_INVALID_PARAMS
        }
    }

    /**
     * 使用[.compareEngine]，提取图像中的人脸特征
     * @param data 图像数据
     * @param width 图像宽度
     * @param height 图像高度
     * @param imageFormat 图像数据的格式
     * @param faceInfo 图像中的人脸坐标信息
     * @param featureMode 特征提取模式
     * @param isMask 图像中的人脸是否戴了口罩
     * @param faceFeature 人脸特征提取结果
     * @return 错误码
     */
    fun extractFaceFeature(
        data: ByteArray?,
        width: Int,
        height: Int,
        imageFormat: ImageFormat?,
        faceInfo: FaceInfo?,
        featureMode: Int,
        isMask: Int,
        faceFeature: FaceFeature?
    ): Int {
        return if (compareEngine == null) {
            ErrorInfo.CV_ERR_BAD_STATE
        } else compareEngine!!.extractFaceFeatureEx(
            data,
            width,
            height,
            imageFormat,
            faceInfo,
            featureMode,
            isMask,
            faceFeature
        )
    }

    /**
     * 使用[.compareEngine]，提取图像中的人脸特征
     * @param bitmap 图像
     * @param faceInfo 图像中的人脸坐标信息
     * @param featureMode 特征提取模式
     * @param isMask 图像中的人脸是否戴了口罩
     * @param faceFeature 人脸特征提取结果
     * @return 错误码
     */
    fun extractFaceFeature(
        bitmap: Bitmap,
        faceInfo: FaceInfo?,
        featureMode: Int,
        isMask: Int,
        faceFeature: FaceFeature?
    ): Int {
        if (compareEngine == null) {
            return ErrorInfo.CV_ERR_BAD_STATE
        }
        val data = getDataFromBitmap(bitmap)
        return if (data != null) {
            compareEngine!!.extractFaceFeatureEx(
                data,
                bitmap.width,
                bitmap.height,
                ImageFormat.PIX_FMT_RGBA8888,
                faceInfo,
                featureMode,
                isMask,
                faceFeature
            )
        } else {
            ErrorInfo.CV_ERR_INVALID_PARAMS
        }
    }

    /**
     * 使用[.imageEngine]和[.compareEngine]，提取图像中的人脸特征，
     * 并默认使用注册场景的特征模式，和不带口罩，一般用于证件照的特征提取
     * @param bitmap 图像
     * @param faceFeature 人脸特征提取结果
     * @return 错误码
     */
    fun extractFaceFeature(bitmap: Bitmap, faceFeature: FaceFeature?): Int {
        if (compareEngine == null) {
            return ErrorInfo.CV_ERR_BAD_STATE
        }
        val faceInfoList: List<FaceInfo> = ArrayList()
        val ret = detectFaceForImage(bitmap, faceInfoList)
        return if (ret == ErrorInfo.CV_OK && faceInfoList.size > 0) {
            extractFaceFeature(
                bitmap,
                faceInfoList[0],
                FeatureMode.REGISTER,
                MaskInfo.NOT_WORN,
                faceFeature
            )
        } else {
            ErrorInfo.CV_ERR_NO_FACE_FOUND
        }
    }

    /**
     * 使用[.compareEngine]，比对两个人脸特征
     * @param faceFeature1 人脸特征1
     * @param faceFeature2 人脸特征2
     * @param faceSimilar 人脸特征比对结果
     * @return 错误码
     */
    fun compareFaceFeature(
        faceFeature1: FaceFeature?,
        faceFeature2: FaceFeature?,
        faceSimilar: FaceSimilar?
    ): Int {
        return if (compareEngine == null) {
            ErrorInfo.CV_ERR_BAD_STATE
        } else compareEngine!!.compareFaceFeature(faceFeature1, faceFeature2, faceSimilar)
    }

    /**
     * 提取静态图片的数据，格式为[Bitmap.Config.ARGB_8888]
     * @param bitmap 图片
     * @return 图片数据
     */
    private fun getDataFromBitmap(bitmap: Bitmap): ByteArray? {
        var bitmap: Bitmap? = bitmap ?: return null
        if (bitmap!!.config != Bitmap.Config.ARGB_8888) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
        val buffer = ByteBuffer.allocate(bitmap!!.byteCount)
        bitmap.copyPixelsToBuffer(buffer)
        return buffer.array()
    }

}