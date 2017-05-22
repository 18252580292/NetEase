package com.hobot.netease.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebSettings
import com.hobot.netease.R
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        var intent = intent
        var detailUrl = intent.getStringExtra("detail_url")
        web_view.settings.javaScriptEnabled = true
        web_view.loadUrl(detailUrl)
    }
}
