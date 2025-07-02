package com.flatfinance.app.ui.screens.reports

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.flatfinance.app.data.models.Expense
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.models.SplitMethod
import com.flatfinance.app.data.repositories.ExpenseRepository
import com.flatfinance.app.utils.PDFGenerator
import com.flatfinance.app.utils.PreferencesManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class ReportsViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = TestCoroutineDispatcher()
    
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var pdfGenerator: PDFGenerator
    private lateinit var context: Context
    private lateinit var viewModel: ReportsViewModel
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        expenseRepository = mockk()
        preferencesManager = mockk()
        pdfGenerator = mockk()
        context = mockk()
        
        // Setup preferences manager
        coEvery { preferencesManager.currentUserIdFlow } returns MutableStateFlow("user123")
        coEvery { preferencesManager.currentFlatIdFlow } returns MutableStateFlow("flat123")
        
        viewModel = ReportsViewModel(expenseRepository, preferencesManager, pdfGenerator, context)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
    
    @Test
    fun `selectPeriod updates UI state and loads data`() = testDispatcher.runBlockingTest {
        // Given
        val initialPeriod = viewModel.uiState.value.selectedPeriod
        val newPeriod = if (initialPeriod == ReportPeriod.MONTHLY) ReportPeriod.YEARLY else ReportPeriod.MONTHLY
        
        // Mock repository responses
        setupMockRepositoryResponses()
        
        // When
        viewModel.selectPeriod(newPeriod)
        
        // Then
        assertEquals(newPeriod, viewModel.uiState.value.selectedPeriod)
    }
    
    @Test
    fun `selectExpenseType updates UI state and loads data`() = testDispatcher.runBlockingTest {
        // Given
        val initialType = viewModel.uiState.value.selectedExpenseType
        val newType = when (initialType) {
            ExpenseType.ALL -> ExpenseType.PERSONAL
            ExpenseType.PERSONAL -> ExpenseType.SHARED
            ExpenseType.SHARED -> ExpenseType.ALL
        }
        
        // Mock repository responses
        setupMockRepositoryResponses()
        
        // When
        viewModel.selectExpenseType(newType)
        
        // Then
        assertEquals(newType, viewModel.uiState.value.selectedExpenseType)
    }
    
    @Test
    fun `navigateToPreviousPeriod updates period and loads data`() = testDispatcher.runBlockingTest {
        // Given
        val initialPeriodTitle = viewModel.uiState.value.periodTitle
        
        // Mock repository responses
        setupMockRepositoryResponses()
        
        // When
        viewModel.navigateToPreviousPeriod()
        
        // Then
        val newPeriodTitle = viewModel.uiState.value.periodTitle
        assert(initialPeriodTitle != newPeriodTitle)
    }
    
    @Test
    fun `navigateToNextPeriod updates period and loads data`() = testDispatcher.runBlockingTest {
        // Given
        val initialPeriodTitle = viewModel.uiState.value.periodTitle
        
        // Mock repository responses
        setupMockRepositoryResponses()
        
        // When
        viewModel.navigateToNextPeriod()
        
        // Then
        val newPeriodTitle = viewModel.uiState.value.periodTitle
        assert(initialPeriodTitle != newPeriodTitle)
    }
    
    private fun setupMockRepositoryResponses() {
        // Create sample expenses
        val expenses = listOf(
            createExpense(
                name = "Groceries",
                amount = 50.0,
                category = ExpenseCategory.GROCERIES,
                date = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000 // 2 days ago
            ),
            createExpense(
                name = "Rent",
                amount = 800.0,
                category = ExpenseCategory.RENT,
                date = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000 // 5 days ago
            ),
            createExpense(
                name = "Electricity",
                amount = 75.0,
                category = ExpenseCategory.ELECTRICITY,
                date = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000 // 10 days ago
            )
        )
        
        // Mock repository responses for any date range
        coEvery { 
            expenseRepository.getExpensesByUserIdAndDateRange(any(), any(), any()) 
        } returns flowOf(expenses)
        
        coEvery { 
            expenseRepository.getExpensesByFlatIdAndDateRange(any(), any(), any()) 
        } returns flowOf(expenses)
    }
    
    private fun createExpense(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test Expense",
        amount: Double = 100.0,
        category: ExpenseCategory = ExpenseCategory.GROCERIES,
        date: Long = System.currentTimeMillis(),
        userId: String = "user123",
        flatId: String? = "flat123",
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