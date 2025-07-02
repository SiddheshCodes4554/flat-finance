package com.flatfinance.app.data.repositories

import com.flatfinance.app.data.local.dao.ExpenseDao
import com.flatfinance.app.data.models.Expense
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.models.SplitMethod
import com.flatfinance.app.data.remote.FirebaseExpenseService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class ExpenseRepositoryTest {
    
    private lateinit var expenseDao: ExpenseDao
    private lateinit var firebaseExpenseService: FirebaseExpenseService
    private lateinit var expenseRepository: ExpenseRepository
    
    @Before
    fun setup() {
        expenseDao = mockk()
        firebaseExpenseService = mockk()
        expenseRepository = ExpenseRepository(expenseDao, firebaseExpenseService)
    }
    
    @Test
    fun `getExpensesByUserId returns expenses for user`() = runBlocking {
        // Given
        val userId = "user123"
        val expenses = listOf(
            createExpense(userId = userId),
            createExpense(userId = userId)
        )
        
        coEvery { expenseDao.getExpensesByUserId(userId) } returns flowOf(expenses)
        
        // When
        val result = expenseRepository.getExpensesByUserId(userId).first()
        
        // Then
        assertEquals(expenses, result)
        coVerify { expenseDao.getExpensesByUserId(userId) }
    }
    
    @Test
    fun `getExpensesByFlatId returns expenses for flat`() = runBlocking {
        // Given
        val flatId = "flat123"
        val expenses = listOf(
            createExpense(flatId = flatId),
            createExpense(flatId = flatId)
        )
        
        coEvery { expenseDao.getExpensesByFlatId(flatId) } returns flowOf(expenses)
        
        // When
        val result = expenseRepository.getExpensesByFlatId(flatId).first()
        
        // Then
        assertEquals(expenses, result)
        coVerify { expenseDao.getExpensesByFlatId(flatId) }
    }
    
    @Test
    fun `getExpensesByUserIdAndDateRange returns expenses in date range`() = runBlocking {
        // Given
        val userId = "user123"
        val startDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 // 7 days ago
        val endDate = System.currentTimeMillis()
        
        val expenses = listOf(
            createExpense(userId = userId, date = startDate + 1000),
            createExpense(userId = userId, date = endDate - 1000)
        )
        
        coEvery { 
            expenseDao.getExpensesByUserIdAndDateRange(userId, startDate, endDate) 
        } returns flowOf(expenses)
        
        // When
        val result = expenseRepository.getExpensesByUserIdAndDateRange(userId, startDate, endDate).first()
        
        // Then
        assertEquals(expenses, result)
        coVerify { expenseDao.getExpensesByUserIdAndDateRange(userId, startDate, endDate) }
    }
    
    @Test
    fun `addExpense inserts expense locally and remotely`() = runBlocking {
        // Given
        val expense = createExpense()
        
        coEvery { expenseDao.insertExpense(expense) } returns Unit
        coEvery { firebaseExpenseService.addExpense(expense) } returns Unit
        
        // When
        expenseRepository.addExpense(expense)
        
        // Then
        coVerify { expenseDao.insertExpense(expense) }
        coVerify { firebaseExpenseService.addExpense(expense) }
    }
    
    @Test
    fun `updateExpense updates expense locally and remotely`() = runBlocking {
        // Given
        val expense = createExpense()
        
        coEvery { expenseDao.updateExpense(expense) } returns Unit
        coEvery { firebaseExpenseService.updateExpense(expense) } returns Unit
        
        // When
        expenseRepository.updateExpense(expense)
        
        // Then
        coVerify { expenseDao.updateExpense(expense) }
        coVerify { firebaseExpenseService.updateExpense(expense) }
    }
    
    @Test
    fun `deleteExpense deletes expense locally and remotely`() = runBlocking {
        // Given
        val expense = createExpense()
        
        coEvery { expenseDao.deleteExpense(expense) } returns Unit
        coEvery { firebaseExpenseService.deleteExpense(expense.id) } returns Unit
        
        // When
        expenseRepository.deleteExpense(expense)
        
        // Then
        coVerify { expenseDao.deleteExpense(expense) }
        coVerify { firebaseExpenseService.deleteExpense(expense.id) }
    }
    
    private fun createExpense(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test Expense",
        amount: Double = 100.0,
        category: ExpenseCategory = ExpenseCategory.GROCERIES,
        date: Long = System.currentTimeMillis(),
        userId: String = "user123",
        flatId: String? = null,
        splitMethod: SplitMethod = SplitMethod.EQUAL,
        splitDetails: Map<String, Double> = emptyMap()
    ): Expense {
        return Expense(
            id = id,
            name = name,
            amount = amount,
            category = category,
            date = date,
            userId = userId,
            flatId = flatId,
            splitMethod = splitMethod,
            splitDetails = splitDetails
        )
    }
}