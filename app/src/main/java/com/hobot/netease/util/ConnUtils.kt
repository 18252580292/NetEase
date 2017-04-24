package com.hobot.netease.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by 10963 on 2017/4/23.
 */
class ConnUtils {
    companion object {

        /**
         * 判断是否有网络连接
         */
        fun isConnNet(context: Context?): Boolean? {
            val cm: ConnectivityManager? = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val info: NetworkInfo? = cm?.activeNetworkInfo
            return info?.isAvailable
        }
    }
}