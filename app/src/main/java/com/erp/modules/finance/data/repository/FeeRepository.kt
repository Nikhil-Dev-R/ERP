package com.erp.modules.finance.data.repository

import com.erp.data.remote.FirebaseService
import com.erp.modules.finance.data.dao.FeeDao
import com.erp.modules.finance.data.model.Fee
import kotlinx.coroutines.flow.Flow

class FeeRepository(
    private val feeDao: FeeDao,
    private val firebaseService: FirebaseService<Fee>
) {
    // Local database operations
    fun getAllFees(): Flow<List<Fee>> = feeDao.getAllFees()
    
    fun getFeeById(id: String): Flow<Fee> = feeDao.getFeeById(id)
    
    fun getFeesByStudent(studentId: String): Flow<List<Fee>> = feeDao.getFeesByStudent(studentId)
    
    fun getFeesByType(feeType: String): Flow<List<Fee>> = feeDao.getFeesByType(feeType)
    
    fun getPendingFees(): Flow<List<Fee>> = feeDao.getPendingFees()
    
    suspend fun insertFee(fee: Fee) {
        feeDao.insertFee(fee)
        firebaseService.insert(fee) // Save to cloud
    }
    
    suspend fun updateFee(fee: Fee) {
        feeDao.updateFee(fee)
        firebaseService.update(fee) // Update in cloud
    }
    
    suspend fun deleteFee(fee: Fee) {
        feeDao.deleteFee(fee)
        firebaseService.delete(fee.id) // Delete from cloud
    }
    
    // Cloud operations
    suspend fun syncWithCloud() {
        val cloudFees = firebaseService.getAll()
        feeDao.insertAllFees(cloudFees)
    }
} 