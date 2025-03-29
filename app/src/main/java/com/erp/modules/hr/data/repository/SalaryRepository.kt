package com.erp.modules.hr.data.repository

import com.erp.modules.hr.data.dao.SalaryDao
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.data.model.SalaryStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

class SalaryRepository(private val salaryDao: SalaryDao) {
    
    fun getAll(): Flow<List<Salary>> = salaryDao.getAll()
    
    fun getById(id: String): Salary? = salaryDao.getById(id)
    
    fun getByEmployeeId(employeeId: String): Flow<List<Salary>> = salaryDao.getByEmployeeId(employeeId)
    
    fun getByStatus(status: SalaryStatus): Flow<List<Salary>> = salaryDao.getByStatus(status)
    
    fun getByPayPeriod(startDate: Date, endDate: Date): Flow<List<Salary>> = 
        salaryDao.getByPayPeriod(startDate, endDate)
    
    fun getPendingSalaries(): Flow<List<Salary>> = salaryDao.getByStatus(SalaryStatus.PENDING)
    
    fun getProcessedSalaries(): Flow<List<Salary>> = salaryDao.getByStatus(SalaryStatus.PROCESSED)
    
    fun getPaidSalaries(): Flow<List<Salary>> = salaryDao.getByStatus(SalaryStatus.PAID)
    
    suspend fun insert(salary: Salary): Long = withContext(Dispatchers.IO) {
        salaryDao.insert(salary)
    }
    
    suspend fun update(salary: Salary): Int = withContext(Dispatchers.IO) {
        salaryDao.update(salary)
    }
    
    suspend fun delete(salary: Salary): Int = withContext(Dispatchers.IO) {
        salaryDao.delete(salary)
    }
    
    suspend fun processSalary(id: String, notes: String? = null): Int = withContext(Dispatchers.IO) {
        val salary = salaryDao.getById(id) ?: return@withContext 0
        val updatedSalary = salary.copy(
            status = SalaryStatus.PROCESSED,
            notes = notes ?: salary.notes,
            updatedAt = Date()
        )
        salaryDao.update(updatedSalary)
    }
    
    suspend fun markAsPaid(id: String, transactionId: String? = null, paymentDate: Date = Date(), notes: String? = null): Int = 
        withContext(Dispatchers.IO) {
            val salary = salaryDao.getById(id) ?: return@withContext 0
            val updatedSalary = salary.copy(
                status = SalaryStatus.PAID,
                transactionId = transactionId ?: salary.transactionId,
                paymentDate = paymentDate,
                notes = notes ?: salary.notes,
                updatedAt = Date()
            )
            salaryDao.update(updatedSalary)
        }
    
    suspend fun cancelSalary(id: String, notes: String? = null): Int = withContext(Dispatchers.IO) {
        val salary = salaryDao.getById(id) ?: return@withContext 0
        val updatedSalary = salary.copy(
            status = SalaryStatus.CANCELLED,
            notes = notes ?: salary.notes,
            updatedAt = Date()
        )
        salaryDao.update(updatedSalary)
    }
} 