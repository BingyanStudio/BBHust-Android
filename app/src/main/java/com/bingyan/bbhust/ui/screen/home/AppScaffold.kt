package com.bingyan.bbhust.ui.screen.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.bingyan.bbhust.ui.common.BottomNavBarView
import com.bingyan.bbhust.ui.screen.home.main.MainScreen
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.widgets.pager.AppHorizontalPager
import com.bingyan.bbhust.ui.widgets.pager.rememberPagerState

object AppScaffold {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun View(
        nav: NavHostController
    ) {
        val state = rememberPagerState(
            pageCount = 4,
            initialOffscreenLimit = 3
        )
        val onTops = remember {
            mutableStateListOf(-1, -1, -1, -1)
        }
//	LaunchedEffect(Msg.pageMod.value) {
//		if (Msg.pageMod.value != -1) {
//			state.animateScrollToPage(Msg.pageMod.value)
//			Msg.pageMod.value = -1
//		}
//	}
        val scaffoldState = rememberScaffoldState()
        Column {
            Spacer(
                modifier = Modifier
                    .background(colors.titleBar)
                    .fillMaxWidth()
                    .padding(
                        WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()
                    )
            )
            Scaffold(
                modifier = Modifier
                    .navigationBarsPadding(),
                scaffoldState = scaffoldState,
                backgroundColor = Color.Transparent,
                bottomBar = {
                    BottomNavBarView(state) {
                        onTops[it] = (++onTops[it]) % 2
                    }
                },
                content = {
                    AppHorizontalPager(
                        state = state,
                        dragEnabled = false
                    ) { page ->
                        val onTop = onTops[page]
                        when (page) {
                            0 -> MainScreen(it, onTop)
//						1 -> TagScreen(it, onTop)
//						2 -> NotificationScreen(it, onTop)
//						3 -> MeScreen(it, onTop)
                        }
                    }
                },
            )
        }
    }
}