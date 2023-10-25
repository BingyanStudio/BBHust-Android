package com.bingyan.bbhust.utils

import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.UriHandler
import com.apollographql.apollo3.api.Optional
import com.blankj.utilcode.util.EncryptUtils
import kotlin.time.Duration.Companion.milliseconds


fun <T> Boolean.ifElse(ifTrue: () -> T, ifFalse: () -> T): T {
    return if (this) {
        ifTrue()
    } else {
        ifFalse()
    }
}

fun <T> MutableState<Boolean>.ifElse(ifTrue: () -> T, ifFalse: () -> T): T {
    return if (this.value) {
        ifTrue()
    } else {
        ifFalse()
    }
}

fun <T> Boolean.ifElse(ifTrue: T, ifFalse: T): T {
    return if (this) {
        ifTrue
    } else {
        ifFalse
    }
}

fun <T> MutableState<Boolean>.ifElse(ifTrue: T, ifFalse: T): T {
    return if (this.value) {
        ifTrue
    } else {
        ifFalse
    }
}

fun MutableState<Boolean>.toggle() {
    this.value = !this.value
}

inline fun (() -> Unit).ifError(block: (Exception) -> Unit) {
    try {
        this()
    } catch (e: Exception) {
        block(e)
    }
}

val <T> T?.wrap get() = Optional.presentIfNotNull(this)
val None = Optional.Absent

fun md5(raw: String): String {
    return EncryptUtils.encryptMD5ToString(raw)
}

fun md5(raw: ByteArray): String {
    return EncryptUtils.encryptMD5ToString(raw)
}

val Int.duration
    get(): String {
        val duration = this.milliseconds
        val sec = duration.inWholeSeconds % 60
        val min = duration.inWholeMinutes % 60
        val hour = duration.inWholeHours
        return if (hour < 1)
            String.format("%d:%02d", min, sec)
        else
            String.format("%d:%02d:%02d", hour, min, sec)
    }

// intent://song/${id}#Intent;scheme=orpheus;package=com.netease.cloudmusic;end


fun UriHandler.openNetease(songId: String) {
    openUri("intent://song/${songId}#Intent;scheme=orpheus;package=com.netease.cloudmusic;end")
}