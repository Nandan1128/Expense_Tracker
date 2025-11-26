package com.example.expensetrackerdemo.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensetrackerdemo.api.PreferenceManager
import kotlin.jvm.java
import kotlin.text.isNullOrEmpty

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceManager(this)
        val token = pref.getToken()
        if (token.isNullOrEmpty()) {
            startActivity(Intent(this, login::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
