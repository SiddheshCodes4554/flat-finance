package com.flatfinance.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flatfinance.app.R
import com.flatfinance.app.ui.components.GoogleSignInButton
import com.flatfinance.app.ui.components.LoadingButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            if (uiState.isOnboardingCompleted) {
                onNavigateToDashboard()
            } else {
                onNavigateToOnboarding()
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = stringResource(id = R.string.flat_finance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text(stringResource(id = R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = uiState.emailError != null
            )
            
            if (uiState.emailError != null) {
                Text(
                    text = uiState.emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text(stringResource(id = R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                isError = uiState.passwordError != null
            )
            
            if (uiState.passwordError != null) {
                Text(
                    text = uiState.passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = { /* TODO: Implement forgot password */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(id = R.string.forgot_password))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LoadingButton(
                text = stringResource(id = R.string.login),
                isLoading = uiState.isLoading,
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GoogleSignInButton(
                text = stringResource(id = R.string.continue_with_google),
                onClick = { viewModel.loginWithGoogle() },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_account),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onNavigateToSignUp) {
                    Text(stringResource(id = R.string.sign_up))
                }
            }
        }
        
        if (uiState.errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = uiState.errorMessage)
            }
        }
    }
}