package com.expensetracker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenseTable WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date DESC")
    fun getExpensesByDate(startOfDay: Long, endOfDay: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyAndThemeMode(currencyAndThemeMode: CurrencyAndThemeMode)

    @Query("SELECT * FROM currencyAndThemeModeTable LIMIT 1")
    suspend fun getCurrencyAndThemeModeOnce(): CurrencyAndThemeMode?

    @Query("SELECT * FROM currencyAndThemeModeTable LIMIT 1")
    fun getCurrencyAndThemeMode(): Flow<CurrencyAndThemeMode>

    @Query("UPDATE currencyAndThemeModeTable SET currency=:currency")
    suspend fun updateCurrency(currency: String)

    @Query("UPDATE currencyAndThemeModeTable SET themeModeStatus = :status")
    suspend fun updateThemeModeStatus(status: Int)
}