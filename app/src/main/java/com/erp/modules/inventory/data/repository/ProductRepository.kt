package com.erp.modules.inventory.data.repository

import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.inventory.data.dao.ProductDao
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.ProductStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date
import android.util.Log

class ProductRepository(
    private val productDao: ProductDao,
    private val firebaseService: FirebaseService<Product>
) : Repository<Product> {
    
    override suspend fun getById(id: String): Product? {
        return productDao.getById(id) ?: firebaseService.getById(id)?.also {
            productDao.insert(it)
        }
    }
    
    override fun getAll(): Flow<List<Product>> {
        return productDao.getAll()
    }
    
    override suspend fun insert(item: Product): String {
        // Log the operation
        Log.d("ProductRepository", "Inserting product: ${item.name} with ID: ${item.id}")
        
        // Insert into local database
        productDao.insert(item)
        
        // Insert into Firebase
        try {
            firebaseService.insert(item)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error inserting product to Firebase: ${e.message}", e)
        }
        
        return item.id
    }
    
    override suspend fun update(item: Product) {
        // Log the operation
        Log.d("ProductRepository", "Updating product: ${item.name} with ID: ${item.id}")
        
        // Update the timestamp
        val updatedItem = item.copy(updatedAt = Date())
        
        // Update local database
        productDao.update(updatedItem)
        
        // Update Firebase
        try {
            firebaseService.update(updatedItem)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error updating product in Firebase: ${e.message}", e)
        }
    }
    
    override suspend fun delete(item: Product) {
        productDao.delete(item)
        firebaseService.delete(item.id)
    }
    
    override suspend fun deleteById(id: String) {
        productDao.deleteById(id)
        firebaseService.delete(id)
    }
    
    suspend fun getBySku(sku: String): Product? {
        return productDao.searchByName(sku).let { flow ->
            var result: Product? = null
            flow.collect { products ->
                result = products.firstOrNull { it.sku == sku }
            }
            result
        }
    }
    
    fun searchByName(query: String): Flow<List<Product>> {
        return productDao.searchByName(query)
    }
    
    fun getByCategory(categoryId: String): Flow<List<Product>> {
        return productDao.getByCategory(categoryId)
    }
    
    fun getLowStockProducts(): Flow<List<Product>> {
        return productDao.getLowStockProducts()
    }
    
    fun getByStatus(status: ProductStatus): Flow<List<Product>> {
        return productDao.getByStatus(status)
    }
    
    fun getByVendor(vendorId: String): Flow<List<Product>> {
        return productDao.getByVendor(vendorId)
    }
    
    suspend fun increaseStock(id: String, quantity: Int) {
        productDao.increaseStock(id, quantity)
        // Update product in Firebase
        getById(id)?.let { product ->
            val updatedProduct = product.copy(
                stockQuantity = product.stockQuantity + quantity,
                lastRestockDate = Date(),
                status = if (product.stockQuantity + quantity > product.reorderLevel) ProductStatus.ACTIVE else product.status
            )
            update(updatedProduct)
        }
    }
    
    suspend fun decreaseStock(id: String, quantity: Int): Boolean {
        val result = productDao.decreaseStock(id, quantity)
        if (result > 0) {
            // Update product in Firebase
            getById(id)?.let { product ->
                val newStock = product.stockQuantity - quantity
                val newStatus = when {
                    newStock <= 0 -> ProductStatus.OUT_OF_STOCK
                    newStock <= product.reorderLevel -> ProductStatus.LOW_STOCK
                    else -> product.status
                }
                val updatedProduct = product.copy(
                    stockQuantity = newStock,
                    status = newStatus
                )
                update(updatedProduct)
            }
            return true
        }
        return false
    }
} 