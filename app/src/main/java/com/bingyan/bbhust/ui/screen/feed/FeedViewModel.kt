package com.bingyan.bbhust.ui.screen.feed

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.bingyan.bbhust.AddCommentMutation
import com.bingyan.bbhust.FavoritePostMutation
import com.bingyan.bbhust.LikePostMutation
import com.bingyan.bbhust.LikeReplyMutation
import com.bingyan.bbhust.PostQuery
import com.bingyan.bbhust.R
import com.bingyan.bbhust.RepliesQuery
import com.bingyan.bbhust.ReplyQuery
import com.bingyan.bbhust.base.BaseViewModel
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.base.TriState
import com.bingyan.bbhust.type.AddReplyInput
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext


const val REPLIES_LIMIT = 20
const val LANDLORD_LIMIT = 100
const val TIMELINE = 1
const val NEWLINE = -1
const val LANDLORD = 0

/**
 * 回复列表的顺序
 * @param pullOrder 该列表从网络拉取时的顺序，1为时间顺序，-1为最新顺序
 * @param limit 该列表每次拉取的限制
 */
enum class CommentDesc(val pullOrder: Int, val limit: Int) {
    //时间顺序
    TimeOrder(1, 20),

    //最新顺序
    NewOrder(-1, 20),

    //只看楼主
    LandlordOrder(1, 50)
}

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
                    getNewReplies(
                        action.id,
                        action.commentDesc,
                        action.onCompletion
                    )
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
                                val newPost = if (post.liked) post.copy(
                                    liked = !post.liked,
                                    like_count = post.like_count - 1
                                )
                                else post.copy(
                                    liked = !post.liked,
                                    like_count = post.like_count + 1
                                )
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
                                val commentDesc = state.commentDesc
                                val newList = state.replies[commentDesc]!!
                                    .mapIndexed {_, reply ->
                                        if(reply.id==action.id){
                                            val newReply =
                                                if (action.like) reply.copy(
                                                    liked = action.like,
                                                    like_count = reply.like_count + 1
                                                )
                                                else reply.copy(
                                                    liked = action.like,
                                                    like_count = reply.like_count - 1
                                                )
                                            newReply
                                        }else{
                                            reply
                                        }
                                    }
                                state.copy(
                                    replies = state.replies.apply { put(commentDesc, newList) },
                                ).update()
                            }
                            .onCompletion {
                                refreshReply(
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
                state.then {
                    val feedId = state.feed?.id ?: return state.copy(
                        feedState = FourState.Error(
                            string(R.string.unknown_feed)
                        )
                    )
                    val id = action.replyAt ?: feedId
                    val reply = state.reply[id] ?: return state.copy(
                        feedState = FourState.Error(
                            string(R.string.upload_failed)
                        )
                    )
                    val text = reply.content.value.text.trim()
                    if (text.isNotBlank()) {
                        val input = AddReplyInput(
                            content = text,
                            post = reply.post,
                            reply_at = Optional.presentIfNotNull(reply.replyAt)
                        )
                        viewModelScope.launch {
                            apollo().mutation(
                                AddCommentMutation(
                                    input
                                )
                            ).toFlow()
                                .onSuccess {
                                    action.onSuccess()
                                }
                                .onCompletion {
                                    state.then {
                                        val newReply = state.reply.toMutableMap()
                                        newReply.remove(id)
                                        state.copy(isReply = false, reply = newReply.toMap()).then {
                                            if (id == feedId)
                                                getNewReplies(
                                                    feedId = feedId,
                                                    commentDesc = state.commentDesc
                                                )
                                            else
                                                refreshReply(
                                                    feedId = feedId,
                                                    replyId = id,
                                                    commentDesc = state.commentDesc
                                                )
                                        }
                                        delay(500)
                                        action.onCompletion()
                                    }
                                }
                                .defaultErrorHandler {
                                    action.onFailed()
                                    state.copy(
                                        feedState = FourState.Error(it)
                                    ).update()
                                }
                                .launchIn(viewModelScope).start()
                        }
                    } else {
                        action.onFailed()
                    }
                }
            }

            is FeedAction.ReadMore -> {
                state
            }

            is FeedAction.OpenReplyScreen -> {
                state.then {
                    val feedId = state.feed?.id ?: return state.copy(
                        feedState = FourState.Error(
                            string(R.string.unknown_feed)
                        )
                    )
                    val replyAt = action.replyAt ?: feedId
                    val reply = state.reply[replyAt]
                    if (reply == null) {
                        val replyNew = Reply(
                            post = feedId,
                            span = action.span,
                            replyAt = action.replyAt
                        )
                        val newReplyMap = state.reply.toMutableMap()
                        newReplyMap[replyAt] = replyNew
                        state.copy(
                            reply = newReplyMap.toMap(),
                            replyCurrent = replyNew
                        ).update()
                    } else {
                        state.copy(
                            replyCurrent = reply
                        ).update()
                    }
                    state.copy(
                        isReply = true
                    ).update()
                }
            }

            is FeedAction.RefreshAll -> {
                state.copy(
                    feedState = FourState.Loading
                ).then {
                    viewModelScope.launch {
                        delay(500)
                        viewModelScope.launch {
                            val commentDesc = state.commentDesc
                            val feedId = action.id
                            val repliesList = state.replies[commentDesc]!!
                            val time = when (commentDesc) {
                                CommentDesc.TimeOrder -> {
                                    "0"
                                }
                                CommentDesc.NewOrder -> {
                                    "${Int.MAX_VALUE}"
                                }

                                CommentDesc.LandlordOrder -> {
                                    "0"
                                }
                            }
                            val limit = repliesList.size + commentDesc.limit
                            val pullOrder = commentDesc.pullOrder
                            apollo().query(RepliesQuery(feedId, time, limit, pullOrder))
                                .toFlow()
                                .onSuccess { new ->
                                    new.replies.changeRepliesList(commentDesc)
                                }
                                .defaultErrorHandler {
                                    state.copy(feedState = FourState.Error(it)).update()
                                }
                                .onCompletion {
                                    action.onCompletion?.invoke()
                                }
                                .launchIn(viewModelScope).start()
                        }
                    }
                }
            }

            is FeedAction.RefreshNew -> {
                state.copy(
                    commentDesc = action.commentDesc,
                    feedState = FourState.Loading
                )
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
                            .onSuccess {
                                val newPost = post.copy(favorite = !post.favorite)
                                state.copy(feed = newPost).update()
                            }
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
                                    .launchIn(viewModelScope).start()
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

            is FeedAction.CloseSubCommentScreen -> {
                state.copy(
                    popupController = state.popupController.copy(showSubCommentsScreen = false),
                    subCommentId = null
                )
            }
            is FeedAction.OpenSubCommentsScreen -> {
                state.copy(
                    popupController = state.popupController.copy(showSubCommentsScreen = true),
                    subCommentId = action.subCommentId
                )
            }
        }
    }

    /**
     * 根据replyId（可以是一级回复或二级回复）刷新该一级回复
     */
    private fun refreshReply(feedId: String, replyId: String, commentDesc: CommentDesc) {
        viewModelScope.launch {
            val newList = state.replies[commentDesc]!!.toMutableList()
            newList.suspendUpdate(
                filter = {
                    it.id == replyId || it.sub_reply?.any { subReply -> subReply.id == replyId } == true
                },
                update = { it, lit ->
                    val time =
                        if (commentDesc == CommentDesc.NewOrder) (it.time.toInt() + 1).toString()
                        else (it.time.toInt() - 1).toString()
                    val sort = commentDesc.pullOrder
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
                            state.copy(
                                replies = state.replies.apply {
                                    put(commentDesc, newList.toList())
                                }
                            ).update()
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
     * 获取新回复
     */
    private fun getNewReplies(
        feedId: String, commentDesc: CommentDesc,
        onCompletion: (suspend () -> Unit)? = null
    ) {
        viewModelScope.launch {
            delay(500)
            val repliesList = state.replies[commentDesc]!!
            val time = when (commentDesc) {
                CommentDesc.TimeOrder -> {
                    if (repliesList.isEmpty()) "0"
                    else repliesList.last().time
                }

                CommentDesc.NewOrder -> {
                    if (repliesList.isEmpty()) "${Int.MAX_VALUE}"
                    else repliesList.last().time
                }

                CommentDesc.LandlordOrder -> {
                    if (repliesList.isEmpty()) "0"
                    else repliesList.last().time
                }
            }
            val limit = commentDesc.limit
            val pullOrder = commentDesc.pullOrder
            apollo().query(RepliesQuery(feedId, time, limit, pullOrder))
                .toFlow()
                .onSuccess { new ->
                    (repliesList+new.replies).changeRepliesList(commentDesc)
                }
                .defaultErrorHandler {
                    state.copy(feedState = FourState.Error(it)).update()
                }
                .onCompletion {
                    onCompletion?.invoke()
                }
                .launchIn(viewModelScope).start()
        }
    }

    private fun List<RepliesQuery.Reply>.changeRepliesList(
        commentDesc: CommentDesc
    ) {
        val oldList = state.replies[commentDesc]!!
        //筛选只看楼主
        val list = when (commentDesc) {
            CommentDesc.LandlordOrder -> {
                this.apply {
                    filter { reply ->
                        state.feed?.let {
                            reply.author.id == it.author.id
                        } ?: true
                    }
                }
            }
            else -> {
                this
            }
        }
        //判断此时是否有更多
        if (this.size - oldList.size < commentDesc.limit) {
            state.copy(
                replies = state.replies.apply { put(commentDesc, list)},
                feedState = FourState.Idle(true)
            ).update()
        }else{
            state.copy(
                replies = state.replies.apply { put(commentDesc, list) },
                feedState = FourState.Idle(false)
            ).update()
        }
    }


    data class Reply(
        val post: String,
        val span: AnnotatedString,
        val replyAt: String?,//回复的对象
        val content: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    )


}