package com.bingyan.bbhust.ui.provider

import android.app.Activity
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.bingyan.bbhust.ui.viewer.ImageViewerManger

inline fun <reified T> localProvider(): ProvidableCompositionLocal<T> {
    return compositionLocalOf {
        error("No Local${T::class.simpleName} provided")
    }
}

inline fun <reified T> localStaticProvider(): ProvidableCompositionLocal<T> {
    return staticCompositionLocalOf {
        error("No Local${T::class.simpleName} provided")
    }
}

val LocalNav = localProvider<NavHostController>()
val LocalPicker = localProvider<ActivityResultLauncher<ChooseFiles>>()
val LocalWebView = localStaticProvider<WebView>()
val LocalImageViewer = localStaticProvider<ImageViewerManger>()
val LocalActivity = localProvider<Activity>()