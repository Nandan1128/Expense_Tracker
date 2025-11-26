package com.example.expensetrackerdemo.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val pref = PreferenceManager(context)
            val client = OkHttpClient.Builder()
                .addInterceptor(TokenInterceptor(pref))
                .addInterceptor(logging)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.102:5000/") // <- replace with your backend IP
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun reset(){
        retrofit = null
    }
}