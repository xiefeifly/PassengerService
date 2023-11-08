package com.hst.business

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Pair
import android.view.SurfaceView
import com.hst.bean.EventMsgEntity
import com.hst.business.FspEvents.*
import com.hst.fsp.*
import com.hst.tools.FspToken
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
import java.util.*

object FspManager : IFspEngineEventHandler, IFspSignalingEventHandler,
    IFspWhiteBoardEventHandler {
    private var m_fspEngine: FspEngine? = null
    private var m_haveInitEngine = false
    private var m_strAppConfig = false
    private var m_strAppid: String? = null
    private var m_strAppSecrectKey: String? = null
    private var m_strAppSecrectAddr: String? = null
    private var m_SelfUserId: String? = null
    private var m_SelfGroupId: String? = null
    private var m_LocalVideoState: Int = FspConstants.LOCAL_VIDEO_CLOSED
    var isVideoPublishing = false
        private set
    var isAudioPublishing = false
        private set
    private var m_nBoardNameSuffix = 1
    var currentProfile: VideoProfile = FspConstants.DEFAULT_PROFILE
        private set
    private var m_nVoiceVariant = 0

    //    val remoteAudios = HashSet<String>()
    val m_remoteAudios = HashSet<String>()
    private val m_remoteVideos = HashSet<Pair<String, String>>()

    //    val remoteVideos = HashSet<Pair<String, String>>()
    private val m_groupUserIds: MutableList<String> = LinkedList()

    //boardid to boardinfo
    private val m_whiteBoards: HashMap<String, WhiteBoardInfo> = HashMap<String, WhiteBoardInfo>()

    // --------------------------- get start --------------------------
    val fspEngine: FspEngine?
        get() = m_fspEngine
    var selfUserId: String?
        get() = if (m_SelfUserId == null) "" else m_SelfUserId
        set(selfUserId) {
            m_SelfUserId = selfUserId
        }
    var selfGroupId: String?
        get() = if (m_SelfGroupId == null) "" else m_SelfGroupId
        set(selfGroupId) {
            m_SelfGroupId = selfGroupId
        }

    fun getRemoteVideos(): HashSet<Pair<String, String>> {
        return m_remoteVideos
    }

    fun getRemoteAudios(): HashSet<String> {
        return m_remoteAudios
    }

    fun generNewWhiteBoardName(): String {
        return "AndroidWb_" + m_nBoardNameSuffix++
    }

    val wbInfosCount: Int
        get() = m_whiteBoards.size

    fun getWbName(boardId: String): String {
        for (wb in m_whiteBoards.values) {
            if (wb.boardId == boardId) {
                return wb.name
            }
        }
        return ""
    }

    val wbInfos: Collection<Any>
        get() = m_whiteBoards.values

    fun removeWb(boardId: String) {
        m_whiteBoards.remove(boardId)
    }

    fun HaveUserVideo(userid: String): Boolean {
        for (pair in m_remoteVideos) {
            if (pair.first == userid && pair.second != FspEngine.RESERVED_VIDEOID_SCREENSHARE) {
                return true
            }
        }
        return false
    }

    fun HaveUserScreenShare(userid: String): Boolean {
        for (pair in m_remoteVideos) {
            if (pair.first == userid && pair.second == FspEngine.RESERVED_VIDEOID_SCREENSHARE) {
                return true
            }
        }
        return false
    }

    fun HaveUserAudio(userid: String): Boolean {
        return m_remoteAudios.contains(userid)
    }

    val version: String?
        get() = if (m_fspEngine == null) null else m_fspEngine!!.getVersion()
    val speakerEnergy: Int
        get() = if (m_fspEngine == null) 0 else m_fspEngine!!.getSpeakerEnergy()
    val microphoneEnergy: Int
        get() = if (m_fspEngine == null) 0 else m_fspEngine!!.getMicrophoneEnergy()

    fun getRemoteAudioEnergy(userId: String?): Int {
        return if (m_fspEngine == null) 0 else m_fspEngine!!.getRemoteAudioEnergy(
            userId,
            FspEngine.RESERVED_AUDIOID_MICROPHONE
        )
    }

    fun prepareScreenShare(activity: Activity?, requestCode: Int): Int {
        return m_fspEngine!!.prepareScreenShare(activity, requestCode)
    }

    fun startScreenShare(activity: Activity?, responseCode: Int, data: Intent?): Int {
        return m_fspEngine!!.startScreenShare(activity, responseCode, data, null)
    }

    fun stopScreenShare(): Int {
        return m_fspEngine!!.stopScreenShare()
    }

    // --------------------------- get end --------------------------
    // --------------------------- checkAppConfigChange start --------------------------
//    fun checkAppConfigChange(): Boolean {
//        val appConfig: Boolean = FspPreferenceManager.getInstance().getAppConfig()
//        val strAppid: String = FspPreferenceManager.getInstance().getAppId()
//        val strAppSecrectKey: String = FspPreferenceManager.getInstance().getAppSecret()
//        val strAppSecrectAddr: String = FspPreferenceManager.getInstance().getAppServerAddr()
//        Logger.d("appConfig: $appConfig appId: $strAppid appSecret: $strAppSecrectKey serverAddr: $strAppSecrectAddr")
//        if (!appConfig && (strAppid.isEmpty() || strAppSecrectKey.isEmpty())) {
//            return false
//        }
//        if (m_haveInitEngine) { // has init
//            // default
//            if (m_strAppConfig && !appConfig) { // first default ,current nonDefault
//                destroyEngine()
//            } else if (!m_strAppConfig) { // user
//                if (appConfig) { // first nooDefault ,current default
//                    destroyEngine()
//                } else { // first noDefault ,current noDefault  有一个参数不一致就需要reset，并重新init
//                    if (strAppid != m_strAppid ||
//                        strAppSecrectKey != m_strAppSecrectKey ||
//                        strAppSecrectAddr != m_strAppSecrectAddr
//                    ) {
//                        destroyEngine()
//                    }
//                }
//            }
//        }
//        return true
//    }

    private fun destroyEngine() {
        Logger.d("destroyEngine: ")
        if (m_fspEngine != null) {
            m_fspEngine!!.destroy()
            m_fspEngine = null
            m_haveInitEngine = false
        }
    }

    // --------------------------- checkAppConfigChange end --------------------------
    fun init(
        context: Context,
        appId: String,
        appSecret: String,
        serverAddr: String
    ): Boolean {
        if (m_haveInitEngine) {
            return true
        }
        FspPreferenceManager.init(context)
        val AppConfig: Boolean = FspPreferenceManager.appConfig
        //
//        if (AppConfig) {
//            appId = FspConstants.DEFAULT_APP_ID;
//            appSecret = FspConstants.DEFAULT_APP_SECRET;
//            serverAddr = FspConstants.DEFAULT_APP_ADDRESS;
//
//        } else {
//            appId = FspPreferenceManager.getInstance().getAppId();
//            appSecret = FspPreferenceManager.getInstance().getAppSecret();
//            serverAddr = FspPreferenceManager.getInstance().getAppServerAddr();
//        }
//        appId = Config.DEFAULT_APP_ID
//        appSecret = Config.DEFAULT_APP_SECRET
//        serverAddr = Config.DEFAULT_APP_ADDRESS
        Logger.e("appId: $appId appSecret: $appSecret serverAddr: $serverAddr")
        val configure = FspEngineConfigure()
        configure.serverAddr = serverAddr
        configure.hardwareEncNumber = 1
        configure.hardwareDecNumber = 0
        configure.recvVoiceVariant = FspPreferenceManager.isRecvVoiceVariant
//        configure.recvVoiceVariant = 1
        if (m_fspEngine == null) {
            m_fspEngine = FspEngine.create(context, appId, configure, this)
        }
        val init: Int = m_fspEngine!!.init()
        Logger.e("init result = $init")
        Logger.d("init result = $init")
        val result = init == FspEngine.ERR_OK
        Logger.e("init is success : $result")
        Logger.d("init is success : $result")
        return if (result) {
            m_haveInitEngine = true
            m_strAppConfig = AppConfig
//            m_strAppConfig = true
            m_strAppid = appId
            m_strAppSecrectKey = appSecret
            m_strAppSecrectAddr = serverAddr
            m_fspEngine!!.getFspSignaling().addEventHandler(this)
            m_fspEngine!!.getFspBoard().setEventHandler(this)
            true
        } else {
            destroyEngine()
            false
        }
    }

    fun clear() {
        m_remoteAudios.clear()
        m_remoteVideos.clear()
        m_groupUserIds.clear()
    }

    fun destroy() {
        clear()
        if (m_fspEngine != null) {
            m_fspEngine!!.getFspSignaling().removeEventHandler(this)
            m_fspEngine!!.leaveGroup()
            m_fspEngine!!.destroy()
            m_fspEngine = null
            m_haveInitEngine = false
        }
    }

    fun login(userId: String?, customName: String?): Boolean {
        if (!m_haveInitEngine) {
            return false
        }
        val isForceLogin: Boolean = FspPreferenceManager.isForceLogin
        val token: String = FspToken.build(m_strAppid, m_strAppSecrectKey, userId)
        val result =
            m_fspEngine!!.login(token, userId, isForceLogin, customName) == FspEngine.ERR_OK
        if (result) {
            m_SelfUserId = userId
        }
        Logger.d("login is success : $result")
        return result
    }

    fun loginOut(): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val result = m_fspEngine!!.loginOut() == FspEngine.ERR_OK
        if (result) {
            m_SelfUserId = null
        }
        Logger.d("loginOut is success : $result")
        return result
    }

    fun leaveGroup(): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val result = m_fspEngine!!.leaveGroup() == FspEngine.ERR_OK
        Logger.d("leaveGroup is success : $result")
        if (result) {
            clear()
        }
        return result
    }

    fun joinGroup(groupId: String?): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val result = m_fspEngine!!.joinGroup(groupId) == FspEngine.ERR_OK
        if (result) {
            m_SelfGroupId = groupId
        }
        Logger.d("joinGroup is success : $result")
        return result
    }

    fun publishVideo(isFrontCamera: Boolean, previewRender: SurfaceView?): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        var fspErrCode: Int = m_fspEngine!!.setVideoProfile(currentProfile)
        if (fspErrCode != FspEngine.ERR_OK) {
            return false
        }
        Logger.d("setVideoProfile is success : ")
        fspErrCode = m_fspEngine!!.startPreviewVideo(previewRender)
        if (fspErrCode != FspEngine.ERR_OK) {
            return false
        }
        Logger.d("startPreviewVideo is success : ")
        if (m_fspEngine!!.isFrontCamera() != isFrontCamera) {
            m_fspEngine!!.switchCamera()
        }
        m_LocalVideoState = if (isFrontCamera) {
            FspConstants.LOCAL_VIDEO_FRONT_PUBLISHED
        } else {
            FspConstants.LOCAL_VIDEO_BACK_PUBLISHED
        }
        fspErrCode = m_fspEngine!!.startPublishVideo()
        if (fspErrCode != FspEngine.ERR_OK) {
            Logger.d("startPreviewVideo is failed : $fspErrCode")
            return false
        }
        isVideoPublishing = true
        return true
    }

    fun switchCamera() {
        if (m_fspEngine == null) {
            return
        }
        m_fspEngine!!.switchCamera()
        if (m_LocalVideoState != FspConstants.LOCAL_VIDEO_CLOSED) {
            m_LocalVideoState = if (m_fspEngine!!.isFrontCamera()) {
                FspConstants.LOCAL_VIDEO_FRONT_PUBLISHED
            } else {
                FspConstants.LOCAL_VIDEO_BACK_PUBLISHED
            }
        }
    }

    fun stopVideoPublish(): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        m_fspEngine!!.stopPublishVideo()
        m_fspEngine!!.stopPreviewVideo()
        m_LocalVideoState = FspConstants.LOCAL_VIDEO_CLOSED
        isVideoPublishing = false
        return true
    }

    fun getVideoStats(userid: String?, videoid: String?): VideoStatsInfo? {
        return if (m_fspEngine != null) m_fspEngine!!.getVideoStats(userid, videoid) else null
    }

    /**
     * 当前本地视频状态
     *
     * @return LOCAL_VIDEO_CLOSED or LOCAL_VIDEO_BACK_PUBLISHED or LOCAL_VIDEO_FRONT_PUBLISHED
     */
    fun currentVideState(): Int {
        return m_LocalVideoState
    }

    fun setRemoteVideoRender(
        userId: String?, videoId: String?,
        renderView: SurfaceView?, renderMode: Int
    ): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val fspErrCode: Int =
            m_fspEngine!!.setRemoteVideoRender(userId, videoId, renderView, renderMode)
        return fspErrCode == FspEngine.ERR_OK
    }

    fun startPublishAudio(): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val fspErrCode: Int = m_fspEngine!!.startPublishAudio()
        if (fspErrCode == FspEngine.ERR_OK) {
            isAudioPublishing = true
        }
        return fspErrCode == FspEngine.ERR_OK
    }

    fun stopPublishAudio(): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val fspErrCode: Int = m_fspEngine!!.stopPublishAudio()
        if (fspErrCode == FspEngine.ERR_OK) {
            isAudioPublishing = false
        }
        return fspErrCode == FspEngine.ERR_OK
    }

    val groupUsers: List<String>
        get() = m_groupUserIds

    fun setProfile(profile: VideoProfile) {
        currentProfile = profile
        if (m_fspEngine == null) {
            return
        }
        m_fspEngine!!.setVideoProfile(currentProfile)
    }

    var voiceVariant: Int
        get() = m_nVoiceVariant
        set(value) {
            m_nVoiceVariant = value
            if (m_fspEngine == null) return
            m_fspEngine!!.setAudioParam(FspEngine.AUDIOPARAM_VOICE_VARIANT, value)
        }

    // ---------------------- signing start ------------------------
    fun refreshAllUserStatus(): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val errCode: Int = m_fspEngine!!.getFspSignaling().refreshAllUserStatus()
        return errCode == FspEngine.ERR_OK
    }

    fun invite(userId: Array<String?>?, groupId: String?, msg: String?): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val localInvite: LocalInvite = m_fspEngine!!.getFspSignaling().invite(userId, groupId, msg)
        Logger.d("localInvite.getErrCode(): " + localInvite.getErrCode())
        return localInvite.getErrCode() == FspEngine.ERR_OK
    }

    fun acceptInvite(inviterUserId: String?, inviteId: Int): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val errCode: Int = m_fspEngine!!.getFspSignaling().acceptInvite(inviterUserId, inviteId)
        return errCode == FspEngine.ERR_OK
    }

    fun rejectInvite(inviterUserId: String?, inviteId: Int): Boolean {
        return if (m_fspEngine == null) {
            false
        } else m_fspEngine!!.getFspSignaling()
            .rejectInvite(inviterUserId, inviteId) == FspEngine.ERR_OK
    }

    fun sendUserMsg(userId: String, msg: String?): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val errCode: Int = m_fspEngine!!.getFspSignaling().sendUserMsg(userId, msg)
        Logger.d("sendUserMsg: userId: $userId errCode: $errCode")
        return errCode == FspEngine.ERR_OK
    }

    fun sendGroupMsg(msg: String?): Boolean {
        if (m_fspEngine == null) {
            return false
        }
        val errCode: Int = m_fspEngine!!.getFspSignaling().sendGroupMsg(msg)
        Logger.d("sendGroupMsg:  errCode: $errCode")
        return errCode == FspEngine.ERR_OK
    }

    // ---------------------- signing end ------------------------
    // -----------------   IFspEngineEventHandler  start  ------------------------
    override fun onLoginResult(errCode: Int) {
        Logger.d("errCode:$errCode")
        if (errCode != FspEngine.ERR_OK) {
            selfUserId = null
        }
        EventBus.getDefault()
            .post(
                LoginResult(
                    errCode == FspEngine.ERR_OK,
                    FspFailMsgUtils.getErrorDesc(errCode)
                )
            )
    }

    override fun onJoinGroupResult(errCode: Int) {
        Logger.d("errCode:$errCode")
        if (errCode != FspEngine.ERR_OK) {
            selfGroupId = null
        }
        EventBus.getDefault().post(
            JoinGroupResult(
                errCode == FspEngine.ERR_OK,
                FspFailMsgUtils.getErrorDesc(errCode)
            )
        )
    }

    override fun onLeaveGroupResult(errCode: Int) {
        Logger.d("onLeaveGroupResult:$errCode")
        if (errCode == FspEngine.ERR_OK) {
            selfGroupId = null
        }
        EventBus.getDefault().post(
            LeaveGroupResult(
                errCode == FspEngine.ERR_OK,
                FspFailMsgUtils.getErrorDesc(errCode)
            )
        )
    }

    override fun onFspEvent(eventType: Int, errCode: Int) {
        Logger.d("eventType:$eventType, errCode:$errCode")
        EventBus.getDefault().post(EventMsgEntity("", FspFailMsgUtils.getFspEventDesc(eventType)))
    }

    override fun onRemoteVideoEvent(userId: String, videoId: String, eventType: Int) {
        Logger.e("==================================userId:$userId videoId:$videoId eventType:$eventType")
        if (eventType == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            m_remoteVideos.add(Pair(userId, videoId))
        } else if (eventType == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            m_remoteVideos.remove(Pair(userId, videoId))
        }
        EventBus.getDefault().post(RemoteVideoEvent(userId, videoId, eventType))
    }

    override fun onRemoteAudioEvent(userId: String, audioId: String, eventType: Int) {
        Logger.e("=================================userId:$userId eventType:$eventType")

        if (eventType == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            m_remoteAudios.add(userId)
        } else if (eventType == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            m_remoteAudios.remove(userId)
        }
        EventBus.getDefault().post(RemoteAudioEvent(userId, eventType))
    }

    override fun onGroupUsersRefreshed(userIds: Array<String>) {
        Logger.d("userIds:" + Arrays.toString(userIds))
        m_groupUserIds.clear()
        m_groupUserIds.addAll(Arrays.asList(*userIds))
    }

    override fun onRemoteUserEvent(userId: String, eventType: Int) {
        Logger.d("userId:$userId eventType:$eventType")
        EventBus.getDefault().post(RemoteUserEvent(userId, eventType))
        if (eventType == FspEngine.REMOTE_USER_JOIN_GROUP) {
            if (!m_groupUserIds.contains(userId)) {
                m_groupUserIds.add(userId)
            }
        } else if (eventType == FspEngine.REMOTE_USER_LEAVE_GROUP) {
            m_groupUserIds.remove(userId)
        }

    }

    // -----------------   IFspEngineEventHandler  end  ------------------------
    // -----------------   IFspSignalingEventHandler  start  ------------------------
    override fun onRefreshUserStatusFinished(
        errCode: Int,
        requestId: Int,
        infos: Array<FspUserInfo>
    ) {
        EventBus.getDefault().post(
            RefreshUserStatusFinished(
                errCode == FspEngine.ERR_OK,
                requestId,
                infos,
                FspFailMsgUtils.getErrorDesc(errCode)
            )
        )
    }

    override fun onUserStatusChange(changedUserInfo: FspUserInfo) {}
    override fun onInviteIncome(
        inviterUserId: String,
        inviteId: Int,
        groupId: String,
        desc: String
    ) {
        Logger.d("inviterUserId:$inviterUserId inviteId:$inviteId groupId:$groupId desc:$desc")
        EventBus.getDefault().post(InviteIncome(inviterUserId, inviteId, groupId, desc))
    }

    override fun onInviteCancled(inviterUserId: String, inviteId: Int, reason: Int) {
        Logger.d("inviterUserId:$inviterUserId inviteId:$inviteId reason:$reason")
    }

    override fun onInviteAccepted(remoteUserId: String, inviteId: Int) {
        Logger.d("remoteUserId:$remoteUserId inviteId:$inviteId")
        EventBus.getDefault().post(EventMsgEntity(remoteUserId, "接受邀请"))
    }

    override fun onInviteRejected(remoteUserId: String, inviteId: Int) {
        Logger.d("remoteUserId:$remoteUserId inviteId:$inviteId")
        EventBus.getDefault().post(EventMsgEntity(remoteUserId, "拒绝邀请"))
    }

    override fun onUserMsgIncome(srcUserId: String, msgId: Int, msg: String) {
        Logger.d("srcUserId:$srcUserId msgId:$msgId msg:$msg")
        EventBus.getDefault().post(ChatMsgItem(false, srcUserId, msgId, msg, false))
    }

    override fun onGroupMsgIncome(srcUserId: String, msgId: Int, msg: String) {
        Logger.d("srcUserId:$srcUserId msgId:$msgId msg:$msg")
        EventBus.getDefault().post(ChatMsgItem(true, srcUserId, msgId, msg, false))
    }

    // -----------------   IFspSignalingEventHandler  end  ------------------------
    // -----------------   IFspWhiteBoardEventHandler  start  ------------------------
    override fun onWhiteBoardCreateResult(boardId: String, boardName: String, errCode: Int) {
        Logger.d("boardId:$boardId boardName:$boardName errCode:$errCode")
    }

    override fun onWhiteBoardPublishStart(boardId: String, boardName: String) {
        Logger.d("boardId:$boardId boardName:$boardName")
        val info = WhiteBoardInfo()
        info.name = boardName
        info.boardId = boardId
        m_whiteBoards[boardId] = info
        EventBus.getDefault().post(WhiteBoardPublishEvent(false, boardId, boardName))
    }

    override fun onWhiteBoardPublishStop(boardId: String) {
        Logger.d("boardId:$boardId")
        m_whiteBoards.remove(boardId)
        EventBus.getDefault().post(WhiteBoardPublishEvent(true, boardId, ""))
    }

    override fun onWhiteBoardInfoUpdate(info: WhiteBoardInfo) {
        Logger.d("boardId:" + info.boardId + ", w=" + info.width + ", height=" + info.height + ", nPages=" + info.pages)
        m_whiteBoards[info.boardId] = info
        EventBus.getDefault().post(WhiteBoardInfoUpdateEvent(info.boardId, info))
    } // -----------------   IFspWhiteBoardEventHandler  end  ------------------------

}