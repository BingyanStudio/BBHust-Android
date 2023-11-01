//package com.bingyan.bbhust.ui.screen.feed
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.slideIn
//import androidx.compose.animation.slideOut
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalClipboardManager
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.unit.IntOffset
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.bingyan.bbhust.AppRoute
//import com.bingyan.bbhust.R
//import com.bingyan.bbhust.ui.provider.LocalNav
//import com.bingyan.bbhust.ui.provider.jump
//import com.bingyan.bbhust.ui.screen.create.CertainDialog
//import com.bingyan.bbhust.ui.theme.black
//import com.bingyan.bbhust.ui.widgets.Menu
//import com.bingyan.bbhust.ui.widgets.MenuDialog
//import com.bingyan.bbhust.utils.AuthorUtils
//import com.bingyan.bbhust.utils.click
//import com.bingyan.bbhust.utils.ifNull
//import com.bingyan.bbhust.utils.string
//import com.bingyan.bbhust.utils.toPost
//
//@Composable
//fun MenuSurface(
//	more: MutableState<Boolean>,
//	id: String,
//	reply: Reply?,
//) {
//	AnimatedVisibility(
//		visible = more.value,
//		enter = fadeIn(),
//		exit = fadeOut()
//	) {
//		Spacer(
//			Modifier
//				.fillMaxSize()
//				.background(black.copy(0.4f))
//				.click {
//					more.value = false
//				},
//		)
//	}
//
//	Column(
//		Modifier
//			.fillMaxSize()
//			.statusBarsPadding(), verticalArrangement = Arrangement.Bottom
//	) {
//		AnimatedVisibility(
//			visible = more.value,
//			enter = slideIn { IntOffset(0, it.height) },
//			exit = slideOut { IntOffset(0, it.height) }
//		) {
//			if (reply != null) {
//				ReplyMenuDialog(more = more, reply = reply)
//			} else if (id.isNotBlank()) {
//				PostMenuDialog(more = more, id = id)
//			}
//		}
//	}
//}
//
//@Composable
//private fun PostMenuDialog(
//	more: MutableState<Boolean>,
//	id: String,
//	vm: FeedViewModel = viewModel()
//) {
//	val clip = LocalClipboardManager.current
//	val nav = LocalNav.current
//	MenuDialog(
//		menu =
//		if (vm.state.feed?.author?.id == AuthorUtils.personID || AuthorUtils.permission) {
//			listOf(
//				Menu(
//					R.drawable.edit_line,
//					R.string.edit.string,
//				) {
//					more.value = false
//					nav.jump(AppRoute.POST_CREATE+ "?id=" + id)
//				},
//				Menu(
//					R.drawable.delete_bin_2_line,
//					R.string.delete.string,
//				) {
//					val show = mutableStateOf(true)
//					Msg.state(GlobalState.PopDialog(show) {
//						CertainDialog(
//							show = show,
//							title = string(R.string.delete_really),
//							content = string(R.string.delete_really_desc)
//						) {
//							vm.deletePost(id)
//						}
//					})
//					more.value = false
//				},
//			)
//		} else {
//			emptyList()
//		} +
//				listOf(
//					Menu(
//						R.drawable.link,
//						R.string.copy_link.string,
//					) {
//						more.value = false
//						clip.setText(AnnotatedString(ShareUtils.getLink(id)))
//						Msg.toast(string(R.string.copied))
//					},
//					Menu(
//						R.drawable.code_line,
//						R.string.copy_ref_code.string,
//					) {
//						more.value = false
//						vm.feed.value?.let {
//							clip.setText(AnnotatedString("[${it.title} - ${it.author.username}]($schemaHttps$domain/post/detail?id=${id})"))
//							Msg.toast(string(R.string.copied))
//						}.ifNull {
//							Msg.toast(string(R.string.try_later))
//						}
//					},
//					Menu(
//						R.drawable.copy_line,
//						R.string.copy_all.string,
//					) {
//						more.value = false
//						vm.feed.value?.content?.let {
//							clip.setText(AnnotatedString(it))
//							Msg.toast(string(R.string.copied))
//						}.ifNull {
//							Msg.toast(string(R.string.try_later))
//						}
//					},
//					Menu(
//						R.drawable.share_line,
//						R.string.share.string
//					) {
//						more.value = false
//						vm.feed.value?.toPost?.let {
//							Msg.post.value = it
//						}.ifNull {
//							Msg.toast(string(R.string.try_later))
//						}
//					}
//				)
//	) {
//		more.value = false
//	}
//}
//
//@Composable
//private fun ReplyMenuDialog(
//	more: MutableState<Boolean>,
//	reply: Reply,
//	vm: FeedViewModel = hiltViewModel()
//) {
//	val clip = LocalClipboardManager.current
//	MenuDialog(
//		menu =
//		if (reply.author == AuthorUtils.personID || AuthorUtils.permission) {
//			listOf(
//				Menu(
//					R.drawable.delete_bin_2_line,
//					R.string.delete.string,
//				) {
//					val show = mutableStateOf(true)
//					Msg.state(GlobalState.PopDialog(show) {
//						CertainDialog(
//							show = show,
//							title = string(R.string.delete_really),
//							content = string(R.string.delete_reply_really_desc)
//						) {
//							vm.deleteReply(reply.id)
//
//						}
//					})
//					more.value = false
//				},
//			)
//		} else {
//			emptyList()
//		} +
//				listOf(
//					Menu(
//						R.drawable.copy_line,
//						R.string.copy_all.string,
//					) {
//						more.value = false
//						reply.content.let {
//							clip.setText(AnnotatedString(it))
//							Msg.toast(string(R.string.copied))
//						}
//					},
//					/*
//										Menu(
//											R.drawable.report_line,
//											R.string.report.string
//										) {
//											more.value = false
//											//举报
//										}*/
//				)
//	) {
//		more.value = false
//	}
//}
//
data class Reply(
	val id: String,
	val author: String,
	val content: String
)