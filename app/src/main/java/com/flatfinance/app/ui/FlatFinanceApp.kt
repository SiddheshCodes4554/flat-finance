package com.flatfinance.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flatfinance.app.ui.navigation.BottomNavigationBar
import com.flatfinance.app.ui.navigation.Screen
import com.flatfinance.app.ui.screens.auth.LoginScreen
import com.flatfinance.app.ui.screens.auth.SignUpScreen
import com.flatfinance.app.ui.screens.dashboard.DashboardScreen
import com.flatfinance.app.ui.screens.expense.AddExpenseScreen
import com.flatfinance.app.ui.screens.expense.ExpenseHistoryScreen
import com.flatfinance.app.ui.screens.onboarding.FlatSetupScreen
import com.flatfinance.app.ui.screens.onboarding.OnboardingScreen
import com.flatfinance.app.ui.screens.reminders.RemindersScreen
import com.flatfinance.app.ui.screens.reports.ReportsScreen
import com.flatfinance.app.ui.screens.settings.SettingsScreen
import com.flatfinance.app.ui.screens.splash.SplashScreen

@Composable
fun FlatFinanceApp(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val appState by viewModel.appState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) },
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToFlatSetup = { navController.navigate(Screen.FlatSetup.route) },
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) }
            )
        }
        
        composable(Screen.FlatSetup.route) {
            FlatSetupScreen(
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) }
            )
        }
        
        composable(Screen.Dashboard.route) {
            MainScreenWithBottomNav(
                navController = navController,
                currentScreen = Screen.Dashboard
            ) {
                DashboardScreen(
                    onNavigateToAddExpense = { navController.navigate(Screen.AddExpense.route) },
                    onNavigateToExpenseHistory = { navController.navigate(Screen.ExpenseHistory.route) }
                )
            }
        }
        
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ExpenseHistory.route) {
            MainScreenWithBottomNav(
                navController = navController,
                currentScreen = Screen.ExpenseHistory
            ) {
                ExpenseHistoryScreen()
            }
        }
        
        composable(Screen.Reminders.route) {
            MainScreenWithBottomNav(
                navController = navController,
                currentScreen = Screen.Reminders
            ) {
                RemindersScreen()
            }
        }
        
        composable(Screen.Reports.route) {
            MainScreenWithBottomNav(
                navController = navController,
                currentScreen = Screen.Reports
            ) {
                ReportsScreen()
            }
        }
        
        composable(Screen.Settings.route) {
            MainScreenWithBottomNav(
                navController = navController,
                currentScreen = Screen.Settings
            ) {
                SettingsScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreenWithBottomNav(
    navController: androidx.navigation.NavController,
    currentScreen: Screen,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    if (screen.route != currentScreen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Dashboard.route)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        content()
    }
}