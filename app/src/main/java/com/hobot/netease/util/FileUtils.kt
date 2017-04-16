package com.hobot.netease.util

import android.os.Environment
import java.io.Closeable
import java.io.File

/**
 * Created by 10963 on 2017/4/16.
 */
class FileUtils {
    companion object {
        val IMG_CACHE_DIR = Environment.getExternalStorageDirectory().absolutePath + File.separator +
                "netease" + File.separator + "img" + File.separator

        fun closeIO(stream: Closeable?) {
            stream?.close()
        }

        fun closeIOs(vararg streams: Closeable?) {
            if (streams.size >= 0) {
                streams.forEach {
                    closeIO(it)
                }
            }
        }
    }
}