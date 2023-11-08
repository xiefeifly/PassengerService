//package com.example.faceoridcard.hst.base
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.annotation.StringRes
//import androidx.appcompat.app.AppCompatActivity
//import com.example.faceoridcard.hst.business.FspManager
//import com.hst.business.FspManager
//import org.greenrobot.eventbus.EventBus
//
//abstract class BaseActivity : AppCompatActivity() {
//    private var m_loadingDialog: LoadingDialog? = null
//
//    //    private boolean m_isPause = false;
//    protected override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        initWindowFlags()
//        setContentView(layoutId)
//        ActivityManager.getInstance().setCurrActivity(this)
//        if (canButterKnife()) ButterKnife.bind(this)
//        init()
//    }
//
//    protected abstract val layoutId: Int
//    protected abstract fun init()
//    protected fun initWindowFlags() {}
//    protected fun canEventBus(): Boolean {
//        return true
//    }
//
//    protected fun canButterKnife(): Boolean {
//        return true
//    }
//
//    //    public boolean isPause() {
//    //        return m_isPause;
//    //    }
//    protected override fun onResume() {
//        super.onResume()
//        //        m_isPause = false;
//    }
//
//    protected override fun onPause() {
//        super.onPause()
//        //        m_isPause = true;
//    }
//
//    protected override fun onDestroy() {
//        super.onDestroy()
//        dismissLoading()
//        ActivityManager.getInstance().removeActivity(this)
//        if (canEventBus()) EventBus.getDefault().unregister(this)
//    }
//
//    protected fun showLoading(
//        @StringRes resId_waitTextTv: Int,
//        @StringRes resId_errorTextBtn: Int
//    ) {
//        m_loadingDialog = LoadingDialog(this, resId_waitTextTv, resId_errorTextBtn)
//        m_loadingDialog.show()
//    }
//
//    protected fun setErrorLoading(msg: String) {
//        if (m_loadingDialog != null) {
//            m_loadingDialog.setErrorStatus(msg)
//        }
//    }
//
//    protected fun setErrorLoading(@StringRes resId: Int) {
//        if (m_loadingDialog != null) {
//            m_loadingDialog.setErrorStatus(resId)
//        }
//    }
//
//    protected fun dismissLoading() {
//        if (m_loadingDialog != null && m_loadingDialog.isShowing()) {
//            m_loadingDialog.dismiss()
//        }
//    }
//
//    protected fun joinGroup(groupId: String?): Boolean {
//        // join group
//        val result: Boolean = FspManager.joinGroup(groupId)
//        if (result) {
//            showLoading(R.string.join_group, R.string.rejoin_group)
//        } else {
//            setErrorLoading(R.string.join_group_fail)
//        }
//        return result
//    }
//
//    protected fun joinGroupResultSuccess() {
//        dismissLoading()
//        //        if (getClass().getSimpleName().equals(InviteIncomeActivity.class.getSimpleName())) {
////            this.finish();
////        }
////        startActivity(new Intent(this, MainActivity.class));
//    }
//
//    protected fun leaveGroup(): Boolean {
//        // join group
//        val result: Boolean = FspManager.instance!!.leaveGroup()
//        if (!result) {
//            Toast.makeText(
//                getApplicationContext(), "离开组失败",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        return result
//    }
//
//    protected fun LeaveGroupResultSuccess() {
//        finish()
//    }
//}