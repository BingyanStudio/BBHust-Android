package com.bingyan.bbhust.ui.screen.feed

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.bingyan.bbhust.FavoritePostMutation
import com.bingyan.bbhust.LikePostMutation
import com.bingyan.bbhust.LikeReplyMutation
import com.bingyan.bbhust.PostQuery
import com.bingyan.bbhust.R
import com.bingyan.bbhust.RepliesQuery
import com.bingyan.bbhust.base.BaseViewModel
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.type.FavoritePostInput
import com.bingyan.bbhust.type.LikePostInput
import com.bingyan.bbhust.type.LikeReplyInput
import com.bingyan.bbhust.utils.apollo
import com.bingyan.bbhust.utils.defaultErrorHandler
import com.bingyan.bbhust.utils.onSuccess
import com.bingyan.bbhust.utils.string
import com.bingyan.bbhust.utils.suspendUpdate
import com.bingyan.bbhust.utils.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val REPLIES_LIMIT = 20
const val LANDLORD_LIMIT = 100
const val TIMELINE = 1
const val NEWLINE = -1
const val LANDLORD = 0

class FeedViewModel : BaseViewModel<FeedState, FeedAction>(FeedState()) {
    override fun reduce(action: FeedAction): FeedState {
        return when (action) {
            is FeedAction.LoadFeed -> {
                state.copy(
                    feedState = FourState.Idle(false)
                ).then {
                    viewModelScope.launch {
                        delay(500)
                        apollo().query(PostQuery(action.id))
                            .toFlow()
                            .onSuccess {
                                state.copy(feed = it.post).update()
                            }
                            .defaultErrorHandler {
                                state.copy(feedState = FourState.Error(it)).update()
                            }
                            .onCompletion {
                                state.copy(feedState = FourState.Idle(false)).update()
                                action.onCompletion()
                            }
                            .launchIn(viewModelScope).start()
                    }
                }
            }

            is FeedAction.DeletePost -> {
                state
            }

            is FeedAction.DeleteReply -> {
                state
            }

            is FeedAction.GetMoreReplies -> {
                state.copy(
                    feedState = action.state
                ).then {
                    viewModelScope.launch {
                        when (action.sort) {
                            TIMELINE, LANDLORD -> {
                                state.then {
                                    val time =
                                        if (state.timelineList.isEmpty()) "0"
                                        else state.timelineList.last().time
                                    val limit =
                                        if (action.sort == TIMELINE) REPLIES_LIMIT
                                        else LANDLORD_LIMIT
                                    val sort = TIMELINE
                                    apollo().query(
                                        RepliesQuery(
                                            action.id,
                                            time,
                                            limit,
                                            sort
                                        )
                                    )
                                        .toFlow()
                                        .onSuccess { new ->
                                            state.addToList(
                                                action.sort,
                                                new.replies
                                            )
                                        }
                                        .defaultErrorHandler {
                                            state.copy(feedState = FourState.Error(it)).update()
                                        }
                                        .onCompletion {
                                            action.onCompletion?.let { it1 -> it1() }
                                        }
                                        .launchIn(viewModelScope).start()
                                }
                            }

                            NEWLINE -> {
                                state.then {
                                    val time =
                                        if (state.newlineList.isEmpty()) "${Int.MAX_VALUE}"
                                        else state.newlineList.last().time
                                    val limit = REPLIES_LIMIT
                                    val sort = NEWLINE
                                    apollo().query(
                                        RepliesQuery(
                                            action.id,
                                            time,
                                            limit,
                                            sort    //只看楼主与时间轴共用一个表
                                        )
                                    ).toFlow()
                                        .onSuccess { new ->
                                            state.addToList(
                                                action.sort,
                                                new.replies
                                            )
                                        }
                                        .defaultErrorHandler {
                                            state.copy(feedState = FourState.Error(it)).update()
                                        }
                                        .onCompletion {
                                            action.onCompletion?.let { it1 -> it1() }
                                        }
                                        .launchIn(viewModelScope).start()
                                }
                            }
                        }
                    }
                }
            }

            is FeedAction.LikePost -> {
                state.then {
                    val post = state.feed ?: return state.copy(
                        feedState = FourState.Error(
                            string(
                                R.string.unknown_feed
                            )
                        )
                    )
                    viewModelScope.launch {
                        apollo().mutation(
                            LikePostMutation(
                                LikePostInput(
                                    id = post.id,
                                    like = !post.liked
                                )
                            )
                        ).toFlow()
                            .onSuccess {
                                val newPost = if(post.liked) post.copy(liked = !post.liked, like_count = post.like_count-1)
                                else post.copy(liked = !post.liked, like_count = post.like_count+1)
                                state.copy(feed = newPost).update()
                            }
                            .onCompletion {
                                viewModelScope.launch {
                                    apollo().query(PostQuery(post.id))
                                        .toFlow()
                                        .onSuccess {
                                            val newPost = it.post
                                            state.copy(
                                                feed = newPost
                                            ).update()
                                        }
                                        .defaultErrorHandler {
                                            state.copy(
                                                feedState = FourState.Error(it)
                                            ).update()
                                        }
                                        .launchIn(viewModelScope).start()
                                }

                            }
                            .defaultErrorHandler {
                                state.copy(feedState = FourState.Error(it)).then {
                                    action.onFailed
                                }
                            }
                            .launchIn(viewModelScope).start()
                    }
                }
            }

            is FeedAction.LikeReply -> {
                state.then {
                    viewModelScope.launch {
                        apollo().mutation(
                            LikeReplyMutation(
                                LikeReplyInput(
                                    id = action.id,
                                    like = action.like
                                )
                            )
                        ).toFlow()
                            .onSuccess {
                                val newList =
                                    if (state.commentDesc == NEWLINE)
                                        state.newlineList.toMutableList()
                                    else
                                        state.timelineList.toMutableList()
                                newList.update(
                                    filter = {
                                        it.id == action.id
                                    },
                                    update = {
                                        val newReply =
                                            if(action.like) it.copy(liked = action.like, like_count = it.like_count+1)
                                            else it.copy(liked = action.like, like_count = it.like_count-1)
                                        newReply
                                    }
                                )
                                if (state.commentDesc == NEWLINE)
                                    state.copy(newlineList = newList).update()
                                else
                                    state.copy(timelineList = newList).update()
                            }
                            .onCompletion {
                                state.refreshReply(
                                    state.feed?.id ?: "",
                                    action.id,
                                    action.commentDesc
                                )
                            }
                            .defaultErrorHandler {
                                state.copy(
                                    feedState = FourState.Error(it)
                                ).update()
                            }
                            .launchIn(viewModelScope).start()
                    }
                }
            }

            is FeedAction.Publish -> {
                state
            }

            is FeedAction.ReadMore -> {
                state
            }

            is FeedAction.RefreshAll -> {
                state.copy(
                    commentDesc = action.commentDesc,
                    feedState = action.feedState
                ).then {
                    viewModelScope.launch {
                        if (action.feedState==FourState.Loading)
                            delay(500)
                        when (action.commentDesc) {
                            TIMELINE, LANDLORD -> {
                                val time = "0"
                                val limit = state.timelineList.size + REPLIES_LIMIT
                                val sort = TIMELINE
                                apollo().query(
                                    RepliesQuery(
                                        action.id,
                                        time,
                                        limit,
                                        sort
                                    )
                                )
                                    .toFlow()
                                    .onSuccess { new ->
                                        state.copy(
                                            feedState = FourState.Idle(false),
                                            timelineList = new.replies
                                        ).update()
                                    }
                                    .onCompletion {
                                        action.onCompletion
                                    }
                                    .defaultErrorHandler {
                                        state.copy(feedState = FourState.Error(it)).update()
                                    }
                                    .launchIn(viewModelScope).start()
                            }

                            NEWLINE -> {
                                val time = "${Int.MAX_VALUE}"
                                val limit = state.timelineList.size
                                val sort = NEWLINE
                                apollo().query(
                                    RepliesQuery(
                                        action.id,
                                        time,
                                        limit,
                                        sort
                                    )
                                ).toFlow()
                                    .onSuccess { new ->
                                                state.copy(
                                                    feedState = FourState.Idle(false),
                                                    newlineList = new.replies
                                                ).update()

                                    }
                                    .defaultErrorHandler {
                                        state.copy(feedState = FourState.Error(it)).update()
                                    }
                                    .launchIn(viewModelScope).start()
                            }
                        }
                    }
                }
            }

            is FeedAction.StarPost -> {
                state.then {
                    val post = state.feed ?: return state.copy(
                        feedState = FourState.Error(
                            string(
                                R.string.unknown_feed
                            )
                        )
                    )
                    viewModelScope.launch {
                        apollo().mutation(
                            FavoritePostMutation(
                                FavoritePostInput(
                                    id = post.id,
                                    favorite = !post.favorite
                                )
                            )
                        ).toFlow()
                            .onCompletion {
                                apollo().query(PostQuery(post.id))
                                    .toFlow()
                                    .onSuccess {
                                        val newPost = it.post
                                        state.copy(
                                            feed = newPost
                                        ).update()
                                    }
                                    .defaultErrorHandler {
                                        state.copy(
                                            feedState = FourState.Error(it)
                                        ).update()
                                    }
                            }
                            .defaultErrorHandler {
                                state.copy(feedState = FourState.Error(it)).then {
                                    action.onFailed
                                }
                            }
                            .launchIn(viewModelScope).start()
                    }
                }
            }

            is FeedAction.UploadImage -> {
                state
            }
        }
    }

    /**
     * 根据replyId（可以是一级回复或二级回复）刷新该一级回复
     */
    private fun FeedState.refreshReply(feedId: String, replyId: String, commentDesc: Int) {
        viewModelScope.launch {
            val newList =
                if (commentDesc == NEWLINE)
                    state.newlineList.toMutableList()
                else
                    state.timelineList.toMutableList()
            newList.suspendUpdate(
                filter = {
                    it.id == replyId || it.sub_reply?.any { subReply -> subReply.id == replyId } == true
                },
                update = { it,lit->
                    val time =
                        if(commentDesc== NEWLINE) (it.time.toInt() + 1).toString()
                        else (it.time.toInt()-1).toString()
                    val sort =
                        if(commentDesc== NEWLINE) NEWLINE
                        else TIMELINE
                    apollo().query(
                        RepliesQuery(feedId, time, 5, sort)
                    ).toFlow()
                        .onSuccess { replies ->
                            replies.replies.let { li ->
                                for (mReply in li) {
                                    if (mReply.id == it.id) {
                                        lit.set(mReply)
                                    }
                                }
                            }
                        }
                        .onCompletion {
                            if (commentDesc == NEWLINE)
                                copy(newlineList = newList.toList()).update()
                            else
                                copy(timelineList = newList.toList()).update()
                        }
                        .defaultErrorHandler {
                            state.copy(
                                feedState = FourState.Error(it)
                            ).update()
                        }
                        .launchIn(viewModelScope).start()
                }
            )
        }
    }

    /**
     * 在发布一级评论后进行刷新
     */
    private fun FeedState.refreshNew(feedId: String, id: String, commentDesc: Int) {
        viewModelScope.launch {
            when (commentDesc) {
                TIMELINE, LANDLORD -> {
                    state.then {
                        val time =
                            if (state.timelineList.isEmpty()) "0"
                            else state.timelineList.last().time
                        val limit =
                            if (commentDesc == TIMELINE) REPLIES_LIMIT
                            else LANDLORD_LIMIT
                        val sort = TIMELINE
                        apollo().query(RepliesQuery(feedId, time, limit, sort))
                            .toFlow()
                            .onSuccess { new ->
                                addToList(
                                    commentDesc,
                                    new.replies
                                )
                            }
                            .defaultErrorHandler {
                                state.copy(feedState = FourState.Error(it)).update()
                            }
                            .launchIn(viewModelScope).start()
                    }
                }

                NEWLINE -> {
                    val time = "${Int.MAX_VALUE}"
                    val limit = REPLIES_LIMIT
                    val sort = NEWLINE
                    apollo().query(RepliesQuery(feedId, time, limit, sort))
                        .toFlow()
                        .onSuccess { new ->
                            state.copy(newlineList = new.replies + newlineList).update()
                        }
                        .defaultErrorHandler {
                            state.copy(feedState = FourState.Error(it)).update()
                        }
                        .launchIn(viewModelScope).start()
                }
            }
        }
    }

    private fun FeedState.addToList(
        type: Int,
        newList: List<RepliesQuery.Reply>
    ) {
        when (type) {
            TIMELINE -> {
                if (newList.size < REPLIES_LIMIT)
                    copy(
                        timelineList = timelineList + newList,
                        feedState = FourState.Idle(true)
                    ).update()
                else
                    copy(
                        timelineList = timelineList + newList,
                        feedState = FourState.Idle(false)
                    ).update()
            }

            NEWLINE -> {
                if (newList.size < REPLIES_LIMIT)
                    copy(
                        newlineList = newlineList + newList,
                        feedState = FourState.Idle(true)
                    ).update()
                else
                    copy(
                        newlineList = newlineList + newList,
                        feedState = FourState.Idle(false)
                    ).update()
            }

            LANDLORD -> {
                if (newList.size < LANDLORD_LIMIT)
                    copy(
                        timelineList = timelineList + newList,
                        feedState = FourState.Idle(true)
                    ).update()
                else
                    copy(
                        timelineList = timelineList + newList,
                        feedState = FourState.Idle(false)
                    ).update()
            }
        }
    }


    data class Reply(
        val post: String,
        val span: AnnotatedString,
        val replyAt: String?,//回复的对象
        val content: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    )


}