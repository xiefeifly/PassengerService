//package com.example.faceoridcard.hst.base
//
//import android.app.Dialog
//import android.content.Context
//import android.os.Bundle
//import android.view.Gravity
//import android.view.KeyEvent
//import android.view.ViewGroup
//import butterknife.ButterKnife
//import com.example.faceoridcard.hst.business.FspEvents
//
//abstract class BaseDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
//    private var m_onDismissListener: OnDialogDismissListener? = null
//    private var m_cancel = false
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(layoutId)
//        if (canButterKnife()) ButterKnife.bind(this)
//        initWindowFlags()
//        setOnDismissListener(object : DialogInterface.OnDismissListener {
//            override fun onDismiss(dialog: DialogInterface) {
//                if (m_onDismissListener != null) {
//                    m_onDismissListener!!.onDismiss()
//                }
//            }
//        })
//        init()
//    }
//
//    protected abstract fun init()
//    protected abstract val layoutId: Int
//    protected fun canButterKnife(): Boolean {
//        return true
//    }
//
//    protected fun initWindowFlags() {
//        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        window!!.attributes.gravity = Gravity.BOTTOM
//    }
//
//    fun notifyDataSetChanged() {}
//    fun notifyDataSetChanged(status: FspEvents.RefreshUserStatusFinished?) {}
//    fun setOnDialogDismissListener(onDismissListener: OnDialogDismissListener?): BaseDialog {
//        m_onDismissListener = onDismissListener
//        return this
//    }
//
//    override fun setCanceledOnTouchOutside(cancel: Boolean) {
//        m_cancel = cancel
//        super.setCanceledOnTouchOutside(cancel)
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        return if (!m_cancel && keyCode == KeyEvent.KEYCODE_BACK) {
//            true
//        } else super.onKeyDown(keyCode, event)
//    }
//
//    interface OnDialogDismissListener {
//        fun onDismiss()
//    }
//}