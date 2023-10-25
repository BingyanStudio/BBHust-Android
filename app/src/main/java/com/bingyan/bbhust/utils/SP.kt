package com.bingyan.bbhust.utils

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.bingyan.bbhust.App
import com.tencent.mmkv.MMKV

// 键值对数据库 （基于 MMKV）
// 可以当作 SharedPreferences 使用
// 无需调用 apply() 方法
// 默认 KV 数据库
val SP = SPP()

// 推送消息计数 KV 数据库
val SPPush = SPP("push")

class SPP(spName: String = "bbhust") {
    private val kv: MMKV = MMKV.mmkvWithID(spName, MMKV.MULTI_PROCESS_MODE)

    init {
        migration(spName)
    }

    private fun migration(name: String) {
        val oldMan: SharedPreferences = App.CONTEXT.getSharedPreferences(name, MODE_PRIVATE)
        kv.importFromSharedPreferences(oldMan)
        oldMan.edit().clear().commit()
    }

    fun clear() {
        kv.clearAll()
    }

    fun get(
        name: String, default: String? = null
    ) = kv.getString(name, default)

    fun getInt(
        name: String, default: Int = -1
    ) = kv.getInt(name, default)

    fun getBoolean(
        name: String, default: Boolean = false
    ) = kv.getBoolean(name, default)

    fun getLong(
        name: String, default: Long = 0
    ) = kv.getLong(name, default)

    fun set(
        name: String, value: String?
    ) {
        kv.edit().apply {
            if (value != null) putString(name, value)
            else remove(name)
        }.apply()
    }

    fun setInt(
        name: String, value: Int?
    ) {
        kv.edit().apply {
            if (value != null) putInt(name, value)
            else remove(name)
        }.apply()
    }

    fun set(
        vararg pair: Pair<String, String?>
    ) {
        kv.edit().apply {
            pair.forEach {
                if (it.second != null) putString(it.first, it.second)
                else remove(it.first)
            }
        }.apply()
    }

    fun setBoolean(
        name: String, value: Boolean?
    ) {
        kv.edit().apply {
            if (value != null) putBoolean(name, value)
            else remove(name)
        }.apply()
    }

    fun setLong(
        name: String, value: Long?
    ) {
        kv.edit().apply {
            if (value != null) putLong(name, value)
            else remove(name)
        }.apply()
    }

    fun remove(vararg name: String) {
        kv.edit().apply {
            name.forEach {
                remove(it)
            }
        }.apply()
    }


    fun isDev(): Boolean {
        val devTime = get("dev") ?: ""
        try {
            if (devTime.isNotBlank()) {
                val time = devTime.toLong()
                if (System.currentTimeMillis() - time < 7 * DAY) {
                    return true
                }
            }
        } catch (_: Exception) {
        }
        return false
    }

    var notify
        get() = (get("notify") ?: "true") == "true"
        set(value) = set("notify", value.toString())
    var isReleaseServer
        get() = get("server", "true").equals("true")
        set(value) = set("server", value.toString())
    var activityNotify
        get() = (get("a_notify") ?: "true") == "true"
        set(value) = set("a_notify", value.toString())
    var notifyTip
        get() = (get("notify_tip") ?: "true") == "true"
        set(value) = set("notify_tip", value.toString())
    var bindPhoneNotifyTip
        get() = (get("bind_phone_notify_tip") ?: "true") == "true"
        set(value) = set("bind_phone_notify_tip", value.toString())
}

val Boolean.toggle get() = !this

object SpUser {
    const val PERSON_TOKEN = "token"
    const val PERSON_ID = "person_id"
    const val PERMISSION = "permission"
}