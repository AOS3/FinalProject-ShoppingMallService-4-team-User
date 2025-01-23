package com.aladin.finalproject_shoppingmallservice_4_team.ui.login

import android.content.Context
import android.util.Log

class SharedPreferencesHelper(context: Context) {
    private val preferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    fun saveAutoLoginToken(token: String) {
        Log.d("SharedPreferencesHelper", "토큰 저장: $token")
        preferences.edit().putString("autoLoginToken", token).apply()
    }

    fun getAutoLoginToken(): String? {
        val token = preferences.getString("autoLoginToken", null)
        Log.d("SharedPreferencesHelper", "불러온 토큰: $token")
        return token
    }

    fun clearAutoLoginToken() {
        preferences.edit().remove("autoLoginToken").apply()
    }
}