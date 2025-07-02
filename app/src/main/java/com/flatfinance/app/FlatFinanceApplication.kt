package com.flatfinance.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.flatfinance.app.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlatFinanceApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Reminders channel
            val remindersChannel = NotificationChannel(
                Constants.REMINDERS_CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for bill reminders and due dates"
                enableLights(true)
                enableVibration(true)
            }
            
            // Updates channel
            val updatesChannel = NotificationChannel(
                Constants.UPDATES_CHANNEL_ID,
                "Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for expense updates and flat activities"
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(remindersChannel)
            notificationManager.createNotificationChannel(updatesChannel)
        }
    }
}