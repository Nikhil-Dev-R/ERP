package com.erp.modules.inventory.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.erp.common.data.BaseDao
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.ProductStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao : BaseDao<Product> {
    @Query("SELECT * FROM products WHERE id = :id")
    override suspend fun getById(id: String): Product?
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    override fun getAll(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun searchByName(name: String): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getByCategory(category: String): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE status = :status ORDER BY name ASC")
    fun getByStatus(status: ProductStatus): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE vendorId = :vendorId ORDER BY name ASC")
    fun getByVendor(vendorId: String): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE stockQuantity <= reorderLevel ORDER BY stockQuantity ASC")
    fun getLowStockProducts(): Flow<List<Product>>
    
    @Query("UPDATE products SET stockQuantity = stockQuantity + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: String, quantity: Int)
    
    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId AND stockQuantity >= :quantity")
    suspend fun decreaseStock(productId: String, quantity: Int): Int
    
    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)
} 