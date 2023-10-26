package com.bingyan.bbhust.utils

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

fun Modifier.click(click: () -> Unit) = composed {
    Modifier.clickable(
        onClick = click,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    )
}

fun Modifier.clickRipple(click: () -> Unit) = composed {
    Modifier.clickable(
        onClick = click,
        indication = RippleIndication,
        interactionSource = remember { MutableInteractionSource() }
    )
}

fun Modifier.clickNoRepeat(
    color: Color = Color.Unspecified,
    repeatDuring: Long = 500,
    click: () -> Unit
) = composed {
    val lastClick = remember { mutableStateOf(0L) }
    Modifier.clickable(
        onClick = {
            val now = System.currentTimeMillis()
            if (now - lastClick.value > repeatDuring) {
                click()
                lastClick.value = now
            }
        },
        indication = rememberRipple(color = color),
        interactionSource = remember { MutableInteractionSource() }
    )
}

/**
 * 水波纹点击效果指示器
 */
object RippleIndication : Indication {

    private class DefaultDebugIndicationInstance(
        private val isPressed: State<Boolean>,
        private val isHovered: State<Boolean>,
        private val isFocused: State<Boolean>,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isPressed.value) {
                drawRect(color = Color.Black.copy(alpha = 0.3f), size = size)
            } else if (isHovered.value || isFocused.value) {
                drawRect(color = Color.Black.copy(alpha = 0.1f), size = size)
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            DefaultDebugIndicationInstance(isPressed, isHovered, isFocused)
        }
    }
}