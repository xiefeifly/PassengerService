package com.passenger.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.passenger.R
import com.passenger.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityRegisterBinding
    private val TAG = "RegisterActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registerMoudel = ViewModelProvider(this)[RegisterMoudel::class.java]
        mBinding = DataBindingUtil.setContentView<ActivityRegisterBinding>(
            this,
            R.layout.activity_register
        ).apply {

            vm = registerMoudel
        }
        mBinding.lifecycleOwner = this
        hideBottomMenu()
        initListener()
    }

    private fun initListener() {
        mBinding.titleRegister.back.setOnClickListener { finish() }
        mBinding.btnType1.setOnClickListener {
            startActivity(Intent(this, FaceIdActivity::class.java))
        }
//        mBinding.btnType2.setOnClickListener {
//
//        }
        mBinding.btnType3.setOnClickListener {

        }
        mBinding.btnType4.setOnClickListener {

        }
        mBinding.btnType5.setOnClickListener {

        }
    }

    fun btnType2() {
        Log.e(TAG, "btnType2:----------- ")
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