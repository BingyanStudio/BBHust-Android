package com.bingyan.bbhust.ui.widgets

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.bingyan.bbhust.utils.SP

/**
 * 通用渐变实现，可应用于任何元素，包括但不限于文字，图标
 */
fun Modifier.gradient() = graphicsLayer(alpha = 0.99f)
    .drawWithCache {
        val brush = Brush.verticalGradient(
            if (SP.get("gradient", "true") == "true") {
                listOf(
                    Color(0xFFFF8D9D),
                    Color(0xFF9541FF)
                )
            } else {
                listOf(
                    Color(0xFF9541FF),
                    Color(0xFF9541FF)
                )
            }
        )
        onDrawWithContent {
            drawContent()
            drawRect(brush, blendMode = BlendMode.SrcAtop)
        }
    }

/**
 * 通用渐变实现，可应用于任何元素，包括但不限于文字，图标
 */
fun Modifier.gradient(brush: Brush) = graphicsLayer(alpha = 0.99f)
    .drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(brush, blendMode = BlendMode.SrcAtop)
        }
    }