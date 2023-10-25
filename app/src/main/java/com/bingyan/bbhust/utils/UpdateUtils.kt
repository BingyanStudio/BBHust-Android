package com.bingyan.bbhust.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.utils.ext.heightDp
import com.bingyan.bbhust.utils.ext.widthDp

//object UpdateUtils {
//	private val model = AppModel()
//
//	fun check(
//		force: Boolean = false,
//		onFailed: (List<Error>?) -> Unit = {},
//		onLatest: () -> Unit = {}
//	) {
//		GlobalScope.launch {
//			when (val resp = model.getLatest()) {
//				is AppResult.Success -> {
//					resp.data?.let {
//						if (it.version_code > AppInfoUtils.versionCode || force) {
//							val show = mutableStateOf(true)
//							Msg.state(
//								GlobalState.PopDialog(
//									show
//								) {
//									UpdateDialog(
//										show = show,
//										oldVersion = "${AppInfoUtils.versionName}(${AppInfoUtils.versionCode})",
//										newVersion = "${it.version}(${it.version_code})",
//										log = it.update_log
//									) {
//										Msg.toast(string(R.string.download_in_browser))
//										Msg.event(GlobalEvent.Browse(it.url, true))
//									}
//								})
//						} else {
//							onLatest()
//						}
//					}
//				}
//
//				is AppResult.Failed -> {
//					onFailed(resp.error)
//				}
//			}
//		}
//	}
//}

@Composable
fun UpdateDialog(
    show: MutableState<Boolean>,
    oldVersion: String,
    newVersion: String,
    log: String,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .clip(CardShapes.large)
            .background(colors.card)
            .padding(vertical = Gap.Big)
            .width(widthDp(0.8f))
            .sizeIn(maxWidth = widthDp(0.8f), maxHeight = heightDp(0.4f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Gap.Mid)
        ) {
            Text(
                text = string(R.string.updatable),
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(horizontal = Gap.Big)
            )
            Text(
                text = "${oldVersion}->${newVersion}",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = Gap.Big)
            )
            Text(
                text = log, style = TextStyle(
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    lineHeight = 2.em,
                ), modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Gap.Big)
                    .weight(1f)
            )
            Box(modifier = Modifier
                .clip(CardShapes.small)
                .clickRipple {
                    show.value = false
                    onClick()
                }
                .background(themeColor)
                .padding(horizontal = Gap.Big * 4, vertical = Gap.Big),
                contentAlignment = Alignment.Center) {
                Text(
                    text = string(R.string.update), color = Color.White, fontSize = 14.sp
                )
            }
            Box(modifier = Modifier
                .clip(CardShapes.small)
                .clickRipple {
                    show.value = false
                }
                .background(colors.onCard)
                .padding(horizontal = Gap.Big * 4, vertical = Gap.Big),
                contentAlignment = Alignment.Center) {
                Text(
                    text = string(R.string.cancel), color = themeColor, fontSize = 14.sp
                )
            }
        }
    }
}