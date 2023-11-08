package com.passenger.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hst.bean.EventMsgEntity
import com.hst.business.FspEvents
import com.hst.business.FspManager
import com.hst.business.FspManager.joinGroup
import com.hst.fsp.FspEngine
import com.hst.widget.FspUserViewGroup
import com.orhanobut.logger.Logger
import com.passenger.R
import com.passenger.contents.Config
import com.passenger.databinding.ActivityInviteincomeBinding
import com.passenger.utils.ContentUtil
import com.passenger.utils.InviteUtils
import kotlinx.coroutines.MainScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InviteIncomeActivity : AppCompatActivity() {
    var mIncometype = -1

    //    val mBinding: ActivityInviteincomeBinding by lazy {
//        ActivityInviteincomeBinding.inflate(layoutInflater)
//    }
    lateinit var mBinding: ActivityInviteincomeBinding
    var msg = "0"
    lateinit var mFspUserViewGroup: FspUserViewGroup
    lateinit var m_UserViewGroup: LinearLayout
    var job = MainScope()
    lateinit var inviteModel: InviteModel
    var mtypeIncomes = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView<ActivityInviteincomeBinding>(
            this,
            R.layout.activity_inviteincome
        )
        EventBus.getDefault().register(this)
        inviteModel = ViewModelProvider(this)[InviteModel::class.java]
        mBinding.apply {
            this.lifecycleOwner = this@InviteIncomeActivity
            this.invitesModels = inviteModel
            titles.weekTimes.text = InviteUtils.getWeek()
            titles.dateTimes.text = InviteUtils.getDate()
        }

        mFspUserViewGroup = findViewById<FspUserViewGroup>(R.id.m_fspUserViewGroup)
        m_UserViewGroup = findViewById<LinearLayout>(R.id.m_UserViewGroup)
        var msgdata = InviteUtils.getMsg(Config.ANDROID_NAME, Config.PC_NAME)
        val userIdData = arrayOfNulls<String>(1)
        userIdData[0] = Config.PC_NAME
        val intent = intent
        if (intent.hasExtra("IM_INFO")) {
            mIncometype = intent.getIntExtra("Incometype", -1);
        }
        when (mIncometype) {
            0 -> {
                FspManager.sendUserMsg(Config.PC_NAME, "PcOrAndroidMsgVideo${msgdata}")
//                msg = "2"
                msg = "2~!!~yitiji~!!~" + ContentUtil.getDeviceSN()
            }
            1 -> {
                FspManager.sendUserMsg(Config.PC_NAME, "PcOrAndroidMsgAudio${msgdata}")
//                msg = "1"
                msg = "1~!!~yitiji~!!~" + ContentUtil.getDeviceSN()
            }
        }
        var groupId: String = "${System.currentTimeMillis()}"
        FspManager.invite(userIdData, groupId, msg)
        joinGroup(groupId)
        if (mIncometype == 0) {
            mFspUserViewGroup.setVisibility(View.VISIBLE)
            m_UserViewGroup.setVisibility(View.GONE)
            FspManager.startPublishAudio()
            FspManager.getRemoteAudios()

            mFspUserViewGroup.startPublishLocalVideo(true)
//            mFspUserViewGroup.onEventRemoteVideoAndAudio()
        } else {
            FspManager.startPublishAudio()
            FspManager.getRemoteAudios()
            m_UserViewGroup.visibility = View.VISIBLE
            mFspUserViewGroup.visibility = View.GONE
            //                    String userId = AdminInformationManager.getInstance().getUserName();
//                    String headUrl = HttpConfigs.getiPNginxServer() + PersonManager.getInstance().getHeadUrl(userId);
//                    Logger.e("---------------------------" + headUrl);
//            m_UserViewGroup.startPublishLocalAudio(headUrl)
//            setIMHST(groupId, "PcOrAndroidMsgAudio")
//            m_UserViewGroup.startPublishLocalAudio()
        }
        isVideoOrAudio(mIncometype)
//        }
        inviteModel.startTimer()
        inviteModel.dataliveData.observe(this) { value ->
//            MainScope().launch(Dispatchers.Main) {
            mBinding.titles.time.text = value
//            }

        }
        inviteModel.liveData.observe(this) { value ->
            if (value) {
                mBinding.microTv.text = "麦克风打开"
                mBinding.microImage.setBackgroundResource(R.mipmap.ic_micro_open)
            } else {
                mBinding.microTv.text = "麦克风关闭"
                mBinding.microImage.setBackgroundResource(R.mipmap.ic_micro_colse)
            }
        }
        mBinding.hangUp.setOnClickListener {
            if (mIncometype == 0) {
                closeVideo()
            } else if (mIncometype == 1) {
                closeAudio()
            }
        }
        mBinding.titles.back.setOnClickListener {
            if (mIncometype == 0) {
                closeVideo()
            } else if (mIncometype == 1) {
                closeAudio()
            }
        }
        hideBottomMenu()
    }

    override fun onDestroy() {
        inviteModel.timer.cancel()
        super.onDestroy()
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun setDate() {
//        Thread(Runnable {
//            try {
//                while (true) {
//                    val currentTime = InviteUtils.getCurrentTime()
//                    runOnUiThread {
//                        Log.e("TAG", "setDate: $currentTime" )
//                        mBinding.titles.dateTimes.text = currentTime }
//                    Thread.sleep(1000)
//                }
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }).start()
//    }

    private fun isVideoOrAudio(incometype: Int) {

    }

    fun isVideoOrAudios() {

    }

    fun closeVideo() {
        mFspUserViewGroup.stopPublishLocalVideo()
        mFspUserViewGroup.stopPublishLocalAudio()
        FspManager.stopVideoPublish()
        FspManager.stopPublishAudio()
        FspManager.leaveGroup()
        FspManager.sendUserMsg(Config.PC_NAME, "hangUp")
//        hangUp()
//        stopMediaPlay()
        finish()
    }

    fun closeAudio() {
        FspManager.stopPublishAudio()
        FspManager.leaveGroup()
        FspManager.sendUserMsg(Config.PC_NAME, "hangUp")
//        stopMediaPlay()
        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRemoteUserEvent(event: FspEvents.RemoteUserEvent) {
        Logger.e("onRemoteUserEvent: " + event.userid.toString() + "===0加入===" + event.eventtype)
        if (event.eventtype === FspEngine.REMOTE_USER_LEAVE_GROUP) {
//            if (!mtypeIncomes.isEmpty()) {
//                if (mtypeIncomes == "PcOrAndroidMsgAudio") {
//                    m_UserViewGroup.onEventRemoteLeaveAudio(event)
//                } else if (mtypeIncomes == "PcOrAndroidMsgVideo") {
//                    m_fspUserViewGroup.onEventRemoteLeaveVideo(event)
//                    val currentChildCount: Int = m_fspUserViewGroup.getCurrentChildCount()
//                    Logger.e("currentChildCount: $currentChildCount")
//                    if (currentChildCount <= 1) {
//                        closeVideo()
//                    }
//                }
//            }
            if (mIncometype != -1) {
                if (mIncometype == 1) {
//                    mFspUserViewGroup.onEventRemoteLeaveAudio(event)
                } else if (mIncometype == 0) {
//                    m_fspUserViewGroup.onEventRemoteLeaveVideo(event)
//                    val currentChildCount: Int = m_fspUserViewGroup.getCurrentChildCount()
//                    Logger.e("currentChildCount: $currentChildCount")
//                    if (currentChildCount <= 1) {
//                        closeVideo()
//                    }
                }
            }
        }
//        if (event.eventtype == FspEngine.REMOTE_USER_JOIN_GROUP) {
//            m_eventMsgLists.push(new EventMsgEntity(event.userid, "加入了组"));
//        }else if (event.eventtype == FspEngine.REMOTE_USER_LEAVE_GROUP) {
//            m_eventMsgLists.push(new EventMsgEntity(event.userid, "离开了组"));
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventRemoteVideo(event: FspEvents.RemoteVideoEvent) {
//        pauseMediaPlay()
        Logger.e("onEventRemoteVideo:======= " + event.userid)
//        call_iv_avator.setVisibility(View.GONE)
//        callTvInviterUserId.setVisibility(View.GONE)
//        if (!mtypeIncomes.isEmpty()) {
//            if (mtypeIncomes == "PcOrAndroidMsgVideo") {
//                mFspUserViewGroup.onEventRemoteVideo(event)
//            } else if (mtypeIncomes == "PcOrAndroidMsgAudio") {
//            }
//        }
        if (mIncometype != -1) {
            if (mIncometype == 0) {
                mFspUserViewGroup.onEventRemoteVideo(event)
//                FspManager.stopVideoPublish()
//                mFspUserViewGroup.removeViewAll()
//                FspManager.startPublishAudio()
//                mFspUserViewGroup.startPublishLocalVideo(true)
//                mFspUserViewGroup.onEventRemoteVideoAndAudio()
            } else if (mIncometype == 1) {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventRemoteAudio(event: FspEvents.RemoteAudioEvent) {
        Logger.e("onEventRemoteAudio: =============" + event.userid)
//        pauseMediaPlay()
//        call_iv_avator.setVisibility(View.GONE)
//        callTvInviterUserId.setVisibility(View.GONE)
//        if (!mtypeIncomes.isEmpty()) {
//            if (mtypeIncomes == "PcOrAndroidMsgVideo") {
////                m_fspUserViewGroup.onEventRemoteAudio(event);
//                m_fspUserViewGroup.onEventRemoteAudio(event)
//            } else if (mtypeIncomes == "PcOrAndroidMsgAudio") {
//                if (event.eventtype === FspEngine.REMOTE_USER_JOIN_GROUP) {
//                    val headUrls: String =
//                        HttpConfigs.getiPNginxServer() + PersonManager.getInstance()
//                            .getHeadUrl(event.userid)
//                    Logger.e("---------------------------$headUrls")
//                    m_UserViewGroup.onEventRemoteAudio(event, headUrls)
//                }
//            }
//        }
        if (mIncometype != -1) {
            if (mIncometype == 0) {
                mFspUserViewGroup.onEventRemoteAudio(event)
            } else if (mIncometype == 1) {
                if (event.eventtype === FspEngine.REMOTE_USER_JOIN_GROUP) {
//                    val headUrls: String =
//                        HttpConfigs.getiPNginxServer() + PersonManager.getInstance()
//                            .getHeadUrl(event.userid)
//                    Logger.e("---------------------------$headUrls")
//                    m_UserViewGroup.onEventRemoteAudio(event, headUrls)
                } else if (event.eventtype === FspEngine.REMOTE_USER_LEAVE_GROUP) {
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMsgIncome(eventMsgEntity: EventMsgEntity) {
        Logger.d("onEventMsgIncome: " + eventMsgEntity.msg)
        if (eventMsgEntity.msg.equals("拒绝邀请")) {
//            ToastUtil.show(
//                applicationContext,
//                eventMsgEntity.getUserId() + eventMsgEntity.getMsg(),
//                ToastUtil.MID
//            )
            //            finish();
        } else if (eventMsgEntity.msg.equals("接受邀请")) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMsgIncome(msgItem: FspEvents.ChatMsgItem) {
        Logger.e("onMsgIncome1==============: " + msgItem.msg)
        if (msgItem.msg.equals("hangUp")) {
            if (mtypeIncomes == "PcOrAndroidMsgVideo" || mIncometype == 0) {
//                val connectionPerson: String = PersonManager.getInstance().getConnectionPerson()
//                val currentChildCount: Int = m_fspUserViewGroup.getCurrentChildCount()
//                if (currentChildCount <= 1) {
                closeVideo()
//                    return
//                }
//                val i: Int = countSubstring(connectionPerson, "~!!~")
//                if (i <= 2) {
//                    closeVideo()
//                }
            } else if (mtypeIncomes == "PcOrAndroidMsgAudio" || mIncometype == 1) {
//                val currentChildCount: Int = m_UserViewGroup.getCurrentChildCount()
//                if (currentChildCount <= 1) {
                closeAudio()
//                }
            }
        }
    }

    var exitTime: Long = 0
    override fun onBackPressed() {
//        super.onBackPressed()
        exitTime = if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(
                applicationContext, "再按一次挂断",
                Toast.LENGTH_SHORT
            ).show()
            System.currentTimeMillis()
        } else {
            if (mtypeIncomes == "PcOrAndroidMsgVideo" || mIncometype == 0) {
                closeVideo()
            } else if (mtypeIncomes == "PcOrAndroidMsgAudio" || mIncometype == 1) {
                closeAudio()
            }
            0
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