package com.bingyan.bbhust.utils

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ScheduleUtils {
    private const val BASE_URL = "https://i.wakeup.fun/"
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