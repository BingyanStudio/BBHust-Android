package com.bingyan.bbhust.ui.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.bingyan.bbhust.R
import com.bingyan.bbhust.utils.click
import com.bingyan.bbhust.utils.ext.mini
import com.bingyan.bbhust.utils.ifBlank


/**
 * 懒加载图片组件
 */

@Composable
fun EasyImage(
    modifier: Modifier = Modifier,
    @DrawableRes src: Int,
    contentDescription: String,
    tint: Color? = null,
    alpha: Float = 1.0f,
    scale: ContentScale = ContentScale.Fit,
) {
    if (src != 0) {
        Image(
            painter = painterResource(id = src),
            modifier = modifier,
            contentDescription = contentDescription,
            colorFilter = tint?.let { ColorFilter.tint(it) },
            alpha = alpha,
            contentScale = scale
        )
    }
}

@Composable
fun LazyImage(
    modifier: Modifier = Modifier,
    src: String?,
    contentDescription: String,
    tint: Color? = null,
    alpha: Float = 1.0f,
    scale: ContentScale = ContentScale.Fit,
    @DrawableRes placeholder: Int = R.drawable.default_avatar,
    @DrawableRes error: Int = R.drawable.image_banned,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = src?.mini?.ifBlank { DEFAULT_AVATAR },
        placeholder = painterResource(placeholder),
        error = painterResource(R.drawable.image_banned),
        fallback = painterResource(placeholder),
        modifier = modifier.then(
            if (onClick != null)
                Modifier.click(onClick)
            else Modifier
        ),
        contentDescription = contentDescription,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alpha = alpha,
        contentScale = scale
    )
}

const val DEFAULT_AVATAR = "https://cdn.bbhust.hust.online/global/default-avatar.png"
