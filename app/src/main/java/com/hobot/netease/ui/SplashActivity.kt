package com.hobot.netease.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.hobot.netease.R
import com.hobot.netease.constant.HttpConstants
import com.hobot.netease.http.OkhttpUtils
import okhttp3.Call
import okhttp3.Response
import java.io.IOException


class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // delete status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        initData()
    }

    private fun initData() {
        OkhttpUtils.get(HttpConstants.BASE_URL, object : OkhttpUtils.HttpCallback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("tag", "haha,error", e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.e("tag", response.toString())
            }

        })
    }
}