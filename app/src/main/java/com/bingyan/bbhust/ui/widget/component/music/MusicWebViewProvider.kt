package com.bingyan.bbhust.ui.widget.component.music

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled") class MusicWebViewProvider(
    ctx: Context,
    onCover: (String) -> Unit,
    onAudio: (String) -> Unit,
    onTitle: (String) -> Unit
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
        web.setBackgroundColor(0)
        web.webViewClient = MusicContentWebViewClient(onCover, onAudio, onTitle)
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

}