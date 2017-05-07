package com.hobot.netease.wrapper

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Created by cui on 17-5-7.
 */
object HttpManager {
    public interface Callback {
        fun onFailure(call: Call?, e: IOException?)
        fun onResponse(call: Call?, response: Response?)
    }

    private val client: OkHttpClient

    init {
        client = OkHttpClient()
    }

    public fun get(url: String, callback: Callback) {
        var request = Request.Builder().url(url).get().build()
        val call = client.newCall(request)
        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.onFailure(call, e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                callback.onResponse(call, response)
            }

        })
    }
}