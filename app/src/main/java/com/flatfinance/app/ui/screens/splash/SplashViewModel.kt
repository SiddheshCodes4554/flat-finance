package com.flatfinance.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatfinance.app.data.repositories.UserRepository
import com.flatfinance.app.ui.AppState
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _appState = MutableStateFlow(AppState.LOADING)
    val appState: StateFlow<AppState> = _appState
    
    init {
        viewModelScope.launch {
            combine(
                preferencesManager.currentUserIdFlow,
                preferencesManager.isOnboardingCompletedFlow
            ) { userId, isOnboardingCompleted ->
                when {
                    userId == null -> AppState.UNAUTHENTICATED
                    !isOnboardingCompleted -> AppState.ONBOARDING
                    else -> AppState.AUTHENTICATED
                }
            }.collect { state ->
                _appState.value = state
            }
        }
    }
}