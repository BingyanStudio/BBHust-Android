package com.bingyan.bbhust.ui.screen.feed

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.text.AnnotatedString
import com.bingyan.bbhust.PostQuery
import com.bingyan.bbhust.RepliesQuery
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.ui.widgets.sheet.BottomSheetDialogState

data class FeedState(
    val commentDesc : CommentDesc = CommentDesc.NewOrder,
    val feed: PostQuery.Post? = null,
    val feedState: FourState = FourState.Loading,
    val replyCurrent : FeedViewModel.Reply? = null,
    val listState : ScrollState = ScrollState(initial = 0),
    val isReply : Boolean = false,
    val reply : Map<String,FeedViewModel.Reply> = mapOf(),
    val replies : SnapshotStateMap<CommentDesc, List<RepliesQuery.Reply>> =
        mutableStateMapOf<CommentDesc, List<RepliesQuery.Reply>>().apply {
            put(CommentDesc.TimeOrder, listOf())
            put(CommentDesc.NewOrder, listOf())
            put(CommentDesc.LandlordOrder, listOf())
        },
    val subCommentId: String? = null,
    val popupController: PopupController = PopupController()
) {

}

data class PopupController(
    val showSubCommentsScreen : Boolean = false
)

sealed class FeedAction {
    data class LoadFeed(
        val id: String, // 帖子ID
        val onCompletion: suspend () -> Unit
    ) : FeedAction()

    data class GetMoreReplies(
        val id: String, //帖子ID
        val commentDesc: CommentDesc,  //排序方式
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
        val id: String,
        val onCompletion: (suspend () -> Unit)? = null
    ):FeedAction()

    data class RefreshNew(
       val commentDesc: CommentDesc
    ):FeedAction()

    data class DeleteReply(
        val id: String
    ):FeedAction()

    data class Publish(
        val replyAt: String?,
        val onSuccess: () -> Unit = {},
        val onFailed: () -> Unit,
        val onCompletion: suspend () -> Unit
    ):FeedAction()

    data class OpenSubCommentsScreen(
        val subCommentId : String
    ):FeedAction()

    data object CloseSubCommentScreen: FeedAction()

    data class ReadMore(
        val id: String
    ):FeedAction()

    data class OpenReplyScreen(
        val span : AnnotatedString,
        val replyAt: String?
    ):FeedAction()

    data class LikeReply(
        val id: String,
        val like:Boolean,
        val commentDesc: CommentDesc,
    ):FeedAction()



}