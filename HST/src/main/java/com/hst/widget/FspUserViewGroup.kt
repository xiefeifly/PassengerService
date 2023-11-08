package com.hst.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.hst.business.FspEvents
import com.hst.business.FspManager
import com.hst.fsp.FspEngine
import com.hst.utils.FspUtils
import com.orhanobut.logger.Logger

class FspUserViewGroup : ViewGroup {
    val TAG = "FspUserViewGroup"

    constructor(context: Context) : this(context, null) {
        removeAllViews()
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        removeAllViews()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        removeAllViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var w = MeasureSpec.getSize(widthMeasureSpec)
        var h = MeasureSpec.getSize(heightMeasureSpec)
//        val childrenCount = childCount
        Log.e(TAG, "onMeasure  w: $w")
        Log.e(TAG, "onMeasure  h: $h")
        if (childCount <= 0) return
        if (childCount == 1) {
            getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec)
        }
        if (childCount == 2) {
            getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec)
            getChildAt(1).measure(
                MeasureSpec.makeMeasureSpec(w / 3, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h / 3, MeasureSpec.EXACTLY)
            )

        }

//        if (childCount <= 2) {
//            for (i in 0 until childCount) {
//                Log.e(TAG, "onMeasure:childCount $i")
//                var child = getChildAt(i)
//                if (i == 1) {
//                    child.measure(
//                        MeasureSpec.makeMeasureSpec(w / 3, MeasureSpec.EXACTLY),
//                        MeasureSpec.makeMeasureSpec(h / 3, MeasureSpec.EXACTLY)
//                    )
////                    child.measure(
////                        MeasureSpec.makeMeasureSpec(w / 3, MeasureSpec.EXACTLY),
////                        MeasureSpec.makeMeasureSpec(h / 3, MeasureSpec.EXACTLY)
////                    )
////                    child.measure(widthMeasureSpec, heightMeasureSpec)
//                } else {
//                    child.measure(widthMeasureSpec, heightMeasureSpec)
//                }
//            }
//        }

        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) return
        if (childCount == 1) {
//            getChildAt(0).layout(0, 0, 900, 600)
            getChildAt(0).layout(0, 0, 900, 600)
        }
        if (childCount == 2) {
            Log.e(TAG, "onLayoutsssssssssssss: $childCount")
            getChildAt(0).layout(0, 0, 900, 600)
            getChildAt(1).layout(600, 0, 900, 200)
//            for (i in 1..0) {
//                Log.e(TAG, "onLayout: $i")
//                var child = getChildAt(i)
//                if (i == 0) {
//                    Log.e(TAG, "onLayout: rry000000000000000000")
//                    child.layout(600, 0, 900, 200)
////                    child.layout(l-5, t-100, r, b)
////                    child.layout(660, 200, 1260, 800)
//                } else {
//                    Log.e(TAG, "onLayout: rrrrrrrrrrrrrrrrrrrrrrr")
//                    child.layout(0, 0, 900, 600)
//                }
//
//            }
        }
    }
    fun stopPublishLocalVideo(): Boolean {
        val videoView = ensureUserView(FspManager.selfUserId, null, false)
        if (videoView != null) {
            videoView.closeVideo()
            removeView(videoView)
            return true
        }
        return false
    }
    fun stopPublishLocalAudio(): Boolean {
        val videoView = ensureUserView(FspManager.selfUserId, null, false)
        if (videoView != null) {
            videoView.closeAudio()
            removeView(videoView)
            return true
        }
        return false
    }
    fun startPublishLocalAudio(): Boolean {
        val videoView = ensureUserView(FspManager.selfUserId, null, false)
        if (videoView != null) {
            videoView.openAudio()
            return true
        }
        return false
    }

    fun onEventRemoteLeaveVideo(event: FspEvents.RemoteUserEvent) {
        for (i in 0 until childCount) {
            var child: FspUserView = getChildAt(i) as FspUserView
            if (child.getUserId() === event.userid) {
                removeView(child)
            }
        }
    }

    fun startPublishLocalVideo(isFront: Boolean): Boolean {
        val videoView = ensureUserView(FspManager.selfUserId, null, true)
        if (videoView != null) {
            videoView.openVideo()
            if (FspManager.publishVideo(isFront, videoView.getSurfaceView())) {
                return true
            } else {
                videoView.closeVideo()
                removeView(videoView)
            }
        }
        return false
    }

    fun onEventRemoteVideoAndAudio() {
        for (remote_video_info in FspManager.getRemoteVideos()) {
            var videoView = ensureUserView(remote_video_info.first, remote_video_info.second, false)
            if (videoView == null) {
                Logger.e(
                    "videoView == null: %s, %s",
                    remote_video_info.first,
                    remote_video_info.second
                )
            } else {
                videoView.openVideo()
            }
        }
    }

    fun onEventRemoteVideo(event: FspEvents.RemoteVideoEvent) {
//        val fspManager: FspManager = FspManager.getInstance()
        val videoView = ensureUserView(
            event.userid, event.videoid,
            event.eventtype === FspEngine.REMOTE_VIDEO_PUBLISH_STARTED
        )
        if (videoView == null) {
            Logger.e("videoView == null  userId: %s, videoId : %s", event.userid, event.videoid)
            return
        }
        if (event.eventtype === FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            videoView.openVideo()
            Logger.e("openVideo success , surfaceView : " + videoView.getSurfaceView())
            if (!FspManager.setRemoteVideoRender(
                    event.userid, event.videoid,
                    videoView.getSurfaceView(), videoView.getVideoRenderMode()
                )
            ) {
                videoView.closeVideo()
                (videoView)
            }
        } else if (event.eventtype === FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            videoView.closeVideo()
            removeView(videoView)
        }
    }

    fun onEventRemoteAudio(event: FspEvents.RemoteAudioEvent) {
        val videoView = ensureUserView(
            event.userid,
            null,
            event.eventtype === FspEngine.REMOTE_AUDIO_PUBLISH_STARTED
        )
        if (videoView == null) {
            Logger.e("videoView == null  userId: %s", event.userid)
            return
        }
        if (event.eventtype === FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            videoView.openAudio()
        } else if (event.eventtype === FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            videoView.closeAudio()
            removeView(videoView)
        }
    }

    fun ensureUserView(
        userId: String?,
        videoId: String?,
        isCreateFspUserView: Boolean
    ): FspUserView? {
        var view: FspUserView? = null
        if (childCount > 0) {
            for (i in 0 until childCount) {
                var child = getChildAt(i)
//                if (child!=null && child instanceof)
                val userView = child as FspUserView
                if (videoId == null) {
                    if (FspUtils.isSameText(userId, userView.getUserId())) {
                        view = userView
                        break
                    }
                } else {
                    if (userView.getVideoId() == null) {
                        if (FspUtils.isSameText(userId, userView.getUserId())) {
                            view = userView
                            userView.setVideoId(videoId)
                            break
                        }
                    } else {
                        if (FspUtils.isSameText(
                                userId,
                                userView.getUserId()
                            ) && FspUtils.isSameText(videoId, userView.getVideoId())
                        ) {
                            view = userView
                            break
                        }
                    }
                }
            }
        }
        if (view == null) {
            if (isCreateFspUserView) {
                var userView = FspUserView(context)
                userView.setUserId(userId)
                userView.setVideoId(videoId)
                view = userView
                addView(view)
            }
        }
        return view
    }

    fun removeViewAll() {
        var ss = getChildAt(0)
        removeView(ss)
    }
}