package com.example.expensetrackerdemo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensetrackerdemo.R
import com.example.expensetrackerdemo.api.ApiService
import com.example.expensetrackerdemo.api.PreferenceManager
import com.example.expensetrackerdemo.api.RetrofitClient
import com.example.expensetrackerdemo.databinding.ActivityLoginBinding
import com.example.expensetrackerdemo.model.UserRequest
import kotlinx.coroutines.launch

class login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var pref: PreferenceManager

    private lateinit var navtosignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = PreferenceManager(this)

        binding.btnLogin.setOnClickListener { loginUser() }

        navtosignup = findViewById(R.id.navtosignup)
        navtosignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }


    private fun loginUser() {

        // 🚨 Internet Check
        if (!isInternetAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
            return
        }

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.tilEmail.error = "Enter email"
            return
        } else binding.tilEmail.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "Enter password"
            return
        } else binding.tilPassword.error = null

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.getInstance(this@login).create(ApiService::class.java)
                val res = api.login(UserRequest(email, password))

                if (res.isSuccessful && res.body() != null) {

                    val token = res.body()!!.token
                    pref.saveToken(token)
                    RetrofitClient.reset()

                    Toast.makeText(this@login, "Login successful", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@login, MainActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(
                        this@login,
                        "Login failed: ${res.errorBody()?.string()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@login, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
    }

}
