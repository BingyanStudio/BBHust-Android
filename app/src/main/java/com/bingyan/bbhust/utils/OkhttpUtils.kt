package com.bingyan.bbhust.utils

import okhttp3.Interceptor
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun interceptor(ak: String, sk: String, token: String) = Interceptor { chain ->
    val request = chain.request()
    val uri = request.url.encodedPath
    val timestamp = Date().alternateIso8601DateFormat
    val authStringPrefix = "bce-auth-v1/$ak/${timestamp}/1800"
    val queryString = request.url.queryParameterNames.sorted().map { key ->
        request.url.queryParameterValues(key).joinToString("&") { value ->
            "$key=$value"
        }
    }.joinToString("&").encode
    val headers = request.headers.names().sorted().map { it.lowercase(Locale.ROOT) }.filter {
        it.startsWith("x-bce-") || it in listOf(
            "host",
            "content-type",
            "content-length",
            "content-md5"
        )
    }.joinToString("\n") {
        "${it.encode}:${request.header(it)?.encode ?: ""}"
    }
    val canonicalRequest =
        "${request.method.uppercase()}\n$uri\n$queryString\n$headers\n$headers"
    val signingKey = authStringPrefix.HMACSha256(sk)
    val sign = canonicalRequest.HMACSha256(signingKey)
    val response = chain.proceed(
        request.newBuilder().addHeader("Authorization", "$authStringPrefix//$sign").build()
    )
    response
}

fun String.HMACSha256(key: String): String {
    val sha256HMAC = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key.toByteArray(charset("utf-8")), "HmacSHA256")
    sha256HMAC.init(secretKey)
    val hash = sha256HMAC.doFinal(this.toByteArray(charset("utf-8")))
    return hash.hex
}

private val ByteArray.hex: String
    get() {
        val stringBuffer = StringBuffer()
        var temp: String?
        for (i in indices) {
            temp = Integer.toHexString(get(i).toInt() and 0xFF)
            if (temp.length == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0")
            }
            stringBuffer.append(temp)
        }
        return stringBuffer.toString()
    }

val String.encode: String get() = URLEncoder.encode(this.trim(), "UTF-8")
private const val DATA_TIME_AlternateIso8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
val Date.alternateIso8601DateFormat: String?
    get() {
        val sdf = SimpleDateFormat(
            DATA_TIME_AlternateIso8601_FORMAT,
            Locale.ENGLISH
        )
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(time)
    }