package com.flatfinance.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BudgetPeriod {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey
    val id: String,
    val userId: String,
    val category: ExpenseCategory? = null, // Null means overall budget
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)