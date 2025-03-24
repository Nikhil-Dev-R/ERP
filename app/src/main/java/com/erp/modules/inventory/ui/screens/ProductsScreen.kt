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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.ProductCategory
import com.erp.modules.inventory.data.model.ProductStatus
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit,
) {
    val products by viewModel.products.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var displayedProducts by remember(products, searchQuery) { 
        mutableStateOf(
            if (searchQuery.isBlank()) products 
            else products.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.sku.contains(searchQuery, ignoreCase = true) 
            }
        ) 
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
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
                            text = { Text("All Products") },
                            onClick = {
                                displayedProducts = products
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Low Stock Products") },
                            onClick = {
                                displayedProducts = products.filter { it.stockQuantity <= it.reorderLevel }
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Active Products") },
                            onClick = {
                                displayedProducts = products.filter { it.status == ProductStatus.ACTIVE }
                                showFilterMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Inactive Products") },
                            onClick = {
                                displayedProducts = products.filter { it.status == ProductStatus.INACTIVE }
                                showFilterMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.clearSelectedProduct()
                showEditDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
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
                placeholder = { Text("Search products by name or SKU") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No products available. Add a new product to get started.",
                        textAlign = TextAlign.Center
                    )
                }
            } else if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No products match your search criteria.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedProducts) { product ->
                        ProductItem(
                            product = product,
                            onProductClick = {
                                viewModel.selectProduct(it)
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        if (showEditDialog) {
            ProductEditDialog(
                product = selectedProduct,
                onDismiss = { showEditDialog = false },
                onSave = { product ->
                    viewModel.saveProduct(product)
                    showEditDialog = false
                }
            )
        }
        
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Product") },
                text = { Text("Are you sure you want to delete this product? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedProduct?.let { viewModel.deleteProduct(it) }
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
    }
}

@Composable
fun ProductItem(
    product: Product,
    onProductClick: (Product) -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    currencyFormat.currency = Currency.getInstance("INR")
    
    val stockColor = when {
        product.stockQuantity <= 0 -> MaterialTheme.colorScheme.error
        product.stockQuantity <= product.reorderLevel -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onProductClick(product) },
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
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    
                    if (product.category != null) {
                        Text(
                            text = product.category.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(product.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Stock: ",
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Text(
                            text = product.stockQuantity.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = stockColor
                        )
                    }
                    
                    val statusColor = when (product.status) {
                        ProductStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                        ProductStatus.INACTIVE -> MaterialTheme.colorScheme.outline
                        ProductStatus.LOW_STOCK -> MaterialTheme.colorScheme.error
                        ProductStatus.OUT_OF_STOCK -> MaterialTheme.colorScheme.error
                    }
                    
                    Text(
                        text = product.status.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun ProductEditDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var category by remember { mutableStateOf(product?.category ?: ProductCategory.GENERAL) }
    var status by remember { mutableStateOf(product?.status ?: ProductStatus.ACTIVE) }
    var stockQuantity by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "0") }
    var reorderLevel by remember { mutableStateOf(product?.reorderLevel?.toString() ?: "5") }
    var vendorId by remember { mutableStateOf(product?.vendorId ?: "") }
    
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }
    
    var nameError by remember { mutableStateOf("") }
    var skuError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var stockError by remember { mutableStateOf("") }
    var reorderError by remember { mutableStateOf("") }
    
    fun validateInputs(): Boolean {
        var isValid = true
        
        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = ""
        }
        
        if (sku.isBlank()) {
            skuError = "SKU is required"
            isValid = false
        } else {
            skuError = ""
        }
        
        try {
            val priceValue = price.toDouble()
            if (priceValue <= 0) {
                priceError = "Price must be greater than zero"
                isValid = false
            } else {
                priceError = ""
            }
        } catch (e: NumberFormatException) {
            priceError = "Please enter a valid price"
            isValid = false
        }
        
        try {
            val stockValue = stockQuantity.toInt()
            if (stockValue < 0) {
                stockError = "Stock quantity cannot be negative"
                isValid = false
            } else {
                stockError = ""
            }
        } catch (e: NumberFormatException) {
            stockError = "Please enter a valid stock quantity"
            isValid = false
        }
        
        try {
            val reorderValue = reorderLevel.toInt()
            if (reorderValue < 0) {
                reorderError = "Reorder level cannot be negative"
                isValid = false
            } else {
                reorderError = ""
            }
        } catch (e: NumberFormatException) {
            reorderError = "Please enter a valid reorder level"
            isValid = false
        }
        
        return isValid
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Add New Product" else "Edit Product") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError.isNotEmpty(),
                    supportingText = { if (nameError.isNotEmpty()) Text(nameError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = skuError.isNotEmpty(),
                    supportingText = { if (skuError.isNotEmpty()) Text(skuError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = priceError.isNotEmpty(),
                    supportingText = { if (priceError.isNotEmpty()) Text(priceError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = stockQuantity,
                    onValueChange = { stockQuantity = it },
                    label = { Text("Stock Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = stockError.isNotEmpty(),
                    supportingText = { if (stockError.isNotEmpty()) Text(stockError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = reorderLevel,
                    onValueChange = { reorderLevel = it },
                    label = { Text("Reorder Level") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = reorderError.isNotEmpty(),
                    supportingText = { if (reorderError.isNotEmpty()) Text(reorderError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = {},
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showCategoryDropdown = true }) {
                                Icon(Icons.Default.FilterList, contentDescription = "Select Category")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false }
                    ) {
                        ProductCategory.values().forEach { productCategory ->
                            DropdownMenuItem(
                                text = { Text(productCategory.name) },
                                onClick = {
                                    category = productCategory
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
                
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
                        ProductStatus.values().forEach { productStatus ->
                            DropdownMenuItem(
                                text = { Text(productStatus.name) },
                                onClick = {
                                    status = productStatus
                                    showStatusDropdown = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = vendorId,
                    onValueChange = { vendorId = it },
                    label = { Text("Vendor ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateInputs()) {
                        val updatedProduct = Product(
                            id = product?.id ?: "",
                            name = name,
                            sku = sku,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            status = status,
                            stockQuantity = stockQuantity.toIntOrNull() ?: 0,
                            reorderLevel = reorderLevel.toIntOrNull() ?: 5,
                            vendorId = vendorId,
                            lastRestockDate = product?.lastRestockDate,
                            createdAt = product?.createdAt ?: Date(),
                            updatedAt = Date()
                        )
                        onSave(updatedProduct)
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