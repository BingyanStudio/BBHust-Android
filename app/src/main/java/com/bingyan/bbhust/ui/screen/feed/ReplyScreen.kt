package com.bingyan.bbhust.ui.screen.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.provider.LocalLoading
import com.bingyan.bbhust.ui.provider.localProvider
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.CardShapesTopHalf
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.black
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.widgets.PlainTextField
import com.bingyan.bbhust.ui.widgets.StaggeredVerticalGrid
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.string
import com.bingyan.bbhust.utils.wrap

@Composable
fun ReplyScreen(
	vm: FeedViewModel = viewModel(),
	onSuccess: () -> Unit = {},
	content : @Composable ()->Unit
) {
	val reply = vm.state.isReply

	content()
	AnimatedVisibility(
		visible = reply,
		enter = fadeIn(),
		exit = fadeOut()
	) {
		Spacer(
			Modifier
				.fillMaxSize()
				.background(black.copy(0.2f))
				.click {
					vm.state = vm.state.copy(isReply = false)
				},
		)
	}
	
	Column(
		Modifier
			.fillMaxSize()
			.statusBarsPadding(), verticalArrangement = Arrangement.Bottom
	) {
		AnimatedVisibility(
			visible = reply,
			enter = slideIn { IntOffset(0, it.height) },
			exit = slideOut { IntOffset(0, it.height) }
		) {
			ReplyCard(onSuccess)
		}
	}
}


/**
 * 回复弹窗
 */
@Composable
private fun ReplyCard(
	onSuccess: () -> Unit = {},
	vm: FeedViewModel = viewModel(),
) {
	val replyCurrent =vm.state.replyCurrent ?: return
	val loading = LocalLoading.current
	Box(
		Modifier.fillMaxWidth(),
		contentAlignment = Alignment.BottomCenter
	) {
		Column(
			Modifier
				.click { }
				.clip(CardShapesTopHalf.large)
				.background(colors.card)
				.fillMaxWidth()
				.imePadding()
				.border(
					width = 0.3.dp,
					color = Color.Black.copy(0.2f),
					shape = CardShapesTopHalf.large
				)
		) {
			Spacer(modifier = Modifier.height(Gap.Big))
			ReferenceCard(replyCurrent.span)
			Spacer(modifier = Modifier.height(Gap.Mid))
			PlainTextField(
				value = replyCurrent.content.value,
				onValueChange = {
					replyCurrent.content.value = it
				},
				hint = string(R.string.content),
				modifier = Modifier
					.fillMaxWidth()
					.weight(0.8f, false)
					.defaultMinSize(minHeight = 150.dp)
					.padding(horizontal = Gap.Big)
			)
			Spacer(modifier = Modifier.height(Gap.Mid))
			StaggeredVerticalGrid(
				maxRows = 8,
				modifier = Modifier
					.shadow(12.dp)
					.background(colors.titleBar)
					.fillMaxWidth()
			) {
				/**FuncItem(
					src = R.drawable.image_add_line,
					desc = string(R.string.image_add)
				) {
					Msg.event(GlobalEvent.ChooseFiles {
						vm.uploadImage(it) { s ->
							replyCurrent.content.value =
								replyCurrent.content.value.insert("![图片](${s})")
						}
					})
				}
				FuncItem(
					src = R.drawable.link,
					desc = string(R.string.link)
				) {
					replyCurrent.content.value = replyCurrent.content.value.replace(-1) {
						"[描述](${it})"
					}
				}
				**/
			}

			Row(
				Modifier
					.fillMaxWidth()
					.padding( Gap.Big,),
				verticalAlignment = Alignment.CenterVertically
			) {
//				Text(
//					text = string(R.string.comment),
//					color = colors.textPrimary,
//					fontSize = 18.sp,
//					modifier = Modifier.padding(vertical = Gap.Big)
//				)
				Spacer(modifier = Modifier.weight(1f))
				Row (Modifier.clip(CardShapes.medium)
					.background(color = colors.primary)
					.clickable {
						loading
							.setState(
								msg = string(R.string.waiting),
								onDismiss = {},
								content = {}
							)
							.show(true)
						vm.act {
							FeedAction.Publish(replyAt = vm.state.replyCurrent?.replyAt, onSuccess = onSuccess, onFailed = {
								loading.show(false)
							} ,onCompletion = {
								loading.show(false)
							})
						}
					}
					.padding(top = 6.dp, bottom = 6.dp, start = 14.dp, end = 14.dp)){
					Text(
						text = string(R.string.publish),
						color = Color.White,
						fontSize = 14.sp
					)
				}
			}
		}
	}
}