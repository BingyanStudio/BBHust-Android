package com.bingyan.bbhust.ui.markdown

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.bingyan.bbhust.ui.widgets.Style
import com.bingyan.bbhust.ui.widgets.style

class LocalContentWebViewClient(
    private val assetLoader: WebViewAssetLoader,
    private val uriHandler: UriHandler,
//    private val onPageFinished: () -> Unit
) :
    WebViewClientCompat() {
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return assetLoader.shouldInterceptRequest(request.url.run {
            Log.i("shouldInterceptRequest", "$this")
            if (this.host == "bbhust.hust.online") {
                val new =
                    toString().replace("%C3%97", "&times").run { this.style(Style.Large) }.toUri()
                Log.i("shouldInterceptRequest", "New:$new")
                return@run new
            }
            this
        }).also {
            Log.i("shouldInterceptRequest", "$it")
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        uriHandler.openUri(request.url.toString())
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
//        onPageFinished()
    }
}

