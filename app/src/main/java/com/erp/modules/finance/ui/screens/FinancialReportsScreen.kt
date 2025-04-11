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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ReportPeriod(val displayName: String, val days: Int) {
    LAST_7_DAYS("Last 7 Days", 7),
    LAST_30_DAYS("Last 30 Days", 30),
    LAST_90_DAYS("Last 90 Days", 90),
    LAST_365_DAYS("Last Year", 365),
    CUSTOM("Custom Range", 0)
}

enum class ReportType(val displayName: String) {
    INCOME_VS_EXPENSE("Income vs Expense"),
    CASH_FLOW("Cash Flow"),
    OUTSTANDING_INVOICES("Outstanding Invoices")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialReportsScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    var selectedReportType by remember { mutableStateOf(ReportType.INCOME_VS_EXPENSE) }
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.LAST_30_DAYS) }
    var isPeriodExpanded by remember { mutableStateOf(false) }
    
    // Custom date range
    var startDate by remember { mutableStateOf(getDateBefore(30)) }
    var endDate by remember { mutableStateOf(Date()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    // Filter data based on selected period
    LaunchedEffect(selectedPeriod, startDate, endDate) {
        when (selectedPeriod) {
            ReportPeriod.LAST_7_DAYS -> {
                startDate = getDateBefore(7)
                endDate = Date()
            }
            ReportPeriod.LAST_30_DAYS -> {
                startDate = getDateBefore(30)
                endDate = Date()
            }
            ReportPeriod.LAST_90_DAYS -> {
                startDate = getDateBefore(90)
                endDate = Date()
            }
            ReportPeriod.LAST_365_DAYS -> {
                startDate = getDateBefore(365)
                endDate = Date()
            }
            ReportPeriod.CUSTOM -> {
                // Keep existing custom dates
            }
        }
    }
    
    // Filter transactions based on date range
    val filteredTransactions = transactions.filter { 
        it.date in startDate..endDate
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Reports") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Report Type Selection Tabs
            TabRow(
                selectedTabIndex = selectedReportType.ordinal
            ) {
                ReportType.values().forEachIndexed { index, reportType ->
                    Tab(
                        selected = selectedReportType.ordinal == index,
                        onClick = { selectedReportType = reportType },
                        text = { Text(reportType.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Period Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Period:",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                ExposedDropdownMenuBox(
                    expanded = isPeriodExpanded,
                    onExpandedChange = { isPeriodExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedPeriod.displayName,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPeriodExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
//                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isPeriodExpanded,
                        onDismissRequest = { isPeriodExpanded = false }
                    ) {
                        ReportPeriod.values().forEach { period ->
                            DropdownMenuItem(
                                text = { Text(period.displayName) },
                                onClick = {
                                    selectedPeriod = period
                                    isPeriodExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Custom date range selection
            if (selectedPeriod == ReportPeriod.CUSTOM) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Start date
                    OutlinedTextField(
                        value = dateFormat.format(startDate),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Start Date") },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showStartDatePicker = true }
                    )
                    
                    // End date
                    OutlinedTextField(
                        value = dateFormat.format(endDate),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("End Date") },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showEndDatePicker = true }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Report content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (transactions.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    // Show appropriate report based on selection
                    when (selectedReportType) {
                        ReportType.INCOME_VS_EXPENSE -> {
                            IncomeVsExpenseReport(
                                transactions = filteredTransactions,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }
                        
                        ReportType.CASH_FLOW -> {
                            CashFlowReport(
                                transactions = filteredTransactions,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }
                        
                        ReportType.OUTSTANDING_INVOICES -> {
                            OutstandingInvoicesReport(
                                invoices = invoices
                            )
                        }
                    }
                }
            }
            
            // Date pickers
            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = startDate.time
                )
                
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    startDate = Date(it)
                                }
                                showStartDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showStartDatePicker = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            
            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = endDate.time
                )
                
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    endDate = Date(it)
                                }
                                showEndDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showEndDatePicker = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}

@Composable
fun IncomeVsExpenseReport(
    transactions: List<Transaction>,
    startDate: Date,
    endDate: Date
) {
    val scrollState = rememberScrollState()
    
    val income = transactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount.toDouble() }
    
    val expenses = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount.toDouble() }
    
    val balance = income - expenses
    
    val incomeByCategory = transactions
        .filter { it.type == TransactionType.INCOME }
        .groupBy { it.categoryId ?: "Uncategorized" }
        .mapValues { it.value.sumOf { transaction -> transaction.amount.toDouble() } }
        .toList()
        .sortedByDescending { it.second }
    
    val expensesByCategory = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId ?: "Uncategorized" }
        .mapValues { it.value.sumOf { transaction -> transaction.amount.toDouble() } }
        .toList()
        .sortedByDescending { it.second }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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
                    text = "Income vs Expense Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
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
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
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
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
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
        
        // Income by category
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
                Text(
                    text = "Income by Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (incomeByCategory.isEmpty()) {
                    Text(
                        text = "No income data available for this period",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    incomeByCategory.forEach { (category, amount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = CurrencyFormatter.formatAsRupees(amount),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
        
        // Expenses by category
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
                Text(
                    text = "Expenses by Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (expensesByCategory.isEmpty()) {
                    Text(
                        text = "No expense data available for this period",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    expensesByCategory.forEach { (category, amount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = CurrencyFormatter.formatAsRupees(amount),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun CashFlowReport(
    transactions: List<Transaction>,
    startDate: Date,
    endDate: Date
) {
    val scrollState = rememberScrollState()
    
    // Group transactions by month
    val transactionsByMonth = transactions.groupBy { transaction ->
        val calendar = Calendar.getInstance()
        calendar.time = transaction.date
        "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
    }.mapKeys { entry ->
        val parts = entry.key.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val monthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        monthFormat.format(calendar.time)
    }.toSortedMap()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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
                    text = "Cash Flow Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                val totalInflow = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount.toDouble() }
                
                val totalOutflow = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount.toDouble() }
                
                val netCashFlow = totalInflow - totalOutflow
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Inflow",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CurrencyFormatter.formatAsRupees(totalInflow),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Total Outflow",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CurrencyFormatter.formatAsRupees(totalOutflow),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Net Cash Flow",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CurrencyFormatter.formatAsRupees(netCashFlow),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (netCashFlow >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        // Monthly cash flow
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
                Text(
                    text = "Monthly Cash Flow",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (transactionsByMonth.isEmpty()) {
                    Text(
                        text = "No cash flow data available for this period",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Column headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Month",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f)
                        )
                        
                        Text(
                            text = "Inflow",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.5f)
                        )
                        
                        Text(
                            text = "Outflow",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.5f)
                        )
                        
                        Text(
                            text = "Net",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                    
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    transactionsByMonth.forEach { (month, monthlyTransactions) ->
                        val inflow = monthlyTransactions
                            .filter { it.type == TransactionType.INCOME }
                            .sumOf { it.amount.toDouble() }
                        
                        val outflow = monthlyTransactions
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amount.toDouble() }
                        
                        val net = inflow - outflow
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = month,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(2f)
                            )
                            
                            Text(
                                text = CurrencyFormatter.formatAsRupees(inflow),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.5f)
                            )
                            
                            Text(
                                text = CurrencyFormatter.formatAsRupees(outflow),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.5f)
                            )
                            
                            Text(
                                text = CurrencyFormatter.formatAsRupees(net),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (net >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                        
                        HorizontalDivider(thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OutstandingInvoicesReport(
    invoices: List<Invoice>
) {
    val scrollState = rememberScrollState()
    
    // Filter outstanding invoices
    val outstandingInvoices = invoices.filter { 
        it.status == InvoiceStatus.SENT || it.status == InvoiceStatus.OVERDUE 
    }.sortedBy { it.dueDate }
    
    // Calculate totals
    val totalOutstanding = outstandingInvoices.sumOf { it.amount.toDouble() }
    val overdueAmount = outstandingInvoices
        .filter { it.status == InvoiceStatus.OVERDUE }
        .sumOf { it.amount.toDouble() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
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
                    text = "Outstanding Invoices Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Outstanding",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CurrencyFormatter.formatAsRupees(totalOutstanding),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Overdue Amount",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = CurrencyFormatter.formatAsRupees(overdueAmount),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Number of Invoices",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${outstandingInvoices.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Outstanding invoices
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
                Text(
                    text = "Outstanding Invoices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (outstandingInvoices.isEmpty()) {
                    Text(
                        text = "No outstanding invoices",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Column headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Invoice #",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.5f)
                        )
                        
                        Text(
                            text = "Customer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f)
                        )
                        
                        Text(
                            text = "Due Date",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.5f)
                        )
                        
                        Text(
                            text = "Amount",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                    
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val today = Date()
                    
                    outstandingInvoices.forEach { invoice ->
                        val isOverdue = invoice.dueDate.before(today)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = invoice.invoiceNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1.5f)
                            )
                            
                            Text(
                                text = invoice.customerName.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(2f)
                            )
                            
                            Text(
                                text = dateFormat.format(invoice.dueDate),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1.5f)
                            )
                            
                            Text(
                                text = CurrencyFormatter.formatAsRupees(invoice.amount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                        
                        HorizontalDivider(thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// Helper function to get date before a certain number of days
private fun getDateBefore(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -days)
    return calendar.time
} 