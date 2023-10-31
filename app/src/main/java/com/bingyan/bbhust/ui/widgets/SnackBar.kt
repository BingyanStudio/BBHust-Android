package com.bingyan.bbhust.ui.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.ui.provider.LocalSnack
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.colors
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume


const val SNACK_INFO = ""
const val SNACK_WARN = " "
const val SNACK_ERROR = "  "
const val SNACK_SUCCESS = "OK"

@Composable
fun AppSnack(
    content: @Composable () -> Unit
) {
    val hostState = remember { SnackHostState() }
    CompositionLocalProvider(
        LocalSnack provides hostState
    ) {
        Box(modifier = Modifier) {
            content()
            SnackbarHost(hostState = hostState) {
                // todo: snackbar content
            }
        }
    }
}

@Composable
fun SnackbarHost(
    hostState: SnackHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackData) -> Unit = { }
) {
    val currentSnackData = hostState.currentSnackData
    val accessibilityManager = LocalAccessibilityManager.current
    LaunchedEffect(currentSnackData) {
        if (currentSnackData != null) {
            val duration = currentSnackData.duration.toMillis(
                false,
                accessibilityManager
            )
            delay(duration)
            currentSnackData.dismiss()
        }
    }
    FadeInFadeOutWithScale(
        current = hostState.currentSnackData,
        modifier = modifier,
        content = snackbar
    )
}

@Composable
fun AppSnackBar(data: SnackData) {
    Row(
        modifier = Modifier
            .shadow(6.dp, CardShapes.large)
            .background(
                when (data.level) {
                    SnackLevel.Info -> colors.info
                    SnackLevel.Warn -> colors.warn
                    SnackLevel.Failed -> colors.error
                    SnackLevel.Success -> colors.success
                    SnackLevel.Normal -> colors.primary
                }, CardShapes.large
            )
            .padding(vertical = Gap.Mid, horizontal = Gap.Big),
    ) {
        Column(
            Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                data.message,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}


////// 以下代码复制至源代码

@Composable
private fun FadeInFadeOutWithScale(
    current: SnackData?,
    modifier: Modifier = Modifier,
    content: @Composable (SnackData) -> Unit
) {
    val state = remember { FadeInFadeOutState<SnackData?>() }
    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()
        if (!keys.contains(current)) {
            keys.add(current)
        }
        state.items.clear()
        keys.filterNotNull().mapTo(state.items) { key ->
            FadeInFadeOutAnimationItem(key) { children ->
                val isVisible = key == current
                val duration = if (isVisible) SnackbarFadeInMillis else SnackbarFadeOutMillis
                val delay = SnackbarFadeOutMillis + SnackbarInBetweenDelayMillis
                val animationDelay = if (isVisible && keys.filterNotNull().size != 1) delay else 0
                val opacity = animatedOpacity(
                    animation = tween(
                        easing = LinearEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible,
                    onAnimationFinish = {
                        if (key != state.current) {
                            // leave only the current in the list
                            state.items.removeAll { it.key == key }
                            state.scope?.invalidate()
                        }
                    }
                )
                val scale = animatedScale(
                    animation = tween(
                        easing = FastOutSlowInEasing,
                        delayMillis = animationDelay,
                        durationMillis = duration
                    ),
                    visible = isVisible
                )
                Box(
                    Modifier
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = opacity.value
                        )
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                ) {
                    children()
                }
            }
        }
    }
    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.forEach { (item, opacity) ->
            key(item) {
                opacity {
                    content(item!!)
                }
            }
        }
    }
}

private class FadeInFadeOutState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    var items = mutableListOf<com.bingyan.bbhust.ui.widgets.FadeInFadeOutAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class FadeInFadeOutAnimationItem<T>(
    val key: T,
    val transition: FadeInFadeOutTransition
)

private typealias FadeInFadeOutTransition = @Composable (content: @Composable () -> Unit) -> Unit

@Composable
private fun animatedOpacity(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {}
): State<Float> {
    val alpha = remember { Animatable(if (!visible) 1f else 0f) }
    LaunchedEffect(visible) {
        alpha.animateTo(
            if (visible) 1f else 0f,
            animationSpec = animation
        )
        onAnimationFinish()
    }
    return alpha.asState()
}

@Composable
private fun animatedScale(animation: AnimationSpec<Float>, visible: Boolean): State<Float> {
    val scale = remember { Animatable(if (!visible) 1f else 0.8f) }
    LaunchedEffect(visible) {
        scale.animateTo(
            if (visible) 1f else 0.8f,
            animationSpec = animation
        )
    }
    return scale.asState()
}

private const val SnackbarFadeInMillis = 150
private const val SnackbarFadeOutMillis = 75
private const val SnackbarInBetweenDelayMillis = 0


internal fun SnackDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?
): Long {
    val original = when (this) {
        SnackDuration.Long -> 4000L
        SnackDuration.Normal -> 2000L
        SnackDuration.Short -> 1000L
    }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction
    )
}

@Stable
class SnackHostState {

    private val mutex = Mutex()

    var currentSnackData by mutableStateOf<SnackData?>(null)
        private set

    suspend fun showSnackbar(
        message: String,
        duration: SnackDuration = SnackDuration.Short,
        level: SnackLevel = SnackLevel.Normal,
    ): SnackbarResult = mutex.withLock {
        try {
            return suspendCancellableCoroutine { c ->
                currentSnackData = SnackData(
                    message,
                    duration,
                    level,
                    c
                )
            }
        } finally {
            currentSnackData = null
        }
    }
}

enum class SnackLevel {
    Success, Info, Failed, Warn, Normal
}

enum class SnackDuration {
    Short, Normal, Long
}

data class SnackData(
    val message: String,
    val duration: SnackDuration = SnackDuration.Short,
    val level: SnackLevel = SnackLevel.Normal,
    private val continuation: CancellableContinuation<SnackbarResult>? = null
) {
    fun performAction() {
        if (continuation != null && continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed)
    }

    fun dismiss() {
        if (continuation != null && continuation.isActive) continuation.resume(SnackbarResult.Dismissed)
    }
}
