package com.example.hsts.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.hsts.business.FspEvents;
import com.example.hsts.business.FspManager;
import com.example.hsts.utils.FspUtils;
import com.hst.fsp.FspEngine;
import com.orhanobut.logger.Logger;


public class UserViewGroup extends ViewGroup {
    private boolean m_isMax = false;
    private final int m_margin = 20;
    private final int m_margin_top = 150;

    public UserViewGroup(Context context) {
        super(context);
        removeAllViews();
    }

    public UserViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        removeAllViews();
    }

    public UserViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        removeAllViews();
    }

    public UserViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        removeAllViews();
    }
    public int getCurrentChildCount(){
        return getChildCount();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
//        Logger.e(w + "==" + h);
        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        if (childrenCount <= 2) {
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
//                if (child.isSelected()) {
//                    child.measure(widthMeasureSpec, heightMeasureSpec);
//                } else {
//                    if (m_isMax) {
//                        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
//                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
//                        continue;
//                    }
                if (i == 0) {
                    child.measure(0, 0);
                } else {
                    child.measure(MeasureSpec.makeMeasureSpec(w / 3, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(w / 3, MeasureSpec.EXACTLY));
                }
//                }
            }
        }
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        if (childrenCount <= 2) {
            int cw = r / 4;
            int ch = cw;
            int ct = t + m_margin_top;
            int cb = ct + ch;
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (i == 0) {
                    child.layout(0, 0, 0, 0);
                } else if (i == 1) {
                    child.layout(r / 2 - cw / 2, 350, r / 2 + cw / 2, 350 + cw);
                }
            }
        }
    }

    public boolean startPublishLocalAudio(String Pathurl) {
        FspManager fspManager = FspManager.getInstance();
        UserView videoView = ensureUserView(fspManager.getSelfUserId(), Pathurl, true);
        if (videoView != null) {
            videoView.openHead();
            return true;
        }
        Logger.e("videoView == null");
        return false;
    }

    public void onEventRemoteAudio(FspEvents.RemoteAudioEvent event, String pathurl) {
        FspManager fspManager = FspManager.getInstance();
        UserView AudioView = ensureUserView(event.userid, pathurl,
                event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED);
        if (AudioView == null) {
            Logger.e("AudioView == null  userId: %s, videoId : %s", event.userid);
            return;
        }
        if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            AudioView.openHead();
        } else if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
//            AudioView.closeAudioHead();
//            if (!AudioView.hasVideoAudio())
//            removeChildView(AudioView);
        }

    }

    public void onEventRemoteLeaveAudio(FspEvents.RemoteUserEvent event) {
        FspManager fspManager = FspManager.getInstance();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            UserView child = (UserView) getChildAt(i);
            if (child.getUserId().equals(event.userid)) {
                removeChildView(child);
            }
        }

    }

    private void removeChildView(UserView audioView) {
//            if (!audioView.hasVideoAudio())
        removeView(audioView);
    }

    public UserView ensureUserView(String userId, String pathUrl, boolean isCreateUserView) {
        UserView view = null;
        int childrenCount = getChildCount();
        if (childrenCount > 0) {
            //search
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child != null && child instanceof UserView) {
                    UserView userView = ((UserView) child);
                    if (FspUtils.isSameText(userId, userView.getUserId())) {
                        view = userView;
                        break;
                    }
                }
            }
        }
        if (view == null) {
            if (childrenCount < 9) {
                if (isCreateUserView) {
                    UserView userView = new UserView(getContext());
                    userView.setUserId(userId);
                    userView.setPathUrl(pathUrl);
                    view = userView;
                    addView(userView);
                }
            }
        }
        return view;
    }
}
