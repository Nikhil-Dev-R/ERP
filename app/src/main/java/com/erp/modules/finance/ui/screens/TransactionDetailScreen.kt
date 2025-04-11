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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionStatus
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.finance.ui.viewmodel.TransactionDetailState
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    viewModel: FinanceViewModel,
    transactionId: String?,
    onNavigateBack: () -> Unit
) {
    val transactionDetailState by viewModel.transactionDetailState.collectAsState()
    val currentTransaction by viewModel.currentTransaction.collectAsState()
    
    // Form state variables
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf(TransactionType.INCOME) }
    var transactionDate by remember { mutableStateOf(Date()) }
    var referenceNumber by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    
    val amountFocusRequester = remember { FocusRequester() }
    var showDatePicker by remember { mutableStateOf(false) }
    var isTransactionTypeExpanded by remember { mutableStateOf(false) }
    var isCategoryExpanded by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    val categories = listOf("Fees", "Salary", "Supplies", "Utilities", "Maintenance", "Food", "Transportation", "Others")
    
    // Load existing transaction if editing
    LaunchedEffect(key1 = transactionId) {
        if (transactionId != null) {
            viewModel.getTransactionDetail(transactionId)
        } else {
            viewModel.createNewTransaction()
        }
    }
    
    // Update form with current transaction data
    LaunchedEffect(key1 = currentTransaction) {
        currentTransaction?.let { transaction ->
            description = transaction.description
            amount = transaction.amount.toString()
            transactionType = transaction.type
            transactionDate = transaction.date
            referenceNumber = transaction.referenceNumber ?: ""
            category = transaction.categoryId ?: ""
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (transactionId == null) "New Transaction" else "Edit Transaction"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (transactionDetailState) {
                is TransactionDetailState.Loading -> {
                    if (transactionId != null) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                
                is TransactionDetailState.Error -> {
                    val errorState = transactionDetailState as TransactionDetailState.Error
                    Text(
                        text = "Error: ${errorState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                
                else -> {
                    // Form content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Transaction type selection
                        ExposedDropdownMenuBox(
                            expanded = isTransactionTypeExpanded,
                            onExpandedChange = { isTransactionTypeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = transactionType.name,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Transaction Type") },
                                leadingIcon = { 
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTransactionTypeExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
//                                    .menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = isTransactionTypeExpanded,
                                onDismissRequest = { isTransactionTypeExpanded = false }
                            ) {
                                TransactionType.values().forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.name) },
                                        onClick = {
                                            transactionType = type
                                            isTransactionTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Amount field
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { 
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    amount = it
                                }
                            },
                            label = { Text("Amount (₹)") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(amountFocusRequester)
                        )
                        
                        // Description field
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Date field
                        OutlinedTextField(
                            value = dateFormat.format(transactionDate),
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Date") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        )
                        
                        // Category field
                        ExposedDropdownMenuBox(
                            expanded = isCategoryExpanded,
                            onExpandedChange = { isCategoryExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Category") },
                                leadingIcon = { 
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
//                                    .menuAnchor()
                            )
                            
                            if (category.isNotEmpty()) {
                                ExposedDropdownMenu(
                                    expanded = isCategoryExpanded,
                                    onDismissRequest = { isCategoryExpanded = false }
                                ) {
                                    categories
                                        .filter { it.contains(category, ignoreCase = true) }
                                        .forEach { filteredCategory ->
                                            DropdownMenuItem(
                                                text = { Text(filteredCategory) },
                                                onClick = {
                                                    category = filteredCategory
                                                    isCategoryExpanded = false
                                                }
                                            )
                                        }
                                }
                            }
                        }
                        
                        // Reference Number field
                        OutlinedTextField(
                            value = referenceNumber,
                            onValueChange = { referenceNumber = it },
                            label = { Text("Reference Number") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.Numbers,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Save button
                        Button(
                            onClick = {
                                val amountValue = if (amount.isNotEmpty()) {
                                    try {
                                        BigDecimal(amount)
                                    } catch (e: Exception) {
                                        BigDecimal.ZERO
                                    }
                                } else {
                                    BigDecimal.ZERO
                                }
                                
                                val transaction = Transaction(
                                    id = currentTransaction?.id ?: UUID.randomUUID().toString(),
                                    amount = amountValue,
                                    type = transactionType,
                                    description = description,
                                    date = transactionDate,
                                    status = TransactionStatus.COMPLETED,
                                    referenceNumber = referenceNumber.ifEmpty { null },
                                    categoryId = category.ifEmpty { null },
                                    accountId = null,
                                    payeeId = null
                                )
                                
                                viewModel.saveTransaction(transaction)
                                onNavigateBack()
                            },
                            enabled = description.isNotEmpty() && amount.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Transaction")
                        }
                    }
                    
                    // Date picker
                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = transactionDate.time
                        )
                        
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            transactionDate = Date(it)
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDatePicker = false }
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
    }
} 