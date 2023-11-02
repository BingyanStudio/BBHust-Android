package com.bingyan.bbhust

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.view.WindowCompat
import com.bingyan.bbhust.ui.provider.AppUriHandler
import com.bingyan.bbhust.ui.provider.LocalBottomDialog
import com.bingyan.bbhust.ui.provider.LocalDialog
import com.bingyan.bbhust.ui.provider.LocalGalley
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.provider.LocalPicker
import com.bingyan.bbhust.ui.provider.LocalShare
import com.bingyan.bbhust.ui.provider.Picker
import com.bingyan.bbhust.ui.provider.ShareProvider
import com.bingyan.bbhust.ui.theme.AppTheme
import com.bingyan.bbhust.ui.viewer.ImageViewerManger
import com.bingyan.bbhust.ui.widgets.AppDialog
import com.bingyan.bbhust.ui.widgets.AppSnack
import com.bingyan.bbhust.ui.widgets.sheet.AppBottomSheetDialog
import com.google.accompanist.navigation.animation.rememberAnimatedNavController


class MainActivity : ComponentActivity() {
    private val getContents = registerForActivityResult(Picker()) {}

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val nav = rememberAnimatedNavController()
            val uriHandler = AppUriHandler(nav, this)
            val imageViewerManger = ImageViewerManger()
            CompositionLocalProvider(
                LocalPicker provides getContents,
                LocalNav provides nav,
                LocalUriHandler provides uriHandler,
                LocalGalley provides imageViewerManger,
                LocalShare provides ShareProvider(),
                LocalDialog provides AppDialog(),
                LocalBottomDialog provides AppBottomSheetDialog()
            ) {
                AppTheme {
                    val bottomSheetDialog = LocalBottomDialog.current
                    AppSnack {
                        AppNav(nav)
                        bottomSheetDialog.Build(nav = nav)
                    }
                }
            }
        }
    }
}