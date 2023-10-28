package com.bingyan.bbhust.ui.screen.browser

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

class MWebViewClient(val onProgress: (Int) -> Unit, val onStart: (String) -> Unit) :
    WebViewClient() {

    /*    override fun shouldOverrideUrlLoading(view: WebView, req: WebResourceRequest): Boolean {
            // 处理自定义scheme
            if (!req.url.toString().startsWith("http")) {
                return true
            }
            return false
        }*/

    override fun onPageStarted(view: WebView, url: String, icon: Bitmap?) {
        onStart(url)
        onProgress(0)
    }

    override fun onPageFinished(view: WebView, url: String) {
        onProgress(100)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//        super.onReceivedSslError(view, handler, error)
        handler?.cancel()
    }


}