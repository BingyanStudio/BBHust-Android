package com.bingyan.bbhust.ui.widgets.sheet

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.bingyan.bbhust.ui.theme.CardShapesTopHalf
import com.bingyan.bbhust.ui.theme.Transparent
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.heightDp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
class AppBottomSheetDialog {
    @Composable
    fun Build(
        show: Boolean,
        state: ShowBottomSheet,
        content: @Composable ()->Unit
    ) {
        content()
        LaunchedEffect(state.state.currentValue) {
            if (state.state.currentValue == BottomSheetDialogValue.Hide) {
                state.onDismiss()
            }
        }
            Surface(color = Transparent) {
                AnimatedVisibility(
                    visible = show, enter = fadeIn(), exit = fadeOut()
                ) {
                    Spacer(modifier = Modifier
                        .background(Color.Black.copy(0.4f))
                        .fillMaxSize()
                        .click {
                            state.onDismiss()
                        })
                }
            }
            AnimatedVisibility(visible = show, enter = slideInVertically {
                it
            }, exit = slideOutVertically {
                it
            }) {
                BottomSheetDialog(
                    modifier = Modifier.systemBarsPadding(),
                    bottomSheetDialogState = state.state,
                    sheetPeekHeight = heightDp(state.peek),
                    sheetShape = CardShapesTopHalf.large,
                    sheetBackgroundColor = colors.background,
                    sheetGesturesEnabled = state.draggable.value,
                    sheetElevation = 0.dp
                ) {
                    state.content(state.onDismiss)
                }

            }
    }

    @Composable
    fun BottomSheetDialog(
        modifier: Modifier = Modifier,
        bottomSheetDialogState: BottomSheetDialogState,
        sheetGesturesEnabled: Boolean = true,
        sheetShape: Shape = MaterialTheme.shapes.large,
        sheetElevation: Dp = BottomSheetScaffoldDefaults.SheetElevation,
        sheetBackgroundColor: Color = MaterialTheme.colors.surface,
        sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
        sheetPeekHeight: Dp = BottomSheetScaffoldDefaults.SheetPeekHeight,
        sheetContent: @Composable ColumnScope.() -> Unit,
    ) {
        val scope = rememberCoroutineScope()
        BoxWithConstraints(modifier) {
            val fullHeight = constraints.maxHeight.toFloat()
            val peekHeightPx = with(LocalDensity.current) { sheetPeekHeight.toPx() }
            var bottomSheetHeight by remember { mutableStateOf(fullHeight) }
            val swipeable = Modifier
                .nestedScroll(bottomSheetDialogState.nestedScrollConnection)
                .dialogSwipeable(
                    state = bottomSheetDialogState,
                    anchors = mapOf(
                        fullHeight to BottomSheetDialogValue.Hide,
                        fullHeight - peekHeightPx to BottomSheetDialogValue.Collapsed,
                        fullHeight - bottomSheetHeight to BottomSheetDialogValue.Expanded
                    ),
                    orientation = Orientation.Vertical,
                    enabled = sheetGesturesEnabled,
                    resistance = null
                )
                .semantics {
                    if (peekHeightPx != bottomSheetHeight) {
                        if (bottomSheetDialogState.isCollapsed) {
                            expand {
                                if (bottomSheetDialogState.confirmStateChange(BottomSheetDialogValue.Expanded)) {
                                    scope.launch { bottomSheetDialogState.expand() }
                                }
                                true
                            }
                        } else {
                            collapse {
                                if (bottomSheetDialogState.confirmStateChange(BottomSheetDialogValue.Collapsed)) {
                                    scope.launch { bottomSheetDialogState.collapse() }
                                }
                                true
                            }
                        }
                    }
                }
            Layout(
                content = {
                    Surface(
                        swipeable
                            .fillMaxWidth()
                            .requiredHeightIn(min = sheetPeekHeight)
                            .onGloballyPositioned {
                                bottomSheetHeight = it.size.height.toFloat()
                            },
                        shape = sheetShape,
                        elevation = sheetElevation,
                        color = sheetBackgroundColor,
                        contentColor = sheetContentColor,
                        content = { Column(content = sheetContent) }
                    )
                }
            ) { measurables, constraints ->
                layout(constraints.maxWidth, constraints.maxHeight) {
                    val sheetPlaceable =
                        measurables.first().measure(constraints.copy(minWidth = 0, minHeight = 0))
                    val sheetOffsetY = bottomSheetDialogState.offset.value.roundToInt()
                    sheetPlaceable.placeRelative(0, sheetOffsetY)
                }
            }
        }
    }
}

data class ShowBottomSheet(
    val onDismiss: () -> Unit,
    val draggable: MutableState<Boolean>,
    val state:BottomSheetDialogState,
    val peek: Float = 0.8f,
    val content: @Composable (onDismiss:()->Unit) -> Unit
)

enum class BottomSheetDialogValue {
    Hide,
    Collapsed,
    Expanded
}