package com.hobot.netease.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.text.TextUtils
import android.view.WindowManager
import com.google.gson.Gson
import com.hobot.netease.ImageCacheService
import com.hobot.netease.R
import com.hobot.netease.bean.Ads
import com.hobot.netease.constant.HttpConstants
import com.hobot.netease.http.OkhttpUtils
import com.hobot.netease.util.FileUtils
import com.hobot.netease.util.LogUtils
import com.hobot.netease.util.SharedPrefUtils
import kotlinx.android.synthetic.main.activity_splash.*
import okhttp3.Call
import okhttp3.Response
import java.io.File
import java.io.IOException


class SplashActivity : Activity() {
    val SPLASH_JSON_CACHE = "json_cache"
    val SPLASH_NEXT_REQ = "splash_next_req"

    val MSG_LOAD_IMG = 0x00
    val MSG_INIT = 0x01
    val MSG_NO_CACHE_IMG = 0X02
    val MSG_START_SERVICE_CACHE_IMG = 0X03
    val MSG_REQUEST_SERVER = 0X04

    val handler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            when (msg?.what) {
                MSG_LOAD_IMG -> loadImgBg()
                MSG_NO_CACHE_IMG -> cacheImg()
                MSG_REQUEST_SERVER -> requestData()
                MSG_START_SERVICE_CACHE_IMG -> {
                    var ads: Ads = msg.obj as Ads
                    var intent: Intent = Intent(this@SplashActivity, ImageCacheService::class.java)
                    intent.putExtra("ads", ads)
                    startService(intent)
                }
            }
            return true
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // delete status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        isReCacheImg()
    }

    private fun isReCacheImg() {
        LogUtils.d("judge the cache is expire !!!", this@SplashActivity.javaClass.simpleName)
        var nextReqTime = SharedPrefUtils.getLong(this@SplashActivity, SPLASH_NEXT_REQ)
        if (nextReqTime - SystemClock.currentThreadTimeMillis() > 6000 * 60 * 1000) {
            LogUtils.d("the cache is already expire !!!", this@SplashActivity.javaClass.simpleName)
            handler.sendEmptyMessage(MSG_REQUEST_SERVER)
        } else {
            LogUtils.d("the cache isn't already expire !!!", this@SplashActivity.javaClass.simpleName)
            handler.sendEmptyMessage(MSG_LOAD_IMG)
        }
    }

    private fun loadImgBg() {
        var imgFiles = File(FileUtils.IMG_CACHE_DIR)
        if (imgFiles.isFile) {
            throw IllegalArgumentException()
        }
        var files: Array<File> = imgFiles.listFiles { file -> file.name.endsWith("jpg") }
        if (files.size == 0) {
            LogUtils.d("no have img")
            handler.sendEmptyMessage(MSG_NO_CACHE_IMG)
            return
        }
        img_bg.setImageBitmap(BitmapFactory.decodeFile(files[0].absolutePath))

        //enter home interface
        handler.removeCallbacksAndMessages(null)
    }


    /**
     * cache splash background img
     */
    private fun cacheImg() {
        var cacheJson = SharedPrefUtils.getString(this@SplashActivity, SPLASH_JSON_CACHE)
        if (!TextUtils.isEmpty(cacheJson)) {
            LogUtils.d(cacheJson)
            var gson: Gson = Gson()
            var result = cacheJson
            val ads: Ads? = gson.fromJson<Ads>(result, Ads::class.java)
            var msg: Message? = Message.obtain()
            msg?.obj = ads
            handler.sendMessage(msg)
        } else {
            handler.sendEmptyMessage(MSG_REQUEST_SERVER)
//            requestData()
        }
    }

    /**
     * 联网更新数据
     */
    private fun requestData() {
        OkhttpUtils.get(HttpConstants.BASE_URL, object : OkhttpUtils.HttpCallback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtils.e("failed", e as Exception, this@SplashActivity.javaClass.simpleName)
            }

            override fun onResponse(call: Call?, response: Response?) {
                var gson: Gson = Gson()
                var result = response?.body()?.string()
                val ads: Ads = gson.fromJson<Ads>(result, Ads::class.java)
                var msg = Message.obtain()
                msg.obj = ads
                SharedPrefUtils.putLong(this@SplashActivity, SPLASH_NEXT_REQ,
                        SystemClock.currentThreadTimeMillis())
                SharedPrefUtils.putString(this@SplashActivity, SPLASH_JSON_CACHE, result)
                handler.sendMessage(msg)
            }

        })
    }
}