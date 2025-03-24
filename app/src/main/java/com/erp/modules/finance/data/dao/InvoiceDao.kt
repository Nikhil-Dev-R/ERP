package com.erp.modules.finance.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.erp.common.data.BaseDao
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface InvoiceDao : BaseDao<Invoice> {
    @Query("SELECT * FROM invoices WHERE id = :id")
    override suspend fun getById(id: String): Invoice?
    
    @Query("SELECT * FROM invoices ORDER BY dueDate DESC")
    override fun getAll(): Flow<List<Invoice>>
    
    @Query("SELECT * FROM invoices WHERE customerId = :customerId ORDER BY dueDate DESC")
    fun getInvoicesByCustomer(customerId: String): Flow<List<Invoice>>
    
    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY dueDate DESC")
    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<Invoice>>
    
    @Query("SELECT * FROM invoices WHERE dueDate < :date AND status != 'PAID' ORDER BY dueDate")
    fun getOverdueInvoices(date: Date = Date()): Flow<List<Invoice>>
    
    @Query("DELETE FROM invoices WHERE id = :id")
    suspend fun deleteById(id: String)
} 