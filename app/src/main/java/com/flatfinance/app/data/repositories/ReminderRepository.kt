package com.flatfinance.app.data.repositories

import com.flatfinance.app.data.dao.ReminderDao
import com.flatfinance.app.data.models.Reminder
import com.flatfinance.app.data.models.ReminderStatus
import com.flatfinance.app.data.models.ReminderType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao,
    private val firestore: FirebaseFirestore
) {
    
    private val remindersCollection = firestore.collection("reminders")
    
    fun getReminderById(reminderId: String): Flow<Reminder?> {
        return reminderDao.getReminderById(reminderId)
    }
    
    fun getRemindersByUserId(userId: String): Flow<List<Reminder>> {
        return reminderDao.getRemindersByUserId(userId)
    }
    
    fun getRemindersByFlatId(flatId: String): Flow<List<Reminder>> {
        return reminderDao.getRemindersByFlatId(flatId)
    }
    
    fun getRemindersByUserIdAndStatus(userId: String, status: ReminderStatus): Flow<List<Reminder>> {
        return reminderDao.getRemindersByUserIdAndStatus(userId, status)
    }
    
    fun getRemindersByUserIdAndType(userId: String, type: ReminderType): Flow<List<Reminder>> {
        return reminderDao.getRemindersByUserIdAndType(userId, type)
    }
    
    fun getRemindersByUserIdAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersByUserIdAndDateRange(userId, startDate, endDate)
    }
    
    fun getUpcomingRemindersByUserId(userId: String, date: Long): Flow<List<Reminder>> {
        return reminderDao.getUpcomingRemindersByUserId(userId, ReminderStatus.PENDING, date)
    }
    
    suspend fun createReminder(
        title: String,
        description: String?,
        type: ReminderType,
        amount: Double,
        dueDate: Long,
        userId: String,
        flatId: String? = null,
        isRecurring: Boolean = false,
        recurringPeriod: Int? = null
    ): Reminder {
        val reminderId = UUID.randomUUID().toString()
        
        val reminder = Reminder(
            id = reminderId,
            title = title,
            description = description,
            type = type,
            amount = amount,
            dueDate = dueDate,
            status = ReminderStatus.PENDING,
            flatId = flatId,
            userId = userId,
            isRecurring = isRecurring,
            recurringPeriod = recurringPeriod
        )
        
        reminderDao.insertReminder(reminder)
        remindersCollection.document(reminderId).set(reminder).await()
        
        return reminder
    }
    
    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
        remindersCollection.document(reminder.id).set(reminder).await()
    }
    
    suspend fun updateReminderStatus(reminderId: String, status: ReminderStatus) {
        reminderDao.updateReminderStatus(reminderId, status)
        remindersCollection.document(reminderId).update("status", status).await()
    }
    
    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
        remindersCollection.document(reminder.id).delete().await()
    }
}