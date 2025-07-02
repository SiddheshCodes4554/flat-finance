package com.flatfinance.app.data.repositories

import com.flatfinance.app.data.dao.BudgetDao
import com.flatfinance.app.data.models.Budget
import com.flatfinance.app.data.models.BudgetPeriod
import com.flatfinance.app.data.models.ExpenseCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val firestore: FirebaseFirestore
) {
    
    private val budgetsCollection = firestore.collection("budgets")
    
    fun getBudgetById(budgetId: String): Flow<Budget?> {
        return budgetDao.getBudgetById(budgetId)
    }
    
    fun getBudgetsByUserId(userId: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsByUserId(userId)
    }
    
    fun getBudgetsByUserIdAndCategory(userId: String, category: ExpenseCategory?): Flow<List<Budget>> {
        return budgetDao.getBudgetsByUserIdAndCategory(userId, category)
    }
    
    fun getActiveBudgetsByUserId(userId: String, date: Long): Flow<List<Budget>> {
        return budgetDao.getActiveBudgetsByUserId(userId, date)
    }
    
    suspend fun createBudget(
        userId: String,
        category: ExpenseCategory?,
        amount: Double,
        period: BudgetPeriod,
        startDate: Long,
        endDate: Long? = null
    ): Budget {
        val budgetId = UUID.randomUUID().toString()
        
        val budget = Budget(
            id = budgetId,
            userId = userId,
            category = category,
            amount = amount,
            period = period,
            startDate = startDate,
            endDate = endDate
        )
        
        budgetDao.insertBudget(budget)
        budgetsCollection.document(budgetId).set(budget).await()
        
        return budget
    }
    
    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
        budgetsCollection.document(budget.id).set(budget).await()
    }
    
    suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
        budgetsCollection.document(budget.id).delete().await()
    }
}