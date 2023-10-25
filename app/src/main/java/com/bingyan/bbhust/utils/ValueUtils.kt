package com.bingyan.bbhust.utils

import okhttp3.OkHttpClient

val appUserAgent = "bbhust-android/${AppUtils.versionName}"

val okHttpClient = OkHttpClient.Builder().build()

val neteaseMusic = """分享[\W\w]*id=([0-9]+)[\W\w]*?\(来自@网易云音乐\)""".toRegex()

val String.replaceNeteaseMusic
    get() =
        replace(neteaseMusic, "@music:$1#163@")