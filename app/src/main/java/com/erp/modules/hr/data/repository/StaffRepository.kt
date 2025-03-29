package com.erp.modules.hr.data.repository

import com.erp.data.remote.FirebaseService
import com.erp.modules.hr.data.dao.StaffDao
import com.erp.modules.hr.data.model.Staff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StaffRepository(
    private val staffDao: StaffDao,
    private val firebaseService: FirebaseService<Staff>
) {
    suspend fun getAllStaff(): Flow<List<Staff>> {
        return flow {
            // First emit from local database
            emit(staffDao.getAllStaff())
            
            // Then fetch from Firebase and update local database
            try {
                val staffList = firebaseService.getAll()
                staffDao.insertAll(staffList)
                emit(staffDao.getAllStaff())
            } catch (e: Exception) {
                // Error handling
            }
        }
    }
    
    suspend fun getStaffById(id: String): Staff? {
        val localStaff = staffDao.getStaffById(id)
        
        return localStaff ?: try {
            val remoteStaff = firebaseService.getById(id)
            remoteStaff?.let { staffDao.insert(it) }
            remoteStaff
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun insertStaff(staff: Staff): String {
        val id = firebaseService.insert(staff)
        if (id.isNotEmpty()) {
            staff.id = id
            staffDao.insert(staff)
        }
        return id
    }
    
    suspend fun updateStaff(staff: Staff): Boolean {
        val success = firebaseService.update(staff)
        if (success) {
            staffDao.update(staff)
        }
        return success
    }
    
    suspend fun deleteStaff(id: String): Boolean {
        val success = firebaseService.delete(id)
        if (success) {
            staffDao.deleteById(id)
        }
        return success
    }
} 