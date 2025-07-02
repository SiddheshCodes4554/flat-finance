package com.flatfinance.app.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.flatfinance.app.data.repositories.BudgetRepository
import com.flatfinance.app.data.repositories.ExpenseRepository
import com.flatfinance.app.data.repositories.ReminderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val expenseRepository: ExpenseRepository,
    private val reminderRepository: ReminderRepository,
    private val budgetRepository: BudgetRepository
) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    suspend fun exportUserData(userId: String, flatId: String?): Uri {
        return withContext(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis()
            val directory = File(context.filesDir, "exports")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            val file = File(directory, "flat_finance_export_$timestamp.csv")
            val fileWriter = FileWriter(file)
            
            try {
                // Export personal expenses
                val personalExpenses = expenseRepository.getExpensesByUserId(userId).value ?: emptyList()
                fileWriter.append("PERSONAL EXPENSES\n")
                fileWriter.append("Date,Name,Category,Amount\n")
                
                personalExpenses.forEach { expense ->
                    val date = dateFormat.format(Date(expense.date))
                    fileWriter.append("$date,${expense.name},${expense.category},${expense.amount}\n")
                }
                
                fileWriter.append("\n")
                
                // Export shared expenses if flatId is available
                if (flatId != null) {
                    val sharedExpenses = expenseRepository.getExpensesByFlatId(flatId).value ?: emptyList()
                    fileWriter.append("SHARED EXPENSES\n")
                    fileWriter.append("Date,Name,Category,Amount,Split Method\n")
                    
                    sharedExpenses.forEach { expense ->
                        val date = dateFormat.format(Date(expense.date))
                        fileWriter.append("$date,${expense.name},${expense.category},${expense.amount},${expense.splitMethod}\n")
                    }
                    
                    fileWriter.append("\n")
                }
                
                // Export reminders
                val reminders = reminderRepository.getRemindersByUserId(userId).value ?: emptyList()
                fileWriter.append("REMINDERS\n")
                fileWriter.append("Title,Type,Amount,Due Date,Status\n")
                
                reminders.forEach { reminder ->
                    val dueDate = dateFormat.format(Date(reminder.dueDate))
                    fileWriter.append("${reminder.title},${reminder.type},${reminder.amount},$dueDate,${reminder.status}\n")
                }
                
                fileWriter.append("\n")
                
                // Export budgets
                val budgets = budgetRepository.getBudgetsByUserId(userId).value ?: emptyList()
                fileWriter.append("BUDGETS\n")
                fileWriter.append("Category,Amount,Period,Start Date,End Date\n")
                
                budgets.forEach { budget ->
                    val startDate = dateFormat.format(Date(budget.startDate))
                    val endDate = budget.endDate?.let { dateFormat.format(Date(it)) } ?: "N/A"
                    fileWriter.append("${budget.category},${budget.amount},${budget.period},$startDate,$endDate\n")
                }
                
            } finally {
                fileWriter.close()
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        }
    }
}