package com.bingyan.bbhust

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.view.WindowCompat
import com.bingyan.bbhust.ui.markdown.WebViewProvider
import com.bingyan.bbhust.ui.provider.AppUriHandler
import com.bingyan.bbhust.ui.provider.LocalActivity
import com.bingyan.bbhust.ui.provider.LocalImageViewer
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.provider.LocalPicker
import com.bingyan.bbhust.ui.provider.LocalWebView
import com.bingyan.bbhust.ui.provider.Picker
import com.bingyan.bbhust.ui.theme.AppTheme
import com.bingyan.bbhust.ui.viewer.ImageViewerManger
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


class MainActivity : ComponentActivity() {
    private val getContents = registerForActivityResult(Picker()) {}

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val nav = rememberAnimatedNavController()
            val imageViewerManger = ImageViewerManger()
            val uriHandler = AppUriHandler(nav, this)
            CompositionLocalProvider(
                LocalPicker provides getContents,
                LocalActivity provides this,
                LocalNav provides nav,
                LocalUriHandler provides uriHandler,
                LocalWebView provides WebViewProvider(this, imageViewerManger, uriHandler).web,
                LocalImageViewer provides imageViewerManger,
            ) {
                AppTheme {
                    AppNav(nav)
                }
            }
        }
    }
}