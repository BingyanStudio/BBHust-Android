package com.bingyan.bbhust.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Elevation
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.RoundedShapes
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.utils.clickRipple

@Composable
fun RoundButton(
    modifier: Modifier = Modifier,
    icon: Int,
    contentDescription: String,
    color: Color = colors.primaryBtnBg,
    tint: Color? = null,
    elevation: Dp = Elevation.Big,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(vertical = Gap.Big * 2, horizontal = Gap.Big),
        elevation = elevation,
        shape = RoundedShapes.medium,
        backgroundColor = color
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedShapes.medium)
                .clickable(onClick = onClick)
                .padding(Gap.Big)
        ) {
            EasyImage(
                modifier = Modifier.size(ImageSize.Mid),
                src = icon,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}

@Composable
fun EllipseButton(
    modifier: Modifier = Modifier,
    icon: Int,
    contentDescription: String,
    color: Color = colors.primaryBtnBg,
    tint: Color? = null,
    elevation: Dp = Elevation.Big,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = RoundedCornerShape(30),
        backgroundColor = color
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30))
                .clickable(onClick = onClick)
                .padding(Gap.Mid * 1.5f)
        ) {
            EasyImage(
                modifier = Modifier.size(ImageSize.Small),
                src = icon,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}

@Composable
fun StokeButton(
    modifier: Modifier = Modifier,
    color: Color = colors.textPrimary,
    textColor: Color = colors.textPrimary,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(CardShapes.large)
            .clickRipple { onClick() }
            .border(2.dp, color, CardShapes.large)
            .padding(horizontal = Gap.Mid, vertical = Gap.Big),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp
        )
    }
}

@Composable
fun FillButton(
    modifier: Modifier = Modifier,
    color: Color = colors.card,
    textColor: Color = colors.onCard,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(CardShapes.large)
            .clickRipple { onClick() }
            .background(color)
            .padding(horizontal = Gap.Mid, vertical = Gap.Big),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp
        )
    }
}