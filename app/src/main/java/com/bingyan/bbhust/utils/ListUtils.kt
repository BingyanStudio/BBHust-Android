package com.bingyan.bbhust.utils

/**
 * 通过指定预测器定位需要更新的位置并使用更新函数更新内容
 */
fun <T> MutableList<T>.update(filter: (T) -> Boolean, update: (T) -> T) {
    val lit = listIterator()
    while (lit.hasNext()) {
        val it = lit.next()
        if (filter(it)) {
            lit.set(update(it))
        }
    }
}