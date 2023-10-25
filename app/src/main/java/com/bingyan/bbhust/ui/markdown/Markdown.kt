package com.bingyan.bbhust.ui.markdown

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bingyan.bbhust.App
import com.bingyan.bbhust.ui.provider.LocalGalley
import com.bingyan.bbhust.ui.theme.DarkColorPalette
import com.bingyan.bbhust.ui.theme.colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MarkdownView(
    markdown: String,
    modifier: Modifier = Modifier,
    finished: () -> Unit = {},
    viewHeight: MutableState<Int> = mutableStateOf(0)
) {
    val data = remember {
        mutableStateOf("")
    }
    val lastCode = remember {
        mutableStateOf(data.value.hashCode())
    }
//    val webView = LocalWebView.current
    val nightMode = colors == DarkColorPalette
    LaunchedEffect(markdown.hashCode()) {
        if (markdown.isNotBlank()) {
            val raw = App.CONTEXT.assets.open("markdown/index.html").readBytes().decodeToString()
            val (head, tail) = raw.split("{{Markdown}}")
            val html = (head + markdown + tail)
            lastCode.value = data.value.hashCode()
            if (nightMode) {
                data.value = html.replaceFirst("github-markdown.css", "github-markdown-dark.css")
            } else {
                data.value = html
            }
        }
    }
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.card)
            .padding(horizontal = 16.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300
                )
            ) { _, target ->
                val targetV = target.height
                if (targetV > 0) {
                    finished()
                    viewHeight.value = targetV
                }
            }
    ) {
        val imageViewerManger = LocalGalley.current
        val uriHandler = LocalUriHandler.current
        AndroidView(factory = { WebViewProvider(it, imageViewerManger, uriHandler).web }) { web ->
            if (data.value.hashCode() != lastCode.value && data.value.isNotBlank()) {
                web.loadDataWithBaseURL(
                    baseUrl,
                    data.value,
                    "text/html",
                    null,
                    null
                )
                scope.launch {
                    delay(300)
                    finished()
                }
            }
        }
    }
}