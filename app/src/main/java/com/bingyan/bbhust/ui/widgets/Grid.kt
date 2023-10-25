package com.bingyan.bbhust.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout


@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxRows: Int,
    content: @Composable () -> Unit
) {
//    val gridScope = GridScope().apply { content() }
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columnWidth = constraints.maxWidth / maxRows
        val itemConstraints = constraints.copy(minWidth = 0, maxWidth = columnWidth)
        val rowsHeight = mutableListOf<Int>()
        val placeables = measurables.mapIndexed { index, measurable ->
            val row = index / maxRows
            if (row >= rowsHeight.size) rowsHeight.addAll(Array(row - rowsHeight.size + 1) { 0 })
            val placeable = measurable.measure(itemConstraints)
            val maxHeight = rowsHeight[row]
            if (placeable.height > maxHeight) {
                rowsHeight[row] = placeable.height
            }
            placeable
        }
        val height = rowsHeight.sum()
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            var accumulateHeight = 0
            placeables.forEachIndexed { index, placeable ->  // 水平放置，满了以后换新行
                if (index % maxRows == 0) {
                    val row = index / maxRows - 1
                    if (row >= 0)
                        accumulateHeight += rowsHeight[row] // 新一行
                }
                placeable.place(
                    x = columnWidth * (index % maxRows),
                    y = accumulateHeight
                )
            }
        }
    }
}

class GridScope {
    val list = mutableListOf<@Composable (Modifier) -> Unit>()
    fun item(content: @Composable () -> Unit) {
        list.add {
            Box(modifier = it) {
                content()
            }
        }
    }

    fun <T> items(data: List<T>, content: @Composable (T) -> Unit) {
        data.forEach { dat ->
            list.add {
                Box(modifier = it) {
                    content(dat)
                }
            }
        }
    }

    fun <T> items(data: Set<T>, content: @Composable (T) -> Unit) {
        data.forEach { dat ->
            list.add {
                Box(modifier = it) {
                    content(dat)
                }
            }
        }
    }

}