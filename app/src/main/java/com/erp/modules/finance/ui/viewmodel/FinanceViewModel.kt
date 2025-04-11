package com.erp.modules.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.finance.data.model.Fee
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.InvoiceStatus
import com.erp.modules.finance.data.model.PaymentStatus
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.model.TransactionStatus
import com.erp.modules.finance.data.model.TransactionType
import com.erp.modules.finance.data.repository.FeeRepository
import com.erp.modules.finance.data.repository.InvoiceRepository
import com.erp.modules.finance.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

data class FinanceUiState(
    val transactionsState: TransactionsUiState,
    val invoicesState: InvoicesUiState,
    val feesState: FeesUiState,
    val feeDetailState: FeeDetailState,
    val transactionDetailState: TransactionDetailState,
    val invoiceDetailState: InvoiceDetailState,

    val currentTransaction: Transaction? = null,
    val currentInvoice: Invoice? = null,
    val currentFee: Fee? = null,

    val selectedTransaction: Transaction? = null,
    val selectedInvoice: Invoice? = null,
    val selectedFee: Fee? = null,

    val transactions: List<Transaction> = emptyList(),
    val invoices: List<Invoice> = emptyList(),
    val fees: List<Fee> = emptyList(),
    val overdueInvoices: List<Invoice> = emptyList()
)

class FinanceViewModel(
    private val transactionRepository: TransactionRepository,
    private val invoiceRepository: InvoiceRepository,
    private val feeRepository: FeeRepository
) : ViewModel() {
    // Transaction States
    private val _transactionsState = MutableStateFlow<TransactionsUiState>(TransactionsUiState.Loading)
    val transactionsState: StateFlow<TransactionsUiState> = _transactionsState

    private val _transactionDetailState = MutableStateFlow<TransactionDetailState>(TransactionDetailState.Loading)
    val transactionDetailState: StateFlow<TransactionDetailState> = _transactionDetailState

    // Invoice States
    private val _invoicesState = MutableStateFlow<InvoicesUiState>(InvoicesUiState.Loading)
    val invoicesState: StateFlow<InvoicesUiState> = _invoicesState

    private val _invoiceDetailState = MutableStateFlow<InvoiceDetailState>(InvoiceDetailState.Loading)
    val invoiceDetailState: StateFlow<InvoiceDetailState> = _invoiceDetailState

    // Fee States
    private val _feesState = MutableStateFlow<FeesUiState>(FeesUiState.Loading)
    val feesState: StateFlow<FeesUiState> = _feesState

    private val _feeDetailState = MutableStateFlow<FeeDetailState>(FeeDetailState.Loading)
    val feeDetailState: StateFlow<FeeDetailState> = _feeDetailState

    // Current editing items
    private val _currentTransaction = MutableStateFlow<Transaction?>(null)
    val currentTransaction: StateFlow<Transaction?> = _currentTransaction

    private val _currentInvoice = MutableStateFlow<Invoice?>(null)
    val currentInvoice: StateFlow<Invoice?> = _currentInvoice

    private val _currentFee = MutableStateFlow<Fee?>(null)
    val currentFee: StateFlow<Fee?> = _currentFee

    private val _financeUiState = MutableStateFlow<FinanceUiState?>(null)
    val financeUiState: StateFlow<FinanceUiState?> = _financeUiState.asStateFlow()

    // Load initial data
    init {
        loadTransactions()
        loadInvoices()
        loadFees()
        loadAll()

    }

    fun loadAll() {
        viewModelScope.launch {
            transactionRepository.getAll().collectLatest {
                _financeUiState.value = _financeUiState.value?.copy(
                    transactions = it
                )
            }

            invoiceRepository.getAll().collectLatest {
                _financeUiState.value = _financeUiState.value?.copy(
                    invoices = it
                )
            }

            feeRepository.getAllFees().collectLatest {
                _financeUiState.value = _financeUiState.value?.copy(
                    fees = it
                )
            }
        }
    }

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
        _financeUiState.value = _financeUiState.value?.copy(
            selectedTransaction = transaction
        )
        _selectedTransaction.value = transaction
    }

    fun clearSelectedTransaction() {
        _financeUiState.value = _financeUiState.value?.copy(
            selectedTransaction = null
        )
        _selectedTransaction.value = null
    }

    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            // For debugging with placeholder data, just reload without repository operations
            loadTransactions()

            /* Uncomment when repository is working
            if (transaction.id.isEmpty()) {
                transactionRepository.insert(transaction)
            } else {
                transactionRepository.update(transaction)
            }

            // Reload transactions after saving
            loadTransactions()
            */
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            // For debugging with placeholder data, just reload without repository operations
            loadTransactions()

            /* Uncomment when repository is working
            transactionRepository.delete(transaction)

            // Reload transactions after deleting
            loadTransactions()
            */
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
        _financeUiState.value = _financeUiState.value?.copy(
            selectedInvoice = invoice
        )
        _selectedInvoice.value = invoice
    }

    fun clearSelectedInvoice() {
        _financeUiState.value = _financeUiState.value?.copy(
            selectedInvoice = null
        )
        _selectedInvoice.value = null
    }

    fun saveInvoice(invoice: Invoice) {
        viewModelScope.launch {
            // For debugging with placeholder data, just reload without repository operations
            loadInvoices()

            /* Uncomment when repository is working
            if (invoice.id.isEmpty()) {
                invoiceRepository.insert(invoice)
            } else {
                invoiceRepository.update(invoice)
            }

            // Reload invoices after saving
            loadInvoices()
            */
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            // For debugging with placeholder data, just reload without repository operations
            loadInvoices()

            /* Uncomment when repository is working
            invoiceRepository.delete(invoice)

            // Reload invoices after deleting
            loadInvoices()
            */
        }
    }

    // Load data functions
    fun loadTransactions() {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                transactionsState = TransactionsUiState.Loading
            )
            _transactionsState.value = TransactionsUiState.Loading

            // Add placeholder data for debugging
            val placeholderTransactions = listOf(
                Transaction(
                    id = "1",
                    amount = BigDecimal("5000.00"),
                    description = "Salary",
                    date = Date(),
                    status = TransactionStatus.COMPLETED,
                    type = TransactionType.INCOME
                ),
                Transaction(
                    id = "2",
                    amount = BigDecimal("1200.00"),
                    description = "Rent Payment",
                    date = Date(),
                    status = TransactionStatus.COMPLETED,
                    type = TransactionType.EXPENSE
                ),
                Transaction(
                    id = "3",
                    amount = BigDecimal("800.00"),
                    description = "Utilities",
                    date = Date(),
                    status = TransactionStatus.COMPLETED,
                    type = TransactionType.EXPENSE
                )
            )

            _financeUiState.value = _financeUiState.value?.copy(
                transactionsState = TransactionsUiState.Success(placeholderTransactions)
            )
            _transactionsState.value = TransactionsUiState.Success(placeholderTransactions)

            // Comment out original flow for now
            /*
            transactionRepository.getAll()
                .catch { error ->
                    _transactionsState.value = TransactionsUiState.Error(error.message ?: "Unknown error")
                }
                .collect { transactions ->
                    _transactionsState.value = if (transactions.isEmpty()) {
                        TransactionsUiState.Empty
                    } else {
                        TransactionsUiState.Success(transactions)
                    }
                }
            */
        }
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                invoicesState = InvoicesUiState.Loading
            )
            _invoicesState.value = InvoicesUiState.Loading

            // Add placeholder data for debugging
            val placeholderInvoices = listOf(
                Invoice(
                    id = "1",
                    invoiceNumber = "INV-2023001",
                    customerId = "CUST-001",
                    customerName = "Raj Enterprises",
                    amount = BigDecimal("12500.00"),
                    issueDate = Date(),
                    dueDate = Date(),
                    status = InvoiceStatus.SENT
                ),
                Invoice(
                    id = "2",
                    invoiceNumber = "INV-2023002",
                    customerId = "CUST-002",
                    customerName = "Sharma Traders",
                    amount = BigDecimal("8000.00"),
                    issueDate = Date(),
                    dueDate = Date(),
                    status = InvoiceStatus.PAID
                ),
                Invoice(
                    id = "3",
                    invoiceNumber = "INV-2023003",
                    customerId = "CUST-003",
                    customerName = "Patel Industries",
                    amount = BigDecimal("15000.00"),
                    issueDate = Date(),
                    dueDate = Date(),
                    status = InvoiceStatus.OVERDUE
                )
            )

            _financeUiState.value = _financeUiState.value?.copy(
                invoicesState = InvoicesUiState.Success(placeholderInvoices)
            )
            _invoicesState.value = InvoicesUiState.Success(placeholderInvoices)

            // Comment out original flow for now
            /*
            invoiceRepository.getAll()
                .catch { error ->
                    _invoicesState.value = InvoicesUiState.Error(error.message ?: "Unknown error")
                }
                .collect { invoices ->
                    _invoicesState.value = if (invoices.isEmpty()) {
                        InvoicesUiState.Empty
                    } else {
                        InvoicesUiState.Success(invoices)
                    }
                }
            */
        }
    }

    // Fee functions
    fun loadFees() {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                feesState = FeesUiState.Loading
            )
            _feesState.value = FeesUiState.Loading

            feeRepository.getAllFees()
                .catch { error ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feesState = FeesUiState.Error(error.message ?: "Unknown error")
                    )
//                    _feesState.value = FeesUiState.Error(error.message ?: "Unknown error")
                }
                .collect { fees ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feesState = if (fees.isEmpty()) {
                            FeesUiState.Success(fees)
                        } else {
                            FeesUiState.Success(fees)
                        }
                    )
                    _feesState.value = if (fees.isEmpty()) {
                        FeesUiState.Empty
                    } else {
                        FeesUiState.Success(fees)
                    }
                }
        }
    }

    fun loadFeesByStudent(studentId: String) {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                feesState = FeesUiState.Loading
            )
            _feesState.value = FeesUiState.Loading

            feeRepository.getFeesByStudent(studentId)
                .catch { error ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feesState = FeesUiState.Error(error.message ?: "Unknown error")
                    )
                    _feesState.value = FeesUiState.Error(error.message ?: "Unknown error")
                }
                .collect { fees ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feesState = if (fees.isEmpty()) {
                            FeesUiState.Success(fees)
                        } else {
                            FeesUiState.Success(fees)
                        }
                    )
                    _feesState.value = if (fees.isEmpty()) {
                        FeesUiState.Empty
                    } else {
                        FeesUiState.Success(fees)
                    }
                }
        }
    }

    fun loadPendingFees() {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                feesState = FeesUiState.Loading
            )
            _feesState.value = FeesUiState.Loading

            feeRepository.getPendingFees()
                .catch { error ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feesState = FeesUiState.Error(error.message ?: "Unknown error")
                    )
                    _feesState.value = FeesUiState.Error(error.message ?: "Unknown error")
                }
                .collect { fees ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feesState = if (fees.isEmpty()) {
                            FeesUiState.Success(fees)
                        } else {
                            FeesUiState.Success(fees)
                        }
                    )
                    _feesState.value = if (fees.isEmpty()) {
                        FeesUiState.Empty
                    } else {
                        FeesUiState.Success(fees)
                    }
                }
        }
    }

    fun getFeeDetail(id: String) {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                feeDetailState = FeeDetailState.Loading
            )
            _feeDetailState.value = FeeDetailState.Loading

            feeRepository.getFeeById(id)
                .catch { error ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feeDetailState = FeeDetailState.Error(error.message ?: "Unknown error")
                    )
                    _feeDetailState.value = FeeDetailState.Error(error.message ?: "Unknown error")
                }
                .collect { fee ->
                    _financeUiState.value = _financeUiState.value?.copy(
                        feeDetailState = FeeDetailState.Success(fee)
                    )
                    _feeDetailState.value = FeeDetailState.Success(fee)
                    _financeUiState.value = _financeUiState.value?.copy(
                        currentFee = fee
                    )
                    _currentFee.value = fee
                }
        }
    }

    fun createNewFee() {
        _financeUiState.value = _financeUiState.value?.copy(
            currentFee = Fee(
                dueDate = Date(),
                academicYear = "2023-2024"
            )
        )
        _currentFee.value = Fee(
            dueDate = Date(),
            academicYear = "2023-2024"
        )
    }

    fun saveFee(fee: Fee) {
        viewModelScope.launch {
            try {
                if (fee.id.isBlank()) {
                    feeRepository.insertFee(fee)
                } else {
                    feeRepository.updateFee(fee)
                }
                loadFees()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun recordFeePayment(fee: Fee, amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            try {
                // Create transaction for the payment
                val transaction = Transaction(
                    amount = BigDecimal(amount),
                    type = TransactionType.INCOME,
                    description = "Fee payment - ${fee.feeType} - Student ID: ${fee.studentId}",
                    date = Date(),
                    status = TransactionStatus.COMPLETED,
                    accountId = null,
                    categoryId = "FEE_PAYMENT",
                    payeeId = fee.studentId,
                    referenceNumber = fee.id
                )

                transactionRepository.insert(transaction)

                // Update fee status
                val updatedFee = if (amount >= fee.amount) {
                    // Payment in full
                    fee.copy(
                        paymentStatus = PaymentStatus.PAID,
                        paymentDate = Date(),
                        paymentMethod = paymentMethod,
                        transactionId = transaction.id
                    )
                } else {
                    // Partial payment
                    fee.copy(
                        paymentStatus = PaymentStatus.PARTIAL,
                        paymentDate = Date(),
                        paymentMethod = paymentMethod,
                        transactionId = transaction.id
                    )
                }

                feeRepository.updateFee(updatedFee)

                // Refresh state
                loadFees()
                loadTransactions()
                getFeeDetail(fee.id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Transaction Detail functions
    fun getTransactionDetail(id: String) {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                transactionDetailState = TransactionDetailState.Loading
            )
            _transactionDetailState.value = TransactionDetailState.Loading

            try {
                // Mocking a transaction fetch for now
                val transaction = Transaction(
                    id = id,
                    amount = BigDecimal("0.00"),
                    description = "Transaction",
                    date = Date(),
                    status = TransactionStatus.COMPLETED,
                    type = TransactionType.EXPENSE
                )

                _financeUiState.value = _financeUiState.value?.copy(
                    transactionDetailState = TransactionDetailState.Success(transaction),
                    currentTransaction = transaction
                )
                _transactionDetailState.value = TransactionDetailState.Success(transaction)
                _currentTransaction.value = transaction
            } catch (e: Exception) {
                _financeUiState.value = _financeUiState.value?.copy(
                    transactionDetailState = TransactionDetailState.Error(e.message ?: "Unknown error")
                )
                _transactionDetailState.value = TransactionDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createNewTransaction() {
        val currentTransaction = Transaction(
            id = UUID.randomUUID().toString(),
            amount = BigDecimal.ZERO,
            description = "",
            date = Date(),
            status = TransactionStatus.COMPLETED,
            type = TransactionType.EXPENSE
        )
        _currentTransaction.value = currentTransaction
        _financeUiState.value = _financeUiState.value?.copy(
            currentTransaction = currentTransaction,
            transactionDetailState = TransactionDetailState.Success(currentTransaction)
        )
        _transactionDetailState.value = TransactionDetailState.Success(_currentTransaction.value!!)
    }

    // Invoice Detail functions
    fun getInvoiceDetail(id: String) {
        viewModelScope.launch {
            _financeUiState.value = _financeUiState.value?.copy(
                invoiceDetailState = InvoiceDetailState.Loading
            )
            _invoiceDetailState.value = InvoiceDetailState.Loading

            try {
                // Mocking an invoice fetch for now
                val invoice = Invoice(
                    id = id,
                    invoiceNumber = "INV-001",
                    customerId = "CUST-001",
                    customerName = "Customer",
                    amount = BigDecimal("0.00"),
                    issueDate = Date(),
                    dueDate = Date(),
                    status = InvoiceStatus.DRAFT
                )

                _financeUiState.value = _financeUiState.value?.copy(
                    invoiceDetailState = InvoiceDetailState.Success(invoice),
                    currentInvoice = invoice
                )
                _invoiceDetailState.value = InvoiceDetailState.Success(invoice)
                _currentInvoice.value = invoice
            } catch (e: Exception) {
                _financeUiState.value = _financeUiState.value?.copy(
                    invoiceDetailState = InvoiceDetailState.Error(e.message ?: "Unknown error")
                )
                _invoiceDetailState.value = InvoiceDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createNewInvoice() {
        val currentInvoice = Invoice(
            id = UUID.randomUUID().toString(),
            invoiceNumber = "",
            customerId = "",
            customerName = "",
            amount = BigDecimal.ZERO,
            issueDate = Date(),
            dueDate = Date(),
            status = InvoiceStatus.DRAFT
        )
        _currentInvoice.value = currentInvoice
        _financeUiState.value = _financeUiState.value?.copy(
            currentInvoice = currentInvoice,
            invoiceDetailState = InvoiceDetailState.Success(currentInvoice)
        )
        _invoiceDetailState.value = InvoiceDetailState.Success(_currentInvoice.value!!)
    }
}

// Transaction UI States
sealed class TransactionsUiState {
    object Loading : TransactionsUiState()
    object Empty : TransactionsUiState()
    data class Success(val transactions: List<Transaction>) : TransactionsUiState()
    data class Error(val message: String) : TransactionsUiState()
}

sealed class TransactionDetailState {
    object Loading : TransactionDetailState()
    data class Success(val transaction: Transaction) : TransactionDetailState()
    data class Error(val message: String) : TransactionDetailState()
}

// Invoice UI States
sealed class InvoicesUiState {
    object Loading : InvoicesUiState()
    object Empty : InvoicesUiState()
    data class Success(val invoices: List<Invoice>) : InvoicesUiState()
    data class Error(val message: String) : InvoicesUiState()
}

sealed class InvoiceDetailState {
    object Loading : InvoiceDetailState()
    data class Success(val invoice: Invoice) : InvoiceDetailState()
    data class Error(val message: String) : InvoiceDetailState()
}

// Fee UI States
sealed class FeesUiState {
    object Loading : FeesUiState()
    object Empty : FeesUiState()
    data class Success(val fees: List<Fee>) : FeesUiState()
    data class Error(val message: String) : FeesUiState()
}

sealed class FeeDetailState {
    object Loading : FeeDetailState()
    data class Success(val fee: Fee) : FeeDetailState()
    data class Error(val message: String) : FeeDetailState()
} 