package com.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.expensetracker.db.ExpenseDao
import com.expensetracker.db.ExpenseDatabase
import com.expensetracker.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExpenseDatabase {
        return Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "ExpenseDB"
        ).build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: ExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(database: ExpenseDatabase): ExpenseRepository {
        return ExpenseRepository(database)
    }
}