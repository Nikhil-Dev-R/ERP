package com.erp.modules.inventory.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.ProductStatus
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.data.model.VendorStatus
import com.erp.modules.inventory.data.repository.ProductRepository
import com.erp.modules.inventory.data.repository.VendorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class InventoryViewModel(
    private val productRepository: ProductRepository,
    private val vendorRepository: VendorRepository
) : ViewModel() {
    
    // Products
    val products = productRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val lowStockProducts = productRepository.getLowStockProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()
    
    // Vendors
    val vendors = vendorRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val activeVendors = vendorRepository.getByStatus(VendorStatus.ACTIVE)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedVendor = MutableStateFlow<Vendor?>(null)
    val selectedVendor = _selectedVendor.asStateFlow()
    
    // Product operations
    fun searchProductsByName(query: String): StateFlow<List<Product>> {
        return productRepository.searchByName(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getProductsByCategory(categoryId: String): StateFlow<List<Product>> {
        return productRepository.getByCategory(categoryId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getProductsByStatus(status: ProductStatus): StateFlow<List<Product>> {
        return productRepository.getByStatus(status)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getProductsByVendor(vendorId: String): StateFlow<List<Product>> {
        return productRepository.getByVendor(vendorId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }
    
    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
    
    fun saveProduct(product: Product) {
        viewModelScope.launch {
            try {
                if (product.id.isBlank()) {
                    // Generate a new ID for new products
                    val newId = UUID.randomUUID().toString()
                    val newProduct = product.copy(id = newId)
                    Log.d("InventoryViewModel", "Inserting new product with ID: $newId")
                    productRepository.insert(newProduct)
                } else {
                    Log.d("InventoryViewModel", "Updating existing product with ID: ${product.id}")
                    productRepository.update(product)
                }
            } catch (e: Exception) {
                Log.e("InventoryViewModel", "Error saving product: ${e.message}", e)
            }
        }
    }
    
    fun increaseProductStock(productId: String, quantity: Int) {
        viewModelScope.launch {
            productRepository.increaseStock(productId, quantity)
        }
    }
    
    fun decreaseProductStock(productId: String, quantity: Int): Boolean {
        var success = false
        viewModelScope.launch {
            success = productRepository.decreaseStock(productId, quantity)
        }
        return success
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.delete(product)
        }
    }
    
    // Vendor operations
    fun searchVendorsByName(query: String): StateFlow<List<Vendor>> {
        return vendorRepository.searchByName(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getVendorsByStatus(status: VendorStatus): StateFlow<List<Vendor>> {
        return vendorRepository.getByStatus(status)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getVendorsByCountry(country: String): StateFlow<List<Vendor>> {
        return vendorRepository.getByCountry(country)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getVendorsByMinRating(minRating: Float): StateFlow<List<Vendor>> {
        return vendorRepository.getByMinRating(minRating)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun selectVendor(vendor: Vendor) {
        _selectedVendor.value = vendor
    }
    
    fun clearSelectedVendor() {
        _selectedVendor.value = null
    }
    
    fun saveVendor(vendor: Vendor) {
        viewModelScope.launch {
            if (vendor.id.isEmpty()) {
                vendorRepository.insert(vendor)
            } else {
                vendorRepository.update(vendor)
            }
        }
    }
    
    fun activateVendor(vendor: Vendor) {
        viewModelScope.launch {
            vendorRepository.activateVendor(vendor.id)
        }
    }
    
    fun deactivateVendor(vendor: Vendor) {
        viewModelScope.launch {
            vendorRepository.deactivateVendor(vendor.id)
        }
    }
    
    fun blacklistVendor(vendor: Vendor) {
        viewModelScope.launch {
            vendorRepository.blacklistVendor(vendor.id, "Blacklisted through UI")
        }
    }
    
    fun deleteVendor(vendor: Vendor) {
        viewModelScope.launch {
            vendorRepository.delete(vendor)
        }
    }

    /**
     * Get a product by its ID
     */
    suspend fun getProductById(id: String): Product? {
        return productRepository.getById(id)
    }
} 