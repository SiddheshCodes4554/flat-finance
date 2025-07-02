package com.flatfinance.app.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flatfinance.app.R
import com.flatfinance.app.ui.AppState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val appState by viewModel.appState.collectAsState()
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )
    
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        when (appState) {
            AppState.AUTHENTICATED -> onNavigateToDashboard()
            AppState.ONBOARDING -> onNavigateToOnboarding()
            else -> onNavigateToLogin()
        }
    }
    
    Splash(alpha = alphaAnim.value)
}

@Composable
fun Splash(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .alpha(alpha),
            contentAlignment = Alignment.Center
        ) {
            // Replace with your app logo
            // Image(
            //     painter = painterResource(id = R.drawable.app_logo),
            //     contentDescription = "Logo",
            //     modifier = Modifier.size(120.dp)
            // )
            
            Text(
                text = stringResource(id = R.string.flat_finance),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}