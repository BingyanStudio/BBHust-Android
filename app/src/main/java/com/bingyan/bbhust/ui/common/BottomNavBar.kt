package com.bingyan.bbhust.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bingyan.bbhust.AppRoute
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.theme.CardShapesTopHalf
import com.bingyan.bbhust.ui.theme.Elevation
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.widgets.EasyImage
import com.bingyan.bbhust.ui.widgets.EllipseButton
import com.bingyan.bbhust.ui.widgets.pager.PagerState
import com.bingyan.bbhust.utils.string
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 底部导航栏
 */
@Composable
fun BottomNavBarView(
    page: PagerState,
//    vm: NotifyViewModel = viewModel(),
    onTop: (Int) -> Unit = {}
) {
    val bottomNavList = listOf(
        BottomNavRoute.Home,
        BottomNavRoute.Tags,
        BottomNavRoute.Add,
        BottomNavRoute.Notification,
        BottomNavRoute.Me,
    )
    val nav = LocalNav.current
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(Gap.Mid, CardShapesTopHalf.large)
            .background(colors.card.copy(alpha = 1f))
            .clip(CardShapesTopHalf.large),
    ) {
        bottomNavList.forEach { screen ->
            val selected = page.currentPage == screen.routeNumber
            val offsets = (calculateOffsets(page, screen.routeNumber) * 10).roundToInt() / 10f
            //根据偏移量设置颜色，0为选中，1为未选中
            BottomNavigationItem(
                enabled = screen.routeNumber != -1,
                modifier = Modifier.background(Color.Transparent),
                icon = {
                    if (screen.routeNumber != -1) {
                        EasyImage(
                            modifier = Modifier
                                .size(ImageSize.Mid),
                            src = screen.icon,
                            contentDescription = stringResource(id = screen.stringId),
                            tint = colors.secondary,
                        )
                        RedotBox(
                            num = if (screen.routeNumber == Route.Notification)
//                                vm.unreadCount.value
                                0
                            else 0
                        ) {
                            EasyImage(
                                modifier = Modifier
                                    .size(ImageSize.Mid),
                                /*.then(
                                    if (screen.routeNumber == Route.Notification)
                                        Modifier.redNumPoint(vm.unreadCount.value)
                                    else Modifier
                                )*/
                                src = screen.iconSelected,
                                contentDescription = string(screen.stringId),
                                alpha = 1 - offsets,//根据偏移量设置选中图标透明度
                            )
                        }
                    } else {
                        EllipseButton(
                            icon = R.drawable.bottom_add,
                            color = colors.secondary,
                            elevation = Elevation.Zero,
                            contentDescription = stringResource(id = R.string.create_post)
                        ) {
                            nav.navigate(AppRoute.POST_CREATE)
                        }
                    }
                },
                alwaysShowLabel = false,
                selected = selected,
                onClick = {
                    if (screen.routeNumber != -1) {
                        scope.launch {
                            if (screen.routeNumber == page.currentPage) {
                                onTop(screen.routeNumber)
                            } else {
                                page.animateScrollToPage(screen.routeNumber, skipPages = false)
                            }
                        }
                    }
                })
        }
    }
}

/**
 * 计算offsets
 *
 * offsets:根据当前位置与offset综合计算得出指定项的偏移状态，0为选中，1为未选中，0~1之间为选中与未选中之间的状态
 */
fun calculateOffsets(state: PagerState, routeNumber: Int): Float {
    val offsetInt = state.currentPageOffset.toInt()
    val selected = (state.currentPage + offsetInt) == routeNumber
    val offset =
        state.currentPageOffset - offsetInt// offset 当前页面为基准，偏移至后一页面为正，前一页面为负，介于0~1或-1~0的float
    val pageOffset =
        (routeNumber - state.currentPage - offsetInt)//页面偏移值，当前选中页面位置与指定页面的位置差
    val offsetD = offset.coerceIn(-1f, 1f)//offsetD 选中项的偏移值，偏移值1极为未选中，设置为1
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