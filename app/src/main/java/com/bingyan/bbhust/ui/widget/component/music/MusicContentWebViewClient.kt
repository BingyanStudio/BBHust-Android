package com.bingyan.bbhust.ui.widget.component.music

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebViewClientCompat

class MusicContentWebViewClient(
    private val onCover: (String) -> Unit,
    private val onAudio: (String) -> Unit,
    private val onTitle: (String) -> Unit
) :
    WebViewClientCompat() {
    private val audioUrl = """\w*.(mp3|m4a|wav|flac|wma|mid|rm|ape)""".toRegex()
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url
        Log.i("shouldInterceptRequest", url.toString())
        if (audioUrl.matches(url.lastPathSegment.orEmpty())) {
            onAudio(url.toString())
            return WebResourceResponse("", "", ByteArray(0).inputStream())
        } else if (url.host.orEmpty().contains("music.126.net") && url.toString()
                .contains("param")
        ) {
            onCover(url.buildUpon().clearQuery().toString())
            return WebResourceResponse("", "", ByteArray(0).inputStream())
        }
        if (url.toString().contains("music"))
            return null
        return WebResourceResponse("", "", ByteArray(0).inputStream())
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript("javascript:document.getElementById(\"title\").textContent") {
            Log.i("title", "end:$it")
            onTitle(it.trim('"'))
        }
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        Log.i("load", "load:$url")
        view?.evaluateJavascript("javascript:document.getElementById(\"title\").textContent") {
            Log.i("title", "load:$it")
            onTitle(it.trim('"'))
        }
    }


}


