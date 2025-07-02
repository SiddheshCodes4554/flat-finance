package com.flatfinance.app.utils

object Constants {
    // Notification Channels
    const val REMINDERS_CHANNEL_ID = "reminders_channel"
    const val UPDATES_CHANNEL_ID = "updates_channel"
    
    // Preferences Keys
    const val PREFERENCES_NAME = "flat_finance_preferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_FLAT_ID = "flat_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_AVATAR = "user_avatar"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    
    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val FLATS_COLLECTION = "flats"
    const val EXPENSES_COLLECTION = "expenses"
    const val REMINDERS_COLLECTION = "reminders"
    const val BUDGETS_COLLECTION = "budgets"
    
    // File Paths
    const val PDF_DIRECTORY = "reports"
    const val CSV_DIRECTORY = "exports"
    
    // Date Formats
    const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
    const val DATE_FORMAT_MONTH_YEAR = "MMMM yyyy"
    const val DATE_FORMAT_YEAR = "yyyy"
    
    // Currency
    const val DEFAULT_CURRENCY = "₹"
    
    // App Info
    const val APP_VERSION = "1.0.0"
    const val PRIVACY_POLICY_URL = "https://flatfinance.app/privacy"
    const val TERMS_URL = "https://flatfinance.app/terms"
}