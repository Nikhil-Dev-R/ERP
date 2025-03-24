package com.erp.modules.finance.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.erp.common.data.BaseDao
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionStatus
import com.erp.modules.finance.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao : BaseDao<Transaction> {
    @Query("SELECT * FROM transactions WHERE id = :id")
    override suspend fun getById(id: String): Transaction?
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    override fun getAll(): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    
    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY date DESC")
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<Transaction>>
    
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)
} 