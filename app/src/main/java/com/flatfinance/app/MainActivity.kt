package com.flatfinance.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.flatfinance.app.ui.FlatFinanceApp
import com.flatfinance.app.ui.theme.FlatFinanceTheme
import com.flatfinance.app.utils.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge design
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val isDarkMode by preferencesManager.isDarkModeFlow.collectAsState(initial = isSystemInDarkTheme())
            
            FlatFinanceTheme(darkTheme = isDarkMode) {
                FlatFinanceApp()
            }
        }
    }
}