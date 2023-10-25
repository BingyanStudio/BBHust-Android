package com.bingyan.bbhust.ui.screen.home.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bingyan.bbhust.R
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.ui.widgets.LoadingBar
import com.bingyan.bbhust.ui.widgets.app.PostCard
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.string
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainList(
    type: MainViewModel.Type,
    paddingValues: PaddingValues,
    animation: Int, // 点击底部按钮返回顶部并刷新
    vm: MainViewModel = viewModel(),
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val vmState = vm.state
    val list = vmState.list(type)
    LaunchedEffect(animation) {
        if (animation != -1) {
            listState.animateScrollToItem(0)
            val firstId = if (list.isNotEmpty()) list[0].id else null
            vm act MainAction.LoadFeedStream(type) {
                scope.launch {
                    if ((firstId != if (list.isNotEmpty()) list[0].id else null) || (type == MainViewModel.Type.Recommend)) {
                        delay(350)
                        withContext(Dispatchers.Main) {
                            listState.scrollToItem(0)
                        }
                    }
                }
            }
        } else if (list.isEmpty()) {
            vm act MainAction.LoadFeedStream(type, refresh = false)
        }
    }
    val state = vm.state.state(type)
    val isRefresh = state is FourState.Loading
    LaunchedEffect(state) {
        Log.e("State", list.size.toString())
    }
    LaunchedEffect(vm.state) {
        Log.e("VM State", vm.state.listMap[type]?.list?.size.toString())
    }
    val refreshState = rememberPullRefreshState(refreshing = isRefresh,
        onRefresh = {
            val firstId = if (list.isNotEmpty()) list[0].id else null
            vm act MainAction.LoadFeedStream(type) {
                scope.launch {
                    if ((firstId != if (list.isNotEmpty()) list[0].id else null) || (type == MainViewModel.Type.Recommend)) {
                        delay(350)
                        withContext(Dispatchers.Main) {
                            listState.scrollToItem(0)
                        }
                    }
                }
            }
        })

    Box(
        Modifier.pullRefresh(refreshState),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = paddingValues
        ) {
            items(
                list,
                key = { it.id + "[MainList:${type.name}]" + it.hashCode() }
            ) {
                PostCard(it) { post ->
                    vm act MainAction.LikePost(
                        post.id,
                        !post.liked
                    )
                }
            }
            item {
                LaunchedEffect(list.size) {
                    if (list.isNotEmpty() && state != FourState.Idle(true))
                    // 继续加载
                        vm act MainAction.LoadFeedStream(type, list.last().reply_time)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Gap.Big),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        is FourState.Idle, is FourState.Error -> {
                            BottomTextComponent(
                                modifier = if (list.isEmpty())
                                    Modifier.fillParentMaxHeight(0.8f)
                                else
                                    Modifier,
                                text = if (state is FourState.Error) {
                                    state.msg // 错误消息
                                } else {
                                    if (list.isNotEmpty()) R.string.no_more.string // 没有更多
                                    else R.string.empty.string // 什么都没有
                                }
                            ) {
                                if (list.isNotEmpty())
                                    vm act MainAction.LoadFeedStream(
                                        type,
                                        list.last().reply_time
                                    )
                                else
                                    vm act MainAction.LoadFeedStream(type)
                            }
                        }

                        FourState.OnMore -> {
                            LoadingBar()
                        }

                        else -> {}
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefresh,
            state = refreshState,
            contentColor = themeColor
        )
    }
}

@Composable
private fun BottomTextComponent(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.textPrimary,
            modifier = Modifier
                .click {
                    onClick()
                },
            fontSize = 14.sp,
            fontWeight = FontWeight.Light
        )
    }
}