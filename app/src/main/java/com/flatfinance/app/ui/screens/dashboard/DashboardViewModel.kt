package com.flatfinance.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.repositories.ExpenseRepository
import com.flatfinance.app.data.repositories.FlatRepository
import com.flatfinance.app.data.repositories.ReminderRepository
import com.flatfinance.app.data.repositories.UserRepository
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "",
    val flatName: String = "",
    val totalMonthlyExpense: Double = 0.0,
    val personalMonthlyExpense: Double = 0.0,
    val sharedMonthlyExpense: Double = 0.0,
    val upcomingBills: List<DashboardViewModel.UpcomingBill> = emptyList(),
    val balances: List<DashboardViewModel.Balance> = emptyList(),
    val recentExpenses: List<DashboardViewModel.RecentExpense> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val flatRepository: FlatRepository,
    private val expenseRepository: ExpenseRepository,
    private val reminderRepository: ReminderRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null
    private var currentFlatId: String? = null
    
    init {
        viewModelScope.launch {
            preferencesManager.currentUserIdFlow.collect { userId ->
                if (userId != null) {
                    currentUserId = userId
                    loadUserData(userId)
                    
                    // Get current flat ID
                    preferencesManager.currentFlatIdFlow.collect { flatId ->
                        if (flatId != null) {
                            currentFlatId = flatId
                            loadFlatData(flatId)
                            loadExpenseData(userId, flatId)
                            loadUpcomingBills(userId, flatId)
                            loadBalances(flatId)
                        }
                    }
                }
            }
        }
    }
    
    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId).collect { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            userName = it.name
                        )
                    }
                }
            }
        }
    }
    
    private fun loadFlatData(flatId: String) {
        viewModelScope.launch {
            flatRepository.getFlatById(flatId).collect { flat ->
                flat?.let {
                    _uiState.update { state ->
                        state.copy(
                            flatName = it.name
                        )
                    }
                }
            }
        }
    }
    
    private fun loadExpenseData(userId: String, flatId: String) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            
            calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
            val startDate = calendar.timeInMillis
            
            calendar.set(currentYear, currentMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val endDate = calendar.timeInMillis
            
            // Load personal expenses
            expenseRepository.getExpensesByUserIdAndDateRange(userId, startDate, endDate).collect { expenses ->
                val personalExpenses = expenses.filter { it.type.name == "PERSONAL" }
                val personalTotal = personalExpenses.sumOf { it.amount }
                
                _uiState.update { state ->
                    state.copy(
                        personalMonthlyExpense = personalTotal
                    )
                }
                
                // Convert to UI model for recent expenses
                val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                val recentExpenses = expenses.sortedByDescending { it.date }.take(5).map { expense ->
                    RecentExpense(
                        id = expense.id,
                        name = expense.name,
                        amount = expense.amount,
                        category = expense.category,
                        date = dateFormat.format(Date(expense.date)),
                        isShared = expense.type.name == "SHARED"
                    )
                }
                
                _uiState.update { state ->
                    state.copy(
                        recentExpenses = recentExpenses
                    )
                }
            }
            
            // Load shared expenses
            expenseRepository.getExpensesByFlatIdAndDateRange(flatId, startDate, endDate).collect { expenses ->
                val sharedTotal = expenses.sumOf { it.amount }
                
                _uiState.update { state ->
                    state.copy(
                        sharedMonthlyExpense = sharedTotal,
                        totalMonthlyExpense = state.personalMonthlyExpense + sharedTotal
                    )
                }
            }
        }
    }
    
    private fun loadUpcomingBills(userId: String, flatId: String) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val currentDate = calendar.timeInMillis
            
            calendar.add(Calendar.DAY_OF_MONTH, 30) // Next 30 days
            val endDate = calendar.timeInMillis
            
            reminderRepository.getUpcomingRemindersByUserId(userId, endDate).collect { reminders ->
                val upcomingBills = reminders.map { reminder ->
                    val daysLeft = TimeUnit.MILLISECONDS.toDays(reminder.dueDate - currentDate).toInt()
                    
                    UpcomingBill(
                        id = reminder.id,
                        title = reminder.title,
                        amount = reminder.amount,
                        dueDate = reminder.dueDate,
                        daysLeft = daysLeft,
                        category = reminder.type.toExpenseCategory()
                    )
                }.sortedBy { it.daysLeft }
                
                _uiState.update { state ->
                    state.copy(
                        upcomingBills = upcomingBills
                    )
                }
            }
        }
    }
    
    private fun loadBalances(flatId: String) {
        viewModelScope.launch {
            // This would typically be calculated based on shared expenses and payments
            // For now, we'll use mock data
            val mockBalances = listOf(
                Balance(
                    userId = "user1",
                    userName = "Alice",
                    amount = 25.50
                ),
                Balance(
                    userId = "user2",
                    userName = "Bob",
                    amount = -15.75
                ),
                Balance(
                    userId = "user3",
                    userName = "Charlie",
                    amount = 10.25
                )
            )
            
            _uiState.update { state ->
                state.copy(
                    balances = mockBalances
                )
            }
        }
    }
    
    data class UpcomingBill(
        val id: String,
        val title: String,
        val amount: Double,
        val dueDate: Long,
        val daysLeft: Int,
        val category: ExpenseCategory
    )
    
    data class Balance(
        val userId: String,
        val userName: String,
        val amount: Double // Positive means they owe you, negative means you owe them
    )
    
    data class RecentExpense(
        val id: String,
        val name: String,
        val amount: Double,
        val category: ExpenseCategory,
        val date: String,
        val isShared: Boolean
    )
    
    // Helper extension function to convert ReminderType to ExpenseCategory
    private fun com.flatfinance.app.data.models.ReminderType.toExpenseCategory(): ExpenseCategory {
        return when (this) {
            com.flatfinance.app.data.models.ReminderType.RENT -> ExpenseCategory.RENT
            com.flatfinance.app.data.models.ReminderType.ELECTRICITY -> ExpenseCategory.ELECTRICITY
            com.flatfinance.app.data.models.ReminderType.WIFI -> ExpenseCategory.WIFI
            com.flatfinance.app.data.models.ReminderType.OTHER -> ExpenseCategory.OTHER
        }
    }
}