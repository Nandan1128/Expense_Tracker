package com.example.expensetrackerdemo.api

import com.example.expensetrackerdemo.model.ApiResponse
import com.example.expensetrackerdemo.model.LoginResponse
import com.example.expensetrackerdemo.model.UserRequest
import com.example.expensetrackerdemo.model.Expense
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/signup")
    suspend fun signup(@Body body: UserRequest): Response<ApiResponse>

    @POST("auth/login")
    suspend fun login(@Body body: UserRequest): Response<LoginResponse>

    // Expenses (token added automatically by interceptor)
    @GET("expenses")
    suspend fun getExpenses(
        @Query("category") category: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<List<Expense>>

    @POST("expenses")
    suspend fun addExpense(@Body expense: Expense): Response<Map<String, String>>

    @PUT("expenses/{id}")
    suspend fun updateExpense(@Path("id") id: String, @Body expense: Expense): Response<Map<String, String>>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<Map<String, String>>
}
