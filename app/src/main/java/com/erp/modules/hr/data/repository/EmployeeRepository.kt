package com.erp.modules.hr.data.repository

import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.hr.data.dao.EmployeeDao
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmploymentStatus
import com.erp.modules.hr.data.model.EmploymentType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class EmployeeRepository(
    private val employeeDao: EmployeeDao,
    private val firebaseService: FirebaseService<Employee>
) : Repository<Employee> {
    
    override suspend fun getById(id: String): Employee? {
        return employeeDao.getById(id) ?: firebaseService.getById(id)?.also {
            employeeDao.insert(it)
        }
    }
    
    override fun getAll(): Flow<List<Employee>> {
        return employeeDao.getAll()
    }
    
    override suspend fun insert(item: Employee): String {
        employeeDao.insert(item)
        return firebaseService.insert(item)
    }
    
    override suspend fun update(item: Employee) {
        val updatedItem = item.copy(updatedAt = Date())
        employeeDao.update(updatedItem)
        firebaseService.update(updatedItem)
    }
    
    override suspend fun delete(item: Employee) {
        employeeDao.delete(item)
        firebaseService.delete(item.id)
    }
    
    override suspend fun deleteById(id: String) {
        employeeDao.deleteById(id)
        firebaseService.delete(id)
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
    
    fun getByStatus(status: EmploymentStatus): Flow<List<Employee>> {
        return employeeDao.getByStatus(status)
    }
    
    fun getByEmploymentType(employmentType: EmploymentType): Flow<List<Employee>> {
        return employeeDao.getByEmploymentType(employmentType)
    }
    
    fun getByManager(managerId: String): Flow<List<Employee>> {
        return employeeDao.getByManager(managerId)
    }
} 