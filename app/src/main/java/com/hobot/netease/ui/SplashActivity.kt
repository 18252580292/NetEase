package com.hobot.netease.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // delete status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        initData()
        setBackground()
    }

    private fun setBackground() {
        var imgFiles = File(FileUtils.IMG_CACHE_DIR)
        if (imgFiles.isFile) {
            throw IllegalArgumentException()
        }
        var files:Array<File> = imgFiles.listFiles { file -> file!!.endsWith("img") }
        if(files.size == 0) {
            LogUtils.d("no have img")
            return
        }
        files.forEach {
            LogUtils.d(it.absolutePath)
        }
        img_bg.setImageBitmap(BitmapFactory.decodeFile(files[0].absolutePath))
    }

    private fun initData() {
        var lastSaveTime = SharedPrefUtils.getLong(this.applicationContext, SPLASH_NEXT_REQ)
        if (SystemClock.currentThreadTimeMillis() - lastSaveTime > 600 * 60 * 1000) {
            requestData()
        } else {
            var cacheJson = SharedPrefUtils.getString(this@SplashActivity, SPLASH_JSON_CACHE)
            if (!TextUtils.isEmpty(cacheJson)) {
                LogUtils.d(cacheJson)
//                parseJsonAndSaveImgByService(cacheJson)
            } else {
                requestData()
            }
        }

    }

    private fun requestData() {
        OkhttpUtils.get(HttpConstants.BASE_URL, object : OkhttpUtils.HttpCallback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogUtils.e("failed", e as Exception, this@SplashActivity.javaClass.simpleName)
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.e("tag", response.toString())
                parseJsonAndSaveImgByService(response?.body()?.string())
            }

        })
    }

    private fun parseJsonAndSaveImgByService(json: String?) {
        var gson: Gson = Gson()
        var result = json
        val ads: Ads = gson.fromJson<Ads>(result, Ads::class.java)
        when (ads) {
            null -> {
                Log.e(this@SplashActivity.javaClass.simpleName, "format json error!!!")
            }
            else -> {
                //将json字符串保存到本地
                SharedPrefUtils.putLong(this@SplashActivity, SPLASH_NEXT_REQ,
                        SystemClock.currentThreadTimeMillis())
                SharedPrefUtils.putString(this@SplashActivity, SPLASH_JSON_CACHE, result)
                var intent: Intent = Intent(this@SplashActivity, ImageCacheService::class.java)
                intent.putExtra("ads", ads)
                startService(intent)
            }
        }
    }
}