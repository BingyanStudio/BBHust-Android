package com.bingyan.bbhust.ui.screen.feed

import Reply
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bingyan.bbhust.AppRoute
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ReplyQuery
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.provider.jump
import com.bingyan.bbhust.ui.theme.*
import com.bingyan.bbhust.ui.widgets.*
import com.bingyan.bbhust.ui.widgets.app.ActionData
import com.bingyan.bbhust.ui.widgets.sheet.BottomSheetDialogState
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.*
import com.bingyan.bbhust.utils.string
//import com.bingyan.markdown.MarkdownParagraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun SubCommentScreen(
    draggable: MutableState<Boolean>,
    state: BottomSheetDialogState,
    show: MutableState<Boolean>,
    sid: String,
    feedId: String,
    vm: FeedViewModel = viewModel(),
    onDismiss: () -> Unit
) {

    val highlight = remember {
        mutableStateOf(-1)
    }
    val more = remember {
        mutableStateOf(false)
    }
    val replyMore = remember {
        mutableStateOf<Reply?>(null)
    }
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val reply: MutableState<ReplyQuery.Reply?> = remember {
        mutableStateOf(null)
    }
    val loadingStats = remember {
        mutableStateOf(true)
    }
    LaunchedEffect(sid) {
        TODO("vm.subComments(sid, reply, loadingStats)")
    }
    val commentsMap: HashMap<String, Int> = hashMapOf()
    val stats = rememberLazyListState()
    val feedDesc = vm.state.commentDesc
    val commentDesc = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(stats) {
        snapshotFlow {
            Pair(
                stats.firstVisibleItemIndex, stats.firstVisibleItemScrollOffset
            )
        }.map { s -> s.first == 0 && s.second == 0 }.distinctUntilChanged().collect {
            draggable.value = it
        }
    }
    BackHandler {
        if (state.isExpanded) {
            scope.launch {
                state.collapse()
            }
        } else {
            show.value = false
            onDismiss()
        }
    }
    Surface(color = Transparent) {
        Column(
            Modifier
                .click { }
                .clip(CardShapesTopHalf.large)
                .background(colors.card)
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()) {
            //顶栏
            Header(name = R.string.comment.string) {
                show.value = false
                onDismiss()
            }
            LazyColumn(
                Modifier.fillMaxSize(), state = stats
            ) {
                item {
                    reply.value?.let {
                        MainCommentCard(
                            post = it, show = show,
                            author = vm.state.feed?.author?.id,
                            onLike = { like ->
                                    vm.act {
                                        FeedAction.LikeReply(it.id,like,feedDesc)
                                    }
                                },
                            onLongClick = {
                                //长按菜单
                                replyMore.value = Reply(
                                    it.id, it.author.id, it.content
                                )
                                more.value = true
                            },
                        ) { _ ->
                            //主回复
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Gap.Big)
                                .background(colors.background)
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
                                        colors.background,
                                        RoundedCornerShape(50)
                                    )
                                    .padding(Gap.Small),
                                horizontalArrangement = Arrangement.spacedBy(Gap.Small)
                            ) {
                                @Composable
                                fun capsule(value: String, selected: Boolean, onClick: () -> Unit) {
                                    Text(
                                        text = value,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .clickable { onClick() }
                                            .background(if (selected) colors.card else colors.background)
                                            .padding(
                                                horizontal = Gap.Mid,
                                                vertical = Gap.Tiny
                                            )
                                    )
                                }
                                capsule(value = "最新", commentDesc.value) {
                                    commentDesc.value = true
                                }
                                capsule(value = "时间轴", !commentDesc.value) {
                                    commentDesc.value = false
                                }
                            }
                        }
                    }
                }

                val parent = reply.value
                val comments = reply.value?.sub_reply
                val commentsReversed = reply.value?.sub_reply?.asReversed()
                if (parent != null && comments != null && commentsReversed != null) {
                    itemsIndexed(
                        if (commentDesc.value) commentsReversed else comments,
                        key = { index, it -> commentsMap[it.id] = index; it.hashCode() }) { i, it ->
                        SelfCommentCard(post = it,
                            author = vm.state.feed?.author?.id,
                            highlight = highlight,
                            index = i,
                            show = show,
                            parent = parent,
                            onLike = { like ->
                                     vm.act {
                                         FeedAction.LikeReply(it.id,like,feedDesc)
                                     }
                            },
                            onLongClick = {
                                //长按菜单
                                replyMore.value = Reply(
                                    it.id, it.author.id, it.content
                                )
                                more.value = true
                            },
                            onRefClick = { ref ->
                                scope.launch {
                                    val index = commentsMap[ref]
                                    if (index != null) {
                                        withContext(Dispatchers.Main) {
                                            stats.animateScrollToItem(index)
                                            highlight.value = index
                                        }
                                    }
                                }
                            }) {
                            //回复
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Gap.Big * 2),
                        contentAlignment = Alignment.Center
                    ) {
                        if (loadingStats.value) {
                            LoadingBar()
                        } else {
                            Text(
                                text = if (comments.isNullOrEmpty()) string(R.string.empty)
                                else string(R.string.comment_count, comments.size),
                                color = colors.textPrimary,
                                modifier = Modifier,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
        }
        ReplyScreen {
            scope.launch {
                TODO("vm.subComments(sid, reply, loadingStats)")
            }
        }
        //MenuSurface(more, "", replyMore.value)
    }
    LaunchedEffect(vm.state.isReply, more.value) {
        if (vm.state.isReply || more.value) {
            state.expand()
        }
        draggable.value = !vm.state.isReply && !more.value
    }
}


/**
 * 用户评论
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainCommentCard(
    post: ReplyQuery.Reply,
    author: String?,
    show: MutableState<Boolean>,
    onLike: (Boolean) -> Unit,
    onLongClick: () -> Unit,
    onClick: (ReplyQuery.Reply) -> Unit
) {
    val nav = LocalNav.current
    val comments = post.sub_reply ?: emptyList()
    Column(Modifier
        .background(colors.card)
        .fillMaxWidth()
        .combinedClickable(onLongClick = onLongClick) { onClick(post) }
        .padding(horizontal = Gap.Big, vertical = Gap.Big)) {
        Row(
            modifier = Modifier.padding(bottom = Gap.Zero)
        ) {
            LazyImage(src = post.author.avatar,
                contentDescription = stringResource(id = R.string.avatar),
                modifier = Modifier
                    .click {
                        show.value = false
                        nav.jump(AppRoute.user(post.author.id))
                    }
                    .size(ImageSize.Normal)
                    .clip(RoundedShapes.medium),
                scale = ContentScale.Crop)
            Spacer(modifier = Modifier.size(Gap.Mid))
            Column {
                //昵称
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.author.username + if (post.author.id == author) string(R.string.post_master) else "",
                        color = colors.primary,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.width(Gap.Mid))
                    //BB鸡的AI提醒
                    if(post.author.id == "6422d2016f6eee4d08cf38ad")
                        Text(text = "机器人",
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(colors.secondary, RoundedCornerShape(30))
                                .padding(Gap.Small, Gap.Tiny)
                        )
                }
                val humanized = post.time.formatHumanized
                val detail = post.time.formatDateTime
                val state = remember {
                    mutableStateOf(true)//时间相同显示发布时间
                }
                //内容
//                MarkdownParagraph(
//                    content = post.content.removeImages,
//                    colors = markdownColors(),
//                )
                Text(
                    text = post.content.removeImages,
                    color = colors.textPrimary,
                )
                //图片
                val images = remember { post.content.pickImages }
                if (images.isNotEmpty()) {
                    StaggeredVerticalGrid(
                        maxRows = 4, modifier = Modifier.fillMaxWidth()
                    ) {
                        images.forEachIndexed { index, it ->
                            LazyImage(
                                src = it.second,
                                contentDescription = string(R.string.image),
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(Gap.Tiny)
                                    .clip(CardShapes.small),
                                scale = ContentScale.Crop,
                                placeholder = R.drawable.placeholder,
                                onClick = {
                                    //Msg.showImage(index, images.map { it.second })
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
                    val liked = post.liked
                    ActionItem(modifier = if (liked) Modifier.gradient() else Modifier,
                        action = ActionData(
                            src = if (liked) R.drawable.thumb_up_fill else R.drawable.thumb_up_line,
                            value = post.like_count.toString(10),
                            color = if (liked) null else colors.textPrimary
                        ) {
                            onLike(liked)
                        })
                    Spacer(modifier = Modifier.width(Gap.Mid))
                    ActionItem(
                        modifier = Modifier, action = ActionData(
                            src = R.drawable.comment,
                            value = comments.size.toString(10),
                            color = colors.textPrimary
                        )
                    )
                }
            }
        }
    }
}

/**
 * 用户评论
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelfCommentCard(
    parent: ReplyQuery.Reply,
    author: String?,
    show: MutableState<Boolean>,
    post: ReplyQuery.Sub_reply,
    highlight: MutableState<Int> = mutableStateOf(-1),
    index: Int = -1,
    onLike: (Boolean) -> Unit,
    onLongClick: () -> Unit,
    onRefClick: (String) -> Unit = {},
    onClick: () -> Unit
) {
    val nav = LocalNav.current
    val cardColor = colors.card
    val highlightColor = colors.forbiddenBtnBg
    val backgroundColor = remember {
        mutableStateOf(cardColor)
    }
    val bgAnima = animateColorAsState(
        targetValue = backgroundColor.value, animationSpec = tween(
            340, 0, FastOutSlowInEasing
        )
    )
    LaunchedEffect(highlight) {
        snapshotFlow { highlight.value }.filter { highlight.value != -1 && highlight.value == index }
            .collect {
                //动画
                for (i in 0..1) {
                    backgroundColor.value = highlightColor
                    delay(340)
                    backgroundColor.value = cardColor
                    delay(340)
                }
                highlight.value = -1
            }
    }
    Column(Modifier
        .background(bgAnima.value)
        .fillMaxWidth()
        .combinedClickable(onLongClick = onLongClick) { onClick() }
        .padding(horizontal = Gap.Big, vertical = Gap.Big)) {
        Row(
            modifier = Modifier.padding(bottom = Gap.Zero)
        ) {
            LazyImage(
                src = post.author.avatar,
                contentDescription = stringResource(id = R.string.avatar),
                modifier = Modifier
                    .size(ImageSize.Normal)
                    .clip(RoundedShapes.medium)
                    .click {
                        show.value = false
                        nav.jump(AppRoute.user(post.author.id))
                    },
                scale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(Gap.Mid))
            Column {
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
                val humanized = post.time.formatHumanized
                val detail = post.time.formatDateTime
                val state = remember {
                    mutableStateOf(true)//时间相同显示发布时间
                }
                if (post.reply_at?.id != null && post.reply_at.id != parent.id) {
                    post.reply_at.let {
                        val refSpan = buildAnnotatedString {
                            pushStringAnnotation(
                                tag = "USER", annotation = it.author.id
                            )
                            withStyle(SpanStyle(color = colors.primary)) {
                                append(it.author.username + if (it.author.id == author) string(R.string.post_master) else "")
                            }
                            pop()
                            append(":")
                            append(it.content)
                        }
                        SelfReferenceCard(refSpan) {
                            onRefClick(it.id)
                        }
                    }
                }
                //内容
                Text(
                    text = post.content.removeImages,
                    color = colors.textPrimary,
                )
//                MarkdownParagraph(
//                    content = post.content.removeImages,
//                    colors = markdownColors(),
//                )
                val images = remember { post.content.pickImages }
                if (images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Gap.Mid))
                    StaggeredVerticalGrid(
                        maxRows = 4, modifier = Modifier.fillMaxWidth()
                    ) {
                        images.forEachIndexed { index, it ->
                            LazyImage(
                                src = it.second,
                                contentDescription = string(R.string.image),
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(Gap.Tiny)
                                    .clip(CardShapes.small),
                                scale = ContentScale.Crop,
                                placeholder = R.drawable.placeholder,
                                onClick = {
                                    //Msg.showImage(index, images.map { it.second })
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
                    val liked = post.liked
                    ActionItem(modifier = if (liked) Modifier.gradient() else Modifier,
                        action = ActionData(
                            src = if (liked) R.drawable.thumb_up_fill else R.drawable.thumb_up_line,
                            value = post.like_count.toString(10),
                            color = if (liked) null else colors.textPrimary
                        ) {
                            onLike(liked)
                        })
                    Spacer(modifier = Modifier.width(Gap.Mid))

                    val comments = remember {
                        parent.sub_reply?.count {
                            it.reply_at?.id == post.id
                        } ?: 0
                    }
                    ActionItem(
                        modifier = Modifier, action = ActionData(
                            src = R.drawable.comment,
                            value = comments.toString(10),
                            color = colors.textPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SelfReferenceCard(span: AnnotatedString, onClick: () -> Unit = {}) {
    //引用对象
    Column(
        Modifier
            .padding(
                horizontal = Gap.Zero, vertical = Gap.Tiny
            )
            .clip(CardShapes.small)
            .background(colors.onCard)
            .fillMaxWidth()
            .clickable { onClick() }) {
        Text(
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
        )
    }
}