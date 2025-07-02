package com.flatfinance.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flatfinance.app.ui.screens.dashboard.DashboardScreen
import com.flatfinance.app.ui.screens.expense.AddExpenseScreen
import com.flatfinance.app.ui.screens.expense.ExpenseHistoryScreen
import com.flatfinance.app.ui.screens.login.LoginScreen
import com.flatfinance.app.ui.screens.onboarding.FlatSetupScreen
import com.flatfinance.app.ui.screens.onboarding.OnboardingScreen
import com.flatfinance.app.ui.screens.reminders.RemindersScreen
import com.flatfinance.app.ui.screens.reports.ReportsScreen
import com.flatfinance.app.ui.screens.settings.SettingsScreen
import com.flatfinance.app.ui.screens.signup.SignUpScreen
import com.flatfinance.app.ui.screens.splash.SplashScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.Login.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        
        composable(
            route = Screen.SignUp.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
        ) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Onboarding.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            OnboardingScreen(
                onCreateFlat = {
                    navController.navigate(Screen.FlatSetup.route)
                },
                onJoinFlat = { flatCode ->
                    // Handle joining a flat with the provided code
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.FlatSetup.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) }
        ) {
            FlatSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            DashboardScreen(
                onAddExpense = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onViewAllExpenses = {
                    navController.navigate(Screen.ExpenseHistory.route)
                },
                onViewAllReminders = {
                    navController.navigate(Screen.Reminders.route)
                }
            )
        }
        
        composable(
            route = Screen.AddExpense.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) }
        ) {
            AddExpenseScreen(
                onExpenseAdded = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ExpenseHistory.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            ExpenseHistoryScreen()
        }
        
        composable(
            route = Screen.Reminders.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            RemindersScreen()
        }
        
        composable(
            route = Screen.Reports.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            ReportsScreen()
        }
        
        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
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