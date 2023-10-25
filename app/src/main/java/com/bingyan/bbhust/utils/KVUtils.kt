package com.bingyan.bbhust.utils

import com.tencent.mmkv.MMKV

val kv = KVUtils()

class KVUtils(id: String = "bbhust") {
    private val kv: MMKV = MMKV.mmkvWithID(id)
    var token
        get() = getString("token")
        set(value) {
            if (value == null) kv.removeValueForKey("token")
            else put("token", value)
        }

    fun put(key: String, value: String) {
        kv.encode(key, value)
    }

    fun put(key: String, value: Int) {
        kv.encode(key, value)
    }

    fun put(key: String, value: Boolean) {
        kv.encode(key, value)
    }

    fun put(key: String, value: Float) {
        kv.encode(key, value)
    }


    fun getString(key: String): String? = kv.decodeString(key)
    fun getInt(key: String): Int = kv.decodeInt(key)
    fun getBoolean(key: String): Boolean = kv.decodeBool(key)
    fun getFloat(key: String): Float = kv.decodeFloat(key)

}