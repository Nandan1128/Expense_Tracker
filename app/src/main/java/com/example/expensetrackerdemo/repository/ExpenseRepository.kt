package com.example.expensetrackerdemo.repository

import android.content.Context
import com.example.expensetrackerdemo.api.ApiService
import com.example.expensetrackerdemo.api.RetrofitClient
import com.example.expensetrackerdemo.model.Expense
import retrofit2.Response
import kotlin.jvm.java

class ExpenseRepository(private val context: Context) {
    private val api = RetrofitClient.getInstance(context).create(ApiService::class.java)

    suspend fun getExpenses(category: String? = null, from: String? = null, to: String? = null): Response<List<Expense>> {
        return api.getExpenses(category, from, to)
    }

    suspend fun addExpense(expense: Expense): Response<Map<String, String>> {
        return api.addExpense(expense)
    }

    suspend fun updateExpense(id: String, expense: Expense): Response<Map<String, String>> {
        return api.updateExpense(id, expense)
    }

    suspend fun deleteExpense(id: String): Response<Map<String, String>> {
        return api.deleteExpense(id)
    }
}
