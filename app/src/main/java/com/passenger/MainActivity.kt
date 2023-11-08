package com.passenger

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hst.business.FspEvents
import com.hst.business.FspManager
import com.hst.utils.AudioManagerUtils
import com.passenger.contents.Config
import com.passenger.databinding.ActivityMainBinding
import com.passenger.ui.CardIdActivity
import com.passenger.ui.FaceIdActivity
import com.passenger.ui.InviteIncomeActivity
import com.passenger.ui.RegisterActivity
import com.passenger.utils.ContentUtil
import com.passenger.utils.ToastUtil
import com.passenger.web.JsJavaBridge
import com.passenger.web.MyWebChromeClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    private val url = Config.URL
    val appId = Config.DEFAULT_APP_ID
    val appSecret = Config.DEFAULT_APP_SECRET
    val serverAddr = Config.DEFAULT_APP_ADDRESS
    lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        hideBottomMenu()
        EventBus.getDefault().register(this)
        AudioManagerUtils.init(this)
//        val deviceSN = ContentUtil.getDeviceSN()
//        Log.e(TAG, "deviceSN: $deviceSN")

        val init = FspManager.init(this, appId, appSecret, serverAddr)
        Log.e(TAG, "onCreate: init $init")
        val login = FspManager.login(Config.ANDROID_NAME, Config.ANDROID_NAME)
        Log.e(TAG, "onCreate: login $login")

        startActivity(Intent(this, CardIdActivity::class.java))
//        startActivity(Intent(this, FaceIdActivity::class.java))
//        startActivity(Intent(this, RegisterActivity::class.java))

//        setWebView(mBinding.webview)
//        mBinding.webview.loadUrl(url)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginResult(result: FspEvents.LoginResult) {
        Log.e(TAG, "lon: ddfsdfsdf===================================" + result.isSuccess)
        if (result.isSuccess) {
            val voiceCallIntent = Intent(this, InviteIncomeActivity::class.java)
            voiceCallIntent.putExtra("Incometype", 0)
            voiceCallIntent.putExtra("IM_INFO", 0)
//            startActivity(voiceCallIntent)
        } else {
            ToastUtil.showBottom(this, result.desc)
        }
    }

    @SuppressLint("JavascriptInterface")
    fun setWebView(webView: WebView) {
        webView.settings.domStorageEnabled = true
        webView.setInitialScale(50)
        webView.webChromeClient = WebChromeClient()
        val setting: WebSettings = webView.settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        setting.javaScriptEnabled = true
        setting.javaScriptCanOpenWindowsAutomatically = true
        webView.addJavascriptInterface(JsJavaBridge(this, webView), "\$Android")
        setting.cacheMode = WebSettings.LOAD_DEFAULT
        setting.domStorageEnabled = true
        setting.databaseEnabled = true
//         setting.setAppCacheEnabled(true);
        //         setting.setAppCacheEnabled(true);
        setting.allowFileAccess = true
        setting.cacheMode = WebSettings.LOAD_NO_CACHE
        setting.savePassword = true
        setting.setSupportZoom(true)
        setting.builtInZoomControls = true
        setting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        setting.useWideViewPort = true
        setting.loadWithOverviewMode = true
        webView.webChromeClient = MyWebChromeClient()
        webView.setWebChromeClient(WebChromeClient())

        //禁止上下左右滚动(不显示滚动条)
        //禁止上下左右滚动(不显示滚动条)
        webView.isScrollContainer = false
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false

        //还有一个类MyWebViewClient
        webView.webViewClient = object : WebViewClient() {
            //开始载入
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.e("webview开始载入url", "${url}");
                Log.e("webview开始载入", "${webView.progress}");
            }

            //载入结束
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.e("webview加载进度条", "${webView.progress}");
            }

            //错误信息
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.e(TAG, "onReceivedError: ${request.url}")
                Log.e(TAG, "onReceivedError: ${request.toString()}")
                Log.e(TAG, "onReceivedError: ${error.errorCode}")
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                super.onReceivedSslError(view, handler, error)
                Log.e("webview的sslerror", error.toString())

                handler.proceed()
            }

            //重新提交
            override fun onFormResubmission(view: WebView, dontResend: Message, resend: Message) {
                super.onFormResubmission(view, dontResend, resend)
                webView.reload()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }
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