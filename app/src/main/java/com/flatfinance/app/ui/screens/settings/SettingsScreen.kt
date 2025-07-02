package com.flatfinance.app.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flatfinance.app.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Profile Section
        SettingsSectionHeader(title = stringResource(id = R.string.profile))
        
        ProfileCard(
            name = uiState.userName,
            email = uiState.userEmail,
            avatarUrl = uiState.userAvatarUrl,
            onClick = { /* Navigate to profile edit screen */ }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Flat Settings Section
        SettingsSectionHeader(title = stringResource(id = R.string.flat_settings))
        
        SettingsCard(
            title = uiState.flatName,
            subtitle = stringResource(id = R.string.manage_flat_settings),
            icon = Icons.Default.Home,
            onClick = { /* Navigate to flat settings screen */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsCard(
            title = stringResource(id = R.string.manage_roommates),
            subtitle = "${uiState.roommateCount} roommates",
            icon = Icons.Default.Group,
            onClick = { /* Navigate to roommates screen */ }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Budget Settings Section
        SettingsSectionHeader(title = stringResource(id = R.string.budget_setting))
        
        SettingsCard(
            title = stringResource(id = R.string.set_budget_limits),
            subtitle = stringResource(id = R.string.manage_monthly_budgets),
            icon = Icons.Default.AccountBalance,
            onClick = { /* Navigate to budget settings screen */ }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // App Settings Section
        SettingsSectionHeader(title = stringResource(id = R.string.app_settings))
        
        SwitchSettingsCard(
            title = stringResource(id = R.string.dark_mode),
            subtitle = stringResource(id = R.string.enable_dark_theme),
            icon = Icons.Default.DarkMode,
            isChecked = uiState.isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SwitchSettingsCard(
            title = stringResource(id = R.string.notifications),
            subtitle = stringResource(id = R.string.enable_push_notifications),
            icon = Icons.Default.Notifications,
            isChecked = uiState.notificationsEnabled,
            onCheckedChange = { viewModel.toggleNotifications() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsCard(
            title = stringResource(id = R.string.export_data),
            subtitle = stringResource(id = R.string.export_all_data_as_csv),
            icon = Icons.Default.Download,
            onClick = { viewModel.exportData() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // About Section
        SettingsSectionHeader(title = stringResource(id = R.string.about))
        
        SettingsCard(
            title = stringResource(id = R.string.app_version),
            subtitle = uiState.appVersion,
            icon = Icons.Default.Info,
            onClick = { /* Show app info */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsCard(
            title = stringResource(id = R.string.privacy_policy),
            subtitle = stringResource(id = R.string.view_privacy_policy),
            icon = Icons.Default.Security,
            onClick = { /* Open privacy policy */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsCard(
            title = stringResource(id = R.string.terms_of_service),
            subtitle = stringResource(id = R.string.view_terms_of_service),
            icon = Icons.Default.Description,
            onClick = { /* Open terms of service */ }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Logout Button
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(id = R.string.logout))
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for navigation bar
    }
    
    // Loading Indicator
    AnimatedVisibility(
        visible = uiState.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // Error Snackbar
    if (uiState.errorMessage != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = uiState.errorMessage)
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(id = R.string.logout_confirmation)) },
            text = { Text(stringResource(id = R.string.logout_confirmation_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(id = R.string.logout))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ProfileCard(
    name: String,
    email: String,
    avatarUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // If avatarUrl is not null, we would load the image using Coil
                // For now, just show the first letter of the name
                Text(
                    text = name.firstOrNull()?.toString() ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SwitchSettingsCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}