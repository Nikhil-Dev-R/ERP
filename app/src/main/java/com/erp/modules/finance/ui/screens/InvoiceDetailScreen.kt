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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceItem
import com.erp.modules.finance.data.model.InvoiceStatus
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.finance.ui.viewmodel.InvoiceDetailState
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    viewModel: FinanceViewModel,
    invoiceId: String?,
    onNavigateBack: () -> Unit
) {
    val invoiceDetailState by viewModel.invoiceDetailState.collectAsState()
    val currentInvoice by viewModel.currentInvoice.collectAsState()
    
    // Form state variables
    var invoiceNumber by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var issueDate by remember { mutableStateOf(Date()) }
    var dueDate by remember { mutableStateOf(Date()) }
    var status by remember { mutableStateOf(InvoiceStatus.DRAFT) }
    var notes by remember { mutableStateOf("") }

    val invoiceItems = remember { mutableStateListOf<InvoiceItem>() }

    var showIssueDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    var isStatusExpanded by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Load existing invoice if editing
    LaunchedEffect(key1 = invoiceId) {
        if (invoiceId != null) {
            viewModel.getInvoiceDetail(invoiceId)
        } else {
            viewModel.createNewInvoice()
        }
    }

    // Update form with current invoice data
    LaunchedEffect(key1 = currentInvoice) {
        currentInvoice?.let { invoice ->
            invoiceNumber = invoice.invoiceNumber
            customerName = invoice.customerName
            amount = invoice.amount.toString()
            issueDate = invoice.issueDate
            dueDate = invoice.dueDate
            status = invoice.status
            notes = invoice.notes ?: ""
            invoiceItems.clear()
            invoice.items?.let { items ->
                invoiceItems.addAll(items)
            }
        }
    }

    // Generate a default invoice number for new invoices
    LaunchedEffect(key1 = invoiceId, key2 = invoiceNumber) {
        if (invoiceId == null && invoiceNumber.isEmpty()) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val random = (1000..9999).random()
            invoiceNumber = "INV-$year$month$day-$random"
        }
    }

    // Calculate the total amount based on invoice items
    fun calculateTotalAmount(): BigDecimal {
        return if (invoiceItems.isNotEmpty()) {
            invoiceItems.fold(BigDecimal.ZERO) { acc, item ->
                acc.add(item.unitPrice.multiply(BigDecimal(item.quantity)))
            }
        } else {
            try {
                if (amount.isNotEmpty()) BigDecimal(amount) else BigDecimal.ZERO
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (invoiceId == null) "New Invoice" else "Edit Invoice") },
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
            when (invoiceDetailState) {
                is InvoiceDetailState.Loading -> {
                    if (invoiceId != null) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                is InvoiceDetailState.Error -> {
                    val errorState = invoiceDetailState as InvoiceDetailState.Error
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
                        // Invoice number field
                        OutlinedTextField(
                            value = invoiceNumber,
                            onValueChange = { invoiceNumber = it },
                            label = { Text("Invoice Number") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Numbers,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Customer name field
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Issue date field
                        OutlinedTextField(
                            value = dateFormat.format(issueDate),
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Issue Date") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showIssueDatePicker = true }
                        )

                        // Due date field
                        OutlinedTextField(
                            value = dateFormat.format(dueDate),
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Due Date") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDueDatePicker = true }
                        )

                        // Status selection
                        ExposedDropdownMenuBox(
                            expanded = isStatusExpanded,
                            onExpandedChange = { isStatusExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = status.name,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Status") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
//                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = isStatusExpanded,
                                onDismissRequest = { isStatusExpanded = false }
                            ) {
                                InvoiceStatus.entries.forEach { invoiceStatus ->
                                    DropdownMenuItem(
                                        text = { Text(invoiceStatus.name) },
                                        onClick = {
                                            status = invoiceStatus
                                            isStatusExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Invoice Items
                        Text(
                            text = "Invoice Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        if (invoiceItems.isEmpty()) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "No items added yet",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Manual amount input if no items
                                    OutlinedTextField(
                                        value = amount,
                                        onValueChange = {
                                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                                amount = it
                                            }
                                        },
                                        label = { Text("Invoice Amount (₹)") },
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
                            }
                        } else {
                            invoiceItems.forEachIndexed { index, item ->
                                InvoiceItemCard(
                                    item = item,
                                    onDelete = { invoiceItems.removeAt(index) }
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Total amount
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Amount:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = CurrencyFormatter.formatAsRupees(calculateTotalAmount()),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Add Item Button
                        Button(
                            onClick = {
                                val newItem = InvoiceItem(
                                    id = UUID.randomUUID().toString(),
                                    description = "New Item",
                                    quantity = 1,
                                    unitPrice = BigDecimal("0.00")
                                )
                                invoiceItems.add(newItem)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Item")
                        }

                        // Notes field
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save button
                        Button(
                            onClick = {
                                val finalAmount = if (invoiceItems.isNotEmpty()) {
                                    calculateTotalAmount()
                                } else {
                                    try {
                                        BigDecimal(amount)
                                    } catch (e: Exception) {
                                        BigDecimal.ZERO
                                    }
                                }

                                val invoice = Invoice(
                                    id = currentInvoice?.id ?: UUID.randomUUID().toString(),
                                    invoiceNumber = invoiceNumber,
                                    customerId = currentInvoice?.customerId ?: "",
                                    customerName = customerName,
                                    amount = finalAmount,
                                    issueDate = issueDate,
                                    dueDate = dueDate,
                                    status = status,
                                    notes = notes.ifEmpty { null },
                                    items = if (invoiceItems.isNotEmpty()) invoiceItems.toList() else null
                                )

                                viewModel.saveInvoice(invoice)
                                onNavigateBack()
                            },
                            enabled = invoiceNumber.isNotEmpty() && customerName.isNotEmpty() &&
                                    (amount.isNotEmpty() || invoiceItems.isNotEmpty()),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Invoice")
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Issue Date picker
                    if (showIssueDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = issueDate.time
                        )

                        DatePickerDialog(
                            onDismissRequest = { showIssueDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            issueDate = Date(it)
                                        }
                                        showIssueDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showIssueDatePicker = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    // Due Date picker
                    if (showDueDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = dueDate.time
                        )

                        DatePickerDialog(
                            onDismissRequest = { showDueDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            dueDate = Date(it)
                                        }
                                        showDueDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDueDatePicker = false }
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

@Composable
fun InvoiceItemCard(
    item: InvoiceItem,
    onDelete: () -> Unit
) {
    var description by remember { mutableStateOf(item.description) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var unitPrice by remember { mutableStateOf(item.unitPrice.toString()) }

    // Make sure to update the item object when these values change
    LaunchedEffect(description, quantity, unitPrice) {
        // This will update the item passed in without direct reassignment
        // The parent component should handle applying these changes
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "Item Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                },
                label = { Text("Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                            quantity = it
                        }
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(0.5f)
                )

                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            unitPrice = it
                        }
                    },
                    label = { Text("Unit Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal: ",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = CurrencyFormatter.formatAsRupees(
                        item.unitPrice.multiply(BigDecimal(item.quantity))
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}