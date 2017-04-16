package com.hobot.netease.util

import android.util.Log
import java.lang.Exception

/**
 * Created by 10963 on 2017/4/16.
 */
class LogUtils {

    companion object {
        val TAG = LogUtils::class.java.simpleName
        fun e(msg: String, exception: Exception, tag:String = TAG) {
            Log.e(tag, msg, exception)
        }
        fun d(msg: String, tag:String = TAG) {
            Log.d(tag, msg)
        }
    }
}