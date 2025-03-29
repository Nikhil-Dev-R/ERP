package com.erp.modules.inventory.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

val LocalInventoryViewModel = compositionLocalOf<InventoryViewModel> { error("No ViewModel provided") }

@Composable
fun InventoryDashboardScreen(
    viewModel: InventoryViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToVendors: () -> Unit,
    onAddProduct: () -> Unit,
    onAddVendor: () -> Unit,
    onNavigateBack: () -> Boolean
) {
    val products by viewModel.products.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()
    val vendors by viewModel.vendors.collectAsState()
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    CompositionLocalProvider(LocalInventoryViewModel provides viewModel) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = if (selectedTabIndex == 0) onAddProduct else onAddVendor
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = if (selectedTabIndex == 0) "Add Product" else "Add Vendor")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = "Inventory Management",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
                
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Products") },
                        icon = { Icon(Icons.Default.Warehouse, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Vendors") },
                        icon = { Icon(Icons.Default.Business, contentDescription = null) }
                    )
                }
                
                when (selectedTabIndex) {
                    0 -> ProductsTab(
                        products = products,
                        lowStockProducts = lowStockProducts,
                        onViewAllProducts = onNavigateToProducts
                    )
                    1 -> VendorsTab(
                        vendors = vendors,
                        onViewAllVendors = onNavigateToVendors
                    )
                }
            }
        }
    }
}

@Composable
fun ProductsTab(
    products: List<Product>,
    lowStockProducts: List<Product>,
    onViewAllProducts: () -> Unit
) {
    var showRestockDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val viewModel = LocalInventoryViewModel.current

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            InventorySummaryCard(products, lowStockProducts)
        }
        
        if (lowStockProducts.isNotEmpty()) {
            item {
                Text(
                    text = "Low Stock Items",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            items(lowStockProducts.take(3)) { product ->
                ProductItem(
                    product = product,
                    onRestock = {
                        selectedProduct = it
                        showRestockDialog = true
                    }
                )
            }
        }
        
        item {
            Text(
                text = "Recent Products",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        val recentProducts = products.sortedByDescending { it.createdAt }.take(5)
        items(recentProducts) { product ->
            ProductItem(product)
        }
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "View All Products",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { onViewAllProducts() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    if (showRestockDialog) {
        RestockDialog(
            product = selectedProduct,
            onDismiss = { showRestockDialog = false },
            onRestock = { productId, quantity ->
                viewModel.increaseProductStock(productId, quantity)
                showRestockDialog = false
            }
        )
    }
}

@Composable
fun VendorsTab(
    vendors: List<Vendor>,
    onViewAllVendors: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            VendorSummaryCard(vendors)
        }
        
        item {
            Text(
                text = "Active Vendors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        val activeVendors = vendors.filter { it.status == com.erp.modules.inventory.data.model.VendorStatus.ACTIVE }
            .sortedByDescending { it.createdAt }
            .take(5)
        
        if (activeVendors.isNotEmpty()) {
            items(activeVendors) { vendor ->
                VendorItem(vendor)
            }
        } else {
            item {
                Text(
                    text = "No active vendors",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
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
                    text = "View All Vendors",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { onViewAllVendors() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InventorySummaryCard(products: List<Product>, lowStockProducts: List<Product>) {
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
                text = "Inventory Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val totalProducts = products.size
            val totalStock = products.sumOf { it.stockQuantity }
            val lowStockCount = lowStockProducts.size
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Products",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warehouse,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = totalProducts.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "Low Stock Items",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = lowStockCount.toString(),
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
                    text = "Total Stock Quantity",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = totalStock.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun VendorSummaryCard(vendors: List<Vendor>) {
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
                text = "Vendor Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val totalVendors = vendors.size
            val activeVendors = vendors.count { it.status == com.erp.modules.inventory.data.model.VendorStatus.ACTIVE }
            val inactiveVendors = vendors.count { it.status == com.erp.modules.inventory.data.model.VendorStatus.INACTIVE }
            val blacklistedVendors = vendors.count { it.status == com.erp.modules.inventory.data.model.VendorStatus.BLACKLISTED }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Vendors",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = totalVendors.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = "Active Vendors",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = activeVendors.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Inactive Vendors",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = inactiveVendors.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = "Blacklisted",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = blacklistedVendors.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onRestock: (Product) -> Unit = {}
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    currencyFormat.currency = Currency.getInstance("INR")
    
    ElevatedCard(
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
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(product.price),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val stockColor = when {
                        product.stockQuantity <= 0 -> MaterialTheme.colorScheme.error
                        product.stockQuantity <= product.reorderLevel -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    }
                    
                    Text(
                        text = "Stock: ${product.stockQuantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = stockColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onRestock(product) }
                ) {
                    Text("Restock")
                }
            }
        }
    }
}

@Composable
fun VendorItem(vendor: Vendor) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vendor.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Contact: ${vendor.contactPerson}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = vendor.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            val statusColor = when (vendor.status) {
                com.erp.modules.inventory.data.model.VendorStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                com.erp.modules.inventory.data.model.VendorStatus.INACTIVE -> MaterialTheme.colorScheme.outline
                com.erp.modules.inventory.data.model.VendorStatus.BLACKLISTED -> MaterialTheme.colorScheme.error
            }
            
            Text(
                text = vendor.status.name,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RestockDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onRestock: (String, Int) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf("") }
    
    fun validateInput(): Boolean {
        var isValid = true
        
        if (quantity.isBlank()) {
            quantityError = "Quantity is required"
            isValid = false
        } else {
            val qty = quantity.toIntOrNull()
            if (qty == null || qty <= 0) {
                quantityError = "Please enter a valid quantity"
                isValid = false
            } else {
                quantityError = ""
            }
        }
        
        return isValid
    }
    
    if (product == null) {
        onDismiss()
        return
    }
    
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restock Product") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Product: ${product.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Current Stock: ${product.stockQuantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                androidx.compose.material3.OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Add Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = quantityError.isNotEmpty(),
                    supportingText = { if (quantityError.isNotEmpty()) Text(quantityError) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validateInput()) {
                        onRestock(product.id, quantity.toInt())
                    }
                }
            ) {
                Text("Add Stock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}