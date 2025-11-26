package com.example.expensetrackerdemo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensetrackerdemo.R
import com.example.expensetrackerdemo.api.ApiService
import com.example.expensetrackerdemo.api.RetrofitClient
import com.example.expensetrackerdemo.api.PreferenceManager
import com.example.expensetrackerdemo.databinding.ActivitySignupBinding
import com.example.expensetrackerdemo.model.UserRequest
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.text.trim
import kotlin.toString

class Signup : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var pref: PreferenceManager

    private lateinit var navtologin : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = PreferenceManager(this)

        binding.btnSignup.setOnClickListener { signupUser() }

        navtologin = findViewById(R.id.navtologin)
        navtologin.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
    }

    private fun signupUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email"
            return
        } else binding.tilEmail.error = null

        if (password.length < 6) {
            binding.tilPassword.error = "At least 6 characters"
            return
        } else binding.tilPassword.error = null

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(this@Signup
                ).create(ApiService::class.java)
                val res = api.signup(UserRequest(email, password))
                if (res.isSuccessful) {
                    Toast.makeText(this@Signup, "Signup successful. Login now.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Signup, login::class.java))
                    finish()
                } else {
                    Toast.makeText(this@Signup, "Signup failed: ${res.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Signup, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
