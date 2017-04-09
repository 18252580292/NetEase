package com.hobot.netease

import android.app.Application
import android.content.Context

/**
 * 应用 一个项目只有一个，要在AndroidManifest.xml文件中配置
 */
class NetEaseApplication : Application() {

    companion object {
        private lateinit var sContext: Context
        //get global context
        fun getContext(): Context {
            return sContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        sContext = applicationContext
    }
}