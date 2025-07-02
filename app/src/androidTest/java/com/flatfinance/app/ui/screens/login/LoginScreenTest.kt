package com.flatfinance.app.ui.screens.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.flatfinance.app.ui.theme.FlatFinanceTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun loginScreen_displaysAllElements() {
        // Start the app
        composeTestRule.setContent {
            FlatFinanceTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSignUp = {}
                )
            }
        }
        
        // Verify all UI elements are displayed
        composeTestRule.onNodeWithText("Welcome to").assertIsDisplayed()
        composeTestRule.onNodeWithText("Flat Finance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue with Google").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_showsErrorOnEmptyEmail() {
        // Start the app
        composeTestRule.setContent {
            FlatFinanceTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSignUp = {}
                )
            }
        }
        
        // Click login without entering email
        composeTestRule.onNodeWithText("Login").performClick()
        
        // Verify error message is displayed
        composeTestRule.onNodeWithText("Email cannot be empty").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_showsErrorOnEmptyPassword() {
        // Start the app
        composeTestRule.setContent {
            FlatFinanceTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSignUp = {}
                )
            }
        }
        
        // Enter email but no password
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Login").performClick()
        
        // Verify error message is displayed
        composeTestRule.onNodeWithText("Password cannot be empty").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_navigatesToSignUp() {
        var navigateToSignUpCalled = false
        
        // Start the app
        composeTestRule.setContent {
            FlatFinanceTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSignUp = { navigateToSignUpCalled = true }
                )
            }
        }
        
        // Click on Sign Up
        composeTestRule.onNodeWithText("Sign Up").performClick()
        
        // Verify navigation callback was called
        assert(navigateToSignUpCalled)
    }
    
    @Test
    fun loginScreen_attemptsLoginWithValidCredentials() {
        var loginSuccessCalled = false
        
        // Start the app
        composeTestRule.setContent {
            FlatFinanceTheme {
                LoginScreen(
                    onLoginSuccess = { loginSuccessCalled = true },
                    onNavigateToSignUp = {}
                )
            }
        }
        
        // Enter valid credentials
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        
        // Click login
        composeTestRule.onNodeWithText("Login").performClick()
        
        // Wait for the login process
        composeTestRule.waitForIdle()
        
        // Note: In a real test, we would mock the ViewModel and verify the login attempt
        // For this example, we're just checking the UI flow
    }
}