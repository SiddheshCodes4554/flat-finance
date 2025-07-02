package com.flatfinance.app.ui.screens.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flatfinance.app.R
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.ui.screens.reports.components.BarChart
import com.flatfinance.app.ui.screens.reports.components.CategoryPieChart
import com.flatfinance.app.ui.screens.reports.components.ExpenseTrendLineChart
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Report Period Selector
        Text(
            text = stringResource(id = R.string.reports),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Period Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            PeriodTab(
                title = stringResource(id = R.string.monthly),
                isSelected = uiState.selectedPeriod == ReportPeriod.MONTHLY,
                onClick = { viewModel.selectPeriod(ReportPeriod.MONTHLY) },
                modifier = Modifier.weight(1f)
            )
            
            PeriodTab(
                title = stringResource(id = R.string.yearly),
                isSelected = uiState.selectedPeriod == ReportPeriod.YEARLY,
                onClick = { viewModel.selectPeriod(ReportPeriod.YEARLY) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Date Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateToPreviousPeriod() }) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            
            Text(
                text = uiState.periodTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            IconButton(onClick = { viewModel.navigateToNextPeriod() }) {
                Text("→", style = MaterialTheme.typography.titleLarge)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Expense Type Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            ExpenseTypeTab(
                title = stringResource(id = R.string.all),
                isSelected = uiState.selectedExpenseType == ExpenseType.ALL,
                onClick = { viewModel.selectExpenseType(ExpenseType.ALL) },
                modifier = Modifier.weight(1f)
            )
            
            ExpenseTypeTab(
                title = stringResource(id = R.string.personal),
                isSelected = uiState.selectedExpenseType == ExpenseType.PERSONAL,
                onClick = { viewModel.selectExpenseType(ExpenseType.PERSONAL) },
                modifier = Modifier.weight(1f)
            )
            
            ExpenseTypeTab(
                title = stringResource(id = R.string.shared),
                isSelected = uiState.selectedExpenseType == ExpenseType.SHARED,
                onClick = { viewModel.selectExpenseType(ExpenseType.SHARED) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Total Expense Card
        TotalExpenseCard(
            totalAmount = uiState.totalAmount,
            previousPeriodAmount = uiState.previousPeriodAmount
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Category Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.category_breakdown),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Pie Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.categoryData.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_data_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        CategoryPieChart(
                            data = uiState.categoryData
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category Legend
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.categoryData.forEach { (category, amount) ->
                        CategoryLegendItem(
                            category = category,
                            amount = amount,
                            percentage = amount / uiState.totalAmount * 100
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Monthly Trend
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (uiState.selectedPeriod == ReportPeriod.MONTHLY) 
                        stringResource(id = R.string.daily_trend) 
                    else 
                        stringResource(id = R.string.monthly_trend),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Line Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.trendData.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_data_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        ExpenseTrendLineChart(
                            data = uiState.trendData
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Top Expenses
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.top_expenses),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bar Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.topExpenses.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_data_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        BarChart(
                            data = uiState.topExpenses
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Export Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.exportReportAsPdf() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.export_pdf))
            }
            
            OutlinedButton(
                onClick = { viewModel.shareReport() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.share))
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for navigation bar
    }
    
    // Loading Indicator
    AnimatedVisibility(
        visible = uiState.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // Error Snackbar
    if (uiState.errorMessage != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = uiState.errorMessage)
        }
    }
}

@Composable
fun PeriodTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ExpenseTypeTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TotalExpenseCard(
    totalAmount: Double,
    previousPeriodAmount: Double
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val percentChange = if (previousPeriodAmount > 0) {
        ((totalAmount - previousPeriodAmount) / previousPeriodAmount) * 100
    } else {
        0.0
    }
    val isIncrease = percentChange > 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.total_expenses),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = currencyFormat.format(totalAmount),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isIncrease) "↑" else "↓",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isIncrease) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = String.format("%.1f%%", Math.abs(percentChange)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isIncrease) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = stringResource(
                        id = if (isIncrease) R.string.from_previous_period_increase else R.string.from_previous_period_decrease
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun CategoryLegendItem(
    category: ExpenseCategory,
    amount: Double,
    percentage: Double
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = getCategoryColor(category),
                    shape = RoundedCornerShape(4.dp)
                )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = currencyFormat.format(amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = String.format("%.1f%%", percentage),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.RENT -> Color(0xFFF59E0B)
        ExpenseCategory.ELECTRICITY -> Color(0xFF3B82F6)
        ExpenseCategory.WIFI -> Color(0xFF8B5CF6)
        ExpenseCategory.GROCERIES -> Color(0xFF10B981)
        ExpenseCategory.MAINTENANCE -> Color(0xFF6B7280)
        ExpenseCategory.FOOD -> Color(0xFFEF4444)
        ExpenseCategory.TRAVEL -> Color(0xFFEC4899)
        ExpenseCategory.ENTERTAINMENT -> Color(0xFFF97316)
        ExpenseCategory.EDUCATION -> Color(0xFF0EA5E9)
        ExpenseCategory.SHOPPING -> Color(0xFF14B8A6)
        ExpenseCategory.HEALTH -> Color(0xFF84CC16)
        ExpenseCategory.OTHER -> Color(0xFF6B7280)
    }
}

@Composable
fun getCategoryDisplayName(category: ExpenseCategory): String {
    return when (category) {
        ExpenseCategory.RENT -> stringResource(id = R.string.rent)
        ExpenseCategory.ELECTRICITY -> stringResource(id = R.string.electricity)
        ExpenseCategory.WIFI -> stringResource(id = R.string.wifi)
        ExpenseCategory.GROCERIES -> stringResource(id = R.string.groceries)
        ExpenseCategory.MAINTENANCE -> stringResource(id = R.string.maintenance)
        ExpenseCategory.FOOD -> stringResource(id = R.string.food)
        ExpenseCategory.TRAVEL -> stringResource(id = R.string.travel)
        ExpenseCategory.ENTERTAINMENT -> stringResource(id = R.string.entertainment)
        ExpenseCategory.EDUCATION -> stringResource(id = R.string.education)
        ExpenseCategory.SHOPPING -> stringResource(id = R.string.shopping)
        ExpenseCategory.HEALTH -> stringResource(id = R.string.health)
        ExpenseCategory.OTHER -> stringResource(id = R.string.other)
    }
}