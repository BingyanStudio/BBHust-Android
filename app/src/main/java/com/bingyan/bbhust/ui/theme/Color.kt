package com.bingyan.bbhust.ui.theme

import androidx.compose.ui.graphics.Color

val Transparent = Color(0x00000000)
val themeColor = Color(0xFFFF769A)
val lightThemeColor = Color(0xFFFFDCE5)
val secondaryColor = Color(0xFFFF96B2)
val splashText = Color(0x25000000)
val white = Color(0xFFFFFFFF)
val blurWhite = Color(0xB3FFFFFF)
val white1 = Color(0xFFF7F7F7)
val onWhite = Color(0xFFF8F8F8)
val white3 = Color(0xFFE5E5E5)
val white4 = Color(0xFFD5D5D5)
val white5 = Color(0xFFCCCCCC)
val black = Color(0xFF000000)
val black1 = Color(0xFF1E1E1E)
val black2 = Color(0xFF111111)
val black3 = Color(0xFF191919)
val black4 = Color(0xFF252525)
val black5 = Color(0xFF2C2C2C)
val black6 = Color(0xFF07130A)
val black7 = Color(0xFF292929)
val grey1 = Color(0xFF888888)
val grey2 = Color(0xFFCCC7BF)
val grey3 = Color(0xFF767676)
val grey4 = Color(0xFFB2B2B2)
val grey5 = Color(0xFF222222)
val blurGrey5 = Color(0xB3333333)
val blurGrey5Light = Color(0x5B333333)
val grey6 = Color(0xFF333333)
val green1 = Color(0xFF89FF06)
val green2 = Color(0x8099E0A2)
val green3 = Color(0xFF09D600)
val red = Color(0xFFFF529D)
val red1 = Color(0xFFDF5554)
val red2 = Color(0xFFDD302E)
val red3 = Color(0xFFF77B7A)
val red4 = Color(0xFFD42220)
val red5 = Color(0xFFC51614)
val red6 = Color(0xFFF74D4B)
val red7 = Color(0xFFDC514E)
val red8 = Color(0xFFCBC7BF)
val warn = Color(0xFFF6CA23)
val purple = Color(0xFF9582FF)
val info = Color(0xFF909399)

data class Palette(
    val background: Color = white1,
    val onBackground: Color = white1,
    val increase: Color = green3,
    val decrease: Color = red,
    val primaryText: Color = black7,
    val secondaryText: Color = green2,
    val primary: Color = themeColor,
    val secondary: Color = red,
)