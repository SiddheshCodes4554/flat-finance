package com.flatfinance.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flatfinance.app.R

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    visible: Boolean = true
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Screen.bottomNavItems().forEach { screen ->
                NavigationBarItem(
                    icon = {
                        screen.icon?.let { Icon(it, contentDescription = screen.title) }
                    },
                    label = { Text(stringResource(getStringResourceForScreen(screen))) },
                    selected = currentScreen.route == screen.route,
                    onClick = { onScreenSelected(screen) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

private fun getStringResourceForScreen(screen: Screen): Int {
    return when (screen) {
        Screen.Dashboard -> R.string.dashboard
        Screen.ExpenseHistory -> R.string.expense_history
        Screen.Reminders -> R.string.reminders
        Screen.Reports -> R.string.reports
        Screen.Settings -> R.string.settings
        else -> R.string.app_name
    }
}