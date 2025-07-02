package com.flatfinance.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.flatfinance.app.MainActivity
import com.flatfinance.app.R
import com.flatfinance.app.data.models.Reminder
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val CHANNEL_ID_REMINDERS = "reminders_channel"
        private const val CHANNEL_ID_EXPENSES = "expenses_channel"
        private const val CHANNEL_ID_GENERAL = "general_channel"
        
        private const val NOTIFICATION_ID_REMINDER = 1000
        private const val NOTIFICATION_ID_EXPENSE = 2000
        private const val NOTIFICATION_ID_GENERAL = 3000
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Bill reminders and due dates"
            }
            
            val expenseChannel = NotificationChannel(
                CHANNEL_ID_EXPENSES,
                "Expenses",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Expense updates and notifications"
            }
            
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(reminderChannel, expenseChannel, generalChannel))
        }
    }
    
    fun showReminderNotification(reminder: Reminder) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NOTIFICATION_TYPE", "REMINDER")
            putExtra("REMINDER_ID", reminder.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val friendlyMessage = getFriendlyReminderMessage(reminder)
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(reminder.title)
            .setContentText(friendlyMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(friendlyMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_REMINDER + reminder.id.hashCode(), builder.build())
        }
    }
    
    fun showExpenseAddedNotification(expenseName: String, amount: Double, isShared: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NOTIFICATION_TYPE", "EXPENSE")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val message = if (isShared) {
            "A shared expense of $amount has been added for $expenseName"
        } else {
            "You added a personal expense of $amount for $expenseName"
        }
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_EXPENSES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Expense Added")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_EXPENSE + expenseName.hashCode(), builder.build())
        }
    }
    
    fun showGeneralNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_GENERAL + title.hashCode(), builder.build())
        }
    }
    
    private fun getFriendlyReminderMessage(reminder: Reminder): String {
        val daysLeft = (reminder.dueDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
        
        return when {
            daysLeft <= 0 -> "Your ${reminder.title} is due today! Don't forget to pay it."
            daysLeft == 1L -> "Your ${reminder.title} is due tomorrow. Get ready to pay it!"
            daysLeft <= 3 -> "Your ${reminder.title} is due in $daysLeft days. Don't ghost your obligations! 😅"
            else -> "Your ${reminder.title} is due in $daysLeft days."
        }
    }
    
    suspend fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
    }
    
    suspend fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
    }
    
    suspend fun getDeviceToken(): String {
        return FirebaseMessaging.getInstance().token.await()
    }
}