package com.example.expensetrackerdemo.model

data class Expense(
    var id: String? = null,
    val amount: Double = 0.0,
    val description: String = "",
    val date: String = "",       // ISO date string (e.g., "2025-11-25")
    val category: String = "",
    val userId: String? = null
)
