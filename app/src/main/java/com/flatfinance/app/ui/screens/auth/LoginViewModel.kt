package com.flatfinance.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatfinance.app.data.repositories.UserRepository
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            preferencesManager.isOnboardingCompletedFlow.collect { isCompleted ->
                _uiState.update { it.copy(isOnboardingCompleted = isCompleted) }
            }
        }
    }
    
    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                errorMessage = null
            )
        }
    }
    
    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                errorMessage = null
            )
        }
    }
    
    fun login() {
        if (!validateInputs()) return
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val user = userRepository.loginUser(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )
                
                preferencesManager.setCurrentUserId(user.id)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Login failed"
                    )
                }
            }
        }
    }
    
    fun loginWithGoogle() {
        // TODO: Implement Google Sign-In
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (_uiState.value.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email cannot be empty") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            isValid = false
        }
        
        if (_uiState.value.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password cannot be empty") }
            isValid = false
        } else if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }
        
        return isValid
    }
}