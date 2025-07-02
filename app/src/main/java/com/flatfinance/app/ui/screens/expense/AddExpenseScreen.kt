package com.flatfinance.app.ui.screens.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flatfinance.app.R
import com.flatfinance.app.data.models.ExpenseCategory
import com.flatfinance.app.data.models.SplitMethod
import com.flatfinance.app.ui.components.LoadingButton
import com.flatfinance.app.ui.screens.dashboard.getCategoryColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isExpenseAdded) {
        if (uiState.isExpenseAdded) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.add_expense)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Expense Type Selection
            ExpenseTypeSelector(
                isPersonal = uiState.isPersonalExpense,
                onPersonalSelected = { viewModel.setExpenseType(true) },
                onSharedSelected = { viewModel.setExpenseType(false) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Expense Name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text(stringResource(id = R.string.expense_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nameError != null
            )
            
            if (uiState.nameError != null) {
                Text(
                    text = uiState.nameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Amount
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text(stringResource(id = R.string.amount)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = uiState.amountError != null
            )
            
            if (uiState.amountError != null) {
                Text(
                    text = uiState.amountError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Category
            Text(
                text = stringResource(id = R.string.category),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            CategorySelector(
                selectedCategory = uiState.category,
                onCategorySelected = { viewModel.updateCategory(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date
            OutlinedTextField(
                value = uiState.dateText,
                onValueChange = { },
                label = { Text(stringResource(id = R.string.date)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.showDatePicker() }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select Date"
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Split Method (only for shared expenses)
            AnimatedVisibility(
                visible = !uiState.isPersonalExpense,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.split_method),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SplitMethodOption(
                            title = stringResource(id = R.string.equal_split),
                            isSelected = uiState.splitMethod == SplitMethod.EQUAL,
                            onClick = { viewModel.updateSplitMethod(SplitMethod.EQUAL) },
                            modifier = Modifier.weight(1f)
                        )
                        
                        SplitMethodOption(
                            title = stringResource(id = R.string.custom_split),
                            isSelected = uiState.splitMethod == SplitMethod.CUSTOM,
                            onClick = { viewModel.updateSplitMethod(SplitMethod.CUSTOM) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Custom Split UI (if selected)
                    AnimatedVisibility(
                        visible = uiState.splitMethod == SplitMethod.CUSTOM,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Text(
                                text = "Custom Split",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // Here we would show UI for custom split percentages
                            // This would be populated with flatmates from the current flat
                            Text(
                                text = "Custom split UI would go here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            LoadingButton(
                text = stringResource(id = R.string.save),
                isLoading = uiState.isLoading,
                onClick = { viewModel.saveExpense() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    
    // Date Picker Dialog
    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date
        )
        
        DatePickerDialog(
            onDismissRequest = { viewModel.hideDatePicker() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.updateDate(it)
                        }
                        viewModel.hideDatePicker()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDatePicker() }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
fun ExpenseTypeSelector(
    isPersonal: Boolean,
    onPersonalSelected: () -> Unit,
    onSharedSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (isPersonal) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable(onClick = onPersonalSelected),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.personal),
                style = MaterialTheme.typography.titleMedium,
                color = if (isPersonal) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (!isPersonal) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .clickable(onClick = onSharedSelected),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.shared),
                style = MaterialTheme.typography.titleMedium,
                color = if (!isPersonal) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategorySelector(
    selectedCategory: ExpenseCategory,
    onCategorySelected: (ExpenseCategory) -> Unit
) {
    val categories = ExpenseCategory.values()
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in categories.indices step 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (j in 0 until 3) {
                    val index = i + j
                    if (index < categories.size) {
                        val category = categories[index]
                        CategoryItem(
                            category = category,
                            isSelected = category == selectedCategory,
                            onClick = { onCategorySelected(category) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: ExpenseCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(category)
    val backgroundColor = if (isSelected) categoryColor.copy(alpha = 0.2f)
                          else MaterialTheme.colorScheme.surfaceVariant
    
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = categoryColor
                    )
                } else {
                    Text(
                        text = category.name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = categoryColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = getCategoryDisplayName(category),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SplitMethodOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                          else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
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