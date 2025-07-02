package com.flatfinance.app.services

import com.flatfinance.app.data.models.Reminder
import com.flatfinance.app.data.models.ReminderStatus
import com.flatfinance.app.data.models.ReminderType
import com.flatfinance.app.utils.NotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FlatFinanceMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Check if message contains a data payload
        remoteMessage.data.let { data ->
            when (data["type"]) {
                "REMINDER" -> {
                    val reminder = Reminder(
                        id = data["id"] ?: "",
                        title = data["title"] ?: "",
                        description = data["description"],
                        type = ReminderType.valueOf(data["reminderType"] ?: "OTHER"),
                        amount = data["amount"]?.toDoubleOrNull() ?: 0.0,
                        dueDate = data["dueDate"]?.toLongOrNull() ?: System.currentTimeMillis(),
                        status = ReminderStatus.valueOf(data["status"] ?: "PENDING"),
                        userId = data["userId"] ?: "",
                        flatId = data["flatId"]
                    )
                    
                    notificationManager.showReminderNotification(reminder)
                }
                
                "EXPENSE" -> {
                    val expenseName = data["name"] ?: "Expense"
                    val amount = data["amount"]?.toDoubleOrNull() ?: 0.0
                    val isShared = data["isShared"]?.toBoolean() ?: false
                    
                    notificationManager.showExpenseAddedNotification(expenseName, amount, isShared)
                }
                
                else -> {
                    // Handle other notification types or show a general notification
                    val title = remoteMessage.notification?.title ?: "Flat Finance"
                    val message = remoteMessage.notification?.body ?: "You have a new notification"
                    
                    notificationManager.showGeneralNotification(title, message)
                }
            }
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // If you want to send messages to this application instance or
        // manage this app's subscriptions on the server side, send the
        // FCM registration token to your app server.
        CoroutineScope(Dispatchers.IO).launch {
            sendRegistrationToServer(token)
        }
    }
    
    private suspend fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
        // This would typically involve sending the token to your backend
        // so it can be stored and used for sending targeted notifications.
    }
}