package com.hst.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceView
import android.widget.RelativeLayout
import com.hst.R
import com.hst.business.FspManager
import com.hst.fsp.FspEngine
import com.hst.utils.FspUtils

class FspUserView : RelativeLayout {
    var m_surfaceView: SurfaceView? = null
    var m_userid: String? = null
    var m_videoid: String? = null
    private var m_haveVideo = false
    private var m_haveAudio = false
    private val m_renderMode = FspEngine.RENDER_MODE_CROP_FILL
    constructor (context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context?) {
        inflate(context, R.layout.user_view, this)
        m_surfaceView = findViewById(R.id.fsp_video_surface)
    }
    fun getSurfaceView(): SurfaceView? {
        return m_surfaceView
    }
    fun getVideoRenderMode(): Int {
        return m_renderMode
    }
    fun setUserId(userid: String?) {
        m_userid = userid
    }

    fun setVideoId(videoid: String?) {
        m_videoid = videoid
    }

    fun getUserId(): String? {
        return m_userid
    }

    fun getVideoId(): String? {
        return m_videoid
    }

    fun openVideo() {
        m_haveVideo = true
        m_surfaceView!!.visibility = VISIBLE
        if (!FspUtils.isSameText(FspManager.selfUserId, m_userid)) {
//            m_btnMore.setVisibility(GONE)
        }
//        m_tvInfo.setVisibility(GONE)
//        showUserName()
    }
    fun closeVideo() {
        if (!FspUtils.isEmptyText(m_userid) && !FspUtils.isEmptyText(m_videoid)) {
            FspManager.setRemoteVideoRender(
                m_userid, m_videoid,
                null, m_renderMode
            )
        }
        m_haveVideo = false
        m_surfaceView!!.visibility = GONE
//        m_btnMore.setVisibility(GONE)
//        m_tvInfo.setVisibility(GONE)
        requestLayout()
        invalidate()
        m_surfaceView!!.invalidate()
        releaseAll()
    }
    private fun releaseAll() {
        //音频和视频都关闭了， 才不属于某个user
//        if (!m_haveAudio && !m_haveVideo) {
            // 释放绑定的视频
            if (!FspUtils.isEmptyText(m_userid) && !FspUtils.isEmptyText(m_videoid)) {
                FspManager.setRemoteVideoRender(
                    m_userid, m_videoid,
                    null, m_renderMode
                )
//            }
            m_userid = null
            m_videoid = null
        }
    }
    fun openAudio() {
        m_haveAudio = true
//        m_pbAudioEnergy.setVisibility(VISIBLE)
//        m_ivMicState.setVisibility(VISIBLE)
//        showUserName()
    }

    fun closeAudio() {
        m_haveAudio = false
//        m_pbAudioEnergy.setVisibility(GONE)
//        m_ivMicState.setVisibility(GONE)
        releaseAll()
    }
}