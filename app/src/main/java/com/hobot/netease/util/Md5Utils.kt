package com.hobot.netease.util

import java.security.MessageDigest

/**
 * Created by 10963 on 2017/4/13.
 */
class Md5Utils {
    companion object {
        /**
         * 将字符串进行MD5加密
         */
        public fun md5Encode(str: String): String {
            var builder: StringBuilder = StringBuilder()
            val digest = MessageDigest.getInstance("MD5")
            var bytes = digest.digest(str.toByteArray())
            bytes.forEach {
                builder.append(Integer.toHexString(it.toInt() and 0xff or 0x100).substring(1, 3))
            }
            return builder.toString()
        }
    }
}