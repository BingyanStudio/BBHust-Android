package com.bingyan.bbhust.ui.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.colors


@Composable
fun LoadingBar(
    modifier: Modifier = Modifier,
    size: LoadingBarSize = LoadingBarSize.Normal
) {
    CircularProgressIndicator(
        modifier = modifier.size(
            when (size) {
                LoadingBarSize.Tiny -> ImageSize.Small
                LoadingBarSize.Small -> ImageSize.Mid
                LoadingBarSize.Normal -> ImageSize.Normal
                LoadingBarSize.Big -> ImageSize.Big
            }
        ), color = colors.secondary, strokeWidth =
        when (size) {
            LoadingBarSize.Tiny -> 2.dp
            LoadingBarSize.Normal, LoadingBarSize.Small, LoadingBarSize.Big -> 4.dp
        }
    )
}

enum class LoadingBarSize {
    Tiny, Small, Normal, Big
}