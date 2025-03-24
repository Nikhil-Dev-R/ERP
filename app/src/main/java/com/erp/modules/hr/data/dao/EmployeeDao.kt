package com.erp.modules.hr.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.erp.common.data.BaseDao
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmploymentStatus
import com.erp.modules.hr.data.model.EmploymentType
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao : BaseDao<Employee> {
    @Query("SELECT * FROM employees WHERE id = :id")
    override suspend fun getById(id: String): Employee?
    
    @Query("SELECT * FROM employees ORDER BY lastName, firstName")
    override fun getAll(): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    suspend fun getByEmployeeId(employeeId: String): Employee?
    
    @Query("SELECT * FROM employees WHERE email = :email")
    suspend fun getByEmail(email: String): Employee?
    
    @Query("SELECT * FROM employees WHERE department = :department ORDER BY lastName, firstName")
    fun getByDepartment(department: String): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE status = :status ORDER BY lastName, firstName")
    fun getByStatus(status: EmploymentStatus): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE employmentType = :employmentType ORDER BY lastName, firstName")
    fun getByEmploymentType(employmentType: EmploymentType): Flow<List<Employee>>
    
    @Query("SELECT * FROM employees WHERE reportingTo = :managerId ORDER BY lastName, firstName")
    fun getByManager(managerId: String): Flow<List<Employee>>
    
    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteById(id: String)
} 