package com.erp.modules.inventory.data.repository

import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.inventory.data.dao.VendorDao
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.data.model.VendorStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class VendorRepository(
    private val vendorDao: VendorDao,
    private val firebaseService: FirebaseService<Vendor>
) : Repository<Vendor> {
    
    override suspend fun getById(id: String): Vendor? {
        return vendorDao.getById(id) ?: firebaseService.getById(id)?.also {
            vendorDao.insert(it)
        }
    }
    
    override fun getAll(): Flow<List<Vendor>> {
        return vendorDao.getAll()
    }
    
    override suspend fun insert(item: Vendor): String {
        vendorDao.insert(item)
        return firebaseService.insert(item)
    }
    
    override suspend fun update(item: Vendor) {
        val updatedItem = item.copy(updatedAt = Date())
        vendorDao.update(updatedItem)
        firebaseService.update(updatedItem)
    }
    
    override suspend fun delete(item: Vendor) {
        vendorDao.delete(item)
        firebaseService.delete(item.id)
    }
    
    override suspend fun deleteById(id: String) {
        vendorDao.deleteById(id)
        firebaseService.delete(id)
    }
    
    fun searchByName(query: String): Flow<List<Vendor>> {
        return vendorDao.searchByName(query)
    }
    
    fun getByEmail(email: String): Flow<Vendor?> {
        return vendorDao.getByEmail(email)
    }
    
    fun getByStatus(status: VendorStatus): Flow<List<Vendor>> {
        return vendorDao.getByStatus(status)
    }
    
    fun getByCountry(country: String): Flow<List<Vendor>> {
        return vendorDao.getByCountry(country)
    }
    
    fun getByMinRating(minRating: Float): Flow<List<Vendor>> {
        return vendorDao.getAll().map { vendors ->
            vendors.filter { it.rating >= minRating }
        }
    }
    
    fun getActiveVendors(): Flow<List<Vendor>> {
        return vendorDao.getByStatus(VendorStatus.ACTIVE)
    }
    
    suspend fun activateVendor(id: String) {
        getById(id)?.let { vendor ->
            update(vendor.copy(status = VendorStatus.ACTIVE))
        }
    }
    
    suspend fun deactivateVendor(id: String) {
        getById(id)?.let { vendor ->
            update(vendor.copy(status = VendorStatus.INACTIVE))
        }
    }
    
    suspend fun blacklistVendor(id: String, reason: String) {
        getById(id)?.let { vendor ->
            update(vendor.copy(
                status = VendorStatus.BLACKLISTED,
                blacklistReason = reason
            ))
        }
    }
    
    suspend fun rateVendor(id: String, rating: Float) {
        getById(id)?.let { vendor ->
            // Update vendor rating as a weighted average of previous rating and new rating
            val totalRatings = vendor.totalRatings
            val newTotalRatings = totalRatings + 1
            val newRating = (vendor.rating * totalRatings + rating) / newTotalRatings
            
            update(vendor.copy(
                rating = newRating,
                totalRatings = newTotalRatings
            ))
        }
    }
} 