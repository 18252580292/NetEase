package com.hobot.netease.bean

import java.io.Serializable

/**
 * Created by 10963 on 2017/4/12.
 */
data class Ads(var ads: List<AdsDetail>, var result: String, var next_req: Int) : Serializable