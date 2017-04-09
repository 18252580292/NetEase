package com.hobot.netease.http

import okhttp3.*
import java.io.IOException


class OkhttpUtils {

    companion object {
        private var mClient: OkHttpClient = OkHttpClient()
        fun get(url: String, callback: HttpCallback) {
            var request = Request.Builder().url(url).get().build()
            var call = mClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    callback.onFailure(call, e)
                }

                override fun onResponse(call: Call?, response: Response?) {
                    callback.onResponse(call, response)
                }

            })
        }
    }

    public interface HttpCallback {
        fun onFailure(call: Call?, e: IOException?)

        fun onResponse(call: Call?, response: Response?)
    }

}