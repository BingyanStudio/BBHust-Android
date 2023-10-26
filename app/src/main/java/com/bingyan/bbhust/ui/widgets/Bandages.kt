package com.bingyan.bbhust.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.utils.click

/**
 * 标签
 */
@Composable
fun NormalBandage(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.White,
    background: Color = colors.primaryBtnBg,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CardShapes.small)
            .background(background, CardShapes.small)
            .click { onClick() }
            .padding(horizontal = Gap.Mid * 1.5f, vertical = Gap.Small)
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
