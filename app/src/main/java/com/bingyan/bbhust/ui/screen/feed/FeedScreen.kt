package com.bingyan.bbhust.ui.screen.feed

import Reply
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bingyan.bbhust.PostQuery
import com.bingyan.bbhust.R
import com.bingyan.bbhust.RepliesQuery
import com.bingyan.bbhust.base.FourState
import com.bingyan.bbhust.base.TriState
import com.bingyan.bbhust.ui.markdown.MarkdownView
import com.bingyan.bbhust.ui.provider.LocalBottomDialog
import com.bingyan.bbhust.ui.provider.LocalSnack
import com.bingyan.bbhust.ui.theme.*
import com.bingyan.bbhust.ui.widgets.*
import com.bingyan.bbhust.ui.widgets.app.ActionData
import com.bingyan.bbhust.ui.widgets.sheet.BottomSheetDialogState
import com.bingyan.bbhust.ui.widgets.sheet.BottomSheetDialogValue
import com.bingyan.bbhust.ui.widgets.sheet.ShowBottomSheet
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.*
import com.bingyan.bbhust.utils.string
import com.bingyan.bbhust.utils.toggle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedScreen(
    id: String,
    nav: NavHostController,
    reply: Boolean,
    vm: FeedViewModel = viewModel(),
) {
    //全局工具
    val snack = LocalSnack.current

    val state = vm.state
    val finished = remember {
        mutableStateOf(false)
    }
    val commentDesc = state.commentDesc
    val feed = state.feed
    LaunchedEffect(id) {
        //载入帖子
        vm.act(FeedAction.LoadFeed(id) {

        })
    }
    LaunchedEffect(id) {
        vm.act(
            FeedAction.GetMoreReplies(
                id = id, commentDesc = commentDesc, state = FourState.OnMore
            )
        )

    }
    val toReply = remember {
        mutableStateOf(!reply)
    }
    val more = remember {
        mutableStateOf(false)
    }
    val replyMore = remember {
        mutableStateOf<Reply?>(null)
    }
    val comment = remember {
        mutableStateOf("")
    }
    val viewHeight = remember {
        mutableIntStateOf(0)
    }
    val isRefresh = vm.state.feedState is FourState.Loading
    val refreshState = rememberPullRefreshState(refreshing = isRefresh,
        onRefresh = {
            vm.act {
                FeedAction.RefreshAll(id) {
                    snack.showSnackbar(string(R.string.refreshed))
                }
            }
        })
    val content = feed?.content
    val scope = rememberCoroutineScope()
    val html = remember { mutableStateOf("") }
    LaunchedEffect(feed?.content) {
        if (content != null) {
            html.value = content
        }
    }
    LaunchedEffect(viewHeight.intValue) {
        if (viewHeight.intValue > 0) {
            if (reply) scope.launch {
                vm.state.listState.animateScrollTo(viewHeight.intValue + 100)
            }
        }
    }

    val comments = state.replies[commentDesc]!!

    Surface(color = Transparent) {
        Dialogs(nav = nav) {
            BottomSliders(nav = nav) {
                Column(
                    Modifier
                        .navigationBarsPadding()
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TitleSpacer()
                    Header(name = string(R.string.feed), onDoubleTap = {
                        scope.launch {
                            vm.state.listState.animateScrollTo(0)
                        }
                    }, navController = nav, forward = {
                        EasyImage(src = R.drawable.more_2_fill,
                            contentDescription = stringResource(id = R.string.more),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    replyMore.value = null
                                    more.value = true
                                }
                                .padding(Gap.Small)
                                .size(ImageSize.Mid),
                            tint = colors.textPrimary)
                    }) {
                        nav.popBackStack()
                    }
                    Box(
                        Modifier
                            .pullRefresh(refreshState)
                            .weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier
                                .background(colors.card)
                                .verticalScroll(vm.state.listState)
                                .fillMaxSize(),
                        ) {
                            AnimatedVisibility(!finished.value, exit = shrinkVertically()) {
                                val tips = remember {
                                    mutableStateOf(Tips.make())
                                }
                                Row(
                                    Modifier
                                        .background(colors.background)
                                        .padding(horizontal = Gap.Big)
                                        .padding(vertical = Gap.Big)
                                        .clip(CardShapes.small)
                                        .click {
                                            tips.value = Tips.make()
                                        }
                                        .fillMaxWidth()
                                        .background(colors.card)
                                        .padding(vertical = Gap.Mid),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    LoadingBar(size = LoadingBarSize.Tiny)
                                    Spacer(modifier = Modifier.width(Gap.Mid))
                                    Text(
                                        text = tips.value,
                                        color = colors.secondary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            if (finished.value) {
                                feed?.let { UserCard(post = it) }
                            }
                            MarkdownView(
                                html.value,
                                modifier = Modifier
                                    .padding(horizontal = Gap.Big)
                                    .alpha(if (finished.value) 1f else 0f),
                                {
                                    vm.viewModelScope.launch {
                                        snack.showSnackbar(string(R.string.refreshed))
                                    }
                                    finished.value = true
                                },
                                viewHeight
                            )
                            if (finished.value) {
                                val cmt = comments

                                if (feed != null) {
//                                item {
                                    Spacer(modifier = Modifier.height(Gap.Big))
                                    Spacer(
                                        modifier = Modifier
                                            .background(colors.background)
                                            .height(Gap.Mid)
                                            .fillMaxWidth()
                                    )

                                    Row(
                                        modifier = Modifier
                                            .background(colors.card)
                                            .padding(
                                                horizontal = Gap.Big, vertical = Gap.Mid
                                            )
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = string(R.string.comment),
                                            color = colors.textPrimary,
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Row(
                                            Modifier
                                                .background(
                                                    colors.background, RoundedCornerShape(50)
                                                )
                                                .padding(Gap.Small),
                                            horizontalArrangement = Arrangement.spacedBy(Gap.Small)
                                        ) {
                                            @Composable
                                            fun capsule(
                                                value: String,
                                                selected: Boolean,
                                                commentDesc: CommentDesc,
                                            ) {
                                                Text(text = value,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50))
                                                        .clickable {
                                                            feed.id.let {
                                                                vm.act {
                                                                    FeedAction.RefreshNew(
                                                                        commentDesc
                                                                    )
                                                                }
                                                                vm.act {
                                                                    FeedAction.RefreshAll(it) {
                                                                        snack.showSnackbar(string(R.string.refreshed))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        .background(if (selected) colors.card else colors.background)
                                                        .padding(
                                                            horizontal = Gap.Mid,
                                                            vertical = Gap.Tiny
                                                        ),
                                                    color = colors.textPrimary
                                                )
                                            }
                                            capsule(
                                                value = "最新", commentDesc == CommentDesc.NewOrder,
                                                CommentDesc.NewOrder
                                            )
                                            capsule(
                                                value = "时间轴",
                                                commentDesc == CommentDesc.TimeOrder,
                                                CommentDesc.TimeOrder
                                            )
                                            capsule(
                                                value = "楼主",
                                                commentDesc == CommentDesc.LandlordOrder,
                                                CommentDesc.LandlordOrder
                                            )
                                        }
                                    }
                                }
                                cmt.forEach { item ->
                                    CommentCard(item,
                                        author = feed?.author?.id,
                                        onLike = { like ->
                                            vm.act {
                                                FeedAction.LikeReply(item.id, !like, commentDesc)
                                            }
                                        },
                                        onComment = {

                                        },
                                        onLongClick = {
                                            //长按回复菜单
                                            replyMore.value = Reply(
                                                item.id, item.author.id, item.content
                                            )
                                            more.value = true
                                        }) {
                                        vm.act {
                                            FeedAction.OpenReplyScreen(
                                                buildAnnotatedString {
                                                    pushStyle(SpanStyle(color = themeColor))
                                                    append(item.author.username)
                                                    pop()
                                                    append(":")
                                                    append(item.content)
                                                },
                                                item.id
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(Gap.Big))
                                if (feed != null) {
                                    when (vm.state.feedState) {
                                        FourState.Idle(true) -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(4f),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (cmt.isEmpty()) string(R.string.no_comment)
                                                    else string(R.string.comment_count, cmt.size),
                                                    color = colors.textSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        FourState.Idle(false) -> {
                                            GetMoreButton(
                                                id = id,
                                                commentDesc = commentDesc
                                            )
                                        }

                                        else -> {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                Arrangement.Center
                                            ) {
                                                LoadingBar()
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(Gap.Big))
                                Spacer(modifier = Modifier.height(Gap.Big))
                                Spacer(modifier = Modifier.height(Gap.Big))
                            }
                        }
                        PullRefreshIndicator(
                            refreshing = isRefresh,
                            state = refreshState,
                            contentColor = themeColor
                        )
                    }

                    if (feed != null && finished.value) {
                        Row(Modifier
                            .drawColoredShadow(
                                Color.Black.copy(alpha = 0.5f),
                                alpha = 0.1f,
                                shadowRadius = 12.dp,
                                roundedRect = false
                            )
                            .background(colors.card)
                            .clickable {
                                feed.let {
                                    vm.act {
                                        FeedAction.OpenReplyScreen(
                                            buildAnnotatedString {
                                                pushStyle(SpanStyle(color = themeColor))
                                                append(it.author.username)
                                                pop()
                                                append(":")
                                                append(it.content)
                                            },
                                            null
                                        )
                                    }
                                }
                            }
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(Gap.Big))
                            EasyImage(
                                src = R.drawable.edit_fill,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(ImageSize.Mid)
                                    .padding(Gap.Small),
                                tint = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(ImageSize.Normal))
                            Text(
                                text = comment.value.ifBlank { string(R.string.comment) },
                                color = colors.textSecondary,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            val replyCount = feed.reply_count
                            Column(modifier = Modifier
                                .clickable {
                                    scope.launch {
                                        vm.state.listState.animateScrollTo(if (toReply.value) viewHeight.intValue + 100 else 0)
                                        toReply.value = toReply.value.toggle
                                    }
                                }
                                .padding(horizontal = Gap.Big, vertical = Gap.Mid),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                EasyImage(
                                    src = R.drawable.comment,
                                    contentDescription = if (replyCount == 0) string(
                                        R.string.reply
                                    )
                                    else replyCount.toString(10),
                                    modifier = Modifier
                                        .size(ImageSize.Mid)
                                        .padding(Gap.Small),
                                    tint = colors.textSecondary
                                )
                                Text(
                                    text = if (replyCount == 0) string(R.string.reply)
                                    else replyCount.toString(10),
                                    color = colors.textSecondary,
                                    fontSize = 10.sp,
                                )
                            }
                            val liked = feed.liked
                            Column(modifier = Modifier
                                .clickable {
                                    vm.act {
                                        FeedAction.LikePost() {}
                                    }
                                }
                                .padding(horizontal = Gap.Big, vertical = Gap.Mid),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                EasyImage(
                                    src = if (liked) R.drawable.thumb_up_fill else R.drawable.thumb_up_line,
                                    contentDescription = if (liked) string(R.string.liked) else string(
                                        R.string.like
                                    ),
                                    modifier = if (liked) {
                                        Modifier.gradient()
                                    } else {
                                        Modifier
                                    }
                                        .size(ImageSize.Mid)
                                        .padding(Gap.Small),
                                    tint = if (liked) null else colors.textSecondary
                                )
                                Text(
                                    text = (feed.like_count).toString(10),
                                    color = if (liked) colors.secondary else colors.textSecondary,
                                    fontSize = 10.sp,
                                )
                            }
                            val stared = feed.favorite
                            Column(modifier = Modifier
                                .clickable {
                                    vm.act {
                                        FeedAction.StarPost() {}
                                    }
                                }
                                .padding(horizontal = Gap.Big, vertical = Gap.Mid),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                EasyImage(
                                    src = if (stared) R.drawable.star_fill else R.drawable.star_line,
                                    contentDescription = if (stared) string(R.string.marked) else string(
                                        R.string.mark
                                    ),
                                    modifier = Modifier
                                        .size(ImageSize.Mid)
                                        .padding(Gap.Small),
                                    tint = if (stared) colors.secondary else colors.textSecondary
                                )
                                Text(
                                    text = if (stared) string(R.string.marked) else string(R.string.mark),
                                    color = if (stared) colors.secondary else colors.textSecondary,
                                    fontSize = 10.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GetMoreButton(
    vm: FeedViewModel = viewModel(),
    id: String,
    commentDesc: CommentDesc
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedShapes.small)
                .background(
                    colors.background
                )
                .clickable() {
                    vm.act(
                        FeedAction.GetMoreReplies(
                            id = id,
                            commentDesc = commentDesc,
                            state = FourState.OnMore
                        )
                    )
                },
        ) {
            Text(
                modifier = Modifier.padding(
                    vertical = Gap.Mid,
                    horizontal = Gap.Big
                ),
                fontSize = 11.sp,
                text = string(R.string.get_moreReplies),
                color = colors.primary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun UserCard(
    post: PostQuery.Post
) {
    Column(
        Modifier
            .padding(horizontal = Gap.Big, vertical = Gap.Mid)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Gap.Mid)
    ) {
        SelectionContainer {
            Text(
                modifier = Modifier,
                text = post.title,
                color = colors.textPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.W700
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        ) {
            LazyImage(src = post.author.avatar,
                contentDescription = stringResource(id = R.string.avatar),
                modifier = Modifier
                    .click {
                        TODO("Msg.navigate(AppNavRoute.USER + / + post.author.id)")
                    }
                    .size(ImageSize.Normal)
                    .clip(RoundedShapes.medium),
                scale = ContentScale.Crop)
            Spacer(modifier = Modifier.size(Gap.Mid))
            Column(Modifier.weight(1f)) {
                Text(
                    text = post.author.username,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                )
                val humanized = "${string(R.string.published_at)} ${post.time.formatHumanized}"
                val realDate = "${string(R.string.published_at)} ${post.time.formatDateTime}"
                val state = remember {
                    mutableStateOf(true)
                }
                Text(text = if (state.value) humanized else realDate,
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.click {
                        state.value = !state.value
                    })
            }

        }
    }
}

@Composable
fun BottomSliders(
    nav: NavHostController,
    vm: FeedViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val bottomSheetDialog = LocalBottomDialog.current
    val draggable = remember {
        mutableStateOf(true)
    }
    val state = remember {
        BottomSheetDialogState(BottomSheetDialogValue.Collapsed)
    }
    LaunchedEffect(key1 = vm.state.popupController.showSubCommentsScreen) {
        if(!vm.state.popupController.showSubCommentsScreen){
            delay(100)
            state.collapse()
        }
    }
    val showBottomSheet = remember {
        ShowBottomSheet(
            {
                vm.act {
                    FeedAction.CloseSubCommentScreen
                }
            },
            mutableStateOf(true),
            state,
            content = { onDismiss ->
                SubCommentScreen(
                    draggable = draggable,
                    state = state,
                    sid = vm.state.subCommentId
                ) {
                    onDismiss()
                }
            }
        )
    }
    ReplyScreen(
        onSuccess = {
            vm.act {
                FeedAction.GetMoreReplies(
                    id = vm.state.feed?.id ?: "", commentDesc = vm.state.commentDesc,
                    state = FourState.OnMore
                )
            }
        }
    ) {
        bottomSheetDialog.Build(
            show = vm.state.popupController.showSubCommentsScreen,
            state = showBottomSheet
        ) {
            content()
        }
    }
}

@Composable
fun Dialogs(
    nav: NavHostController,
    content: @Composable () -> Unit
) {
    val dialog = remember {
        AppDialog().apply {

        }
    }
    dialog.Build(show = false, onDismissRequest = {

    }) {
        content()
    }

}

/**
 * 用户评论
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentCard(
    comment: RepliesQuery.Reply,
    author: String?,
    vm: FeedViewModel = viewModel(),
    onLike: (Boolean) -> Unit,
    onComment: () -> Unit,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val bottomSheetDialog = LocalBottomDialog.current
    val comments = comment.sub_reply ?: emptyList()
    Column(Modifier
        .background(colors.card)
        .fillMaxWidth()
        .combinedClickable(onLongClick = onLongClick) { onClick() }
        .padding(horizontal = Gap.Big, vertical = Gap.Big)) {
        Row(
            modifier = Modifier.padding(bottom = Gap.Zero)
        ) {
            LazyImage(src = comment.author.avatar,
                contentDescription = stringResource(id = R.string.avatar),
                modifier = Modifier
                    .click {
                        TODO("Msg.navigate(AppNavRoute.USER + / + post.author.id)")
                    }
                    .size(ImageSize.Normal)
                    .clip(RoundedShapes.medium),
                scale = ContentScale.Crop)
            Spacer(modifier = Modifier.size(Gap.Mid))
            Column {
                //昵称
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.author.username + if (comment.author.id == author) string(R.string.post_master) else "",
                        color = colors.primary,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(Gap.Mid))
                    //BB鸡的AI提醒
                    if (comment.author.id == "6422d2016f6eee4d08cf38ad")
                        Text(
                            text = "机器人",
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(colors.secondary, RoundedCornerShape(30))
                                .padding(Gap.Small, Gap.Tiny)
                        )
                }
                val humanized = comment.time.formatHumanized
                val detail = comment.time.formatDateTime
                val state = remember {
                    mutableStateOf(true)//时间相同显示发布时间
                }
                //内容
//                Text(post.content.removeImages)
                Text(
                    text = comment.content.removeImages,
                    color = colors.textPrimary,
                )
                //图片
                val images = comment.content.pickImages
                if (images.isNotEmpty()) {
                    StaggeredVerticalGrid(
                        maxRows = 4, modifier = Modifier.fillMaxWidth()
                    ) {
                        images.forEachIndexed { index, it ->
                            LazyImage(src = it.second,
                                contentDescription = string(R.string.image),
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(Gap.Tiny)
                                    .clip(CardShapes.small),
                                scale = ContentScale.Crop,
                                placeholder = R.drawable.placeholder,
                                onClick = {
                                    TODO("Msg.showImage(index, images.map { it.second })")
                                })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Gap.Mid))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (state.value) humanized else detail,
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.click {
                            state.value = !state.value
                        })
                    Spacer(modifier = Modifier.weight(1f))
                    val liked = comment.liked
                    ActionItem(modifier = if (liked) Modifier.gradient() else Modifier,
                        action = ActionData(
                            src = if (liked) R.drawable.thumb_up_fill else R.drawable.thumb_up_line,
                            value = comment.like_count.toString(10),
                            color = if (liked) null else colors.textPrimary
                        ) {
                            onLike(liked)
                        })
                    Spacer(modifier = Modifier.width(Gap.Mid))
                    ActionItem(modifier = Modifier, action = ActionData(
                        src = R.drawable.comment,
                        value = comments.size.toString(10),
                        color = colors.textPrimary
                    ) {
                        onComment()
                    })
                }
                //评论
                if (comments.isNotEmpty()) {
                    Column(
                        Modifier
                            .padding(top = Gap.Mid)
                            .clip(CardShapes.small)
                            .background(colors.onCard)
                            .fillMaxWidth()
                    ) {
                        comments.forEachIndexed { index, it ->
                            if (comments.size - index <= 3) {
                                SubCommentCard(
                                    modifier = Modifier,
                                    author = author,
                                    parent = comment,
                                    post = it
                                ) {
                                    vm.act {
                                        FeedAction.OpenSubCommentsScreen(comment.id)
                                    }
                                }
                            }
                        }

                        //查看更多
                        if (comments.size > 3) {
                            val span = AnnotatedString(
                                string(R.string.read_more, comments.size),
                                SpanStyle(color = colors.primary)
                            )
                            Text(text = span,
                                color = colors.textPrimary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable {

                                        vm.act {
                                            FeedAction.OpenSubCommentsScreen(comment.id)
                                        }
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = Gap.Big, vertical = Gap.Small))
                        }

                    }
                }
            }
        }
    }
}


/**
 * 评论的评论
 */
@Composable
private fun SubCommentCard(
    modifier: Modifier = Modifier,
    author: String?,
    parent: RepliesQuery.Reply,
    post: RepliesQuery.Sub_reply,
    onClick: () -> Unit
) {
    val bgAnima = animateColorAsState(
        targetValue = colors.onCard, animationSpec = tween(
            340, 0, FastOutSlowInEasing
        )
    )
    Column(
        modifier
            .background(bgAnima.value)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Gap.Big, vertical = Gap.Mid)) {
        Column {
            Row(
                modifier = Modifier.padding(bottom = Gap.Zero),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyImage(src = post.author.avatar,
                    contentDescription = stringResource(id = R.string.avatar),
                    modifier = Modifier
                        .click {
                            TODO("Msg.navigate(AppNavRoute.USER + / + post.author.id)")
                        }
                        .size(ImageSize.Mid)
                        .clip(RoundedShapes.medium),
                    scale = ContentScale.Crop)
                Spacer(modifier = Modifier.size(Gap.Mid))
                //昵称 回复 Obj
                val extra = if (post.reply_at?.id != null && post.reply_at.id != parent.id) {
                    post.reply_at.author.let { replyAt ->
                        AnnotatedString(
                            " " + string(R.string.reply) + " ",
                            SpanStyle(color = colors.textPrimary)
                        ) + AnnotatedString(
                            replyAt.username + if (replyAt.id == author) string(R.string.post_master) else "",
                            SpanStyle(color = colors.primary)
                        )
                    }
                } else AnnotatedString("")
                val span = AnnotatedString(
                    post.author.username + if (post.author.id == author) string(R.string.post_master) else "",
                    SpanStyle(color = colors.primary)
                ) + extra
                Text(
                    text = span,
                    color = colors.primary,
                    fontSize = 14.sp,
                )
            }
            val humanized = post.time.formatHumanized
            val detail = post.time.formatDateTime
            val state = remember {
                mutableStateOf(true)//时间相同显示发布时间
            }
            Column(Modifier.padding(start = ImageSize.Mid + Gap.Mid)) {
                //内容
                Text(
                    text = post.content.removeImages,
                    color = colors.textPrimary,
                )
//                MarkdownParagraph(
//                    content = post.content.removeImages,
//                    colors = markdownColors(),
//                )
                //图片
                val images = remember { post.content.pickImages }
                if (images.isNotEmpty()) {
                    StaggeredVerticalGrid(
                        maxRows = 4, modifier = Modifier.fillMaxWidth()
                    ) {
                        images.forEachIndexed { index, it ->
                            LazyImage(src = it.second,
                                contentDescription = string(R.string.image),
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(Gap.Tiny)
                                    .clip(CardShapes.small),
                                scale = ContentScale.Crop,
                                placeholder = R.drawable.placeholder,
                                onClick = {
                                    TODO("Msg.showImage(index, images.map { it.second })")
                                })
                        }
                    }
                }
                Text(text = if (state.value) humanized else detail,
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.click {
                        state.value = !state.value
                    })
            }
        }
    }
}

@Composable
fun ReferenceCard(span: AnnotatedString, onClick: () -> Unit = {}) {
    //引用对象
    Column(
        Modifier
            .padding(
                horizontal = Gap.Big, vertical = Gap.Zero
            )
            .clip(CardShapes.small)
            .background(colors.onCard)
            .fillMaxWidth()
            .clickable { onClick() }) {
        ClickableText(
            text = span,
            style = TextStyle(
                color = colors.textPrimary,
                fontSize = 14.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Gap.Mid, vertical = Gap.Small),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        ) {
            // We check if there is an *URL* annotation attached to the text
            // at the clicked position
            span.getStringAnnotations(
                tag = "USER", start = it, end = it
            ).firstOrNull()?.let { range ->
                TODO("Msg.navigateUser(range.item)")
            }
        }
    }
}


@Composable
fun ActionItem(modifier: Modifier = Modifier, action: ActionData) {
    Row(modifier
        .padding(horizontal = Gap.Tiny)
        .clip(CardShapes.small)
        .clickable(enabled = action.onCLick != null) { action.onCLick?.invoke() }
        .padding(vertical = Gap.Small, horizontal = Gap.Mid),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        EasyImage(
            src = action.src,
            contentDescription = action.value,
            modifier = Modifier.size(ImageSize.Small),
            tint = action.color
        )
        Spacer(modifier = Modifier.width(Gap.Small))
        Text(
            text = action.value, color = action.color ?: colors.primary, fontSize = 14.sp
        )
    }
}