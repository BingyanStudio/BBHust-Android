package com.bingyan.bbhust.ui.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BadgedBox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.utils.ext.sp2px
import kotlin.math.max


fun Modifier.redPoint(v: Int = 0): Modifier {
    return drawWithContent {
        drawContent()
        if (v > 0) {
            drawIntoCanvas {
                val paint = Paint().apply {
                    color = Color.Red
                }
                it.drawCircle(
                    center = Offset(x = size.width, y = 0f),
                    radius = (8.dp / 2).toPx(),
                    paint = paint
                )
            }
        }
    }
}

fun Modifier.redNumPoint(v: Int = 0): Modifier {
    return drawWithContent {
        drawContent()
        if (v > 0) {
            val vStr = if (v > 99) "99+" else v.toString(10)
            drawIntoCanvas {
                val nativePaint = android.graphics.Paint().let { paint1 ->
                    paint1.apply {
                        textSize = 10.sp2px
                        textAlign = android.graphics.Paint.Align.CENTER
                        this.isAntiAlias = true
                        color = android.graphics.Color.WHITE
                    }
                }
                val nativeBgPaint = android.graphics.Paint().let { paint1 ->
                    paint1.apply {
                        this.isAntiAlias = true
                        color = android.graphics.Color.RED
                    }
                }
                val vLen = max((nativePaint.measureText(vStr)) / 2f, 5.sp2px)
                val baseY = nativePaint.fontMetrics.bottom
                Log.i("FontMetrics", nativePaint.fontMetrics.run {
                    "accent:$ascent,descent:$descent,top:$top,bottom:$bottom"
                })
                it.nativeCanvas.drawRoundRect(
                    size.width - vLen - 3.sp2px,
                    (-7).sp2px,
                    size.width + vLen + 3.sp2px,
                    7.sp2px,
                    50.sp2px,
                    50.sp2px,
                    nativeBgPaint
                )
                it.nativeCanvas.drawText(
                    vStr,
                    size.width,
                    baseY,
                    nativePaint
                )
            }
        }
    }
}

@Composable
private fun Redot(num: Int) {
    if (num > 0)
        Box(
            modifier = Modifier
                .background(Color.Red, CircleShape)
                .defaultMinSize(minHeight = 8.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (num > 1) {
                Text(
                    text = num.toString(10),
                    color = Color.White,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
}

@Composable
fun RedotBox(
    modifier: Modifier = Modifier,
    num: Int,
    content: @Composable BoxScope.() -> Unit
) {
    BadgedBox(
        badge = {
            Redot(num = num)
        },
        modifier = modifier
    ) {
        content()
    }
}