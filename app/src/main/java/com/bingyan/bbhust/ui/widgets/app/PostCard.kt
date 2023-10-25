package com.bingyan.bbhust.ui.widgets.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.AppRoute
import com.bingyan.bbhust.PostsQuery
import com.bingyan.bbhust.R
import com.bingyan.bbhust.SearchQuery
import com.bingyan.bbhust.ui.provider.LocalGalley
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.provider.LocalShare
import com.bingyan.bbhust.ui.provider.jump
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.RoundedShapes
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.widgets.EasyImage
import com.bingyan.bbhust.ui.widgets.LazyImage
import com.bingyan.bbhust.ui.widgets.NormalBandage
import com.bingyan.bbhust.ui.widgets.StaggeredVerticalGrid
import com.bingyan.bbhust.ui.widgets.gradient
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.compress
import com.bingyan.bbhust.utils.ext.formatDateTime
import com.bingyan.bbhust.utils.ext.formatHumanized
import com.bingyan.bbhust.utils.string
import com.bingyan.bbhust.utils.toPost

@Composable
fun <T> PostCard(
    post: SearchQuery.OnPost,
    likePost: (SearchQuery.OnPost) -> Unit
) {
    PostCard(post = post.toPost) { _ ->
        likePost(post)
    }
}

@Composable
fun PostCard(
    post: PostsQuery.Post,
    likePost: (PostsQuery.Post) -> Unit
) {
    val isLike = post.liked
    val likeCount = post.like_count
    val nav = LocalNav.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Gap.Big, vertical = Gap.Mid)
            .clip(CardShapes.medium)
            .background(colors.card, CardShapes.medium)
            .clickable {
                nav.jump(AppRoute.post(post.id))
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = Gap.Big, start = Gap.Big, end = Gap.Big)
        ) {
            LazyImage(
                src = post.author.avatar,
                contentDescription = stringResource(id = R.string.avatar),
                modifier = Modifier
                    .click {
                        nav.jump(AppRoute.user(post.author.id))
                    }
                    .size(ImageSize.Normal)
                    .clip(RoundedShapes.medium),
                scale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(Gap.Mid))
            Column {
                Text(
                    text = post.author.username,
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                )
                val updateHumanized =
                    "${string(R.string.updated_at)} ${post.reply_time.formatHumanized}"
                val updateRealDate =
                    "${string(R.string.updated_at)} ${post.reply_time.formatDateTime}"
                val humanized =
                    "${string(R.string.published_at)} ${post.time.formatHumanized}"
                val realDate =
                    "${string(R.string.published_at)} ${post.time.formatDateTime}"
                val state = remember {
                    mutableIntStateOf(0)
                }
                Text(
                    text = when (state.intValue) {
                        0 -> updateHumanized
                        1 -> updateRealDate
                        2 -> humanized
                        else -> realDate
                    },
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.click {
                        state.intValue = (state.intValue + 1) % 4
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Gap.Big, vertical = Gap.Small),
            horizontalArrangement = Arrangement.spacedBy(Gap.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f, false),
                text = post.title,
                color = colors.textPrimary,
                fontSize = 16.sp,
            )
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                EasyImage(
//                    src = R.drawable.hot,
//                    contentDescription = string(R.string.hot_value),
//                    modifier = Modifier.size(ImageSize.XSmall)
//                )
//                Text(
//                    modifier = Modifier,
//                    text = post.hot_value.toString(10),
//                    color = Color(0xFFFF5C00),
//                    fontSize = 14.sp,
//                )
//            }
        }
        Text(
            modifier = Modifier.padding(horizontal = Gap.Big, vertical = Gap.Small),
            text = post.digest.compress,
            color = colors.textSecondary,
            fontSize = 12.sp,
        )
        val images = remember {
            post.images.take(9)
        }
        val gallery = LocalGalley.current
        if (images.isNotEmpty()) {
            StaggeredVerticalGrid(
                maxRows = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Gap.Big)
            ) {
                images.forEachIndexed { i, it ->
                    LazyImage(
                        src = it,
                        contentDescription = it,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(Gap.Tiny)
                            .clip(CardShapes.small),
                        scale = ContentScale.Crop,
                        placeholder = R.drawable.placeholder,
                        onClick = {
                            gallery.showBigImage(i, images)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(Gap.Mid))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Gap.Big),
            horizontalArrangement = Arrangement.spacedBy(Gap.Mid)
        ) {
            post.category.forEach { c ->
                NormalBandage(
                    text = c.name,
                    onClick = { },
                    textColor = colors.textPrimary,
                    background = colors.onCard
                )
            }
        }
        Spacer(modifier = Modifier.height(Gap.Mid))
        Row(Modifier.fillMaxWidth()) {
            ActionItem(
                modifier = Modifier
                    .weight(1f),
                contentModifier =
                if (isLike) Modifier.gradient()
                else Modifier,
                action = ActionData(
                    src = if (isLike) R.drawable.thumb_up_fill else R.drawable.thumb_up_line,
                    value = likeCount.toString(10),
                    color = if (isLike) colors.primary else colors.textPrimary
                ) {
                    likePost(post)
                }
            )
            val comments = post.reply_count
            ActionItem(
                modifier = Modifier.weight(1f),
                action = ActionData(
                    src = R.drawable.comment,
                    value = if (comments == 0) R.string.comment.string else comments.toString(10),
                    color = colors.textPrimary
                ) {
                    nav.jump(AppRoute.postReply(post.id))
                }
            )
            val share = LocalShare.current
            ActionItem(
                modifier = Modifier.weight(1f),
                action = ActionData(
                    src = R.drawable.share_line,
                    value = R.string.share.string,
                    color = colors.textPrimary
                ) {
                    share.sharePost(post)
                }
            )
        }
    }
}

@Composable
private fun ActionItem(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    action: ActionData
) {
    Row(
        modifier
            .clickable(enabled = action.onCLick != null) { action.onCLick?.invoke() }
            .padding(vertical = Gap.Big),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        EasyImage(
            src = action.src,
            contentDescription = action.value,
            modifier = contentModifier.size(ImageSize.Small),
            tint = action.color
        )
        Spacer(modifier = Modifier.width(Gap.Small))
        Text(
            modifier = contentModifier,
            text = action.value,
            color = action.color ?: colors.primary,
            fontSize = 14.sp
        )
    }
}


data class ActionData(
    @DrawableRes val src: Int,
    val value: String,
    val color: Color?,
    val onCLick: (() -> Unit)? = null
)