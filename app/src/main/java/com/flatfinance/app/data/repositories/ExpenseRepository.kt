package com.flatfinance.app.data.repositories

import com.flatfinance.app.data.dao.ExpenseDao
import com.flatfinance.app.data.models.Expense
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.models.ExpenseType
import com.flatfinance.app.data.models.SplitMethod
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestore: FirebaseFirestore
) {
    
    private val expensesCollection = firestore.collection("expenses")
    
    fun getExpenseById(expenseId: String): Flow<Expense?> {
        return expenseDao.getExpenseById(expenseId)
    }
    
    fun getExpensesByUserId(userId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByUserId(userId)
    }
    
    fun getExpensesByFlatId(flatId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByFlatId(flatId)
    }
    
    fun getExpensesByUserIdAndType(userId: String, type: ExpenseType): Flow<List<Expense>> {
        return expenseDao.getExpensesByUserIdAndType(userId, type)
    }
    
    fun getExpensesByFlatIdAndType(flatId: String, type: ExpenseType): Flow<List<Expense>> {
        return expenseDao.getExpensesByFlatIdAndType(flatId, type)
    }
    
    fun getExpensesByUserIdAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByUserIdAndDateRange(userId, startDate, endDate)
    }
    
    fun getExpensesByFlatIdAndDateRange(flatId: String, startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByFlatIdAndDateRange(flatId, startDate, endDate)
    }
    
    fun getExpensesByUserIdAndCategory(userId: String, category: ExpenseCategory): Flow<List<Expense>> {
        return expenseDao.getExpensesByUserIdAndCategory(userId, category)
    }
    
    fun getTotalExpensesByUserIdAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<Double?> {
        return expenseDao.getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate)
    }
    
    fun getTotalExpensesByFlatIdAndDateRange(flatId: String, startDate: Long, endDate: Long): Flow<Double?> {
        return expenseDao.getTotalExpensesByFlatIdAndDateRange(flatId, startDate, endDate)
    }
    
    suspend fun createPersonalExpense(
        name: String,
        amount: Double,
        category: ExpenseCategory,
        date: Long,
        userId: String
    ): Expense {
        val expenseId = UUID.randomUUID().toString()
        
        val expense = Expense(
            id = expenseId,
            name = name,
            amount = amount,
            category = category,
            type = ExpenseType.PERSONAL,
            date = date,
            createdBy = userId
        )
        
        expenseDao.insertExpense(expense)
        expensesCollection.document(expenseId).set(expense).await()
        
        return expense
    }
    
    suspend fun createSharedExpense(
        name: String,
        amount: Double,
        category: ExpenseCategory,
        date: Long,
        userId: String,
        flatId: String,
        splitMethod: SplitMethod,
        splits: Map<String, Double>
    ): Expense {
        val expenseId = UUID.randomUUID().toString()
        
        val expense = Expense(
            id = expenseId,
            name = name,
            amount = amount,
            category = category,
            type = ExpenseType.SHARED,
            date = date,
            createdBy = userId,
            flatId = flatId,
            splitMethod = splitMethod,
            splits = splits
        )
        
        expenseDao.insertExpense(expense)
        expensesCollection.document(expenseId).set(expense).await()
        
        return expense
    }
    
    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
        expensesCollection.document(expense.id).set(expense).await()
    }
    
    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
        expensesCollection.document(expense.id).delete().await()
    }
}