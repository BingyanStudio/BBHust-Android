package com.bingyan.bbhust.utils

import java.util.UUID


fun random(max: Int) = (Math.random() * max).toInt()

// Object Inherited Methods
fun UUID.string(): String {
    return toString().replace("-", "")
}