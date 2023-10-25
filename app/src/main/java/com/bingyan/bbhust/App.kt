package com.bingyan.bbhust

import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV

class App : Application() {
    companion object {
        lateinit var CONTEXT: Application
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MMKV.initialize(this)
        CONTEXT = this
    }
}