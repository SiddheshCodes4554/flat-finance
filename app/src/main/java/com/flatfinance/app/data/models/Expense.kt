package com.flatfinance.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory {
    RENT, ELECTRICITY, WIFI, GROCERIES, MAINTENANCE, FOOD, TRAVEL, 
    ENTERTAINMENT, EDUCATION, SHOPPING, HEALTH, OTHER
}

enum class ExpenseType {
    PERSONAL, SHARED
}

enum class SplitMethod {
    EQUAL, CUSTOM
}

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey
    val id: String,
    val name: String,
    val amount: Double,
    val category: ExpenseCategory,
    val type: ExpenseType,
    val date: Long,
    val createdBy: String,
    val flatId: String? = null,
    val splitMethod: SplitMethod? = null,
    val splits: Map<String, Double>? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)