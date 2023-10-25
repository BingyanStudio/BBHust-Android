package com.bingyan.bbhust.ui.screen.home.main


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.AppRoute
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.common.TabBar
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.provider.jump
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.RoundedShapes
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.widgets.EasyImage
import com.bingyan.bbhust.ui.widgets.gradient
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.px2dp
import com.bingyan.bbhust.utils.string
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onTop: Int,
) {
    val animationToTops = remember {
        mutableStateListOf(-1, -1, -1)
    }
    val pagerState = rememberPagerState { 3 }
    LaunchedEffect(onTop) {
        if (onTop != -1) {
            animationToTops[pagerState.currentPage] =
                (animationToTops[pagerState.currentPage] + 1) % 2
        }
    }
    val toolbarHeightPx = remember {
        mutableFloatStateOf(0f)
    }
    val toolbarOffsetHeightPx = remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.floatValue + delta
                toolbarOffsetHeightPx.floatValue =
                    newOffset.coerceIn(-toolbarHeightPx.floatValue, 0f)
                return Offset.Zero
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        //Tab + 列表
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = toolbarHeightPx.floatValue.px2dp)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.floatValue.roundToInt()) }
        ) {
            Column(
                Modifier
                    .click { }
                    .fillMaxWidth()
                    .background(colors.titleBar)) {
                val scope = rememberCoroutineScope()
                TabBar(modifier = Modifier.padding(horizontal = Gap.Big),
                    pagerState = pagerState,
                    horizontalArrangement = Arrangement.spacedBy(Gap.Big),
                    onClick = {
                        scope.launch {
                            if (it == pagerState.currentPage) {
                                animationToTops[pagerState.currentPage] =
                                    (animationToTops[pagerState.currentPage] + 1) % 2
                            } else {
                                pagerState.animateScrollToPage(it)
                            }
                        }
                    }) { i, offset ->
                    val str = when (i) {
                        0 -> R.string.recommend.string
                        1 -> R.string.realtime.string
                        else -> R.string.following.string
                    }
                    Text(
                        text = str,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700,
                        modifier = if (offset != 0f) {
                            Modifier
                                .alpha(offset)
                                .gradient()
                        } else {
                            Modifier
                        }.padding(vertical = Gap.Mid),
                        color = colors.secondary
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.8.dp)
                        .background(colors.divider)
                )
            }
            HorizontalPager(
                state = pagerState,// flingBehavior = pagerFlingBehavior(state = pagerState)
            ) { page ->
                when (page) {
                    0 -> MainList(
                        type = MainViewModel.Type.Recommend,
                        paddingValues = paddingValues,
                        animation = animationToTops[0]
                    )

                    1 -> MainList(
                        type = MainViewModel.Type.Latest,
                        paddingValues = paddingValues,
                        animation = animationToTops[1]
                    )

                    2 -> MainList(
                        type = MainViewModel.Type.Follow,
                        paddingValues = paddingValues,
                        animation = animationToTops[2]
                    )
                }
            }
        }

        // 顶部标题栏
        Row(
            Modifier
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.floatValue.roundToInt()) }
                .onSizeChanged { size ->
                    toolbarHeightPx.floatValue = size.height.toFloat()
                }
                .background(colors.titleBar)
                .padding(horizontal = Gap.Big, vertical = Gap.Mid)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            EasyImage(
                src = R.drawable.logo,
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .height(ImageSize.Big),
                scale = ContentScale.FillHeight,
            )
            Spacer(modifier = Modifier.weight(1f))
            val nav = LocalNav.current
            EasyImage(src = R.drawable.icon_search,
                contentDescription = stringResource(id = R.string.search),
                modifier = Modifier
                    .size(ImageSize.Normal)
                    .clip(RoundedShapes.medium)
                    .clickable {
                        nav.jump(AppRoute.SEARCH)
                    }
                    .padding(Gap.Small),
                scale = ContentScale.Crop,
                tint = colors.textPrimary)
        }
    }
}
