package com.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.expensetracker.repository.ExpenseRepository

class ExpenseViewModelFactory(private val expenseRepository: ExpenseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExpenseViewModel(expenseRepository) as T
    }
}