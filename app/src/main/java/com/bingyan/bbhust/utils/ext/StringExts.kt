package com.bingyan.bbhust.utils.ext

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bingyan.bbhust.App
import com.bingyan.bbhust.CurrentUserQuery
import java.io.File
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min


/**
 * 字符串相关扩展
 */
//寻找第一个匹配指定正则表达式的字符指针，不存在返回-1
fun String.indexOfRegex(pattern: String): Int {
    val matcher = Pattern.compile(pattern).matcher(this)
    if (matcher.find()) return matcher.start()
    return -1
}

val String.urlEncode: String get() = URLEncoder.encode(this, Charset.defaultCharset().name())

/**
 * 将一个字符串分割为前后两部分
 */
fun String.divide(divider: String): Pair<String, String>? {
    val index = indexOf(divider)
    if (index < 0) return null
    return Pair(substring(0, index), substring(index + 1, length - 1))
}

//寻找第一个匹配指定正则表达式的字符指针，不存在返回-1
fun String.indexOfRegexRange(pattern: String): IntRange {
    val matcher = Pattern.compile(pattern).matcher(this)
    if (matcher.find()) return IntRange(matcher.start(), matcher.end())
    return IntRange.EMPTY
}

//寻找第一个匹配指定正则表达式的字符指针，不存在返回-1
fun CharSequence.indexOfRegex(pattern: String): Int {
    val matcher = Pattern.compile(pattern).matcher(this)
    if (matcher.find()) return matcher.start()
    return -1
}

//反向匹配指定正则表达式的字符指针，不存在返回-1
fun CharSequence.lastIndexOfRegex(pattern: String, start: Int): Int {
    val result = pattern.toRegex().findAll(this).findLast { it.range.last < start } ?: return -1
    return result.range.last
}

//从Start开始快速反向查找指定的Char的位置，不存在返回null
fun CharSequence.lastIndexOfChars(
    start: Int = length - 1,
    end: Int = 0,
    once: Array<Char>,
    twice: Array<Char>
): Int? {
    var t = Char(0)
    for (i in start downTo end) {
        if (this[i] in once) return i
        if (this[i] in twice) {
            if (t != this[i]) t = this[i]
            else return i
        }
    }
    return null
}

//从Start开始快速正向查找指定的Char的位置，不存在返回null
fun CharSequence.indexOfChars(start: Int = 0, end: Int = length - 1, vararg char: Char): Int? {
    for (i in start..end) {
        if (this.length > i) {
            if (this[i] in char) return i
        } else {
            return null
        }
    }
    return null
}

val String.asFile get() = File(this)
val String.preview: String
    get() = if (this.contains("cdn.bbhust.hust.online")) "$this@small" else this
val String.mini: String
    get() = if (this.contains("cdn.bbhust.hust.online")) "$this@mini" else this
val String.previewLocal: String
    get() = "$this@small"
val String.origin
    get() = replace(".pre", "")

fun String.verify(regex: Regex? = null, tips: String): Boolean {
    if (regex == null) {
        if (this.isBlank()) {
            Toast.makeText(App.CONTEXT, tips, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    } else {
        if (regex.matches(this)) {
            return true
        }
        Toast.makeText(App.CONTEXT, tips, Toast.LENGTH_SHORT).show()
        return false
    }
}

/**
 * 压缩摘要文字，去除多余换行等
 */
val String.compress
    get() = run {
        this.replace("\n", " ")
            .replace("\t", " ")
            .replace(Regex("( ){2,}"), " ")
    }

/**
 * 提取摘要，去除多余换行等
 * 前100个字符
 */
val String.digest
    get() = run {
        this
            .substring(0, min(this.length, 200))
            .replace("\n", " ")
            .replace("\t", " ")
            .replace(Regex("( ){2,}"), " ")
    }

/**
 * 从正文中提取图片
 * @return List<Pair<图片名称,图片链接>>
 */
val String.pickImages
    get() = Regex("!\\[([\\W\\w]*?)]\\(([\\S]+?)\\)")
        .findAll(this).map {
            Pair(it.groupValues[1], it.groupValues[2])
        }.take(9).toSet()
val String.pickExternalImages
    get() = Regex("!\\[([\\W\\w]*?)]\\(([\\S]+?)\\)")
        .findAll(this).map {
            Pair(it.groupValues[1], it.groupValues[2])
        }.toSet().filter { !it.second.contains("hust.online") && it.second.startsWith("http") }

val String.removeImages
    get() =
        replace(Regex("!\\[]\\((\\S+?)\\)"), "[图片]")
            .replace(Regex("!\\[([\\W\\w]*?)]\\((\\S+?)\\)"), "[$1]")
            .trim()
val String.removeLinkWithDesc
    get() =
        replace(Regex("!\\[]\\((\\S+?)\\)"), "")
            .replace(Regex("(?:!|)\\[([\\W\\w]*?)]\\((\\S+?)\\)"), "[$1]")

val CurrentUserQuery.Account_info.shadowMail
    get() = person_id.run {
        if (length >= 3) {
            val st = 3.coerceIn(1, length - 1)
            val ed = (length - 3).coerceIn(1, length - 1)
            replaceRange(min(st, ed), max(st, ed), "***")
        } else "***"
    } + if (is_alumni) "@alumni.hust.edu.cn" else "@hust.edu.cn"

val CurrentUserQuery.Account_info.shadowPhone
    get() = phone.run {
        if (length >= 3) {
            val st = 3.coerceIn(1, length - 1)
            val ed = (length - 3).coerceIn(1, length - 1)
            replaceRange(min(st, ed), max(st, ed), "***")
        } else "***"
    }

fun Intent.printExtra() {
    Log.i("Bundle Content", "Component:" + this.component)
    Log.i("Bundle Content", "Type:" + this.type)
    Log.i("Bundle Content", "Package:" + this.`package`)
    Log.i("Bundle Content", "id:" + this.identifier)
    Log.i("Bundle Content", "action:" + this.action)
    Log.i("Bundle Content", "DataString:" + this.dataString)
    val bundle: Bundle = extras ?: return
    for (key in bundle.keySet()) {
        Log.i("Bundle Content", "Key=" + key + ", content=" + bundle.get(key))
    }
}