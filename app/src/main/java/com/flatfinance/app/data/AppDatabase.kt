package com.flatfinance.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flatfinance.app.data.dao.*
import com.flatfinance.app.data.models.*
import com.flatfinance.app.utils.Converters

@Database(
    entities = [
        User::class,
        Flat::class,
        Expense::class,
        Reminder::class,
        Budget::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun flatDao(): FlatDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun reminderDao(): ReminderDao
    abstract fun budgetDao(): BudgetDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flat_finance_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}