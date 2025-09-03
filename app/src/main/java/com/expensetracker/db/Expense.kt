package com.expensetracker.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenseTable")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: Long
)
