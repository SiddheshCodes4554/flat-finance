package com.flatfinance.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ReminderType {
    RENT, ELECTRICITY, WIFI, OTHER
}

enum class ReminderStatus {
    PENDING, SNOOZED, PAID
}

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val type: ReminderType,
    val amount: Double,
    val dueDate: Long,
    val status: ReminderStatus = ReminderStatus.PENDING,
    val flatId: String? = null,
    val userId: String,
    val isRecurring: Boolean = false,
    val recurringPeriod: Int? = null, // Days between recurrences
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)