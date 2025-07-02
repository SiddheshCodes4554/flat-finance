package com.flatfinance.app.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatfinance.app.BuildConfig
import com.flatfinance.app.data.repositories.FlatRepository
import com.flatfinance.app.data.repositories.UserRepository
import com.flatfinance.app.utils.DataExporter
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val userAvatarUrl: String? = null,
    val flatName: String = "",
    val roommateCount: Int = 0,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val appVersion: String = BuildConfig.VERSION_NAME,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val flatRepository: FlatRepository,
    private val preferencesManager: PreferencesManager,
    private val dataExporter: DataExporter,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null
    private var currentFlatId: String? = null
    
    init {
        viewModelScope.launch {
            preferencesManager.isDarkModeFlow.collect { isDarkMode ->
                _uiState.update { it.copy(isDarkMode = isDarkMode) }
            }
        }
        
        viewModelScope.launch {
            preferencesManager.currentUserIdFlow.collect { userId ->
                if (userId != null) {
                    currentUserId = userId
                    loadUserData(userId)
                    
                    preferencesManager.currentFlatIdFlow.collect { flatId ->
                        if (flatId != null) {
                            currentFlatId = flatId
                            loadFlatData(flatId)
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
                            userName = user.name,
                            userEmail = user.email,
                            userAvatarUrl = user.avatarUrl
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
                            flatName = flat.name,
                            roommateCount = flat.memberIds.size
                        )
                    }
                }
            }
        }
    }
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            val newDarkModeState = !_uiState.value.isDarkMode
            preferencesManager.setDarkMode(newDarkModeState)
            _uiState.update { it.copy(isDarkMode = newDarkModeState) }
        }
    }
    
    fun toggleNotifications() {
        viewModelScope.launch {
            val newNotificationsState = !_uiState.value.notificationsEnabled
            // In a real app, we would update this in the preferences and update FCM subscription
            _uiState.update { it.copy(notificationsEnabled = newNotificationsState) }
        }
    }
    
    fun exportData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                val userId = currentUserId ?: throw IllegalStateException("User ID not found")
                val flatId = currentFlatId
                
                dataExporter.exportUserData(userId, flatId)
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to export data"
                    )
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                
                userRepository.logoutUser()
                preferencesManager.clearCurrentUserId()
                preferencesManager.clearCurrentFlatId()
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to logout"
                    )
                }
            }
        }
    }
}