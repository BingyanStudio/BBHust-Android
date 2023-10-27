package com.bingyan.bbhust

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.view.WindowCompat
import com.bingyan.bbhust.ui.markdown.WebViewProvider
import com.bingyan.bbhust.ui.provider.AppUriHandler
import com.bingyan.bbhust.ui.provider.LocalActivity
import com.bingyan.bbhust.ui.provider.LocalGalley
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.provider.LocalPicker
import com.bingyan.bbhust.ui.provider.LocalShare
import com.bingyan.bbhust.ui.provider.LocalSnack
import com.bingyan.bbhust.ui.provider.LocalWebView
import com.bingyan.bbhust.ui.provider.Picker
import com.bingyan.bbhust.ui.provider.ShareProvider
import com.bingyan.bbhust.ui.theme.AppTheme
import com.bingyan.bbhust.ui.viewer.ImageViewerManger
import com.bingyan.bbhust.ui.widgets.SnackHostState
import com.bingyan.bbhust.ui.widgets.SnackbarHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


class MainActivity : ComponentActivity() {
    private val getContents = registerForActivityResult(Picker()) {}

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val nav = rememberAnimatedNavController()
            val hostState = remember { SnackHostState() }
            val imageViewerManger = ImageViewerManger()
            val uriHandler = AppUriHandler(nav, this)
            CompositionLocalProvider(
                LocalPicker provides getContents,
                LocalActivity provides this,
                LocalNav provides nav,
                LocalUriHandler provides uriHandler,
                LocalWebView provides WebViewProvider(this, imageViewerManger, uriHandler).web,
                LocalGalley provides imageViewerManger,
                LocalShare provides ShareProvider(),
                LocalSnack provides hostState
            ) {
                AppTheme {
                    SnackbarHost(hostState = hostState) {
                        AppNav(nav)
                    }
                }
            }
        }
    }
}