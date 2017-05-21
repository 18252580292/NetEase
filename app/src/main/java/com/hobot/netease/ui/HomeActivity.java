package com.hobot.netease.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hobot.netease.ImageCacheService;
import com.hobot.netease.R;
import com.hobot.netease.bean.Ads;
import com.hobot.netease.constant.HttpConstants;
import com.hobot.netease.http.OkhttpUtils;
import com.hobot.netease.util.FileUtils;
import com.hobot.netease.util.LogUtils;
import com.hobot.netease.util.SharedPrefUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by cui on 17-5-7.
 */

public class HomeActivity extends Activity implements View.OnClickListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int MSG_LOAD_IMG = 0x00;
    private static final int MSG_ENTER_HOME = 0x01;
    private static final int MSG_NO_CACHE_IMG = 0x02;
    private static final int MSG_START_SERVICE_CACHE_IMG = 0x03;
    private static final int MSG_REQUEST_SERVER = 0x04;
    private static final int MSG_FAILED = 0x05;
    private final String SPLASH_JSON_CACHE = "json_cache";
    private final String SPLASH_NEXT_REQ = "splash_next_req";
    private MyHandler mHandler;
    private ImageView mBgImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBgImg = (ImageView) findViewById(R.id.img_bg);
        handlerMsg();
        isReCacheImg();
        mBgImg.setOnClickListener(this);
    }

    private void handlerMsg() {
        mHandler = new MyHandler(this);
    }

    /**
     * 判断是否需要重新刷新缓存
     */
    private void isReCacheImg() {
        LogUtils.Companion.d("judge the cache is expire !!!", TAG);
        long nextReqTime = SharedPrefUtils.Companion.getLong(HomeActivity.this, SPLASH_NEXT_REQ);
        if (nextReqTime - SystemClock.currentThreadTimeMillis() > 6000 * 60 * 1000) {
            LogUtils.Companion.d("the cache is already expire !!!", TAG);
            mHandler.sendEmptyMessage(MSG_REQUEST_SERVER);
        } else {
            LogUtils.Companion.d("the cache isn't expire !!!", TAG);
            mHandler.sendEmptyMessage(MSG_LOAD_IMG);
        }
    }

    /**
     * load splash background
     */
    private void loadImgBg() {
        File imgFiles = new File(FileUtils.Companion.getIMG_CACHE_DIR());
        if (imgFiles.isFile()) {
            throw new IllegalArgumentException();
        }
        imgFiles.mkdirs();
        File[] files = imgFiles.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("jpg");
            }
        });
        if (files.length == 0) {
            LogUtils.Companion.d("no have img", TAG);
            mHandler.sendEmptyMessage(MSG_NO_CACHE_IMG);
            return;
        }
        //random load img
        Random random = new Random();
        int tempIndex = random.nextInt(files.length);
        mBgImg.setImageBitmap(BitmapFactory.decodeFile(files[tempIndex].getAbsolutePath()));

        //enter home interface
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(MSG_ENTER_HOME, 3000);
    }

    /**
     * cache splash background img
     */
    private void cacheImg() {
        LogUtils.Companion.d("start cache img", TAG);
        String cacheJson = SharedPrefUtils.Companion.getString(HomeActivity.this, SPLASH_JSON_CACHE);
        if (!TextUtils.isEmpty(cacheJson)) {
            LogUtils.Companion.d(cacheJson, TAG);
            Gson gson = new Gson();
            String result = cacheJson;
            Ads ads = gson.fromJson(result, Ads.class);
            Message msg = Message.obtain();
            msg.obj = ads;
            msg.what = MSG_START_SERVICE_CACHE_IMG;
            mHandler.sendMessage(msg);
        } else {
            mHandler.sendEmptyMessage(MSG_REQUEST_SERVER);
//            requestData()
        }
    }

    /**
     * 联网更新数据
     */
    private void requestData() {
        OkhttpUtils.Companion.get(HttpConstants.Companion.getBASE_URL(), new OkhttpUtils.HttpCallback() {
            public void onFailure(Call call, IOException e) {
                LogUtils.Companion.e("failed", e, TAG);

                mHandler.sendEmptyMessageDelayed(MSG_FAILED, 3000);
            }

            public void onResponse(Call call, Response response) {
                try {
                    LogUtils.Companion.d("request data success", TAG);
                    Gson gson = new Gson();
                    String result = response.body().string();
                    Ads ads = gson.fromJson(result, Ads.class);
                    Message msg = Message.obtain();
                    msg.obj = ads;
                    msg.what = MSG_START_SERVICE_CACHE_IMG;
                    SharedPrefUtils.Companion.putLong(HomeActivity.this, SPLASH_NEXT_REQ,
                            SystemClock.currentThreadTimeMillis());
                    SharedPrefUtils.Companion.putString(HomeActivity.this, SPLASH_JSON_CACHE, result);
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onClick(View view) {

    }

    static class MyHandler extends Handler {
        private WeakReference<Activity> atyRf;
        private Message msg ;
        public MyHandler(Activity activity) {
            atyRf = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeActivity activity = (HomeActivity) atyRf.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_LOAD_IMG:
                        activity.loadImgBg();
                        break;
                    case MSG_NO_CACHE_IMG:
                        activity.cacheImg();
                        break;
                    case MSG_START_SERVICE_CACHE_IMG:
                        Ads ads = (Ads) msg.obj;
                        Intent intent = new Intent(activity, ImageCacheService.class);
                        intent.putExtra("ads", ads);
                        activity.startService(intent);
                        this.sendEmptyMessageDelayed(MSG_LOAD_IMG, 200);
                        break;
                    case MSG_REQUEST_SERVER:
                        activity.requestData();
                        break;
                }
            }
        }
    }
}
