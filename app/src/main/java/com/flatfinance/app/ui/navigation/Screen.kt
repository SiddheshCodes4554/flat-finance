package com.flatfinance.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val showInBottomNav: Boolean = false
) {
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Sign Up")
    object Onboarding : Screen("onboarding", "Onboarding")
    object FlatSetup : Screen("flat_setup", "Flat Setup")
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home, true)
    object AddExpense : Screen("add_expense", "Add Expense")
    object ExpenseHistory : Screen("expense_history", "Expense History", Icons.Filled.History, true)
    object Reminders : Screen("reminders", "Reminders", Icons.Filled.Notifications, true)
    object Reports : Screen("reports", "Reports", Icons.Filled.BarChart, true)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, true)
    
    companion object {
        fun bottomNavItems() = listOf(
            Dashboard, ExpenseHistory, Reminders, Reports, Settings
        )
    }
}