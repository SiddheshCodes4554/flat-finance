package com.flatfinance.app.data.dao

import androidx.room.*
import com.flatfinance.app.data.models.Budget
import com.flatfinance.app.data.models.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    
    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    fun getBudgetById(budgetId: String): Flow<Budget?>
    
    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getBudgetsByUserId(userId: String): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE userId = :userId AND category = :category")
    fun getBudgetsByUserIdAndCategory(userId: String, category: ExpenseCategory?): Flow<List<Budget>>
    
    @Query("SELECT * FROM budgets WHERE userId = :userId AND startDate <= :date AND (endDate IS NULL OR endDate >= :date)")
    fun getActiveBudgetsByUserId(userId: String, date: Long): Flow<List<Budget>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)
    
    @Update
    suspend fun updateBudget(budget: Budget)
    
    @Delete
    suspend fun deleteBudget(budget: Budget)
}