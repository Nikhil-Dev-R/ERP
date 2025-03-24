package com.erp.modules.inventory.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.erp.common.data.BaseDao
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.data.model.VendorStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao : BaseDao<Vendor> {
    @Query("SELECT * FROM vendors WHERE id = :id")
    override suspend fun getById(id: String): Vendor?
    
    @Query("SELECT * FROM vendors ORDER BY name ASC")
    override fun getAll(): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE email = :email LIMIT 1")
    fun getByEmail(email: String): Flow<Vendor?>
    
    @Query("SELECT * FROM vendors WHERE name LIKE '%' || :name || '%' ORDER BY name ASC")
    fun searchByName(name: String): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE status = :status ORDER BY name ASC")
    fun getByStatus(status: VendorStatus): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE country = :country ORDER BY name ASC")
    fun getByCountry(country: String): Flow<List<Vendor>>
    
    @Query("SELECT * FROM vendors WHERE rating >= :minRating ORDER BY rating DESC")
    fun getByMinimumRating(minRating: Float): Flow<List<Vendor>>
    
    @Query("UPDATE vendors SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: VendorStatus)
    
    @Query("DELETE FROM vendors WHERE id = :id")
    suspend fun deleteById(id: String)
} 