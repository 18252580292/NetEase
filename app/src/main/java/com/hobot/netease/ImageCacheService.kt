package com.hobot.netease

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.hobot.netease.bean.Ads
import com.hobot.netease.bean.AdsDetail
import com.hobot.netease.util.FileUtils
import com.hobot.netease.util.LogUtils
import com.hobot.netease.util.Md5Utils
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Created by 10963 on 2017/4/13.
 */
class ImageCacheService : IntentService("image_cache") {

    override fun onHandleIntent(intent: Intent?) {
        var ads: Ads = intent?.getSerializableExtra("ads") as Ads
        var adsDetails: List<AdsDetail> = ads.ads
        adsDetails.forEach {
            var imgName: String = it.res_url[0]
            LogUtils.d(imgName)
            downloadImg(imgName)
        }
    }

    /**
     *  从网络上下载图片
     */
    private fun downloadImg(imgName: String) {
        var url: URL = URL(imgName)
        val openConnection = url.openConnection()
        var bitmap = BitmapFactory.decodeStream(openConnection.getInputStream())
        save2SDcard(imgName, bitmap)
    }

    /**
     * 将图片缓存到本地
     */
    private fun save2SDcard(imgName: String, bitmap: Bitmap?) {
        var resName: String = Md5Utils.md5Encode(imgName)
        var file: File = File(FileUtils.IMG_CACHE_DIR + resName + ".img")
        if (file.exists()) {
            return
        }
        file.parentFile.mkdirs()
        var out:FileOutputStream = FileOutputStream(file)
        LogUtils.d(file.absolutePath)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 60, out)
        out.flush()
        out.close()
    }

}