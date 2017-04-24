package com.hobot.netease.util

import android.content.Context

/**
 * Created by 10963 on 2017/4/23.
 */
class SharedPrefUtils {
    companion object {
        private val PREF_TITLE = "splash_cache_json"
        fun putString(context: Context, key: String?, value: String?) {
            val preferences = context.getSharedPreferences(PREF_TITLE, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getString(context: Context, key: String): String {
            val preferences = context.getSharedPreferences(PREF_TITLE, Context.MODE_PRIVATE)
            return preferences.getString(key, "")
        }

        fun putLong(context: Context, key: String?, value: Long) {
            val preferences = context.getSharedPreferences(PREF_TITLE, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putLong(key, value)
            editor.apply()
        }

        fun getLong(context: Context, key: String): Long {
            val preferences = context.getSharedPreferences(PREF_TITLE, Context.MODE_PRIVATE)
            return preferences.getLong(key, 0)
        }
    }
}