package com.passenger.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.hsts.base.BaseActivity;
import com.example.hsts.bean.EventMsgEntity;
import com.example.hsts.bean.MainFinishEntity;
import com.example.hsts.bean.SettingFinishEntity;
import com.example.hsts.business.FspEvents;
import com.example.hsts.business.FspManager;
import com.example.hsts.ui.view.FspUserViewGroup;
import com.example.hsts.ui.view.UserViewGroup;
import com.hst.fsp.FspEngine;
import com.orhanobut.logger.Logger;
import com.passenger.R;
import com.passenger.contents.Configs;
import com.passenger.utils.ContentUtil;
import com.passenger.utils.InviteUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 被呼叫
 */
public class InviteIncomeActivity extends BaseActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    LinearLayout microLinearLayout;
    LinearLayout UserViewGroups;
    TextView title;
    TextView time;
    TextView back;
    TextView weekTimes;
    TextView dateTimes;
    LinearLayout call_tv_reject;
    FspUserViewGroup m_fspUserViewGroup;
    UserViewGroup m_UserViewGroup;
    ImageView microImage;
    TextView microTv;

    private FspEvents.InviteIncome m_inviteIncome;
    private int mIncometype = -1;
    private String mtypeIncomes = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_call;
    }

    @Override
    protected void initWindowFlags() {
    }

    String headUrl;
    Timer timer;

    public void startTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time.setText(InviteUtils.INSTANCE.getCurrentTime());
                    }
                });

            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        weekTimes = findViewById(R.id.weekTimes);
        dateTimes = findViewById(R.id.dateTimes);

        weekTimes.setText(InviteUtils.INSTANCE.getWeek());
        dateTimes.setText(InviteUtils.INSTANCE.getDate());

        call_tv_reject = findViewById(R.id.callTvReject);
        microLinearLayout = findViewById(R.id.microLinearLayout);

        m_fspUserViewGroup = findViewById(R.id.fsp_user_view_group);
        m_UserViewGroup = findViewById(R.id.user_view_group);
        UserViewGroups = findViewById(R.id.m_UserViewGroup);
        microImage = findViewById(R.id.microImage);
        microTv = findViewById(R.id.microTv);

//        AudioManagerUtils.getInstance().closeSpeaker();
        m_fspUserViewGroup.setVisibility(View.VISIBLE);
        startMediaPlay();

        Intent intent = getIntent();
        if (intent.hasExtra(InviteIncomeActivity.class.getSimpleName())) {
            m_inviteIncome = intent.getParcelableExtra(InviteIncomeActivity.class.getSimpleName());
            mtypeIncomes = intent.getStringExtra("typeIncome");
//            callTvInviterUserId.setText(m_inviteIncome.inviterUserId);
//            acceptVideoOrAudio(mtypeIncomes);
        }
        if (intent.hasExtra("IM_INFO")) {
            mIncometype = intent.getIntExtra("Incometype", -1);
            String msgdata = InviteUtils.INSTANCE.getMsg(Configs.ANDROID_NAME, Configs.PC_NAME);
            String[] userIdData = new String[1];
            userIdData[0] = Configs.PC_NAME;
            String groupId = String.valueOf(System.currentTimeMillis());
            Logger.e("userIdData.length:============== " + userIdData.length);
            String msg = "";
            if (mIncometype == 0) {
                FspManager.getInstance().sendUserMsg(Configs.PC_NAME, "PcOrAndroidMsgVideo" + msgdata);
                msg = "2~!!~yitiji~!!~" + ContentUtil.INSTANCE.getDeviceSN();
            } else if (mIncometype == 1) {
                FspManager.getInstance().sendUserMsg(Configs.PC_NAME, "PcOrAndroidMsgAudio" + msgdata);
                msg = "1~!!~yitiji~!!~" + ContentUtil.INSTANCE.getDeviceSN();
            }
            FspManager.getInstance().invite(userIdData, groupId, msg);
            joinGroup(groupId);
            if (mIncometype == 0) {
                m_fspUserViewGroup.setVisibility(View.VISIBLE);
                m_UserViewGroup.setVisibility(View.GONE);
                UserViewGroups.setVisibility(View.GONE);
                FspManager.getInstance().startPublishAudio();
                FspManager.getInstance().getRemoteAudios();
                m_fspUserViewGroup.startPublishLocalVideo(true);
                m_fspUserViewGroup.onEventRemoteVideoAndAudio();
            } else {
                FspManager.getInstance().startPublishAudio();
                FspManager.getInstance().getRemoteAudios();
                m_UserViewGroup.setVisibility(View.VISIBLE);
                UserViewGroups.setVisibility(View.VISIBLE);
                m_fspUserViewGroup.setVisibility(View.GONE);
////                    String userId = AdminInformationManager.getInstance().getUserName();
////                    String headUrl = HttpConfigs.getiPNginxServer() + PersonManager.getInstance().getHeadUrl(userId);
////                    Logger.e("---------------------------" + headUrl);
//                    m_UserViewGroup.startPublishLocalAudio(headUrl);
//                    setIMHST(groupId, "PcOrAndroidMsgAudio");
////                    m_fspUserViewGroup.startPublishLocalAudio();
            }
//                isVideoOrAudio(mIncometype);
//            }
        }
        startTimer();
    }

    boolean isSelected = true;

    @Override
    protected void listener() {
        back.setOnClickListener((View) -> {
            if (mIncometype == 0) {
                closeVideo();
            } else if (mIncometype == 1) {
                closeAudio();
            }
        });
        call_tv_reject.setOnClickListener((View) -> {
            if (mIncometype == 0) {
                closeVideo();
            } else if (mIncometype == 1) {
                closeAudio();
            }
        });

        microLinearLayout.setOnClickListener((View) -> {
            if (isSelected) {
                isSelected = false;
                FspManager.getInstance().stopPublishAudio();
                microTv.setText("麦克风关闭");
                microImage.setBackgroundResource(com.example.hsts.R.drawable.ic_micro_colse);
            } else {
                isSelected = true;
                FspManager.getInstance().startPublishAudio();
                microTv.setText("麦克风打开");
                microImage.setBackgroundResource(com.example.hsts.R.drawable.ic_micro_open);
            }
        });

    }


//
//    @OnClick(R.id.call_tv_accept)
//    public void onClickTvAccept() {
//        pauseMediaPlay();
//        Logger.e("接受-------------");
//        call_tv_reject.setVisibility(View.GONE);
//        call_tv_accept.setVisibility(View.GONE);
//        if (m_inviteIncome == null) {
//            return;
//        }
//        if (!acceptInvite_result) {
//            acceptInvite_result = FspManager.getInstance().acceptInvite(m_inviteIncome.inviterUserId, m_inviteIncome.inviteId);
//        }
//        if (acceptInvite_result) {
//            chronometer.setBase(SystemClock.elapsedRealtime() - times);
//            chronometer.start();
//            if ("".equals(FspManager.getInstance().getSelfGroupId())) {
//                // 没有组就加入组
//                joinGroup(m_inviteIncome.groupId);
//                Logger.d("没有组");
//            } else {
//                Logger.d("已经有组离开组");
//                // 有组就离开组
//                leaveGroup();
//            }
//        }
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoteUserEvent(FspEvents.RemoteUserEvent event) {
        Logger.e("onRemoteUserEvent: " + event.userid + "===0加入===" + event.eventtype);
        if (event.eventtype == FspEngine.REMOTE_USER_LEAVE_GROUP) {
            if (!mtypeIncomes.isEmpty()) {
                if (mtypeIncomes.equals("PcOrAndroidMsgAudio")) {
                    m_UserViewGroup.onEventRemoteLeaveAudio(event);
                } else if (mtypeIncomes.equals("PcOrAndroidMsgVideo")) {
                    m_fspUserViewGroup.onEventRemoteLeaveVideo(event);
                    int currentChildCount = m_fspUserViewGroup.getCurrentChildCount();
                    Logger.e("currentChildCount: " + currentChildCount);
                    if (currentChildCount <= 1) {
                        closeVideo();
                    }
                }

            }
            if (mIncometype != -1) {
                if (mIncometype == 1) {
                    m_UserViewGroup.onEventRemoteLeaveAudio(event);
                } else if (mIncometype == 0) {
                    m_fspUserViewGroup.onEventRemoteLeaveVideo(event);
                    int currentChildCount = m_fspUserViewGroup.getCurrentChildCount();
                    Logger.e("currentChildCount: " + currentChildCount);
                    if (currentChildCount <= 1) {
                        closeVideo();
                    }
                }
            }


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventJoinGroupResult(FspEvents.JoinGroupResult result) {
        dismissLoading();
        Logger.e("JoinGroupResult===================" + result.isSuccess);
        if (result.isSuccess) {
            openAndAccept(mtypeIncomes);
        } else {
            setErrorLoading(result.desc);
        }
    }

    public void openAndAccept(String type) {
        if (type.isEmpty()) {
            return;
        }
        if (type.equals("PcOrAndroidMsgVideo")) {
            FspManager.getInstance().startPublishAudio();
            FspManager.getInstance().getRemoteAudios();
            m_fspUserViewGroup.startPublishLocalVideo(true);
            m_fspUserViewGroup.onEventRemoteVideoAndAudio();
        } else if (type.equals("PcOrAndroidMsgAudio")) {
            FspManager.getInstance().startPublishAudio();
            FspManager.getInstance().getRemoteAudios();
            m_UserViewGroup.startPublishLocalAudio(headUrl);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLeaveGroupResult(FspEvents.LeaveGroupResult result) {
        Logger.e("LeaveGroupResult===================" + result.isSuccess);
        if (result.isSuccess) {
            LeaveGroupResultSuccess();
        } else {
            Toast.makeText(getApplicationContext(), "离开组失败",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void LeaveGroupResultSuccess() {
        // 离开了组就离开了之前的界面
        EventBus.getDefault().post(new SettingFinishEntity(true));
        EventBus.getDefault().post(new MainFinishEntity(true));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteVideo(FspEvents.RemoteVideoEvent event) {
        pauseMediaPlay();
        Logger.e("onEventRemoteVideo:======= " + event.userid);
        if (!mtypeIncomes.isEmpty()) {
            if (mtypeIncomes.equals("PcOrAndroidMsgVideo")) {
                m_fspUserViewGroup.onEventRemoteVideo(event);
            } else if (mtypeIncomes.equals("PcOrAndroidMsgAudio")) {

            }
        }
        if (mIncometype != -1) {
            if (mIncometype == 0) {
                m_fspUserViewGroup.onEventRemoteVideo(event);
            } else if (mIncometype == 1) {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteAudio(FspEvents.RemoteAudioEvent event) {
        Logger.e("onEventRemoteAudio: =============" + event.userid);
        pauseMediaPlay();
        if (!mtypeIncomes.isEmpty()) {
            if (mtypeIncomes.equals("PcOrAndroidMsgVideo")) {
                m_fspUserViewGroup.onEventRemoteAudio(event);
            } else if (mtypeIncomes.equals("PcOrAndroidMsgAudio")) {
                if (event.eventtype == FspEngine.REMOTE_USER_JOIN_GROUP) {
//                    String headUrls = HttpConfigs.getiPNginxServer() + PersonManager.getInstance().getHeadUrl(event.userid);
//                    Logger.e("---------------------------" + headUrls);
//                    m_UserViewGroup.onEventRemoteAudio(event, headUrls);
                }
            }
        }
        if (mIncometype != -1) {
            if (mIncometype == 0) {
                m_fspUserViewGroup.onEventRemoteAudio(event);
            } else if (mIncometype == 1) {
                if (event.eventtype == FspEngine.REMOTE_USER_JOIN_GROUP) {
//                    String headUrls = HttpConfigs.getiPNginxServer() + PersonManager.getInstance().getHeadUrl(event.userid);
//                    Logger.e("---------------------------" + headUrls);
//                    m_UserViewGroup.onEventRemoteAudio(event, headUrls);
                } else if (event.eventtype == FspEngine.REMOTE_USER_LEAVE_GROUP) {

                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgIncome(EventMsgEntity eventMsgEntity) {
        Logger.d("onEventMsgIncome: " + eventMsgEntity.getMsg());
        if (eventMsgEntity.getMsg().equals("拒绝邀请")) {
//            ToastUtil.show(getApplicationContext(), eventMsgEntity.getUserId() + eventMsgEntity.getMsg(), ToastUtil.MID);
//            finish();
        } else if (eventMsgEntity.getMsg().equals("接受邀请")) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgIncome(FspEvents.ChatMsgItem msgItem) {
        Logger.e("onMsgIncome1==============: " + msgItem.msg);
        if (msgItem.msg.equals("hangUp")) {
            if (mtypeIncomes.equals("PcOrAndroidMsgVideo") || mIncometype == 0) {
//                String connectionPerson = PersonManager.getInstance().getConnectionPerson();
//                int currentChildCount = m_fspUserViewGroup.getCurrentChildCount();
//                if (currentChildCount <= 1) {
//                    closeVideo();
//                    return;
//                }
//                int i = countSubstring(connectionPerson, "~!!~");
//                if (i <= 2) {
//                    closeVideo();
//                }
            } else if (mtypeIncomes.equals("PcOrAndroidMsgAudio") || mIncometype == 1) {
                int currentChildCount = m_UserViewGroup.getCurrentChildCount();
                if (currentChildCount <= 1) {
                    closeAudio();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        leaveGroup();
        m_fspUserViewGroup.onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void closeVideo() {
        m_fspUserViewGroup.stopPublishLocalVideo();
        m_fspUserViewGroup.stopPublishLocalAudio();
        FspManager.getInstance().stopVideoPublish();
        FspManager.getInstance().stopPublishAudio();
        leaveGroup();
        hangUp(Configs.PC_NAME);
        stopMediaPlay();
        finish();
    }

    public void closeAudio() {
        FspManager.getInstance().stopPublishAudio();
        leaveGroup();
        stopMediaPlay();
        hangUp(Configs.PC_NAME);
        finish();
    }

    MediaPlayer mediaPlayer;

    public void startMediaPlay() {
        mediaPlayer = MediaPlayer.create(this, R.raw.weichat_voice);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void pauseMediaPlay() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    public void stopMediaPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void hangUp(String userId) {
        FspManager.getInstance().sendUserMsg(userId, "hangUp");

    }

    long exitTime = 0;

    @Override
    public void onBackPressed() {
//        boolean backPressed = FragmentContentManager.getInstance().onBackPressed();
//        if (backPressed) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次挂断",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (mtypeIncomes.equals("PcOrAndroidMsgVideo") || mIncometype == 0) {
                closeVideo();
            } else if (mtypeIncomes.equals("PcOrAndroidMsgAudio") || mIncometype == 1) {
                closeAudio();
            }
            exitTime = 0;
        }
//        } else {
//            super.onBackPressed();
//        }
    }
}
