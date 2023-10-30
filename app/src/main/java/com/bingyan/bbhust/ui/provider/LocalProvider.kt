package com.bingyan.bbhust.ui.provider

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.bingyan.bbhust.ui.viewer.ImageViewerManger
import com.bingyan.bbhust.ui.widgets.SnackHostState

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
val LocalGalley = localStaticProvider<ImageViewerManger>()
val LocalShare = localStaticProvider<ShareProvider>()
val LocalSnack = localProvider<SnackHostState>()