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
import android.widget.Toast
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
import java.util.*


class SplashActivity : Activity() {
    val TAG = SplashActivity::class.java.simpleName!!
    val SPLASH_JSON_CACHE = "json_cache"
    val SPLASH_NEXT_REQ = "splash_next_req"

    val MSG_LOAD_IMG = 0x00
    val MSG_ENTER_HOME = 0x01
    val MSG_NO_CACHE_IMG = 0X02
    val MSG_START_SERVICE_CACHE_IMG = 0X03
    val MSG_REQUEST_SERVER = 0X04
    val MSG_FAILED = 0x05

    val handler = Handler(Handler.Callback { msg ->
        when (msg?.what) {
            MSG_LOAD_IMG -> loadImgBg()
            MSG_NO_CACHE_IMG -> cacheImg()
            MSG_REQUEST_SERVER -> requestData()
            MSG_ENTER_HOME -> {
                Toast.makeText(this@SplashActivity, "enter home ui", Toast.LENGTH_SHORT)
            }
            MSG_FAILED -> {
                Toast.makeText(this@SplashActivity, "请求失败", Toast.LENGTH_SHORT)
                var tempMsg = Message.obtain(msg)
                tempMsg.what = MSG_ENTER_HOME
                tempMsg.sendToTarget()
            }
            MSG_START_SERVICE_CACHE_IMG -> {
                var ads: Ads = msg.obj as Ads
                var intent: Intent = Intent(this@SplashActivity, ImageCacheService::class.java)
                intent.putExtra("ads", ads)
                startService(intent)
            }
        }
        true
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // delete status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        isReCacheImg()
    }

    /**
     * 判断是否需要重新刷新缓存
     */
    private fun isReCacheImg() {
        LogUtils.d("judge the cache is expire !!!", TAG)
        var nextReqTime = SharedPrefUtils.getLong(this@SplashActivity, SPLASH_NEXT_REQ)
        if (nextReqTime - SystemClock.currentThreadTimeMillis() > 6000 * 60 * 1000) {
            LogUtils.d("the cache is already expire !!!", TAG)
            handler.sendEmptyMessage(MSG_REQUEST_SERVER)
        } else {
            LogUtils.d("the cache isn't expire !!!", TAG)
            handler.sendEmptyMessage(MSG_LOAD_IMG)
        }
    }

    private fun loadImgBg() {
        var imgFiles = File(FileUtils.IMG_CACHE_DIR)
        if (imgFiles.isFile) {
            throw IllegalArgumentException()
        }
        imgFiles.mkdirs()
        var files: Array<File> = imgFiles.listFiles { file -> file.name.endsWith("jpg") }
        if (files.size == 0) {
            LogUtils.d("no have img")
            handler.sendEmptyMessage(MSG_NO_CACHE_IMG)
            return
        }
        //random load img
        var random: Random = Random()
        var tempIndex = random.nextInt(files.size)
        img_bg.setImageBitmap(BitmapFactory.decodeFile(files[tempIndex].absolutePath))

        //enter home interface
        handler.removeCallbacksAndMessages(null)
        handler.sendEmptyMessageDelayed(MSG_ENTER_HOME, 3000)
    }


    /**
     * cache splash background img
     */
    private fun cacheImg() {
        LogUtils.d("start cache img", TAG)
        var cacheJson = SharedPrefUtils.getString(this@SplashActivity, SPLASH_JSON_CACHE)
        if (!TextUtils.isEmpty(cacheJson)) {
            LogUtils.d(cacheJson)
            var gson: Gson = Gson()
            var result = cacheJson
            val ads: Ads? = gson.fromJson<Ads>(result, Ads::class.java)
            var msg: Message? = Message.obtain()
            msg?.obj = ads
            msg?.what = MSG_LOAD_IMG
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
                LogUtils.e("failed", e as Exception, TAG)

                handler.sendEmptyMessageDelayed(MSG_FAILED, 3000)
            }

            override fun onResponse(call: Call?, response: Response?) {
                LogUtils.d("request data success")
                var gson: Gson = Gson()
                var result = response?.body()?.string()
                val ads: Ads = gson.fromJson<Ads>(result, Ads::class.java)
                var msg = Message.obtain()
                msg.obj = ads
                msg.what = MSG_LOAD_IMG
                SharedPrefUtils.putLong(this@SplashActivity, SPLASH_NEXT_REQ,
                        SystemClock.currentThreadTimeMillis())
                SharedPrefUtils.putString(this@SplashActivity, SPLASH_JSON_CACHE, result)
                handler.sendMessage(msg)
            }

        })
    }
}