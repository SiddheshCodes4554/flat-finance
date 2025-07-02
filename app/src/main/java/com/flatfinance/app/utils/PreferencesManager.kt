package com.flatfinance.app.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "flat_finance_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        private val CURRENT_FLAT_ID = stringPreferencesKey("current_flat_id")
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    }
    
    // Current User ID
    val currentUserIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID]
    }
    
    suspend fun setCurrentUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }
    
    suspend fun clearCurrentUserId() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
        }
    }
    
    // Current Flat ID
    val currentFlatIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[CURRENT_FLAT_ID]
    }
    
    suspend fun setCurrentFlatId(flatId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_FLAT_ID] = flatId
        }
    }
    
    suspend fun clearCurrentFlatId() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_FLAT_ID)
        }
    }
    
    // Dark Mode
    val isDarkModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }
    
    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDarkMode
        }
    }
    
    // Onboarding
    val isOnboardingCompletedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_ONBOARDING_COMPLETED] ?: false
    }
    
    suspend fun setOnboardingCompleted(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] = isCompleted
        }
    }
}