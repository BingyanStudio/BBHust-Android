package com.bingyan.bbhust.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object UploadImageUtils {
    private const val BASE_URL = "https://up-z1.qiniup.com/"
    private const val TIMEOUT = 10

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()

    init {
        builder.connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
    }

    fun <T> create(clazz: Class<T>): T {
        builder.addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("Content-Type", "multipart/form-data")
                .method(original.method, original.body)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        })
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(builder.build())
            .build()
        return retrofit.create(clazz)
    }
}

object DownloadImageUtils {
    fun download(url: String, onFailed: () -> Unit, onSuccess: (String) -> Unit) {
        val client = OkHttpClient()
        val request: Request = Request.Builder() //访问路径
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailed()
            }

            override fun onResponse(call: Call, response: Response) {
                val bytes = response.body?.bytes()
                if (bytes != null && bytes.isNotEmpty()) {
                    AppCache.write(md5(url), bytes)
                    onSuccess("tmp/" + md5(url))
                }
            }
        })
    }
}