package com.bingyan.bbhust.utils

fun String.unescapeHtml() = this
    .replace("&lt;".toRegex(), "<")
    .replace("&gt;".toRegex(), ">")
    .replace("&amp;".toRegex(), "&")
    .replace("&quot;".toRegex(), "\"")
    .replace("&copy;".toRegex(), "©")
    .replace("&reg;".toRegex(), "®")
    .replace("&times;".toRegex(), "×")
    .replace("&divide;".toRegex(), "÷")