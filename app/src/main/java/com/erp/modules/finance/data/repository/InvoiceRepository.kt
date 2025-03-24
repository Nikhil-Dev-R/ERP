package com.erp.modules.finance.data.repository

import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.finance.data.dao.InvoiceDao
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

class InvoiceRepository(
    private val invoiceDao: InvoiceDao,
    private val firebaseService: FirebaseService<Invoice>
) : Repository<Invoice> {
    
    override suspend fun getById(id: String): Invoice? {
        return invoiceDao.getById(id) ?: firebaseService.getById(id)?.also {
            invoiceDao.insert(it)
        }
    }
    
    override fun getAll(): Flow<List<Invoice>> {
        return invoiceDao.getAll()
    }
    
    override suspend fun insert(item: Invoice): String {
        invoiceDao.insert(item)
        return firebaseService.insert(item)
    }
    
    override suspend fun update(item: Invoice) {
        val updatedItem = item.copy(updatedAt = Date())
        invoiceDao.update(updatedItem)
        firebaseService.update(updatedItem)
    }
    
    override suspend fun delete(item: Invoice) {
        invoiceDao.delete(item)
        firebaseService.delete(item.id)
    }
    
    override suspend fun deleteById(id: String) {
        invoiceDao.deleteById(id)
        firebaseService.delete(id)
    }
    
    fun getInvoicesByCustomer(customerId: String): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesByCustomer(customerId)
    }
    
    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesByStatus(status)
    }
    
    fun getOverdueInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getOverdueInvoices()
    }
} 