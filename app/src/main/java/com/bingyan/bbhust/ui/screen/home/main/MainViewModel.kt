package com.bingyan.bbhust.ui.screen.home.main


import androidx.lifecycle.viewModelScope
import com.bingyan.bbhust.FollowingPostQuery
import com.bingyan.bbhust.PostsQuery
import com.bingyan.bbhust.RecommendPostsQuery
import com.bingyan.bbhust.base.BaseViewModel
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.utils.apollo
import com.bingyan.bbhust.utils.defaultErrorHandler
import com.bingyan.bbhust.utils.onSuccess
import com.bingyan.bbhust.utils.toPost
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

const val PAGE_LIMIT = 20

class MainViewModel : BaseViewModel<MainState, MainAction>(MainState()) {
    override fun reduce(action: MainAction): MainState {
        return when (action) {
            is MainAction.LoadFeedStream -> {
                state.then {
                    viewModelScope.launch {
                        if (action.time == null && action.refresh) {
                            state.updateState(action.type, FourState.Loading)
                        }
                        delay(500)

                        when (action.type) {
                            Type.Latest -> {
                                apollo().query(
                                    PostsQuery(
                                        time = action.time ?: Int.MAX_VALUE.toString(),
                                        limit = PAGE_LIMIT
                                    )
                                )
                                    .toFlow()
                                    .onSuccess { new ->
                                        println("Update list 1")
                                        state.updateList(
                                            action.type,
                                            FourState.Idle(new.posts.post.size < PAGE_LIMIT)
                                        ) { old ->
                                            if (action.refresh)
                                                new.posts.post
                                            else {
                                                old + new.posts.post
                                            }
                                        }
                                    }
                                    .defaultErrorHandler {
                                        state.updateState(
                                            action.type,
                                            FourState.Error(it)
                                        )
                                    }
                                    .onCompletion {
                                        action.onFinished()
                                    }
                                    .launchIn(viewModelScope).start()
                            }

                            Type.Recommend -> {
                                apollo().query(
                                    RecommendPostsQuery(
                                        rand = state.recommendRand,
                                        page = state.recommendPage
                                    )
                                )
                                    .toFlow()
                                    .onSuccess { new ->
                                        println("Update list 2")
                                        state.updateList(
                                            action.type,
                                            FourState.Idle(false)
                                        ) { old ->
                                            if (action.refresh) // 下拉刷新, 保留一页旧数据
                                                new.posts.map { it.toPost } + old.take(PAGE_LIMIT)
                                            else { // 加载更多
                                                old + new.posts.map { it.toPost }
                                            }
                                        }
                                    }
                                    .defaultErrorHandler {
                                        state.updateState(
                                            action.type,
                                            FourState.Error(it)
                                        )
                                    }
                                    .onCompletion {
                                        action.onFinished()
                                    }
                                    .launchIn(viewModelScope).start()
                            }

                            Type.Follow -> {
                                apollo().query(
                                    FollowingPostQuery(
                                        time = action.time ?: Int.MAX_VALUE.toString(),
                                        limit = PAGE_LIMIT
                                    )
                                )
                                    .toFlow()
                                    .onSuccess { new ->
                                        println("Update list 3")
                                        state.updateList(
                                            action.type,
                                            FourState.Idle(new.posts.post.size < PAGE_LIMIT)
                                        ) { old ->
                                            if (action.refresh)
                                                new.posts.post.map { it.toPost }
                                            else {
                                                old + new.posts.post.map { it.toPost }
                                            }
                                        }
                                    }
                                    .defaultErrorHandler {
                                        state.updateState(
                                            action.type,
                                            FourState.Error(it)
                                        )
                                    }
                                    .onCompletion {
                                        action.onFinished()
                                    }
                                    .launchIn(viewModelScope).start()
                            }
                        }
                    }
                }
            }

            is MainAction.LikePost -> {
                state.copy(
                    listMap = state.listMap.apply {
//                        this[action.type] = action.value
                    }
                )
            }
        }
    }

    private fun MainState.newState(type: Type, state: FourState): MainState {
        return copy(
            listMap = listMap.apply {
                val newData = this[type]?.copy(state = state) ?: return@apply
                this[type] = newData
            }
        )
    }

    private fun MainState.updateState(type: Type, state: FourState) {
        newState(type, state).update()
    }

    private fun MainState.updateList(
        type: Type,
        state: FourState,
        list: (List<PostsQuery.Post>) -> List<PostsQuery.Post>
    ) {
        copy(
            listMap = listMap.apply {
                val newList = list(this[type]?.list ?: emptyList())
                this[type] = PageData(state = state, list = newList)
            }
        ).update()
    }

    enum class Type {
        Latest,
        Recommend,
        Follow
    }
}