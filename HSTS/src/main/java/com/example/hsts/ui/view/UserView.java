package com.example.hsts.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.hsts.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserView extends RelativeLayout {
    private String m_userid;
    private String m_videoid;
    private String mHeadPath;
    Context mContext;

    ImageView mfsp_head_image;
    TextView userTv;

    public UserView(Context context) {
        this(context, null);
    }

    public UserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.users_view, this);
        mContext = context;
        mfsp_head_image = findViewById(R.id.head_images);
        userTv = findViewById(R.id.userTv);
//        ButterKnife.bind(this);
    }

    public void setUserId(String userid) {
        m_userid = userid;
    }

    public String getUserId() {
        return m_userid;
    }

    public void setPathUrl(String headPath) {
        mHeadPath = headPath;
    }

    public void openHead() {
        userTv.setText(m_userid);
        mfsp_head_image.setVisibility(View.VISIBLE);
//        GlideApp.with(mContext).load(mHeadPath)
//                .apply(new RequestOptions()
//                        .transform(new RoundedCorners(1))
//                        .transform(new CenterCrop(), new RoundedCorners(1)))
//                .error(R.drawable.ic_admin_df)
//                .into(mfsp_head_image);
    }

    public void closeAudioHead() {

//        if (!m_haveAudio && !m_haveVideo) {
//            // 释放绑定的视频
//            if (!FspUtils.isEmptyText(m_userid)) {
//                FspManager.getInstance().setRemote(m_userid, m_videoid,
//                        null, m_renderMode);
//            }

        m_userid = null;
//            m_videoid = null;
    }
}
