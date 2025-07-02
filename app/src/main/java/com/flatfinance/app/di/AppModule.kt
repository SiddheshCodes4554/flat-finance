package com.flatfinance.app.di

import android.content.Context
import com.flatfinance.app.data.AppDatabase
import com.flatfinance.app.data.dao.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideFlatDao(database: AppDatabase): FlatDao {
        return database.flatDao()
    }
    
    @Provides
    @Singleton
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }
    
    @Provides
    @Singleton
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }
    
    @Provides
    @Singleton
    fun provideBudgetDao(database: AppDatabase): BudgetDao {
        return database.budgetDao()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
}