package com.bingyan.bbhust.ui.screen.home.main

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.bingyan.bbhust.PostsQuery
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.utils.random

data class MainState(
    val listMap: SnapshotStateMap<MainViewModel.Type, PageData> = SnapshotStateMap(),
    val recommendPage: Int = 0,
    val recommendRand: Int = random(1000),
) {
    fun state(type: MainViewModel.Type) = listMap[type]?.state ?: FourState.Loading
    fun list(type: MainViewModel.Type): List<PostsQuery.Post> {
        return listMap[type]?.list ?: emptyList()
    }
}

data class PageData(
    val state: FourState = FourState.Loading,
    val list: List<PostsQuery.Post> = emptyList(),
)

sealed class MainAction {
    data class LoadFeedStream(
        val type: MainViewModel.Type, // 最新 or 推荐 or 关注
        val time: String? = null, // 最新帖子的 reply_time 用于加载更多 / 推荐流下拉刷新时传入 null,加载更多随意传入一个非 null 值即可
        val refresh: Boolean = true, // 是否下拉刷新，time 为 null 也可能是第一次加载，第一次加载不需要刷新
        val onFinished: () -> Unit = {} // 加载完成回调
    ) : MainAction()

    data class LikePost(val id: String, val isLike: Boolean) : MainAction()
}