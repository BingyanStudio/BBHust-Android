package com.bingyan.bbhust.ui.widget.component.music

import android.util.LruCache
import com.tencent.mmkv.MMKV


private const val MAX_CACHE_SIZE = 32

object CacheManager {
    private const val PREF_FILE_NAME = "music_cache"
    private const val CACHE_KEY_PREFIX = "cache"
    private val mPref: MMKV = MMKV.mmkvWithID(PREF_FILE_NAME)
    private val mCache: LruCache<String, MusicCache> = LruCache(MAX_CACHE_SIZE)

    init {
        loadCache()
    }

    fun put(type: String, id: String, value: MusicCache) {
        val key = getKey(type, id)
        mCache.put(key, value)
        saveCache(type, id, value)
    }

    operator fun get(type: String, id: String): MusicCache? {
        return mCache.get(getKey(type, id))
    }

    fun getOr(type: String, id: String, onMiss: () -> MusicCache): MusicCache {
        val cache = mCache.get(getKey(type, id))
        if (cache == null) {
            val newData = onMiss()
            put(type, id, newData)
            return newData
        }
        return cache
    }

    private fun loadCache() {
        mPref.allKeys()?.forEach { key ->
            if (key.startsWith(CACHE_KEY_PREFIX)) {
                val value = mPref.getString(key, "")
                val v: MusicCache = MusicCache.parse(value.toString())
                mCache.put(key, v)
            }
        }
    }

    private fun saveCache(type: String, id: String, value: MusicCache) {
        val editor = mPref.edit()
        editor.putString(getKey(type, id), value.toString())
        editor.apply()
    }

    private fun getKey(type: String, id: String) = "$CACHE_KEY_PREFIX@${type}#$id"

}

data class MusicCache(
    val cover: String?,
    val title: String?,
) {
    override fun toString(): String {
        return "$title\n$cover"
    }

    companion object {
        fun parse(data: String): MusicCache {
            val v = data.split("\n")
            return MusicCache(
                title = v.getOrNull(0),
                cover = v.getOrNull(1),
            )
        }
    }
}