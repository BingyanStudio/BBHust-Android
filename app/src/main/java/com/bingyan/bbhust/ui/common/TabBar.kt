package com.bingyan.bbhust.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.dp2px
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabBar(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onClick: (Int) -> Unit,
    content: @Composable (Int, Float) -> Unit
) {
    val rowSize = remember {
        mutableStateListOf<Int>()
    }
    val tabScroll = rememberScrollState()
    LaunchedEffect(pagerState.currentPage) {
        val target = (pagerState.currentPage - 3).coerceAtLeast(0)
        tabScroll.animateScrollTo(
            rowSize.subList(0, target).sum() +
                    target *
                    horizontalArrangement.spacing.dp2px.toInt()
        )
    }
    Row(
        modifier = modifier
            .horizontalScroll(
                tabScroll
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        for (i in 0 until pagerState.pageCount) {
            val offsets = (calculateOffsets(pagerState, i) * 10).roundToInt() / 10f
            //根据偏移量设置颜色，0为选中，1为未选中
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .click {
                        onClick(i)
                    }
                    .onPlaced {
                        rowSize.add(it.size.width)
                    }
            ) {
                content(
                    i,
                    0f,
                )//铺底内容，未选中
                content(
                    i, 1 - offsets
                )//选中内容
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun calculateOffsets(state: PagerState, routeNumber: Int): Float {
    val offset = state.currentPageOffsetFraction
    val selected = (state.currentPage == routeNumber)
    val pageOffset = routeNumber - state.currentPage//页面偏移值，当前选中页面位置与指定页面的位置差
    val offsetD = offset.coerceIn(-1f, 1f)
    return if (selected) {
        abs(offsetD)//当前页面
    } else if (pageOffset < 0) {//前面的页面
        if (offset < 0) {
            if (offset - pageOffset < 0) {//继续滑动，远离当前页面，反方向渐变
                -(offset - pageOffset)
            } else if (offset - pageOffset <= 1) {
                offset - pageOffset
            } else {
                1f//继续远离，无关，置灰
            }
        }//向前滑动，靠近当前页面，启动渐变
        else {
            1f
        }//向后滑动，保持原状
    } else if (pageOffset > 0) {//后面的页面
        if (offset > 0) {
            if (pageOffset - offset < 0) {//继续滑动，远离当前页面，反方向渐变
                -(pageOffset - offset)
            } else if (pageOffset - offset <= 1) {//向后滑动，靠近当前页面，启动渐变
                pageOffset - offset
            } else {
                1f//继续远离，无关，置灰
            }
        } else {
            1f
        }//向前滑动，无关，置灰
    } else {//其余页面，无关，置灰
        1f
    }
}
