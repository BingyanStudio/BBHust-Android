package com.bingyan.bbhust.ui.screen.browser

import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.provider.LocalActivity
import com.bingyan.bbhust.ui.provider.browseExternal
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.ui.widgets.EasyImage
import com.bingyan.bbhust.ui.widgets.Header
import com.bingyan.bbhust.ui.widgets.TitleSpacer
import com.bingyan.bbhust.utils.string
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * service: Bool 是否 App 内 H5 网页（如登录注册）
 */
@Composable
fun BrowserScreen(nav: NavHostController, url: String, service: Boolean = false) {
    var webView: WebView? = null
    val progress = remember {
        mutableIntStateOf(100)
    }
    val title = remember {
        mutableStateOf(string(R.string.browser))
    }
    val uri = remember {
        mutableStateOf(url)
    }
    BackHandler {
        if (webView != null) {
            if (webView?.canGoBackOrForward(-1) == true) {
                webView?.goBack()
            } else {
                nav.popBackStack()
            }
        } else {
            nav.popBackStack()
        }
    }
    Column(
        Modifier
            .background(colors.background)
            .fillMaxSize()
    ) {
        TitleSpacer()
        val activity = LocalActivity.current
        Header(
            title.value,
            subtitle = if (service) string(R.string.verified_link) else uri.value,
            navController = nav,
            forward = {
                if (!service) {
                    EasyImage(
                        src = R.drawable.share_line,
                        contentDescription = stringResource(id = R.string.open_external),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                activity.browseExternal(uri.value)
                            }
                            .size(ImageSize.Mid)
                            .padding(Gap.Tiny),
                        tint = colors.textPrimary
                    )
                } else {
                    EasyImage(
                        src = R.drawable.refresh_line,
                        contentDescription = stringResource(id = R.string.refresh),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                webView?.reload()
                            }
                            .size(ImageSize.Normal)
                            .padding(Gap.Small),
                        tint = colors.textPrimary
                    )
                }
            })
        if (progress.intValue < 100) {
            LinearProgressIndicator(
                progress = progress.intValue.toFloat() / 100,
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(),
                color = themeColor
            )
        }
        val scope = rememberCoroutineScope()
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val web = WebView(ctx).apply {
                    //setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    val webSettings = settings
                    webSettings.javaScriptEnabled = true
                    webSettings.javaScriptCanOpenWindowsAutomatically = true
                    //缩放操作
                    webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
                    webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该LitWebView不可缩放
                    webSettings.displayZoomControls = false //隐藏原生的缩放控件
                    webSettings.useWideViewPort = true
                    webSettings.domStorageEnabled = true
                    webSettings.allowFileAccess = true // 设置允许访问文件数据
                    webSettings.setSupportMultipleWindows(true) // 设置允许开启多窗口
                    webSettings.cacheMode = WebSettings.LOAD_DEFAULT
                    webSettings.textZoom = 100
                    webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    webViewClient = MWebViewClient(onProgress = {
                        progress.value = it
                    }) {
                        uri.value = it
                    }
                    webChromeClient = MWebChromeClient(onReceiveTitle = {
                        title.value = it
                    }) { progress.value = it }
                    if (service) {
                        val bridge = JsBridge {
                            scope.launch(Dispatchers.Main) {
                                it(nav)
                            }
                        }
                        addJavascriptInterface(bridge, "app")
                    }
                    loadUrl("")
                    loadUrl(url)
                }
                webView = web
                web
            })
    }
}