package com.bingyan.bbhust.ui.screen.browser

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView

class MWebChromeClient(
    val onReceiveTitle: (String) -> Unit,
    val onProgress: (Int) -> Unit,
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgress(newProgress)
    }

    override fun onReceivedTitle(view: WebView, title: String) {
        super.onReceivedTitle(view, title)
        onReceiveTitle(title)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.i("onConsoleMessage", consoleMessage.toString())
        return super.onConsoleMessage(consoleMessage)
    }

}