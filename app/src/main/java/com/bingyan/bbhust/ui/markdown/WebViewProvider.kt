package com.bingyan.bbhust.ui.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.ui.platform.UriHandler
import androidx.webkit.WebViewAssetLoader
import com.bingyan.bbhust.ui.viewer.ImageViewerManger

const val baseDomain = "bb.hust.online"
const val baseUrl = "https://$baseDomain"

@SuppressLint("SetJavaScriptEnabled") class WebViewProvider(
    ctx: Context,
    imageViewerManger: ImageViewerManger,
    uriHandler: UriHandler
) {
    val web: WebView

    init {
        web = WebView(ctx)
        web.isHorizontalScrollBarEnabled = false
        web.isVerticalScrollBarEnabled = false
        web.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain(baseDomain)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(ctx))
            .build()
        web.setBackgroundColor(0)
        web.webViewClient = LocalContentWebViewClient(assetLoader, uriHandler)
        web.webChromeClient = LocalWebChromeClient(imageViewerManger)
        web.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            useWideViewPort = false
            displayZoomControls = false
            domStorageEnabled = true
            allowFileAccess = true
            setSupportMultipleWindows(false)
            cacheMode = WebSettings.LOAD_DEFAULT
            textZoom = 100
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    fun load(data: String) {
        web.loadDataWithBaseURL(baseUrl, data, "text/html", null, null)
    }
}