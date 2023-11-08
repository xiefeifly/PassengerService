package com.passenger.web

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView

class MyWebChromeClient : WebChromeClient() {
    override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return super.onJsAlert(view, url, message, result)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.i(
            "bqt", "【onConsoleMessage】" + "\nmessage=" + consoleMessage?.message()
                    + "\nlineNumber=" + consoleMessage?.lineNumber()
                    + "\nmessageLevel=" + consoleMessage?.messageLevel() + "\nsourceId=" + consoleMessage?.sourceId()
        );
        return super.onConsoleMessage(consoleMessage);

    }
}