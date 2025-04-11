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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.finance.ui.viewmodel.InvoicesUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: FinanceViewModel,
    onNavigateToInvoiceDetail: (String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val invoicesState by viewModel.invoicesState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<InvoiceStatus?>(null) }
    
    // Ensure invoices are loaded when the screen is displayed
    LaunchedEffect(key1 = Unit) {
        viewModel.loadInvoices()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoices") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                            text = { Text("All Invoices") },
                            onClick = {
                                selectedFilter = null
                                showFilterMenu = false
                            }
                        )
                        InvoiceStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text("${status.name} Invoices") },
                                onClick = {
                                    selectedFilter = status
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToInvoiceDetail(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Create Invoice")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = invoicesState) {
                is InvoicesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is InvoicesUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No invoices found",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Click the + button to create a new invoice",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                is InvoicesUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                
                is InvoicesUiState.Success -> {
                    val invoices = state.invoices
                    
                    // Apply filter if one is selected
                    val filteredInvoices = if (selectedFilter != null) {
                        invoices.filter { it.status == selectedFilter }
                    } else {
                        invoices
                    }
                    
                    // Sort invoices by due date (closest due first)
                    val sortedInvoices = filteredInvoices.sortedBy { it.dueDate }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            InvoiceSummaryCard(invoices)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        items(sortedInvoices) { invoice ->
                            InvoiceListItem(
                                invoice = invoice,
                                onClick = { onNavigateToInvoiceDetail(invoice.id) }
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
fun InvoiceSummaryCard(invoices: List<Invoice>) {
    val today = Date()
    
    val pendingAmount = invoices
        .filter { it.status == InvoiceStatus.SENT || it.status == InvoiceStatus.OVERDUE }
        .sumOf { it.amount.toDouble() }
    
    val overdueInvoices = invoices.filter { it.status == InvoiceStatus.OVERDUE }
    val overdueAmount = overdueInvoices.sumOf { it.amount.toDouble() }
    
    val paidAmount = invoices
        .filter { it.status == InvoiceStatus.PAID }
        .sumOf { it.amount.toDouble() }
    
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
                text = "Invoice Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(pendingAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                Column {
                    Text(
                        text = "Overdue",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(overdueAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                Column {
                    Text(
                        text = "Paid",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(paidAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (overdueInvoices.isNotEmpty()) {
                    BadgedBox(
                        badge = {
                            Badge {
                                Text("${overdueInvoices.size}")
                            }
                        }
                    ) {
                        Text(
                            text = "Overdue Invoices",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceListItem(
    invoice: Invoice,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val today = Date()
    val isOverdue = invoice.dueDate.before(today) && invoice.status != InvoiceStatus.PAID
    
    val statusColor = when (invoice.status) {
        InvoiceStatus.DRAFT -> MaterialTheme.colorScheme.outline
        InvoiceStatus.SENT -> MaterialTheme.colorScheme.tertiary
        InvoiceStatus.OVERDUE -> MaterialTheme.colorScheme.error
        InvoiceStatus.PAID -> MaterialTheme.colorScheme.primary
        InvoiceStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Invoice #${invoice.invoiceNumber}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = CurrencyFormatter.formatAsRupees(invoice.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "To: ${invoice.customerName ?: invoice.customerId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "Due: ${dateFormat.format(invoice.dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    )
                }
                
                Text(
                    text = invoice.status.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }
        }
    }
} 