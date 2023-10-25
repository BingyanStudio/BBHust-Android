package com.bingyan.bbhust.ui.effect

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.bingyan.bbhust.utils.keep
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt


fun Modifier.verticalShortScroll(scrollState: ScrollState) = composed {
    val accumulator = keep(v = 0f)
    val tween = animateFloatAsState(targetValue = accumulator.value, animationSpec = spring())
    val scope = rememberCoroutineScope()
    val isNew = keep(v = true)
    val draggableState = rememberDraggableState(onDelta = { delta ->
        if ((delta < 0 && scrollState.value < scrollState.maxValue) || (delta > 0 && scrollState.value > 0)) {
            scope.launch {
                scrollState.scrollBy(-delta)
            }
        } else {
            accumulator.value += delta
        }
//        println("Del:${delta},Value:${scrollState.value},Max:${scrollState.maxValue}")
    })
    verticalScroll(scrollState)
        .draggable(draggableState, orientation = Orientation.Vertical,
            onDragStarted = {
                isNew.value = true
            },
            onDragStopped = {
                isNew.value = false
                scope.launch {
                    // 抛物线算法，正常速度在 10000 左右，目标位移约为 1000 px，由 x = 0.5vt^2，x = 1000，t = 0.3s，v = 6.67 /ms，所以 v 除以 1500
                    // V = 6.67 /ms, 速度衰减为 6.67 / 300 = 0.022 /ms
                    // deltaT = 16ms, deltaV = 0.022 * 16 = 0.352, deltaS =  * 16 = 1707
                    var velocity = -it / 1000f
                    val deltaT = 4 // 250 逻辑帧
                    val a = 0.02f
                    val deltaV = a * deltaT
                    while (abs(velocity) > 0.35f && !isNew.value) {
                        velocity -= sign(velocity) * deltaV
                        val deltaS = velocity * deltaT
                        scrollState.scrollBy(deltaS)
                        delay(deltaT.toLong())
                    }
                }
                accumulator.value = 0f
            })
        .graphicsLayer {
            translationY = sign(tween.value) * sqrt(abs(tween.value)) * 10
        }
}