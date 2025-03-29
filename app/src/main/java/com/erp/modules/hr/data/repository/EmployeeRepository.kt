package com.erp.modules.hr.data.repository

import android.util.Log
import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.hr.data.dao.EmployeeDao
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmployeeStatus
import com.erp.modules.hr.data.model.EmploymentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class EmployeeRepository(
    private val employeeDao: EmployeeDao,
    private val firebaseService: FirebaseService<Employee>
) : Repository<Employee> {
    
    private val TAG = "EmployeeRepository"
    
    override suspend fun getById(id: String): Employee? {
        // First try local
        val localEmployee = employeeDao.getByIdSync(id)
        
        // If not found locally, try Firebase
        if (localEmployee == null) {
            try {
                Log.d(TAG, "Fetching employee with ID $id from Firebase")
                val remoteEmployee = firebaseService.getById(id)
                
                if (remoteEmployee != null) {
                    Log.d(TAG, "Found employee in Firebase, saving to local database")
                    employeeDao.insert(remoteEmployee)
                    return remoteEmployee
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching employee from Firebase: ${e.message}", e)
            }
        }
        
        return localEmployee
    }
    
    override fun getAll(): Flow<List<Employee>> = flow {
        // First emit from local database
        Log.d(TAG, "Getting employees from local database")
        val localEmployees = employeeDao.getAllSync()
        emit(localEmployees)
        
        try {
            // Then fetch from Firebase and update local database
            Log.d(TAG, "Fetching employees from Firebase")
            val remoteEmployees = firebaseService.getAll()
            Log.d(TAG, "Fetched ${remoteEmployees.size} employees from Firebase")
            
            if (remoteEmployees.isNotEmpty()) {
                // Insert all into local database
                for (employee in remoteEmployees) {
                    employeeDao.insert(employee)
                }
                
                // Emit updated list
                val updatedEmployees = employeeDao.getAllSync()
                Log.d(TAG, "Emitting ${updatedEmployees.size} employees after Firebase sync")
                emit(updatedEmployees)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing employees with Firebase: ${e.message}", e)
            // We already emitted local data, so just log the error
        }
    }
    
    override suspend fun insert(item: Employee): String {
        Log.d(TAG, "Inserting employee: ${item.firstName} ${item.lastName}")
        
        try {
            // First save to Firebase to get an ID
            val id = firebaseService.insert(item)
            Log.d(TAG, "Employee saved to Firebase with ID: $id")
            
            if (id.isNotEmpty()) {
                // Update the item with the Firebase ID
                item.id = id
                employeeDao.insert(item)
                Log.d(TAG, "Employee saved to local database")
            } else {
                Log.e(TAG, "Failed to get ID from Firebase, saving only to local database")
                employeeDao.insert(item)
            }
            return id
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting employee: ${e.message}", e)
            // Save to local database anyway
            employeeDao.insert(item)
            return item.id
        }
    }
    
    override suspend fun update(item: Employee) {
        Log.d(TAG, "Updating employee: ${item.firstName} ${item.lastName}, ID: ${item.id}")
        
        try {
            // Update in Firebase
            val success = firebaseService.update(item)
            Log.d(TAG, "Employee update in Firebase success: $success")
            
            // Update local database regardless of Firebase result
            employeeDao.update(item)
            Log.d(TAG, "Employee updated in local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating employee in Firebase: ${e.message}", e)
            // Update local database anyway
            employeeDao.update(item)
        }
    }
    
    override suspend fun delete(item: Employee) {
        Log.d(TAG, "Deleting employee with ID: ${item.id}")
        
        try {
            // Delete from Firebase
            val success = firebaseService.delete(item.id)
            Log.d(TAG, "Employee deletion from Firebase success: $success")
            
            // Delete from local database regardless of Firebase result
            employeeDao.delete(item)
            Log.d(TAG, "Employee deleted from local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting employee from Firebase: ${e.message}", e)
            // Delete from local database anyway
            employeeDao.delete(item)
        }
    }
    
    override suspend fun deleteById(id: String) {
        Log.d(TAG, "Deleting employee by ID: $id")
        
        try {
            // Delete from Firebase
            val success = firebaseService.delete(id)
            Log.d(TAG, "Employee deletion from Firebase success: $success")
            
            // Delete from local database regardless of Firebase result
            employeeDao.deleteById(id)
            Log.d(TAG, "Employee deleted from local database")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting employee from Firebase: ${e.message}", e)
            // Delete from local database anyway
            employeeDao.deleteById(id)
        }
    }
    
    suspend fun getByEmployeeId(employeeId: String): Employee? {
        return employeeDao.getByEmployeeId(employeeId)
    }
    
    suspend fun getByEmail(email: String): Employee? {
        return employeeDao.getByEmail(email)
    }
    
    fun getByDepartment(department: String): Flow<List<Employee>> {
        return employeeDao.getByDepartment(department)
    }
    
    fun getByStatus(status: EmployeeStatus): Flow<List<Employee>> {
        return employeeDao.getByStatus(status)
    }
    
    fun getByEmploymentType(employmentType: EmploymentType): Flow<List<Employee>> {
        return employeeDao.getByEmploymentType(employmentType)
    }
    
    fun getByManager(managerId: String): Flow<List<Employee>> {
        return employeeDao.getByManager(managerId)
    }
    
    // Sync data from Firebase
    suspend fun syncWithFirebase() {
        Log.d(TAG, "Syncing employees with Firebase")
        try {
            val remoteEmployees = firebaseService.getAll()
            Log.d(TAG, "Fetched ${remoteEmployees.size} employees from Firebase")
            
            for (employee in remoteEmployees) {
                employeeDao.insert(employee)
            }
            
            Log.d(TAG, "Employees synced successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing employees with Firebase: ${e.message}", e)
        }
    }
} 