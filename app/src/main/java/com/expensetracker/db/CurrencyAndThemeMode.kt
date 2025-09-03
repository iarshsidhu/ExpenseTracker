package com.expensetracker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencyAndThemeModeTable")
data class CurrencyAndThemeMode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currency: String,
    val themeModeStatus: Int
)
