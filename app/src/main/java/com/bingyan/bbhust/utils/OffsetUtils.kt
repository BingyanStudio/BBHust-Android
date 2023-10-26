package com.bingyan.bbhust.utils

import androidx.compose.ui.graphics.Color

/**间值动画
 * 从this变化到target
 */
fun Float.to(target: Float, percent: Float) =
    if (percent >= 0) {
        this * (1 - percent) + target * percent
    } else {
        target * (1 + percent) + target * (-percent)
    }

/**
 * 颜色间值动画
 * 从Color0->Color1
 */
fun Color.toColor(target: Color, percent: Float) =
    Color(
        red = this.red.to(target.red, percent).coerceIn(0f, 1f),
        blue = this.blue.to(target.blue, percent).coerceIn(0f, 1f),
        green = this.green.to(target.green, percent).coerceIn(0f, 1f),
        alpha = this.alpha.to(target.alpha, percent).coerceIn(0f, 1f)
    )