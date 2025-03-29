package com.erp.modules.finance.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.finance.ui.viewmodel.TransactionsUiState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    onNavigateToTransactionDetail: (String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val transactionsState by viewModel.transactionsState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }
    
    // Ensure transactions are loaded when the screen is displayed
    LaunchedEffect(key1 = Unit) {
        viewModel.loadTransactions()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Transactions") },
                            onClick = {
                                selectedFilter = null
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Income Only") },
                            onClick = {
                                selectedFilter = TransactionType.INCOME
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Expenses Only") },
                            onClick = {
                                selectedFilter = TransactionType.EXPENSE
                                showFilterMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToTransactionDetail(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = transactionsState) {
                is TransactionsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is TransactionsUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No transactions found",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Click the + button to add a new transaction",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                is TransactionsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                
                is TransactionsUiState.Success -> {
                    val transactions = state.transactions
                    
                    // Apply filter if one is selected
                    val filteredTransactions = if (selectedFilter != null) {
                        transactions.filter { it.type == selectedFilter }
                    } else {
                        transactions
                    }
                    
                    // Sort transactions by date (newest first)
                    val sortedTransactions = filteredTransactions.sortedByDescending { it.date }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            TransactionSummaryCard(transactions)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(sortedTransactions) { transaction ->
                            TransactionListItem(
                                transaction = transaction,
                                onClick = { onNavigateToTransactionDetail(transaction.id) }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionSummaryCard(transactions: List<Transaction>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Transaction Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            val income = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.toDouble() }
            
            val expenses = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.toDouble() }
            
            val balance = income - expenses
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Income",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(income),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column {
                    Text(
                        text = "Total Expenses",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(expenses),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                Column {
                    Text(
                        text = "Net Balance",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(balance),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = dateFormat.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                
                if (transaction.referenceNumber != null && transaction.referenceNumber.isNotEmpty()) {
                    Text(
                        text = "Ref: ${transaction.referenceNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatAsRupees(transaction.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.type) {
                        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Text(
                    text = transaction.type.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (transaction.type) {
                        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
} 