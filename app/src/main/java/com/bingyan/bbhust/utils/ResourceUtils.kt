package com.bingyan.bbhust.utils

import android.content.res.Resources
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.App

fun string(id: Int) = resource.getString(id)

fun string(id: Int, vararg formatArgs: Any) = resource.getString(id, *formatArgs)

val resource: Resources = App.CONTEXT.resources

/**
 * 屏幕相关扩展函数
 */

//获取屏幕宽度 px
fun width(fraction: Float = 1.0f) = Resources.getSystem().displayMetrics.widthPixels * fraction

//获取屏幕高度 px
fun height(fraction: Float = 1.0f) = Resources.getSystem().displayMetrics.heightPixels * fraction

@Composable
fun statusHeight() = WindowInsets.statusBars
    .run {

        getBottom(LocalDensity.current) - getTop(LocalDensity.current)
    }

@Composable
fun statusHeightDp() = statusHeight().px2dp

//是否为竖屏状态
val isPortrait get() = height() > width()

//获取屏幕宽度 dp
fun widthDp(fraction: Float = 1.0f) = width(fraction).px2dp

//获取屏幕宽度 dp
fun minDp(fraction: Float = 1.0f) = if (isPortrait) widthDp(fraction) else heightDp(fraction)

//获取屏幕高度 dp
fun heightDp(fraction: Float = 1.0f) = height(fraction).px2dp

//dp 转 px(float)
val Dp.dp2px get() = this.value * Resources.getSystem().displayMetrics.density
val Int.dp2px get() = this * Resources.getSystem().displayMetrics.density

//px 转 Dp
val Float.px2dp get() = (this / Resources.getSystem().displayMetrics.density).dp

val Int.px2dp get() = (this * 1f / Resources.getSystem().displayMetrics.density).dp

val density get() = Resources.getSystem().displayMetrics.density

val fontScale get() = Resources.getSystem().configuration.fontScale

val Float.px2sp get() = (this * 1f / Resources.getSystem().displayMetrics.density * fontScale).sp
val Int.px2sp get() = (this * 1f / Resources.getSystem().displayMetrics.density * fontScale).sp
val Dp.dp2sp get() = (this.value * fontScale).sp
val Int.dp2sp get() = (this * fontScale).sp
val Int.sp2px get() = this * Resources.getSystem().displayMetrics.density / fontScale
val Float.sp2px get() = this * Resources.getSystem().displayMetrics.density / fontScale