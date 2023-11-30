package com.example.hsts.base;

import android.app.ActivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hsts.R;
import com.example.hsts.business.FspManager;
import com.example.hsts.utils.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {

    private LoadingDialog m_loadingDialog;
//    private boolean m_isPause = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowFlags();
        setContentView(getLayoutId());
//        ActivityManager.getInstance().setCurrActivity(this);
        if (canButterKnife()) ButterKnife.bind(this);
        hideBottomMenu();
        init();
        listener();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    protected abstract void listener();

    protected void initWindowFlags() {

    }

    protected boolean canEventBus() {
        return true;
    }

    protected boolean canButterKnife() {
        return true;
    }

//    public boolean isPause() {
//        return m_isPause;
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        m_isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        m_isPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
//        ActivityManager.getInstance().removeActivity(this);
        if (canEventBus()) EventBus.getDefault().unregister(this);
    }

    protected void showLoading(@StringRes int resId_waitTextTv, @StringRes int resId_errorTextBtn) {
        m_loadingDialog = new LoadingDialog(this, resId_waitTextTv, resId_errorTextBtn);
        m_loadingDialog.show();
    }

    protected void setErrorLoading(@NonNull String msg) {
        if (m_loadingDialog != null) {
            m_loadingDialog.setErrorStatus(msg);
        }
    }

    protected void setErrorLoading(@StringRes int resId) {
        if (m_loadingDialog != null) {
            m_loadingDialog.setErrorStatus(resId);
        }
    }

    protected void dismissLoading() {
        if (m_loadingDialog != null && m_loadingDialog.isShowing()) {
            m_loadingDialog.dismiss();
        }
    }

    protected boolean joinGroup(String groupId) {
        // join group
        boolean result = FspManager.getInstance().joinGroup(groupId);
        if (result) {
            showLoading(R.string.join_group, R.string.rejoin_group);
        } else {
            setErrorLoading(R.string.join_group_fail);
        }
        return result;
    }

    protected void joinGroupResultSuccess() {
        dismissLoading();
//        if (getClass().getSimpleName().equals(InviteIncomeActivity.class.getSimpleName())) {
//            this.finish();
//        }
//        startActivity(new Intent(this, MainActivity.class));
    }

    protected boolean leaveGroup() {
        // join group
        boolean result = FspManager.getInstance().leaveGroup();
        if (!result) {
            Toast.makeText(getApplicationContext(), "离开组失败",
                    Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    protected void LeaveGroupResultSuccess() {
        finish();
    }

    public final void hideBottomMenu() {
        final View decorView = getWindow().getDecorView();
        int option = 0x1613006 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & 4) == 0) {
                decorView.setSystemUiVisibility(option);
            }
        });
    }

}
