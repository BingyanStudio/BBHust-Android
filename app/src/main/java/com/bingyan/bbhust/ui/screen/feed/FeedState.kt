package com.bingyan.bbhust.ui.screen.feed

import androidx.compose.foundation.ScrollState
import com.bingyan.bbhust.PostQuery
import com.bingyan.bbhust.RepliesQuery
import com.bingyan.bbhust.base.FourState

data class FeedState(
    val commentDesc : Int = -1,//帖子顺序，1为按时间顺序，-1为最新，0为按时间顺序看楼主
    val timelineList: List<RepliesQuery.Reply> = emptyList(),
    val newlineList: List<RepliesQuery.Reply> = emptyList(),
    val feed: PostQuery.Post? = null,
    val feedState: FourState = FourState.Loading,
    val replyCurrent : FeedViewModel.Reply? = null,
    val listState : ScrollState = ScrollState(initial = 0),
    val isReply : Boolean = false
) {

}

sealed class FeedAction {
    data class LoadFeed(
        val id: String, // 帖子ID
        val onCompletion: suspend () -> Unit
    ) : FeedAction()

    data class GetMoreReplies(
        val id: String, //帖子ID
        val limit: Int = REPLIES_LIMIT, //一次拉取的限制
        val sort: Int,  //排序方式
        val onCompletion: (suspend () -> Unit)? = null,
        val state: FourState
    ):FeedAction()

    data class UploadImage(
        val srcList: List<String>,
        val onSuccess: (String) -> Unit
    ):FeedAction()

    data class LikePost(
        val onFailed: (() -> Unit)? = null
    ):FeedAction()

    data class StarPost(
        val onFailed: (() -> Unit)? = null
    ):FeedAction()

    data class DeletePost(
        val id: String
    ):FeedAction()

    data class RefreshAll(
        val feedState: FourState,
        val commentDesc: Int,
        val id: String,
        val onCompletion: (suspend () -> Unit)? = null
    ):FeedAction()

    data class DeleteReply(
        val id: String
    ):FeedAction()

    data class Publish(
        val replyAt: String?,
        val onSuccess: () -> Unit = {}
    ):FeedAction()

    data class ReadMore(
        val id: String
    ):FeedAction()

    data class LikeReply(
        val id: String,
        val like:Boolean,
        val commentDesc: Int,
    ):FeedAction()



}