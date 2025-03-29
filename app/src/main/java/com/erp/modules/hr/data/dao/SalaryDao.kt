package com.erp.modules.hr.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.data.model.SalaryStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SalaryDao {
    @Query("SELECT * FROM salary_payments ORDER BY payPeriodEnd DESC")
    fun getAll(): Flow<List<Salary>>
    
    @Query("SELECT * FROM salary_payments WHERE id = :id LIMIT 1")
    fun getById(id: String): Salary?
    
    @Query("SELECT * FROM salary_payments WHERE employeeId = :employeeId ORDER BY payPeriodEnd DESC")
    fun getByEmployeeId(employeeId: String): Flow<List<Salary>>
    
    @Query("SELECT * FROM salary_payments WHERE status = :status ORDER BY payPeriodEnd DESC")
    fun getByStatus(status: SalaryStatus): Flow<List<Salary>>
    
    @Query("SELECT * FROM salary_payments WHERE payPeriodStart >= :startDate AND payPeriodEnd <= :endDate ORDER BY employeeId")
    fun getByPayPeriod(startDate: Date, endDate: Date): Flow<List<Salary>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(salary: Salary): Long
    
    @Update
    fun update(salary: Salary): Int
    
    @Delete
    fun delete(salary: Salary): Int
} 