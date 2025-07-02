package com.flatfinance.app.utils

import android.content.Context
import android.graphics.Color
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.ui.screens.reports.TopExpense
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PDFGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    fun generateExpenseReport(
        title: String,
        totalAmount: Double,
        categoryData: Map<ExpenseCategory, Double>,
        trendData: Map<String, Double>,
        topExpenses: List<TopExpense>
    ): File {
        val file = File(context.filesDir, "expense_report_${System.currentTimeMillis()}.pdf")
        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A4)
        
        try {
            // Add title
            val titleParagraph = Paragraph(title)
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            document.add(titleParagraph)
            
            // Add generation date
            val dateParagraph = Paragraph("Generated on: ${dateFormat.format(Date())}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(dateParagraph)
            
            document.add(Paragraph("\n"))
            
            // Add total amount
            val totalParagraph = Paragraph("Total Expenses: ${currencyFormat.format(totalAmount)}")
                .setFontSize(16f)
                .setBold()
            document.add(totalParagraph)
            
            document.add(Paragraph("\n"))
            
            // Add category breakdown
            document.add(Paragraph("Category Breakdown").setFontSize(14f).setBold())
            
            val categoryTable = Table(UnitValue.createPercentArray(new float[]{40f, 30f, 30f}))
                .useAllAvailableWidth()
            
            // Add header row
            categoryTable.addHeaderCell(createCell("Category", true))
            categoryTable.addHeaderCell(createCell("Amount", true))
            categoryTable.addHeaderCell(createCell("Percentage", true))
            
            // Add data rows
            categoryData.forEach { (category, amount) ->
                val percentage = amount / totalAmount * 100
                categoryTable.addCell(createCell(getCategoryDisplayName(category)))
                categoryTable.addCell(createCell(currencyFormat.format(amount)))
                categoryTable.addCell(createCell(String.format("%.1f%%", percentage)))
            }
            
            document.add(categoryTable)
            
            document.add(Paragraph("\n"))
            
            // Add trend data
            document.add(Paragraph("Expense Trend").setFontSize(14f).setBold())
            
            val trendTable = Table(UnitValue.createPercentArray(new float[]{50f, 50f}))
                .useAllAvailableWidth()
            
            // Add header row
            trendTable.addHeaderCell(createCell("Period", true))
            trendTable.addHeaderCell(createCell("Amount", true))
            
            // Add data rows
            trendData.forEach { (period, amount) ->
                trendTable.addCell(createCell(period))
                trendTable.addCell(createCell(currencyFormat.format(amount)))
            }
            
            document.add(trendTable)
            
            document.add(Paragraph("\n"))
            
            // Add top expenses
            document.add(Paragraph("Top Expenses").setFontSize(14f).setBold())
            
            val topExpensesTable = Table(UnitValue.createPercentArray(new float[]{40f, 30f, 30f}))
                .useAllAvailableWidth()
            
            // Add header row
            topExpensesTable.addHeaderCell(createCell("Expense", true))
            topExpensesTable.addHeaderCell(createCell("Category", true))
            topExpensesTable.addHeaderCell(createCell("Amount", true))
            
            // Add data rows
            topExpenses.forEach { expense ->
                topExpensesTable.addCell(createCell(expense.name))
                topExpensesTable.addCell(createCell(getCategoryDisplayName(expense.category)))
                topExpensesTable.addCell(createCell(currencyFormat.format(expense.amount)))
            }
            
            document.add(topExpensesTable)
            
            document.add(Paragraph("\n"))
            
            // Add footer
            val footerParagraph = Paragraph("Generated by Flat Finance App")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic()
            document.add(footerParagraph)
            
        } finally {
            document.close()
        }
        
        return file
    }
    
    private fun createCell(text: String, isHeader: Boolean = false): Cell {
        val cell = Cell().add(Paragraph(text))
        
        if (isHeader) {
            cell.setBackgroundColor(DeviceRgb(240, 240, 240))
                .setBold()
        }
        
        return cell
    }
    
    private fun getCategoryDisplayName(category: ExpenseCategory): String {
        return when (category) {
            ExpenseCategory.RENT -> "Rent"
            ExpenseCategory.ELECTRICITY -> "Electricity"
            ExpenseCategory.WIFI -> "WiFi"
            ExpenseCategory.GROCERIES -> "Groceries"
            ExpenseCategory.MAINTENANCE -> "Maintenance"
            ExpenseCategory.FOOD -> "Food"
            ExpenseCategory.TRAVEL -> "Travel"
            ExpenseCategory.ENTERTAINMENT -> "Entertainment"
            ExpenseCategory.EDUCATION -> "Education"
            ExpenseCategory.SHOPPING -> "Shopping"
            ExpenseCategory.HEALTH -> "Health"
            ExpenseCategory.OTHER -> "Other"
        }
    }
}