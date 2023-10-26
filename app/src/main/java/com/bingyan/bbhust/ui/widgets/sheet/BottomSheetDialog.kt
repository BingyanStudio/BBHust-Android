package com.bingyan.bbhust.ui.widgets.sheet

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
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

enum class BottomSheetDialogValue {
    Hide,
    Collapsed,
    Expanded
}