package com.bingyan.bbhust.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.CardShapesTopHalf
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.RoundedShapes
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.string

/**
 * 底部弹出的菜单
 */
@Composable
fun MenuDialog(
    menu: List<Menu>, cancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .click { }
            .background(
                colors.card, CardShapesTopHalf.large
            ),
    ) {
        StaggeredVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Gap.Big), maxRows = 4
        ) {
            menu.forEach {
                MenuItem(menu = it, cancel)
            }
        }
        Text(text = R.string.cancel.string,
            color = colors.primary,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    cancel()
                }
                .padding(vertical = Gap.Big),
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun MenuItem(menu: Menu, cancel: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = Gap.Tiny)
            .fillMaxWidth()
            .clip(CardShapes.medium)
            .clickable { menu.onClick(cancel) }
            .padding(horizontal = Gap.Mid, vertical = Gap.Mid),
        verticalArrangement = Arrangement.spacedBy(Gap.Mid),
        horizontalAlignment = Alignment.CenterHorizontally) {
        EasyImage(
            src = menu.src,
            contentDescription = menu.name,
            modifier = Modifier
                .background(
                    color = menu.backgroundColor ?: colors.onCard, RoundedShapes.small
                )
                .padding(Gap.Mid)
                .size(ImageSize.Mid),
            tint = menu.foregroundColor ?: colors.primary
        )
        Text(
            text = menu.name,
            color = colors.textPrimary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 菜单项
 * @param src 菜单图标
 * @param name 菜单名称
 * @param foregroundColor 前景色，图标色，默认为主题色
 * @param backgroundColor 背景色，默认为浅灰色
 */
data class Menu(
    @DrawableRes val src: Int,
    val name: String,
    val foregroundColor: Color? = null,
    val backgroundColor: Color? = null,
    val onClick: (() -> Unit) -> Unit
)


//fun imageMenu(position: Int, images: List<String>) =
//	listOf(
//		Menu(
//			R.drawable.check_line, R.string.save.string
//		) {
//			ShareUtils.savePictures(listOf(images[position]))
//		},
//	) + if (images.size > 1) {
//		listOf(
//			Menu(
//				R.drawable.check_double_line, R.string.save_all.string
//			) {
//				ShareUtils.savePictures(images)
//			},
//		)
//	} else {
//		emptyList()
//	} + listOf(
//		Menu(
//			R.drawable.share_line,
//			R.string.share.string,
//		) {
//			it()
//			ShareUtils.share(images[position])
//		}
//	)
