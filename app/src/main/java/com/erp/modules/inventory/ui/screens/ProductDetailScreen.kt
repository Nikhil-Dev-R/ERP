package com.erp.modules.inventory.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.ProductCategory
import com.erp.modules.inventory.data.model.ProductStatus
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: InventoryViewModel,
    productId: String? = null,
    onNavigateBack: () -> Unit
) {
    val allVendors by viewModel.vendors.collectAsState()

    // States for form fields
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var price by remember { mutableDoubleStateOf(0.0) }
    var stockQuantity by remember { mutableIntStateOf(0) }
    var reorderLevel by remember { mutableIntStateOf(10) }
    var category by remember { mutableStateOf(ProductCategory.GENERAL) }
    var status by remember { mutableStateOf(ProductStatus.ACTIVE) }
    var vendorId by remember { mutableStateOf("") }
    
    // Dropdown state for vendors
    var isVendorExpanded by remember { mutableStateOf(false) }
    var selectedVendorName by remember { mutableStateOf("Select a vendor") }
    
    // Dropdown state for status
    var isStatusExpanded by remember { mutableStateOf(false) }
    
    // Dropdown state for category
    var isCategoryExpanded by remember { mutableStateOf(false) }
    
    // Load existing product data if editing
    LaunchedEffect(productId) {
        if (productId != null) {
            // If product ID is provided, we're in edit mode
            viewModel.getProductById(productId)?.let { product ->
                name = product.name
                description = product.description
                sku = product.sku
                price = product.price
                stockQuantity = product.stockQuantity
                reorderLevel = product.reorderLevel
                category = product.category
                status = product.status
                vendorId = product.vendorId
                
                // Set selected vendor name
                allVendors.find { it.id == vendorId }?.let {
                    selectedVendorName = it.name
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId != null) "Edit Product" else "Add Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (name.isNotBlank() && sku.isNotBlank() && price > 0) {
                        // Always generate a new UUID for new products
                        val newId = if (productId == null) UUID.randomUUID().toString() else productId
                        
                        val product = Product(
                            id = newId,
                            name = name,
                            description = description,
                            sku = sku,
                            price = price,
                            stockQuantity = stockQuantity,
                            reorderLevel = reorderLevel,
                            category = category,
                            status = status,
                            vendorId = vendorId,
                            createdAt = Date()
                        )
                        
                        viewModel.saveProduct(product)
                        onNavigateBack()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Save"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Product Details",
                style = MaterialTheme.typography.titleLarge
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            
            OutlinedTextField(
                value = sku,
                onValueChange = { sku = it },
                label = { Text("SKU") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = if (price == 0.0) "" else price.toString(),
                onValueChange = { 
                    price = it.toDoubleOrNull() ?: 0.0
                },
                label = { Text("Price (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("₹") },
                singleLine = true
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = if (stockQuantity == 0) "" else stockQuantity.toString(),
                    onValueChange = { 
                        stockQuantity = it.toIntOrNull() ?: 0
                    },
                    label = { Text("Stock Quantity") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = if (reorderLevel == 0) "" else reorderLevel.toString(),
                    onValueChange = { 
                        reorderLevel = it.toIntOrNull() ?: 0
                    },
                    label = { Text("Reorder Level") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            
            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = isCategoryExpanded,
                onExpandedChange = { isCategoryExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
//                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false }
                ) {
                    ProductCategory.values().forEach { categoryOption ->
                        DropdownMenuItem(
                            text = { Text(categoryOption.name) },
                            onClick = { 
                                category = categoryOption
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Status dropdown
            ExposedDropdownMenuBox(
                expanded = isStatusExpanded,
                onExpandedChange = { isStatusExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
//                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = isStatusExpanded,
                    onDismissRequest = { isStatusExpanded = false }
                ) {
                    ProductStatus.values().forEach { statusOption ->
                        DropdownMenuItem(
                            text = { Text(statusOption.name) },
                            onClick = { 
                                status = statusOption
                                isStatusExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Vendor dropdown
            ExposedDropdownMenuBox(
                expanded = isVendorExpanded,
                onExpandedChange = { isVendorExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedVendorName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vendor") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isVendorExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
//                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = isVendorExpanded,
                    onDismissRequest = { isVendorExpanded = false }
                ) {
                    allVendors.forEach { vendor ->
                        DropdownMenuItem(
                            text = { Text(vendor.name) },
                            onClick = { 
                                vendorId = vendor.id ?: ""
                                selectedVendorName = vendor.name
                                isVendorExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    // Always generate a new UUID for new products
                    val newId = if (productId == null) UUID.randomUUID().toString() else productId
                    
                    val product = Product(
                        id = newId,
                        name = name,
                        description = description,
                        sku = sku,
                        price = price,
                        stockQuantity = stockQuantity,
                        reorderLevel = reorderLevel,
                        category = category,
                        status = status,
                        vendorId = vendorId,
                        createdAt = Date()
                    )
                    
                    viewModel.saveProduct(product)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && sku.isNotBlank() && price > 0
            ) {
                Text(if (productId != null) "Update Product" else "Add Product")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
} 