package com.erp.modules.finance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import java.util.UUID

data class CategoryBudget(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val budgetAmount: BigDecimal,
    val spentAmount: BigDecimal
) {
    val remainingAmount: BigDecimal
        get() = budgetAmount.subtract(spentAmount)
    
    val progressPercentage: Float
        get() {
            if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) return 0f
            val percentage = spentAmount.toFloat() / budgetAmount.toFloat()
            return percentage.coerceIn(0f, 1f)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagementScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    
    // Sample budget data
    val budgetCategories = remember {
        listOf(
            "Salary", "Utilities", "Supplies", "Maintenance", "Food", 
            "Transportation", "Marketing", "Others"
        )
    }
    
    // Dialog states
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var budgetAmount by remember { mutableStateOf("") }
    
    // Current month's transactions
    val currentMonthTransactions = remember(transactions) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        
        transactions.filter { transaction ->
            val transactionCalendar = Calendar.getInstance()
            transactionCalendar.time = transaction.date
            transactionCalendar.get(Calendar.YEAR) == year && 
                    transactionCalendar.get(Calendar.MONTH) == month
        }
    }
    
    // Mock budget data - in a real app, this would come from a repository
    val budgets = remember {
        mutableStateOf(
            listOf(
                CategoryBudget(
                    category = "Utilities",
                    budgetAmount = BigDecimal("15000"),
                    spentAmount = BigDecimal("8750")
                ),
                CategoryBudget(
                    category = "Supplies",
                    budgetAmount = BigDecimal("50000"),
                    spentAmount = BigDecimal("42500")
                ),
                CategoryBudget(
                    category = "Maintenance",
                    budgetAmount = BigDecimal("20000"),
                    spentAmount = BigDecimal("5000")
                ),
                CategoryBudget(
                    category = "Food",
                    budgetAmount = BigDecimal("30000"),
                    spentAmount = BigDecimal("28000")
                )
            )
        )
    }
    
    // Calculate actual spending by category from transactions
    val spendingByCategory = currentMonthTransactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId ?: "Others" }
        .mapValues { it.value.sumOf { transaction -> transaction.amount.toDouble() } }
    
    // Sync budget data with actual spending
    LaunchedEffect(spendingByCategory) {
        val updatedBudgets = budgets.value.map { budget ->
            val actualSpent = spendingByCategory[budget.category] ?: 0.0
            budget.copy(spentAmount = BigDecimal(actualSpent))
        }
        budgets.value = updatedBudgets
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddBudgetDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Monthly Budget Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val totalBudget = budgets.value.sumOf { it.budgetAmount.toDouble() }
                    val totalSpent = budgets.value.sumOf { it.spentAmount.toDouble() }
                    val remainingBudget = totalBudget - totalSpent
                    val progressPercentage = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Budget",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = CurrencyFormatter.formatAsRupees(totalBudget),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Total Spent",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = CurrencyFormatter.formatAsRupees(totalSpent),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (totalSpent > totalBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Remaining",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = CurrencyFormatter.formatAsRupees(remainingBudget),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (remainingBudget < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Overall progress bar
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Overall Budget Usage",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "${(progressPercentage * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    progressPercentage > 1f -> MaterialTheme.colorScheme.error
                                    progressPercentage > 0.85f -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = { progressPercentage.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = when {
                                progressPercentage > 1f -> MaterialTheme.colorScheme.error
                                progressPercentage > 0.85f -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
            
            // Category budgets
            Text(
                text = "Category Budgets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (budgets.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No budgets set yet.\nTap the + button to add a category budget.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(budgets.value) { budget ->
                        BudgetCategoryCard(
                            budget = budget,
                            onEdit = {
                                selectedCategory = budget.category
                                budgetAmount = budget.budgetAmount.toString()
                                showAddBudgetDialog = true
                            }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
    
    // Add Budget Dialog
    if (showAddBudgetDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddBudgetDialog = false
                selectedCategory = ""
                budgetAmount = ""
            },
            title = {
                Text(
                    text = if (selectedCategory.isNotEmpty()) "Edit Budget" else "Add Budget",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category selection
                    if (selectedCategory.isEmpty()) {
                        // Only show category selection for new budgets
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { selectedCategory = it },
                            label = { Text("Category") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Category: $selectedCategory",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Budget amount
                    OutlinedTextField(
                        value = budgetAmount,
                        onValueChange = { 
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                budgetAmount = it
                            }
                        },
                        label = { Text("Budget Amount (₹)") },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedCategory.isNotEmpty() && budgetAmount.isNotEmpty()) {
                            val amount = try {
                                BigDecimal(budgetAmount)
                            } catch (e: Exception) {
                                BigDecimal.ZERO
                            }
                            
                            val currentList = budgets.value.toMutableList()
                            val existingIndex = currentList.indexOfFirst { it.category == selectedCategory }
                            
                            if (existingIndex >= 0) {
                                // Update existing budget
                                currentList[existingIndex] = currentList[existingIndex].copy(
                                    budgetAmount = amount
                                )
                            } else {
                                // Add new budget
                                currentList.add(
                                    CategoryBudget(
                                        category = selectedCategory,
                                        budgetAmount = amount,
                                        spentAmount = BigDecimal(spendingByCategory[selectedCategory] ?: 0.0)
                                    )
                                )
                            }
                            
                            budgets.value = currentList
                            showAddBudgetDialog = false
                            selectedCategory = ""
                            budgetAmount = ""
                        }
                    },
                    enabled = (selectedCategory.isNotEmpty() && budgetAmount.isNotEmpty())
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddBudgetDialog = false
                        selectedCategory = ""
                        budgetAmount = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BudgetCategoryCard(
    budget: CategoryBudget,
    onEdit: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = budget.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Budget",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Budget amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(budget.budgetAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = "Spent",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(budget.spentAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = when {
                            budget.spentAmount > budget.budgetAmount -> MaterialTheme.colorScheme.error
                            budget.progressPercentage > 0.85f -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(budget.remainingAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = when {
                            budget.remainingAmount < BigDecimal.ZERO -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Used ${(budget.progressPercentage * 100).toInt()}% of budget",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                LinearProgressIndicator(
                    progress = { budget.progressPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = when {
                        budget.progressPercentage >= 1f -> MaterialTheme.colorScheme.error
                        budget.progressPercentage > 0.85f -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
} 