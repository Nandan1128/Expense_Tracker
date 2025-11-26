package com.example.expensetrackerdemo.api

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val pref: PreferenceManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = pref.getToken()
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

