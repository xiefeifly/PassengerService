//package com.example.faceoridcard.hst.utils
//
//import android.app.Dialog
//import android.content.Context
//import android.os.Bundle
//import android.view.KeyEvent
//import android.view.View
//import android.view.animation.AnimationUtils
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.annotation.StringRes
//import com.example.faceoridcard.R
//
//class LoadingDialog(
//    context: Context,
//    @StringRes resId_waitTextTv: Int,
//    @StringRes resId_errorTextBtn: Int
//) : Dialog(context, R.style.DialogStyleTransparent) {
//    var m_loginIvLoginState: ImageView? = null
//
//    var m_loginTvLoginState: TextView? = null
//
//    var m_loginBtnRejoin: TextView? = null
//
//    @StringRes
//    private val resId_waitTextTv: Int
//
//    @StringRes
//    private val resId_errorTextBtn: Int
//
//    init {
//        setCanceledOnTouchOutside(false)
//        this.resId_waitTextTv = resId_waitTextTv
//        this.resId_errorTextBtn = resId_errorTextBtn
//    }
//
//    override fun onCreate(savedInstanceState: Bundle) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.dialog_loading)
//        m_loginIvLoginState = findViewById<ImageView>(R.id.login_iv_login_state)
//        m_loginTvLoginState = findViewById<TextView>(R.id.login_tv_login_state)
//        m_loginBtnRejoin = findViewById<TextView>(R.id.login_btn_rejoin)
//        ButterKnife.bind(this)
//        m_loginIvLoginState.setImageResource(R.mipmap.login_waiting)
//        val waitingAnimation: Animation = AnimationUtils.loadAnimation(
//            context, R.anim.join_group
//        )
//        m_loginIvLoginState.startAnimation(waitingAnimation)
//        m_loginTvLoginState.setText(resId_waitTextTv)
//        m_loginBtnRejoin.setText(resId_errorTextBtn)
//        m_loginBtnRejoin.setVisibility(View.GONE)
//
//        // 设置window偏右
//        window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        window!!.attributes.gravity = Gravity.CENTER
//    }
//
//    fun setErrorStatus(msg: String) {
//        m_loginIvLoginState!!.clearAnimation()
//        m_loginIvLoginState!!.setImageResource(R.mipmap.login_icon_warning)
//        if (msg != null) {
//            m_loginTvLoginState.setText(msg)
//        }
//        m_loginBtnRejoin.setVisibility(View.VISIBLE)
//    }
//
//    fun setErrorStatus(@StringRes resId: Int) {
//        m_loginIvLoginState!!.clearAnimation()
//        m_loginIvLoginState!!.setImageResource(R.mipmap.login_icon_warning)
//        m_loginTvLoginState.setText(resId)
//        m_loginBtnRejoin.setVisibility(View.VISIBLE)
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        return if (keyCode == KeyEvent.KEYCODE_BACK) {
//            true
//        } else super.onKeyDown(keyCode, event)
//    }
//
//    override fun dismiss() {
//        m_loginTvLoginState.clearAnimation()
//        super.dismiss()
//    }
//
//    // @OnClick(R.id.login_btn_rejoin)
//    fun onClickRejoinBtn() {
//        dismiss()
//    }
//}