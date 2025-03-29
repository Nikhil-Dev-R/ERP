package com.erp.modules.inventory.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.data.model.VendorStatus
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreen(
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit
) {
    val vendors by viewModel.vendors.collectAsState()
    val selectedVendor by viewModel.selectedVendor.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var displayedVendors by remember(vendors, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) vendors
            else vendors.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.contactPerson.contains(searchQuery, ignoreCase = true)
            }
        )
    }
    
    var showStatusActionDialog by remember { mutableStateOf(false) }
    var statusActionType by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendors") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Vendors") },
                            onClick = {
                                displayedVendors = vendors
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Active Vendors") },
                            onClick = {
                                displayedVendors = vendors.filter { it.status == VendorStatus.ACTIVE }
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Inactive Vendors") },
                            onClick = {
                                displayedVendors = vendors.filter { it.status == VendorStatus.INACTIVE }
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Blacklisted Vendors") },
                            onClick = {
                                displayedVendors = vendors.filter { it.status == VendorStatus.BLACKLISTED }
                                showFilterMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.clearSelectedVendor()
                showEditDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Vendor")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search vendors by name, email or contact person") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (vendors.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No vendors available. Add a new vendor to get started.",
                        textAlign = TextAlign.Center
                    )
                }
            } else if (displayedVendors.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No vendors match your search criteria.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedVendors) { vendor ->
                        VendorCard(
                            vendor = vendor,
                            onEdit = {
                                viewModel.selectVendor(vendor)
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.selectVendor(vendor)
                                showDeleteConfirmation = true
                            },
                            onActivate = {
                                viewModel.selectVendor(vendor)
                                statusActionType = "activate"
                                showStatusActionDialog = true
                            },
                            onDeactivate = {
                                viewModel.selectVendor(vendor)
                                statusActionType = "deactivate"
                                showStatusActionDialog = true
                            },
                            onBlacklist = {
                                viewModel.selectVendor(vendor)
                                statusActionType = "blacklist"
                                showStatusActionDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        if (showEditDialog) {
            VendorEditDialog(
                vendor = selectedVendor,
                onDismiss = { showEditDialog = false },
                onSave = { vendor ->
                    viewModel.saveVendor(vendor)
                    showEditDialog = false
                }
            )
        }
        
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Vendor") },
                text = { Text("Are you sure you want to delete this vendor? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedVendor?.let { viewModel.deleteVendor(it) }
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        if (showStatusActionDialog) {
            val title = when (statusActionType) {
                "activate" -> "Activate Vendor"
                "deactivate" -> "Deactivate Vendor"
                "blacklist" -> "Blacklist Vendor"
                else -> "Change Vendor Status"
            }
            
            val message = when (statusActionType) {
                "activate" -> "Are you sure you want to activate this vendor? They will be able to supply products."
                "deactivate" -> "Are you sure you want to deactivate this vendor? They will be temporarily unavailable for new orders."
                "blacklist" -> "Are you sure you want to blacklist this vendor? This indicates serious issues with the vendor."
                else -> "Are you sure you want to change the status of this vendor?"
            }
            
            AlertDialog(
                onDismissRequest = { showStatusActionDialog = false },
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedVendor?.let { vendor ->
                                when (statusActionType) {
                                    "activate" -> viewModel.activateVendor(vendor)
                                    "deactivate" -> viewModel.deactivateVendor(vendor)
                                    "blacklist" -> viewModel.blacklistVendor(vendor)
                                }
                            }
                            showStatusActionDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStatusActionDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun VendorCard(
    vendor: Vendor,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    onBlacklist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vendor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Contact: ${vendor.contactPerson}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = vendor.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column {
                    Text(
                        text = "Phone",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = vendor.phone,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column {
                    Text(
                        text = "Country",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = vendor.country,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row {
                        Text(
                            text = "${vendor.rating}/5",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                val statusColor = when (vendor.status) {
                    VendorStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    VendorStatus.INACTIVE -> MaterialTheme.colorScheme.outline
                    VendorStatus.BLACKLISTED -> MaterialTheme.colorScheme.error
                }
                
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = vendor.status.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (vendor.status) {
                    VendorStatus.INACTIVE -> {
                        TextButton(onClick = onActivate) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Activate")
                        }
                    }
                    VendorStatus.ACTIVE -> {
                        TextButton(onClick = onDeactivate) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deactivate")
                        }
                    }
                    else -> {} // No action for blacklisted vendors
                }
                
                if (vendor.status != VendorStatus.BLACKLISTED) {
                    TextButton(onClick = onBlacklist) {
                        Icon(Icons.Default.Block, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Blacklist")
                    }
                }
            }
        }
    }
}

@Composable
fun VendorEditDialog(
    vendor: Vendor?,
    onDismiss: () -> Unit,
    onSave: (Vendor) -> Unit
) {
    var name by remember { mutableStateOf(vendor?.name ?: "") }
    var email by remember { mutableStateOf(vendor?.email ?: "") }
    var phone by remember { mutableStateOf(vendor?.phone ?: "") }
    var address by remember { mutableStateOf(vendor?.address ?: "") }
    var country by remember { mutableStateOf(vendor?.country ?: "") }
    var contactPerson by remember { mutableStateOf(vendor?.contactPerson ?: "") }
    var website by remember { mutableStateOf(vendor?.website ?: "") }
    var notes by remember { mutableStateOf(vendor?.notes ?: "") }
    var ratingString by remember { mutableStateOf(vendor?.rating?.toString() ?: "3.0") }
    var status by remember { mutableStateOf(vendor?.status ?: VendorStatus.ACTIVE) }
    
    var showStatusDropdown by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var ratingError by remember { mutableStateOf("") }
    
    fun validateInputs(): Boolean {
        var isValid = true
        
        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = ""
        }
        
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = ""
        }
        
        if (phone.isBlank()) {
            phoneError = "Phone is required"
            isValid = false
        } else {
            phoneError = ""
        }
        
        try {
            val ratingValue = ratingString.toFloat()
            if (ratingValue < 0 || ratingValue > 5) {
                ratingError = "Rating must be between 0 and 5"
                isValid = false
            } else {
                ratingError = ""
            }
        } catch (e: NumberFormatException) {
            ratingError = "Please enter a valid rating"
            isValid = false
        }
        
        return isValid
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (vendor == null) "Add New Vendor" else "Edit Vendor") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Vendor Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError.isNotEmpty(),
                    supportingText = { if (nameError.isNotEmpty()) Text(nameError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError.isNotEmpty(),
                    supportingText = { if (emailError.isNotEmpty()) Text(emailError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError.isNotEmpty(),
                    supportingText = { if (phoneError.isNotEmpty()) Text(phoneError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = contactPerson,
                    onValueChange = { contactPerson = it },
                    label = { Text("Contact Person") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = ratingString,
                    onValueChange = { ratingString = it },
                    label = { Text("Rating (0-5)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = ratingError.isNotEmpty(),
                    supportingText = { if (ratingError.isNotEmpty()) Text(ratingError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = status.name,
                        onValueChange = {},
                        label = { Text("Status") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showStatusDropdown = true }) {
                                Icon(Icons.Default.FilterList, contentDescription = "Select Status")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        VendorStatus.values().forEach { vendorStatus ->
                            DropdownMenuItem(
                                text = { Text(vendorStatus.name) },
                                onClick = {
                                    status = vendorStatus
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateInputs()) {
                        val updatedVendor = Vendor(
                            id = vendor?.id ?: "",
                            name = name,
                            email = email,
                            phone = phone,
                            address = address,
                            country = country,
                            contactPerson = contactPerson,
                            website = website,
                            notes = notes,
                            rating = ratingString.toFloatOrNull() ?: 3.0f,
                            totalRatings = vendor?.totalRatings ?: 0,
                            status = status,
                            blacklistReason = vendor?.blacklistReason,
                            createdAt = vendor?.createdAt ?: Date(),
                            updatedAt = Date()
                        )
                        onSave(updatedVendor)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 