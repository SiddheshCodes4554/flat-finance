package com.flatfinance.app.ui.screens.reports

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.repositories.ExpenseRepository
import com.flatfinance.app.utils.PDFGenerator
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class ReportPeriod {
    MONTHLY, YEARLY
}

enum class ExpenseType {
    ALL, PERSONAL, SHARED
}

data class ReportsUiState(
    val selectedPeriod: ReportPeriod = ReportPeriod.MONTHLY,
    val selectedExpenseType: ExpenseType = ExpenseType.ALL,
    val periodTitle: String = "",
    val totalAmount: Double = 0.0,
    val previousPeriodAmount: Double = 0.0,
    val categoryData: Map<ExpenseCategory, Double> = emptyMap(),
    val trendData: Map<String, Double> = emptyMap(),
    val topExpenses: List<TopExpense> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pdfUri: Uri? = null
)

data class TopExpense(
    val name: String,
    val amount: Double,
    val category: ExpenseCategory
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val preferencesManager: PreferencesManager,
    private val pdfGenerator: PDFGenerator,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null
    private var currentFlatId: String? = null
    
    private val calendar = Calendar.getInstance()
    private var currentYear = calendar.get(Calendar.YEAR)
    private var currentMonth = calendar.get(Calendar.MONTH)
    
    init {
        viewModelScope.launch {
            preferencesManager.currentUserIdFlow.collect { userId ->
                if (userId != null) {
                    currentUserId = userId
                    
                    preferencesManager.currentFlatIdFlow.collect { flatId ->
                        currentFlatId = flatId
                        loadReportData()
                    }
                }
            }
        }
        
        updatePeriodTitle()
    }
    
    fun selectPeriod(period: ReportPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        updatePeriodTitle()
        loadReportData()
    }
    
    fun selectExpenseType(type: ExpenseType) {
        _uiState.update { it.copy(selectedExpenseType = type) }
        loadReportData()
    }
    
    fun navigateToPreviousPeriod() {
        if (_uiState.value.selectedPeriod == ReportPeriod.MONTHLY) {
            if (currentMonth == 0) {
                currentMonth = 11
                currentYear--
            } else {
                currentMonth--
            }
        } else {
            currentYear--
        }
        
        updatePeriodTitle()
        loadReportData()
    }
    
    fun navigateToNextPeriod() {
        if (_uiState.value.selectedPeriod == ReportPeriod.MONTHLY) {
            if (currentMonth == 11) {
                currentMonth = 0
                currentYear++
            } else {
                currentMonth++
            }
        } else {
            currentYear++
        }
        
        updatePeriodTitle()
        loadReportData()
    }
    
    private fun updatePeriodTitle() {
        val title = if (_uiState.value.selectedPeriod == ReportPeriod.MONTHLY) {
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            calendar.set(Calendar.YEAR, currentYear)
            calendar.set(Calendar.MONTH, currentMonth)
            monthFormat.format(calendar.time)
        } else {
            currentYear.toString()
        }
        
        _uiState.update { it.copy(periodTitle = title) }
    }
    
    private fun loadReportData() {
        val userId = currentUserId ?: return
        val flatId = currentFlatId
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                // Calculate date range
                val (startDate, endDate) = getDateRange()
                
                // Load expenses based on selected type
                val expenses = when (_uiState.value.selectedExpenseType) {
                    ExpenseType.ALL -> {
                        val personalExpenses = expenseRepository.getExpensesByUserIdAndDateRange(userId, startDate, endDate).value ?: emptyList()
                        val sharedExpenses = if (flatId != null) {
                            expenseRepository.getExpensesByFlatIdAndDateRange(flatId, startDate, endDate).value ?: emptyList()
                        } else {
                            emptyList()
                        }
                        personalExpenses + sharedExpenses
                    }
                    ExpenseType.PERSONAL -> {
                        expenseRepository.getExpensesByUserIdAndDateRange(userId, startDate, endDate).value ?: emptyList()
                    }
                    ExpenseType.SHARED -> {
                        if (flatId != null) {
                            expenseRepository.getExpensesByFlatIdAndDateRange(flatId, startDate, endDate).value ?: emptyList()
                        } else {
                            emptyList()
                        }
                    }
                }
                
                // Calculate total amount
                val totalAmount = expenses.sumOf { it.amount }
                
                // Calculate previous period amount
                val (prevStartDate, prevEndDate) = getPreviousPeriodDateRange()
                val previousExpenses = when (_uiState.value.selectedExpenseType) {
                    ExpenseType.ALL -> {
                        val personalExpenses = expenseRepository.getExpensesByUserIdAndDateRange(userId, prevStartDate, prevEndDate).value ?: emptyList()
                        val sharedExpenses = if (flatId != null) {
                            expenseRepository.getExpensesByFlatIdAndDateRange(flatId, prevStartDate, prevEndDate).value ?: emptyList()
                        } else {
                            emptyList()
                        }
                        personalExpenses + sharedExpenses
                    }
                    ExpenseType.PERSONAL -> {
                        expenseRepository.getExpensesByUserIdAndDateRange(userId, prevStartDate, prevEndDate).value ?: emptyList()
                    }
                    ExpenseType.SHARED -> {
                        if (flatId != null) {
                            expenseRepository.getExpensesByFlatIdAndDateRange(flatId, prevStartDate, prevEndDate).value ?: emptyList()
                        } else {
                            emptyList()
                        }
                    }
                }
                val previousPeriodAmount = previousExpenses.sumOf { it.amount }
                
                // Calculate category data
                val categoryData = expenses.groupBy { it.category }
                    .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }
                    .toMap()
                
                // Calculate trend data
                val trendData = calculateTrendData(expenses)
                
                // Calculate top expenses
                val topExpenses = expenses.sortedByDescending { it.amount }
                    .take(5)
                    .map { expense ->
                        TopExpense(
                            name = expense.name,
                            amount = expense.amount,
                            category = expense.category
                        )
                    }
                
                _uiState.update {
                    it.copy(
                        totalAmount = totalAmount,
                        previousPeriodAmount = previousPeriodAmount,
                        categoryData = categoryData,
                        trendData = trendData,
                        topExpenses = topExpenses,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load report data"
                    )
                }
            }
        }
    }
    
    private fun getDateRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        
        return if (_uiState.value.selectedPeriod == ReportPeriod.MONTHLY) {
            // Monthly range
            calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
            val startDate = calendar.timeInMillis
            
            calendar.set(currentYear, currentMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val endDate = calendar.timeInMillis
            
            Pair(startDate, endDate)
        } else {
            // Yearly range
            calendar.set(currentYear, Calendar.JANUARY, 1, 0, 0, 0)
            val startDate = calendar.timeInMillis
            
            calendar.set(currentYear, Calendar.DECEMBER, 31, 23, 59, 59)
            val endDate = calendar.timeInMillis
            
            Pair(startDate, endDate)
        }
    }
    
    private fun getPreviousPeriodDateRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        
        return if (_uiState.value.selectedPeriod == ReportPeriod.MONTHLY) {
            // Previous month
            var prevYear = currentYear
            var prevMonth = currentMonth - 1
            
            if (prevMonth < 0) {
                prevMonth = 11
                prevYear--
            }
            
            calendar.set(prevYear, prevMonth, 1, 0, 0, 0)
            val startDate = calendar.timeInMillis
            
            calendar.set(prevYear, prevMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val endDate = calendar.timeInMillis
            
            Pair(startDate, endDate)
        } else {
            // Previous year
            calendar.set(currentYear - 1, Calendar.JANUARY, 1, 0, 0, 0)
            val startDate = calendar.timeInMillis
            
            calendar.set(currentYear - 1, Calendar.DECEMBER, 31, 23, 59, 59)
            val endDate = calendar.timeInMillis
            
            Pair(startDate, endDate)
        }
    }
    
    private fun calculateTrendData(expenses: List<com.flatfinance.app.data.models.Expense>): Map<String, Double> {
        return if (_uiState.value.selectedPeriod == ReportPeriod.MONTHLY) {
            // Daily trend for monthly report
            val daysInMonth = Calendar.getInstance().apply {
                set(currentYear, currentMonth, 1)
            }.getActualMaximum(Calendar.DAY_OF_MONTH)
            
            val dailyExpenses = mutableMapOf<String, Double>()
            
            for (day in 1..daysInMonth) {
                val dayStr = String.format("%02d", day)
                dailyExpenses[dayStr] = 0.0
            }
            
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            
            expenses.forEach { expense ->
                val date = Date(expense.date)
                val day = dateFormat.format(date)
                dailyExpenses[day] = (dailyExpenses[day] ?: 0.0) + expense.amount
            }
            
            dailyExpenses
        } else {
            // Monthly trend for yearly report
            val monthlyExpenses = mutableMapOf<String, Double>()
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
            
            for (month in 0..11) {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, month)
                val monthStr = monthFormat.format(calendar.time)
                monthlyExpenses[monthStr] = 0.0
            }
            
            expenses.forEach { expense ->
                val date = Date(expense.date)
                val calendar = Calendar.getInstance()
                calendar.time = date
                val month = calendar.get(Calendar.MONTH)
                val monthStr = monthFormat.format(calendar.time)
                monthlyExpenses[monthStr] = (monthlyExpenses[monthStr] ?: 0.0) + expense.amount
            }
            
            monthlyExpenses
        }
    }
    
    fun exportReportAsPdf() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val reportTitle = "Expense Report - ${_uiState.value.periodTitle}"
                val file = pdfGenerator.generateExpenseReport(
                    title = reportTitle,
                    totalAmount = _uiState.value.totalAmount,
                    categoryData = _uiState.value.categoryData,
                    trendData = _uiState.value.trendData,
                    topExpenses = _uiState.value.topExpenses
                )
                
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pdfUri = uri
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to export PDF"
                    )
                }
            }
        }
    }
    
    fun shareReport() {
        // This would be implemented to share the report via Android's share functionality
        // For now, we'll just export the PDF and then share it
        if (_uiState.value.pdfUri == null) {
            exportReportAsPdf()
        }
    }
}