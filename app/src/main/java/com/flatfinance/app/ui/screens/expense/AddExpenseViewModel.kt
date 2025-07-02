package com.flatfinance.app.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.models.SplitMethod
import com.flatfinance.app.data.repositories.ExpenseRepository
import com.flatfinance.app.data.repositories.FlatRepository
import com.flatfinance.app.data.repositories.UserRepository
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class AddExpenseUiState(
    val isPersonalExpense: Boolean = true,
    val name: String = "",
    val amount: String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val date: Long = System.currentTimeMillis(),
    val dateText: String = "",
    val splitMethod: SplitMethod = SplitMethod.EQUAL,
    val splits: Map<String, Double> = emptyMap(),
    val nameError: String? = null,
    val amountError: String? = null,
    val isLoading: Boolean = false,
    val isExpenseAdded: Boolean = false,
    val errorMessage: String? = null,
    val showDatePicker: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val flatRepository: FlatRepository,
    private val expenseRepository: ExpenseRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null
    private var currentFlatId: String? = null
    
    init {
        viewModelScope.launch {
            preferencesManager.currentUserIdFlow.collect { userId ->
                if (userId != null) {
                    currentUserId = userId
                    
                    preferencesManager.currentFlatIdFlow.collect { flatId ->
                        currentFlatId = flatId
                    }
                }
            }
        }
        
        // Initialize date text
        updateDateText()
    }
    
    fun setExpenseType(isPersonal: Boolean) {
        _uiState.update { it.copy(isPersonalExpense = isPersonal) }
    }
    
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }
    
    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount, amountError = null) }
    }
    
    fun updateCategory(category: ExpenseCategory) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun updateDate(date: Long) {
        _uiState.update { it.copy(date = date) }
        updateDateText()
    }
    
    fun updateSplitMethod(splitMethod: SplitMethod) {
        _uiState.update { it.copy(splitMethod = splitMethod) }
    }
    
    fun showDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }
    
    fun hideDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }
    
    private fun updateDateText() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateText = dateFormat.format(Date(_uiState.value.date))
        _uiState.update { it.copy(dateText = dateText) }
    }
    
    fun saveExpense() {
        if (!validateInputs()) return
        
        val userId = currentUserId ?: return
        val flatId = currentFlatId
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val amountValue = _uiState.value.amount.toDoubleOrNull() ?: 0.0
                
                if (_uiState.value.isPersonalExpense) {
                    // Create personal expense
                    expenseRepository.createPersonalExpense(
                        name = _uiState.value.name,
                        amount = amountValue,
                        category = _uiState.value.category,
                        date = _uiState.value.date,
                        userId = userId
                    )
                } else if (flatId != null) {
                    // Create shared expense
                    // For now, we'll use equal splits for all flatmates
                    // In a real app, we would calculate this based on the selected split method
                    val splits = mutableMapOf<String, Double>()
                    
                    // Get flatmates
                    val flat = flatRepository.getFlatById(flatId).value
                    flat?.memberIds?.forEach { memberId ->
                        splits[memberId] = amountValue / flat.memberIds.size
                    }
                    
                    expenseRepository.createSharedExpense(
                        name = _uiState.value.name,
                        amount = amountValue,
                        category = _uiState.value.category,
                        date = _uiState.value.date,
                        userId = userId,
                        flatId = flatId,
                        splitMethod = _uiState.value.splitMethod,
                        splits = splits
                    )
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isExpenseAdded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to add expense"
                    )
                }
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Expense name cannot be empty") }
            isValid = false
        }
        
        if (_uiState.value.amount.isBlank()) {
            _uiState.update { it.copy(amountError = "Amount cannot be empty") }
            isValid = false
        } else {
            val amountValue = _uiState.value.amount.toDoubleOrNull()
            if (amountValue == null || amountValue <= 0) {
                _uiState.update { it.copy(amountError = "Please enter a valid amount") }
                isValid = false
            }
        }
        
        return isValid
    }
}