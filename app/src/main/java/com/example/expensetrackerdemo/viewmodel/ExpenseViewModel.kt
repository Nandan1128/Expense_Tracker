package com.example.expensetrackerdemo.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.expensetrackerdemo.model.Expense
import com.example.expensetrackerdemo.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application)  {

    private val repo = ExpenseRepository(application)

    private val _expenses = MutableLiveData<List<Expense>>(emptyList())
    val expenses: LiveData<List<Expense>> = _expenses

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchExpenses(category: String? = null, from: String? = null, to: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getExpenses(category, from, to)
                if (res.isSuccessful) {
                    _expenses.value = res.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed: ${res.code()} ${res.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addExpense(expense: Expense, onComplete: (Boolean)->Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.addExpense(expense)
                onComplete(res.isSuccessful)
                if (res.isSuccessful) fetchExpenses()
                else _error.value = "Add failed: ${res.code()}"
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                onComplete(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateExpense(id: String, expense: Expense, onComplete: (Boolean)->Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.updateExpense(id, expense)
                onComplete(res.isSuccessful)
                if (res.isSuccessful) fetchExpenses()
                else _error.value = "Update failed: ${res.code()}"
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                onComplete(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            try {
                val res = repo.deleteExpense(id)
                if (res.isSuccessful) fetchExpenses()
                else _error.value = "Delete failed"
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }
}
