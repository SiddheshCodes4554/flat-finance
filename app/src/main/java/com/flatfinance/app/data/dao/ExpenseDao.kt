package com.flatfinance.app.data.dao

import androidx.room.*
import com.flatfinance.app.data.models.Expense
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.models.ExpenseType
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ExpenseDao {
    
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    fun getExpenseById(expenseId: String): Flow<Expense?>
    
    @Query("SELECT * FROM expenses WHERE createdBy = :userId ORDER BY date DESC")
    fun getExpensesByUserId(userId: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE flatId = :flatId ORDER BY date DESC")
    fun getExpensesByFlatId(flatId: String): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE createdBy = :userId AND type = :type ORDER BY date DESC")
    fun getExpensesByUserIdAndType(userId: String, type: ExpenseType): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE flatId = :flatId AND type = :type ORDER BY date DESC")
    fun getExpensesByFlatIdAndType(flatId: String, type: ExpenseType): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE createdBy = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByUserIdAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE flatId = :flatId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByFlatIdAndDateRange(flatId: String, startDate: Long, endDate: Long): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE createdBy = :userId AND category = :category ORDER BY date DESC")
    fun getExpensesByUserIdAndCategory(userId: String, category: ExpenseCategory): Flow<List<Expense>>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE createdBy = :userId AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByUserIdAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM expenses WHERE flatId = :flatId AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByFlatIdAndDateRange(flatId: String, startDate: Long, endDate: Long): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)
    
    @Update
    suspend fun updateExpense(expense: Expense)
    
    @Delete
    suspend fun deleteExpense(expense: Expense)
}