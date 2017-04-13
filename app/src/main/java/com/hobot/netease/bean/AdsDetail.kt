package com.hobot.netease.bean

import java.io.Serializable

/**
 * Created by 10963 on 2017/4/12.
 */
data class AdsDetail(var res_url:List<String>, var action_params:Action): Serializable{

    data class Action(var link_url:String): Serializable
}