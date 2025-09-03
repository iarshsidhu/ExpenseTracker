package com.expensetracker.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.expensetracker.db.CurrencyAndThemeMode
import com.expensetracker.db.Expense
import com.expensetracker.db.ExpenseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.ZoneId

class ExpenseRepository(private val expenseDatabase: ExpenseDatabase) {

    suspend fun insertExpense(expense: Expense) {
        expenseDatabase.expenseDao().insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDatabase.expenseDao().deleteExpense(expense)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getExpensesByDate(date: LocalDate): Flow<List<Expense>> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay =
            date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return expenseDatabase.expenseDao().getExpensesByDate(startOfDay, endOfDay)
    }

    suspend fun insertDefaultCurrencyAndTheme() {
        val existing = expenseDatabase.expenseDao().getCurrencyAndThemeModeOnce()
        if (existing == null) {
            val defaultRow = CurrencyAndThemeMode(
                currency = "₹ - Indian Rupee",
                themeModeStatus = 0 // 0 = light, 1 = dark
            )
            expenseDatabase.expenseDao().insertCurrencyAndThemeMode(defaultRow)
        }
    }

    fun getCurrencyAndThemeMode(): Flow<CurrencyAndThemeMode> =
        expenseDatabase.expenseDao().getCurrencyAndThemeMode()

    suspend fun updateCurrency(currency: String) {
        expenseDatabase.expenseDao().updateCurrency(currency)
    }

    suspend fun updateThemeModeStatus(status: Int) {
        expenseDatabase.expenseDao().updateThemeModeStatus(status)
    }
}