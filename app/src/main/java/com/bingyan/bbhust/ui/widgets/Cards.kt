package com.bingyan.bbhust.ui.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bingyan.bbhust.ui.theme.AppTypography
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.utils.click

/**
 * 带标题卡片
 */
@Composable
fun TitleCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    NormalCard(modifier = modifier) {
        Box(Modifier.padding(horizontal = 0.dp, vertical = 0.dp)) {
            Text(
                text = title,
                color = colors.textPrimary,
                style = AppTypography.card_title
            )
        }
        content()
    }
}

/**
 * 普通卡片
 */
@Composable
fun NormalCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(CardShapes.medium)
            .background(colors.card, CardShapes.medium)
            .padding(Gap.Big)
    ) {
        content()
    }
}

/**
 * 普通卡片
 */
@Composable
fun PlainCard(
    modifier: Modifier = Modifier,
    inner: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(CardShapes.medium)
            .background(colors.card, CardShapes.medium)
            .padding(inner)//inner inner
    ) {
        content()
    }
}

/**
 * 水平普通卡片
 */
@Composable
fun HorizontalCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 12.dp,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShapes.medium)
            .background(colors.card, CardShapes.medium)
            .padding(Gap.Big),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

/**
 * 可折叠展开卡片(带图标)
 */
/*
@Composable
fun StretchCard(
    modifier: Modifier = Modifier,
    title: String,
    vState: MutableState<Boolean>,
    content: @Composable ColumnScope.() -> Unit,
) {
    var state by remember {
        vState
    }
    val rotationAngle by animateFloatAsState(
        targetValue = if (state) 180F else 0F,
        animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing)
    )
    NormalCard(modifier = modifier
        .click {
            state = !state
        }) {
        Row(
            Modifier
                .padding(horizontal = 0.dp, vertical = 0.dp)
        ) {
            Text(
                text = title,
                color = colors.textPrimary,
                style = AppTypography.card_title
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .size(ImageSize.Mid)
                    .rotate(rotationAngle)
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.arrow_down_line),
                contentDescription = stringResource(
                    id = if (!state) R.string.expand else R.string.shrink
                )
            )
        }
        Column(Modifier.animateContentSize()) {
            if (state) {
                Spacer(modifier = Modifier.height(Gap.Small))
                content()
            }
        }
    }
}
*/

/**
 * 伸缩卡片(不带图标)
 */
@Composable
fun TagCard(
    modifier: Modifier = Modifier,
    title: String,
    vState: MutableState<Boolean>,
    content: @Composable ColumnScope.() -> Unit,
) {
    var state by remember {
        vState
    }
    NormalCard(modifier = modifier
        .click {
            state = !state
        }) {
        Row(
            Modifier
                .padding(horizontal = 0.dp, vertical = 0.dp)
        ) {
            Text(
                text = title,
                color = colors.textPrimary,
                style = AppTypography.card_title
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(Modifier.animateContentSize()) {
            if (state) {
                Spacer(modifier = Modifier.height(Gap.Small))
                content()
            }
        }
    }
}