package com.bingyan.bbhust.ui.effect

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.EdgeEffect
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.OverscrollConfiguration
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.NativeCanvas
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.bingyan.bbhust.ui.effect.EdgeEffectCompat.distanceCompat
import com.bingyan.bbhust.ui.effect.EdgeEffectCompat.onAbsorbCompat
import com.bingyan.bbhust.ui.effect.EdgeEffectCompat.onPullDistanceCompat
import com.bingyan.bbhust.ui.effect.EdgeEffectCompat.onReleaseWithOppositeDelta
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberDampingOverscrollEffect(onScroll: (Offset, NestedScrollSource) -> Unit): OverscrollEffect {
    val context = LocalContext.current
    val config = LocalOverscrollConfiguration.current
    return if (config != null) {
        remember(context, config) { DampingOverscrollEffect(context, config, onScroll) }
    } else {
        NoOpOverscrollEffect
    }
}

private class DrawOverscrollModifier(
    private val overscrollEffect: DampingOverscrollEffect,
    inspectorInfo: InspectorInfo.() -> Unit
) : DrawModifier, InspectorValueInfo(inspectorInfo) {

    override fun ContentDrawScope.draw() {
        drawContent()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawOverscrollModifier) return false

        return overscrollEffect == other.overscrollEffect
    }

    override fun hashCode(): Int {
        return overscrollEffect.hashCode()
    }

    override fun toString(): String {
        return "DrawOverscrollModifier(overscrollEffect=$overscrollEffect)"
    }
}

@OptIn(ExperimentalFoundationApi::class)
class DampingOverscrollEffect(
    context: Context,
    private val overscrollConfig: OverscrollConfiguration,
    private val onScroll: (Offset, NestedScrollSource) -> Unit
) : OverscrollEffect {
    private var pointerPosition: Offset? = null

    private var topEffect = EdgeEffectCompat.create(context, null)
    private var bottomEffect = EdgeEffectCompat.create(context, null)
    private var leftEffect = EdgeEffectCompat.create(context, null)
    private var rightEffect = EdgeEffectCompat.create(context, null)
    private val allEffects = listOf(leftEffect, topEffect, rightEffect, bottomEffect)

    // hack explanation: those edge effects are used to negate the previous effect
    // of the corresponding edge
    // used to mimic the render node reset that is not available in the platform
    private val topEffectNegation = EdgeEffectCompat.create(context, null)
    private val bottomEffectNegation = EdgeEffectCompat.create(context, null)
    private val leftEffectNegation = EdgeEffectCompat.create(context, null)
    private val rightEffectNegation = EdgeEffectCompat.create(context, null)

    init {
        allEffects.fastForEach { it.color = overscrollConfig.glowColor.toArgb() }
    }

    private val redrawSignal = mutableStateOf(Unit, neverEqualPolicy())

    @VisibleForTesting
    internal var invalidationEnabled = true

    private var scrollCycleInProgress: Boolean = false

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset
    ): Offset {
        onScroll(delta, source)
        Log.i("Damping", "applyToScroll: $delta, $source")
        // Early return
        if (containerSize.isEmpty()) {
            return performScroll(delta)
        }

        if (!scrollCycleInProgress) {
            stopOverscrollAnimation()
            scrollCycleInProgress = true
        }
        val pointer = pointerPosition ?: containerSize.center
        val consumedPixelsY = when {
            delta.y == 0f -> 0f
            topEffect.distanceCompat != 0f -> {
                pullTop(delta, pointer).also {
                    if (topEffect.distanceCompat == 0f) topEffect.onRelease()
                }
            }

            bottomEffect.distanceCompat != 0f -> {
                pullBottom(delta, pointer).also {
                    if (bottomEffect.distanceCompat == 0f) bottomEffect.onRelease()
                }
            }

            else -> 0f
        }
        val consumedPixelsX = when {
            delta.x == 0f -> 0f
            leftEffect.distanceCompat != 0f -> {
                pullLeft(delta, pointer).also {
                    if (leftEffect.distanceCompat == 0f) leftEffect.onRelease()
                }
            }

            rightEffect.distanceCompat != 0f -> {
                pullRight(delta, pointer).also {
                    if (rightEffect.distanceCompat == 0f) rightEffect.onRelease()
                }
            }

            else -> 0f
        }
        val consumedOffset = Offset(consumedPixelsX, consumedPixelsY)
        if (consumedOffset != Offset.Zero) invalidateOverscroll()

        val leftForDelta = delta - consumedOffset
        val consumedByDelta = performScroll(leftForDelta)
        val leftForOverscroll = leftForDelta - consumedByDelta

        var needsInvalidation = false
        if (source == NestedScrollSource.Drag) {
            // Ignore small deltas (< 0.5) as this usually comes from floating point rounding issues
            // and can cause scrolling to lock up (b/265363356)
            val appliedHorizontalOverscroll = if (leftForOverscroll.x > 0.5f) {
                pullLeft(leftForOverscroll, pointer)
                true
            } else if (leftForOverscroll.x < -0.5f) {
                pullRight(leftForOverscroll, pointer)
                true
            } else {
                false
            }
            val appliedVerticalOverscroll = if (leftForOverscroll.y > 0.5f) {
                pullTop(leftForOverscroll, pointer)
                true
            } else if (leftForOverscroll.y < -0.5f) {
                pullBottom(leftForOverscroll, pointer)
                true
            } else {
                false
            }
            needsInvalidation = appliedHorizontalOverscroll || appliedVerticalOverscroll
        }
        needsInvalidation = releaseOppositeOverscroll(delta) || needsInvalidation
        if (needsInvalidation) invalidateOverscroll()

        return consumedOffset + consumedByDelta
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        // Early return
        if (containerSize.isEmpty()) {
            performFling(velocity)
            return
        }
        val consumedX = if (velocity.x > 0f && leftEffect.distanceCompat != 0f) {
            leftEffect.onAbsorbCompat(velocity.x.roundToInt())
            velocity.x
        } else if (velocity.x < 0 && rightEffect.distanceCompat != 0f) {
            rightEffect.onAbsorbCompat(-velocity.x.roundToInt())
            velocity.x
        } else {
            0f
        }
        val consumedY = if (velocity.y > 0f && topEffect.distanceCompat != 0f) {
            topEffect.onAbsorbCompat(velocity.y.roundToInt())
            velocity.y
        } else if (velocity.y < 0f && bottomEffect.distanceCompat != 0f) {
            bottomEffect.onAbsorbCompat(-velocity.y.roundToInt())
            velocity.y
        } else {
            0f
        }
        val consumed = Velocity(consumedX, consumedY)
        if (consumed != Velocity.Zero) invalidateOverscroll()

        val remainingVelocity = velocity - consumed
        val consumedByVelocity = performFling(remainingVelocity)
        val leftForOverscroll = remainingVelocity - consumedByVelocity

        scrollCycleInProgress = false
        if (leftForOverscroll.x > 0) {
            leftEffect.onAbsorbCompat(leftForOverscroll.x.roundToInt())
        } else if (leftForOverscroll.x < 0) {
            rightEffect.onAbsorbCompat(-leftForOverscroll.x.roundToInt())
        }
        if (leftForOverscroll.y > 0) {
            topEffect.onAbsorbCompat(leftForOverscroll.y.roundToInt())
        } else if (leftForOverscroll.y < 0) {
            bottomEffect.onAbsorbCompat(-leftForOverscroll.y.roundToInt())
        }
        if (leftForOverscroll != Velocity.Zero) invalidateOverscroll()
        animateToRelease()
    }

    private var containerSize = Size.Zero

    override val isInProgress: Boolean
        get() {
            return allEffects.fastAny { it.distanceCompat != 0f }
        }

    private fun stopOverscrollAnimation(): Boolean {
        var stopped = false
        val fakeDisplacement = containerSize.center // displacement doesn't matter here
        if (leftEffect.distanceCompat != 0f) {
            pullLeft(Offset.Zero, fakeDisplacement)
            stopped = true
        }
        if (rightEffect.distanceCompat != 0f) {
            pullRight(Offset.Zero, fakeDisplacement)
            stopped = true
        }
        if (topEffect.distanceCompat != 0f) {
            pullTop(Offset.Zero, fakeDisplacement)
            stopped = true
        }
        if (bottomEffect.distanceCompat != 0f) {
            pullBottom(Offset.Zero, fakeDisplacement)
            stopped = true
        }
        return stopped
    }

    private val onNewSize: (IntSize) -> Unit = { size ->
        val differentSize = size.toSize() != containerSize
        containerSize = size.toSize()
        if (differentSize) {
            topEffect.setSize(size.width, size.height)
            bottomEffect.setSize(size.width, size.height)
            leftEffect.setSize(size.height, size.width)
            rightEffect.setSize(size.height, size.width)

            topEffectNegation.setSize(size.width, size.height)
            bottomEffectNegation.setSize(size.width, size.height)
            leftEffectNegation.setSize(size.height, size.width)
            rightEffectNegation.setSize(size.height, size.width)
        }
        if (differentSize) {
            invalidateOverscroll()
            animateToRelease()
        }
    }

    private var pointerId: PointerId? = null

    override val effectModifier: Modifier = Modifier
        .then(StretchOverscrollNonClippingLayer)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                pointerId = down.id
                pointerPosition = down.position
                do {
                    val pressedChanges = awaitPointerEvent().changes.fastFilter { it.pressed }
                    // If the same ID we are already tracking is down, use that. Otherwise, use
                    // the next down, to move the overscroll to the next pointer.
                    val change = pressedChanges
                        .fastFirstOrNull { it.id == pointerId } ?: pressedChanges.firstOrNull()
                    if (change != null) {
                        // Update the id if we are now tracking a new down
                        pointerId = change.id
                        pointerPosition = change.position
                    }
                } while (pressedChanges.isNotEmpty())
                pointerId = null
                // Explicitly not resetting the pointer position until the next down, so we
                // don't change any existing effects
            }
        }
        .onSizeChanged(onNewSize)
        .then(
            DrawOverscrollModifier(
                this@DampingOverscrollEffect,
                debugInspectorInfo {
                    name = "overscroll"
                    value = this@DampingOverscrollEffect
                })
        )

    fun DrawScope.drawOverscroll() {
//        if (containerSize.isEmpty()) {
//            return
//        }
//        this.drawIntoCanvas {
//            redrawSignal.value // <-- value read to redraw if needed
//            val canvas = it.nativeCanvas
//            var needsInvalidate = false
//            // each side workflow:
//            // 1. reset what was draw in the past cycle, effectively clearing the effect
//            // 2. Draw the effect on the edge
//            // 3. Remember how much was drawn to clear in 1. in the next cycle
//            if (leftEffectNegation.distanceCompat != 0f) {
//                drawRight(leftEffectNegation, canvas)
//                leftEffectNegation.finish()
//            }
//            if (!leftEffect.isFinished) {
//                needsInvalidate = drawLeft(leftEffect, canvas) || needsInvalidate
//                leftEffectNegation.onPullDistanceCompat(leftEffect.distanceCompat, 0f)
//            }
//            if (topEffectNegation.distanceCompat != 0f) {
//                drawBottom(topEffectNegation, canvas)
//                topEffectNegation.finish()
//            }
//            if (!topEffect.isFinished) {
//                needsInvalidate = drawTop(topEffect, canvas) || needsInvalidate
//                topEffectNegation.onPullDistanceCompat(topEffect.distanceCompat, 0f)
//            }
//            if (rightEffectNegation.distanceCompat != 0f) {
//                drawLeft(rightEffectNegation, canvas)
//                rightEffectNegation.finish()
//            }
//            if (!rightEffect.isFinished) {
//                needsInvalidate = drawRight(rightEffect, canvas) || needsInvalidate
//                rightEffectNegation.onPullDistanceCompat(rightEffect.distanceCompat, 0f)
//            }
//            if (bottomEffectNegation.distanceCompat != 0f) {
//                drawTop(bottomEffectNegation, canvas)
//                bottomEffectNegation.finish()
//            }
//            if (!bottomEffect.isFinished) {
//                needsInvalidate = drawBottom(bottomEffect, canvas) || needsInvalidate
//                bottomEffectNegation.onPullDistanceCompat(bottomEffect.distanceCompat, 0f)
//            }
//            if (needsInvalidate) invalidateOverscroll()
//        }
    }

    private fun DrawScope.drawLeft(left: EdgeEffect, canvas: NativeCanvas): Boolean {
        val restore = canvas.save()
        canvas.rotate(270f)
        canvas.translate(
            -containerSize.height,
            overscrollConfig.drawPadding.calculateLeftPadding(layoutDirection).toPx()
        )
        val needsInvalidate = left.draw(canvas)
        canvas.restoreToCount(restore)
        return needsInvalidate
    }

    val TAG = "DampingOverscrollEffect"
    private fun DrawScope.drawTop(top: EdgeEffect, canvas: NativeCanvas): Boolean {
        val restore = canvas.save()
        canvas.translate(0f, overscrollConfig.drawPadding.calculateTopPadding().toPx())
        Log.i(
            TAG,
            "drawTop: ${overscrollConfig.drawPadding.calculateTopPadding()}, $overscrollConfig"
        )
        val needsInvalidate = top.draw(canvas)
        canvas.restoreToCount(restore)
        return needsInvalidate
    }

    private fun DrawScope.drawRight(right: EdgeEffect, canvas: NativeCanvas): Boolean {
        val restore = canvas.save()
        val width = containerSize.width.roundToInt()
        val rightPadding = overscrollConfig.drawPadding.calculateRightPadding(layoutDirection)
        canvas.rotate(90f)
        canvas.translate(0f, -width.toFloat() + rightPadding.toPx())
        val needsInvalidate = right.draw(canvas)
        canvas.restoreToCount(restore)
        return needsInvalidate
    }

    private fun DrawScope.drawBottom(bottom: EdgeEffect, canvas: NativeCanvas): Boolean {
        val restore = canvas.save()
        canvas.rotate(180f)
        val bottomPadding = overscrollConfig.drawPadding.calculateBottomPadding().toPx()
        canvas.translate(-containerSize.width, -containerSize.height + bottomPadding)
        val needsInvalidate = bottom.draw(canvas)
        canvas.restoreToCount(restore)
        return needsInvalidate
    }

    private fun invalidateOverscroll() {
        if (invalidationEnabled) {
            redrawSignal.value = Unit
        }
    }

    // animate the edge effects to 0 (no overscroll). Usually needed when the finger is up.
    private fun animateToRelease() {
        var needsInvalidation = false
        allEffects.fastForEach {
            it.onRelease()
            needsInvalidation = it.isFinished || needsInvalidation
        }
        if (needsInvalidation) invalidateOverscroll()
    }

    private fun releaseOppositeOverscroll(delta: Offset): Boolean {
        var needsInvalidation = false
        if (!leftEffect.isFinished && delta.x < 0) {
            leftEffect.onReleaseWithOppositeDelta(delta = delta.x)
            needsInvalidation = leftEffect.isFinished
        }
        if (!rightEffect.isFinished && delta.x > 0) {
            rightEffect.onReleaseWithOppositeDelta(delta = delta.x)
            needsInvalidation = needsInvalidation || rightEffect.isFinished
        }
        if (!topEffect.isFinished && delta.y < 0) {
            topEffect.onReleaseWithOppositeDelta(delta = delta.y)
            needsInvalidation = needsInvalidation || topEffect.isFinished
        }
        if (!bottomEffect.isFinished && delta.y > 0) {
            bottomEffect.onReleaseWithOppositeDelta(delta = delta.y)
            needsInvalidation = needsInvalidation || bottomEffect.isFinished
        }
        return needsInvalidation
    }

    private fun pullTop(scroll: Offset, displacement: Offset): Float {
        val displacementX: Float = displacement.x / containerSize.width
        val pullY = scroll.y / containerSize.height
        val consumed = topEffect.onPullDistanceCompat(pullY, displacementX) * containerSize.height
        Log.i(TAG, "pullTop: ${consumed}")
        // If overscroll is showing, assume we have consumed all the provided scroll, and return
        // that amount directly to avoid floating point rounding issues (b/265363356)
        return if (topEffect.distanceCompat != 0f) {
            scroll.y
        } else {
            consumed
        }
    }

    private fun pullBottom(scroll: Offset, displacement: Offset): Float {
        val displacementX: Float = displacement.x / containerSize.width
        val pullY = scroll.y / containerSize.height
        val consumed = -bottomEffect.onPullDistanceCompat(
            -pullY,
            1 - displacementX
        ) * containerSize.height
        // If overscroll is showing, assume we have consumed all the provided scroll, and return
        // that amount directly to avoid floating point rounding issues (b/265363356)
        return if (bottomEffect.distanceCompat != 0f) {
            scroll.y
        } else {
            consumed
        }
    }

    private fun pullLeft(scroll: Offset, displacement: Offset): Float {
        val displacementY: Float = displacement.y / containerSize.height
        val pullX = scroll.x / containerSize.width
        val consumed = leftEffect.onPullDistanceCompat(
            pullX,
            1 - displacementY
        ) * containerSize.width
        // If overscroll is showing, assume we have consumed all the provided scroll, and return
        // that amount directly to avoid floating point rounding issues (b/265363356)
        return if (leftEffect.distanceCompat != 0f) {
            scroll.x
        } else {
            consumed
        }
    }

    private fun pullRight(scroll: Offset, displacement: Offset): Float {
        val displacementY: Float = displacement.y / containerSize.height
        val pullX = scroll.x / containerSize.width
        val consumed = -rightEffect.onPullDistanceCompat(
            -pullX,
            displacementY
        ) * containerSize.width
        // If overscroll is showing, assume we have consumed all the provided scroll, and return
        // that amount directly to avoid floating point rounding issues (b/265363356)
        return if (rightEffect.distanceCompat != 0f) {
            scroll.x
        } else {
            consumed
        }
    }
}

internal val MaxSupportedElevation = 30.dp

private val StretchOverscrollNonClippingLayer: Modifier =
    // we only need to fix the layer size when the stretch overscroll is active (Android 12+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val extraSizePx = (MaxSupportedElevation * 2).roundToPx()
                layout(
                    placeable.measuredWidth - extraSizePx,
                    placeable.measuredHeight - extraSizePx
                ) {
                    // because this modifier report the size which is larger than the passed max
                    // constraints this larger box will be automatically centered within the
                    // constraints. we need to first add out offset and then neutralize the centering.
                    placeable.placeWithLayer(
                        -extraSizePx / 2 - (placeable.width - placeable.measuredWidth) / 2,
                        -extraSizePx / 2 - (placeable.height - placeable.measuredHeight) / 2
                    )
                }
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val extraSizePx = (MaxSupportedElevation * 2).roundToPx()
                val width = placeable.width + extraSizePx
                val height = placeable.height + extraSizePx
                layout(width, height) {
                    placeable.place(extraSizePx / 2, extraSizePx / 2)
                }
            }
    } else {
        Modifier
    }

@OptIn(ExperimentalFoundationApi::class)
internal object NoOpOverscrollEffect : OverscrollEffect {
    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset
    ): Offset = performScroll(delta)

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        performFling(velocity)
    }

    override val isInProgress: Boolean
        get() = false

    override val effectModifier: Modifier
        get() = Modifier
}