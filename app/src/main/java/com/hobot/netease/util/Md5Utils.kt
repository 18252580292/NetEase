package com.hobot.netease.util

import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Created by 10963 on 2017/4/13.
 */
class Md5Utils {
    companion object {
        public fun md5Encode(str: String): String {
            val digest = MessageDigest.getInstance("MD5")
            return String(digest.digest(str.toByteArray(Charset.forName("UTF-8"))))
        }
    }
}