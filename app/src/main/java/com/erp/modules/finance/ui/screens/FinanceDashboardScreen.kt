package com.erp.modules.finance.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.ui.viewmodel.FeeDetailState
import com.erp.modules.finance.ui.viewmodel.FeesUiState
import com.erp.modules.finance.ui.viewmodel.FinanceUiState
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.finance.ui.viewmodel.InvoiceDetailState
import com.erp.modules.finance.ui.viewmodel.InvoicesUiState
import com.erp.modules.finance.ui.viewmodel.TransactionDetailState
import com.erp.modules.finance.ui.viewmodel.TransactionsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FinanceDashboardScreen(
//    viewModel: FinanceViewModel,
    observeUiState: StateFlow<FinanceUiState?>,
    onNavigateToTransactions: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onAddTransaction: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by observeUiState.collectAsStateWithLifecycle()
    val transactions = uiState?.transactions
    val invoices = uiState?.invoices
    val overdueInvoices = uiState?.overdueInvoices
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Finance Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                if (transactions != null && invoices != null) {
                    FinancialSummaryCard(transactions, invoices)
                }
            }
            
            // Quick Access Menu
            item {
                Text(
                    text = "Finance Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Transactions
                    QuickAccessButton(
                        text = "Transactions",
                        icon = Icons.Default.SwapHoriz,
                        onClick = onNavigateToTransactions
                    )
                    
                    // Invoices
                    QuickAccessButton(
                        text = "Invoices",
                        icon = Icons.Default.Receipt,
                        onClick = onNavigateToInvoices
                    )
                    
                    // Fees
                    QuickAccessButton(
                        text = "Fees",
                        icon = Icons.Default.School,
                        onClick = onNavigateToFees
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Reports
                    QuickAccessButton(
                        text = "Reports",
                        icon = Icons.Default.BarChart,
                        onClick = onNavigateToReports
                    )
                    
                    // Budgets
                    QuickAccessButton(
                        text = "Budgets",
                        icon = Icons.Default.AccountBalance,
                        onClick = onNavigateToBudgets
                    )
                    
                    // Spacer for 3-column alignment
                    Box(modifier = Modifier.width(100.dp))
                }
            }
            
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            val recentTransactions = transactions?.sortedByDescending { it.date }?.take(5)
            recentTransactions?.let {
                items(it) { transaction ->
                    TransactionItem(transaction)
                }
            }
            
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "View All Transactions",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onNavigateToTransactions() }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Overdue Invoices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            overdueInvoices?.let {
                if (it.isEmpty()) {
                    item {
                        Text(
                            text = "No overdue invoices",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                } else {
                    items(overdueInvoices.take(3)) { invoice ->
                        InvoiceItem(invoice)
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "View All Invoices",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onNavigateToInvoices() }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FinancialSummaryCard(transactions: List<Transaction>, invoices: List<Invoice>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Financial Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                        text = "Income",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = CurrencyFormatter.formatAsRupees(income),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = CurrencyFormatter.formatAsRupees(expenses),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = CurrencyFormatter.formatAsRupees(balance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val pendingInvoices = invoices
                .filter { it.status == InvoiceStatus.SENT || it.status == InvoiceStatus.OVERDUE }
                .sumOf { it.amount.toDouble() }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pending Invoices",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = CurrencyFormatter.formatAsRupees(pendingInvoices),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateFormat.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Text(
                text = CurrencyFormatter.formatAsRupees(transaction.amount),
                style = MaterialTheme.typography.bodyLarge,
                color = when (transaction.type) {
                    TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                    TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
fun InvoiceItem(invoice: Invoice) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Invoice #${invoice.invoiceNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Due: ${dateFormat.format(invoice.dueDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Text(
                text = CurrencyFormatter.formatAsRupees(invoice.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickAccessButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun FinanceDashboardScreenPreview() {
    FinanceDashboardScreen(
        observeUiState = MutableStateFlow(
            FinanceUiState(
                transactionsState = TransactionsUiState.Loading,
                invoicesState = InvoicesUiState.Loading,
                feesState = FeesUiState.Loading,
                feeDetailState = FeeDetailState.Loading,
                transactionDetailState = TransactionDetailState.Loading,
                invoiceDetailState = InvoiceDetailState.Loading,
            )
        ).asStateFlow(),
        onNavigateToTransactions = {},
        onNavigateToInvoices = {},
        onAddTransaction = {},
        onNavigateToFees = {},
        onNavigateToReports = {},
        onNavigateToBudgets = { },
        onNavigateBack = {},
    )
}