package com.bingyan.bbhust.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
private val type = Types.newParameterizedType(List::class.java, String::class.java)
val stringsAdapter: JsonAdapter<List<String>> = moshi.adapter(type)
private val mapStringsType = Types.newParameterizedType(
    Map::class.java,
    String::class.java,
    String::class.java
)
val mapStringsAdapter: JsonAdapter<Map<String, String>> = moshi.adapter(mapStringsType)
val List<String>.pack: String? get() = stringsAdapter.toJson(this)
//val swipeIndicator = @Composable { state: SwipeRefreshState, trigger: Dp ->
//    SwipeRefreshIndicator(
//        state, trigger,
//        contentColor = colors.secondary
//    )
//}

fun <T> Any?.ifNull(block: () -> T?): T? {
    if (this == null) {
        return block()
    }
    return null
}

fun String.ifBlank(block: () -> String): String {
    if (this.isBlank()) {
        return block()
    }
    return this
}