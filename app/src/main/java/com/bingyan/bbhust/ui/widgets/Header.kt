package com.bingyan.bbhust.ui.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.AppTypography
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.colors


/**
 * 顶部Bar（带Forward按钮）
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Header(
    name: String,
    color: Color = colors.titleBar,
    subtitle: String? = null,
    navController: NavHostController? = null,
    onDoubleTap: () -> Unit = {},
    forward: @Composable BoxScope.() -> Unit = {},
    onClose: () -> Unit = { navController?.popBackStack() },
) {
    Row(
        Modifier
            .combinedClickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onDoubleClick = onDoubleTap
            ) {}
            .fillMaxWidth()
            .background(color)
            .padding(horizontal = Gap.Mid, vertical = Gap.Mid),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EasyImage(
            src = R.drawable.close_line,
            contentDescription = stringResource(id = R.string.close),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable {
                    onClose()
                }
                .size(ImageSize.Normal)
                .padding(Gap.Tiny)
                .align(Alignment.CenterVertically),
            tint = colors.textPrimary
        )
        Spacer(modifier = Modifier.width(Gap.Mid))
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            Text(
                text = name,
                color = colors.textPrimary,
                style = AppTypography.title,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = colors.textSecondary,
                    style = AppTypography.subtitle,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(Modifier.align(Alignment.CenterVertically)) {
            forward()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TitleSpacer(color: Color = colors.titleBar) {
    Spacer(
        modifier = Modifier
            .background(color)
            .fillMaxWidth()
            .padding(
                WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()
            )
    )
}

@Composable
fun Divider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.8.dp)
            .background(colors.divider)
    )
}

@Composable
fun NormalHeader(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(Color.Transparent)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = AppTypography.title,
            color = colors.textPrimary,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = Gap.Big, vertical = Gap.Mid)
        )
        content()
    }
}