package com.erp.modules.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionStatus
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.data.repository.InvoiceRepository
import com.erp.modules.finance.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class FinanceViewModel(
    private val transactionRepository: TransactionRepository,
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {
    
    // Transactions
    val transactions = transactionRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction = _selectedTransaction.asStateFlow()
    
    // Invoices
    val invoices = invoiceRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val overdueInvoices = invoiceRepository.getOverdueInvoices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedInvoice = MutableStateFlow<Invoice?>(null)
    val selectedInvoice = _selectedInvoice.asStateFlow()
    
    // Transaction operations
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): StateFlow<List<Transaction>> {
        return transactionRepository.getTransactionsByDateRange(startDate, endDate)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getTransactionsByType(type: TransactionType): StateFlow<List<Transaction>> {
        return transactionRepository.getTransactionsByType(type)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getTransactionsByStatus(status: TransactionStatus): StateFlow<List<Transaction>> {
        return transactionRepository.getTransactionsByStatus(status)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun selectTransaction(transaction: Transaction) {
        _selectedTransaction.value = transaction
    }
    
    fun clearSelectedTransaction() {
        _selectedTransaction.value = null
    }
    
    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            if (transaction.id.isEmpty()) {
                transactionRepository.insert(transaction)
            } else {
                transactionRepository.update(transaction)
            }
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction)
        }
    }
    
    // Invoice operations
    fun getInvoicesByCustomer(customerId: String): StateFlow<List<Invoice>> {
        return invoiceRepository.getInvoicesByCustomer(customerId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getInvoicesByStatus(status: InvoiceStatus): StateFlow<List<Invoice>> {
        return invoiceRepository.getInvoicesByStatus(status)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun selectInvoice(invoice: Invoice) {
        _selectedInvoice.value = invoice
    }
    
    fun clearSelectedInvoice() {
        _selectedInvoice.value = null
    }
    
    fun saveInvoice(invoice: Invoice) {
        viewModelScope.launch {
            if (invoice.id.isEmpty()) {
                invoiceRepository.insert(invoice)
            } else {
                invoiceRepository.update(invoice)
            }
        }
    }
    
    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            invoiceRepository.delete(invoice)
        }
    }
} 