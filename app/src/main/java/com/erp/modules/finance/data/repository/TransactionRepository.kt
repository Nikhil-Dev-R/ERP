package com.erp.modules.finance.data.repository

import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.finance.data.dao.TransactionDao
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionStatus
import com.erp.modules.finance.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val firebaseService: FirebaseService<Transaction>
) : Repository<Transaction> {
    
    override suspend fun getById(id: String): Transaction? {
        return transactionDao.getById(id) ?: firebaseService.getById(id)?.also {
            transactionDao.insert(it)
        }
    }
    
    override fun getAll(): Flow<List<Transaction>> {
        return transactionDao.getAll()
    }
    
    override suspend fun insert(item: Transaction): String {
        transactionDao.insert(item)
        return firebaseService.insert(item)
    }
    
    override suspend fun update(item: Transaction) {
        val updatedItem = item.copy(updatedAt = Date())
        transactionDao.update(updatedItem)
        firebaseService.update(updatedItem)
    }
    
    override suspend fun delete(item: Transaction) {
        transactionDao.delete(item)
        firebaseService.delete(item.id)
    }
    
    override suspend fun deleteById(id: String) {
        transactionDao.deleteById(id)
        firebaseService.delete(id)
    }
    
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }
    
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type)
    }
    
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByStatus(status)
    }
} 