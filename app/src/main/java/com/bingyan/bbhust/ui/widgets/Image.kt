package com.bingyan.bbhust.ui.widgets

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.bingyan.bbhust.App
import com.bingyan.bbhust.utils.ifElse
import okio.FileSystem

@Composable
fun CacheImage(
    modifier: Modifier = Modifier,
    src: String,
    style: Style = Style.Mini,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop
) {
    val ctx = LocalContext.current
    val key = src.key(style)
    AsyncImage(
        model = ImageRequest.Builder(ctx)
            .data(src.style(style))
            .allowHardware(true)
            .diskCacheKey(key)
            .memoryCacheKey(key)
            .build(),
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        contentScale = contentScale,
    )
}

enum class Style {
    Mini, Middle, Large, Original
}

val Style.string: String
    get() = when (this) {
        Style.Mini -> "mini"
        Style.Middle -> "middle"
        Style.Large -> "large"
        Style.Original -> ""
    }

val String.key: String
    get():String {
        //        Log.i("CacheKey", "Image: $this, CacheKey: $key")
        return """[a-fA-F0-9]+?/[a-fA-F0-9]+?\.(png|gif|webp|bmp|jpg|jpeg)""".toRegex()
            .find(this)?.value ?: this
    }

fun String.key(style: Style): String {
    return if (style.string.isBlank()) key else "$key@${style.string}"
}

fun String.style(style: Style): String {
//        Log.i("CacheKey", "Image: $this, CacheKey: $key")
    return if (style.string.isBlank()) this else
        contains("?").ifElse(
            "$this&x-bce-process=style/${style.string}",
            "$this?x-bce-process=style/${style.string}"
        ).also {
            Log.i("Style", it)
        }
}


val imageLoader = ImageLoader.Builder(App.CONTEXT)
    .crossfade(true)
    .diskCache(
        DiskCache.Builder()
            .maxSizePercent(0.1)
            .minimumMaxSizeBytes(1024)
            .fileSystem(FileSystem.SYSTEM)
            .directory(App.CONTEXT.externalCacheDir ?: App.CONTEXT.cacheDir)
            .build()
    )
    .allowHardware(true)
    .diskCachePolicy(CachePolicy.ENABLED)
    .networkCachePolicy(CachePolicy.ENABLED)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .memoryCache(
        MemoryCache.Builder(App.CONTEXT)
            .strongReferencesEnabled(true)
            .weakReferencesEnabled(true)
            .build()
    )
    .build()
