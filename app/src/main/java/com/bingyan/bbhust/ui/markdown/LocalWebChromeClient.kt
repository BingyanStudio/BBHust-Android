package com.bingyan.bbhust.ui.markdown

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import com.bingyan.bbhust.ui.viewer.ImageViewerManger

class LocalWebChromeClient(private val imageViewerManger: ImageViewerManger) : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        val message = consoleMessage?.message()
        Log.i("WebView", "onConsole: $message")
        if (message != null && message.startsWith("img:")) {
            val r = message.split("\n")
            if (r.size > 2) {
                val index = r[1].toIntOrNull()
                val images = r.drop(2).filter { it.isNotBlank() }
                index?.let { imageViewerManger.showBigImage(index, images) }
            }
        }
        return true
    }
}