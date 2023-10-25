package com.bingyan.bbhust.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//夜色主题
val DarkColorPalette = AppColors(
    primary = themeColor,
    secondary = secondaryColor,
    unfocused = grey2,
    background = black2,
    titleBar = grey6,
    divider = black4,
    textPrimary = white4,
    textSecondary = grey2,
    card = grey6,
    onCard = grey5,
    list = grey5,
    info = info,
    warn = warn,
    success = green3,
    error = red2,
    primaryBtnBg = black1,
    forbiddenBtnBg = grey1,
    secondBtnBg = grey5,
)

//白天主题
val LightColorPalette = AppColors(
    primary = themeColor,
    secondary = secondaryColor,
    unfocused = grey1,
    background = white1,
    titleBar = white,
    divider = white3,
    textPrimary = black3,
    textSecondary = grey1,
    card = white,
    onCard = white1,
    list = white,
    info = info,
    warn = warn,
    success = green3,
    error = red2,
    primaryBtnBg = themeColor,
    forbiddenBtnBg = white5,
    secondBtnBg = white3,
)

data class AppColors(
    val primary: Color,//主色
    val secondary: Color,//次级主色
    val unfocused: Color,//无焦点颜色
    val background: Color,//默认背景色
    val titleBar: Color,//标题栏渐变主色
    val divider: Color,//分割线颜色
    val textPrimary: Color,//文字主色
    val textSecondary: Color,//文字次要色，一般为不重要文字颜色，例如描述
    val card: Color,//卡片颜色
    val onCard: Color,//卡片内卡片颜色
    val list: Color,//列表颜色，一般与卡片颜色一致
    val info: Color,//提示信息颜色
    val warn: Color,//警告信息颜色
    val success: Color,//成功信息颜色
    val error: Color,//错误信息颜色
    val primaryBtnBg: Color,//按钮主色
    val forbiddenBtnBg: Color,//禁用按钮主色
    val secondBtnBg: Color,//按钮次级主色，一般用于渐变按钮
    val increase: Color = green2,//收入颜色
    val decrease: Color = red,//支出颜色
)

val LocalColor = compositionLocalOf {
    LightColorPalette
}


@Stable
val colors: AppColors
    @Composable
    get() = LocalColor.current

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            if (Build.VERSION.SDK_INT >= 28)
                window.navigationBarDividerColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    CompositionLocalProvider(LocalColor provides colorScheme) {
        MaterialTheme(
            typography = Typography,
            content = content
        )
    }
}