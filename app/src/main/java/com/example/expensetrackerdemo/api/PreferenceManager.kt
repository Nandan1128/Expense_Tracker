package com.example.expensetrackerdemo.api

import android.content.Context

class PreferenceManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()
    fun getToken(): String? = prefs.getString("token", null)
    fun clear() = prefs.edit().remove("token").apply()
}
