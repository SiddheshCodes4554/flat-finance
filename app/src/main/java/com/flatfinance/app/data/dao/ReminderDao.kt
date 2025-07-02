package com.flatfinance.app.data.dao

import androidx.room.*
import com.flatfinance.app.data.models.Reminder
import com.flatfinance.app.data.models.ReminderStatus
import com.flatfinance.app.data.models.ReminderType
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    
    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    fun getReminderById(reminderId: String): Flow<Reminder?>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY dueDate ASC")
    fun getRemindersByUserId(userId: String): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE flatId = :flatId ORDER BY dueDate ASC")
    fun getRemindersByFlatId(flatId: String): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND status = :status ORDER BY dueDate ASC")
    fun getRemindersByUserIdAndStatus(userId: String, status: ReminderStatus): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND type = :type ORDER BY dueDate ASC")
    fun getRemindersByUserIdAndType(userId: String, type: ReminderType): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND dueDate BETWEEN :startDate AND :endDate ORDER BY dueDate ASC")
    fun getRemindersByUserIdAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND status = :status AND dueDate <= :date ORDER BY dueDate ASC")
    fun getUpcomingRemindersByUserId(userId: String, status: ReminderStatus = ReminderStatus.PENDING, date: Long): Flow<List<Reminder>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Delete
    suspend fun deleteReminder(reminder: Reminder)
    
    @Query("UPDATE reminders SET status = :status WHERE id = :reminderId")
    suspend fun updateReminderStatus(reminderId: String, status: ReminderStatus)
}