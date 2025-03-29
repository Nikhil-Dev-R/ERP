package com.erp.modules.fee.data.repository

import android.util.Log
import com.erp.data.remote.FirebaseService
import com.erp.modules.fee.data.dao.FeeDao
import com.erp.modules.fee.data.model.Fee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeeRepository(
    private val feeDao: FeeDao,
    private val firebaseService: FirebaseService<Fee>
) {
    private val TAG = "FeeRepository"
    
    // Local database operations
    fun getAllFees(): Flow<List<Fee>> = feeDao.getAllFees()
    
    fun getFeeById(id: String): Flow<Fee?> = flow {
        // First try local
        val localFee = feeDao.getFeeById(id)
        emit(null) // Default value
        
        try {
            // Then try Firebase
            Log.d(TAG, "Fetching fee $id from Firebase")
            val remoteFee = firebaseService.getById(id)
            
            if (remoteFee != null) {
                Log.d(TAG, "Found fee in Firebase, updating local database")
                feeDao.insertFee(remoteFee)
                emit(remoteFee)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching fee $id from Firebase: ${e.message}", e)
            // Already emitted local data, so we don't throw the exception
        }
    }
    
    fun getFeesByStudent(studentId: String): Flow<List<Fee>> = feeDao.getFeesByStudent(studentId)
    
    fun getFeesByType(feeType: String): Flow<List<Fee>> = feeDao.getFeesByType(feeType)
    
    fun getPendingFees(): Flow<List<Fee>> = feeDao.getPendingFees()
    
    suspend fun insertFee(fee: Fee): String {
        Log.d(TAG, "Inserting fee with ID: ${fee.id}")
        feeDao.insertFee(fee)
        
        // Insert to Firebase
        return try {
            val id = firebaseService.insert(fee)
            Log.d(TAG, "Fee inserted to Firebase with ID: $id")
            id
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting fee to Firebase: ${e.message}", e)
            fee.id
        }
    }
    
    suspend fun updateFee(fee: Fee): Boolean {
        Log.d(TAG, "Updating fee with ID: ${fee.id}")
        feeDao.updateFee(fee)
        
        // Update in Firebase
        return try {
            val success = firebaseService.update(fee)
            Log.d(TAG, "Fee update to Firebase success: $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error updating fee in Firebase: ${e.message}", e)
            false
        }
    }
    
    suspend fun deleteFee(fee: Fee): Boolean {
        Log.d(TAG, "Deleting fee with ID: ${fee.id}")
        
        try {
            // Delete from Firebase
            val success = firebaseService.delete(fee.id)
            Log.d(TAG, "Fee deletion from Firebase success: $success")
            
            // Delete from local database regardless of Firebase result
            feeDao.deleteFee(fee)
            Log.d(TAG, "Fee deleted from local database")
            
            return success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting fee from Firebase: ${e.message}", e)
            // Delete from local database anyway
            feeDao.deleteFee(fee)
            return false
        }
    }
    
    // Cloud operations
    suspend fun syncWithCloud() {
        try {
            Log.d(TAG, "Syncing fees with cloud")
            val cloudFees = firebaseService.getAll()
            Log.d(TAG, "Retrieved ${cloudFees.size} fees from cloud")
            feeDao.insertAllFees(cloudFees)
            Log.d(TAG, "Fees synced successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing fees with cloud: ${e.message}", e)
        }
    }
} 