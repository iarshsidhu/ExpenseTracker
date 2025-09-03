package com.expensetracker.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.db.Expense
import com.expensetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(private val expenseRepository: ExpenseRepository) :
    ViewModel() {

    private val _filteredExpensesStateFlow = MutableStateFlow<List<Expense>>(emptyList())
    val filteredExpensesStateFlow: StateFlow<List<Expense>> = _filteredExpensesStateFlow

    private val _totalAmountStateFlow = MutableStateFlow(0.0)
    val totalAmountStateFlow: StateFlow<Double> = _totalAmountStateFlow

    private val _currencyStateFlow = MutableStateFlow("")
    val currencyStateFlow: StateFlow<String> = _currencyStateFlow

    private val _themeModeStateFlow = MutableStateFlow(0)
    val themeModeStateFlow: StateFlow<Int> = _themeModeStateFlow

    private var loadExpensesJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertDefaultCurrencyAndTheme()
        }
        getCurrencyAndThemeMode()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val today = LocalDate.now()
            loadExpensesForDate(today)
        } else {
            Log.e("ExpenseViewModel", "LocalDate requires API 26 or higher")
        }
    }

    fun insertExpense(title: String, amount: Double, date: Long) {
        val expense = Expense(
            title = title,
            amount = amount,
            date = date
        )
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadExpensesForDate(date: LocalDate) {
        loadExpensesJob?.cancel()
        loadExpensesJob = viewModelScope.launch {
            expenseRepository.getExpensesByDate(date).collect { expenses ->
                Log.d("ExpenseViewModel", "$expenses")
                _filteredExpensesStateFlow.value = expenses
                _totalAmountStateFlow.value = expenses.sumOf { it.amount }
            }
        }
    }

    fun getCurrencyAndThemeMode() {
        viewModelScope.launch {
            expenseRepository.getCurrencyAndThemeMode().collect { currencyAndThemeMode ->
                _currencyStateFlow.value = currencyAndThemeMode.currency
                _themeModeStateFlow.value = currencyAndThemeMode.themeModeStatus
            }
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            expenseRepository.updateCurrency(currency)
        }
    }

    fun updateThemeModeStatus(status: Int) {
        viewModelScope.launch {
            expenseRepository.updateThemeModeStatus(status)
        }
    }
}