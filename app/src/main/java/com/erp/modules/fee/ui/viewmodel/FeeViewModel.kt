package com.erp.modules.fee.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.fee.data.model.Fee
import com.erp.modules.fee.data.model.FeeType
import com.erp.modules.fee.data.model.PaymentStatus
import com.erp.modules.fee.data.repository.FeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class FeeViewModel(private val feeRepository: FeeRepository) : ViewModel() {
    private val TAG = "FeeViewModel"
    
    // UI states
    private val _allFees = MutableStateFlow<List<Fee>>(emptyList())
    val allFees: StateFlow<List<Fee>> = _allFees.asStateFlow()
    
    private val _studentFees = MutableStateFlow<List<Fee>>(emptyList())
    val studentFees: StateFlow<List<Fee>> = _studentFees.asStateFlow()
    
    private val _pendingFees = MutableStateFlow<List<Fee>>(emptyList())
    val pendingFees: StateFlow<List<Fee>> = _pendingFees.asStateFlow()
    
    private val _currentFee = MutableStateFlow<Fee?>(null)
    val currentFee: StateFlow<Fee?> = _currentFee.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadAllFees()
        loadPendingFees()
        syncWithCloud()
    }
    
    fun loadAllFees() {
        viewModelScope.launch {
            _isLoading.value = true
            feeRepository.getAllFees()
                .catch { e ->
                    _error.value = "Error loading fees: ${e.message}"
                    Log.e(TAG, "Error loading fees", e)
                    _isLoading.value = false
                }
                .collect { fees ->
                    _allFees.value = fees
                    _isLoading.value = false
                }
        }
    }
    
    fun loadFeeById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            feeRepository.getFeeById(id)
                .catch { e ->
                    _error.value = "Error loading fee: ${e.message}"
                    Log.e(TAG, "Error loading fee with ID: $id", e)
                    _isLoading.value = false
                }
                .collect { fee ->
                    _currentFee.value = fee
                    _isLoading.value = false
                }
        }
    }
    
    fun loadFeesByStudent(studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            feeRepository.getFeesByStudent(studentId)
                .catch { e ->
                    _error.value = "Error loading student fees: ${e.message}"
                    Log.e(TAG, "Error loading fees for student: $studentId", e)
                    _isLoading.value = false
                }
                .collect { fees ->
                    _studentFees.value = fees
                    _isLoading.value = false
                }
        }
    }
    
    fun loadPendingFees() {
        viewModelScope.launch {
            _isLoading.value = true
            feeRepository.getPendingFees()
                .catch { e ->
                    _error.value = "Error loading pending fees: ${e.message}"
                    Log.e(TAG, "Error loading pending fees", e)
                    _isLoading.value = false
                }
                .collect { fees ->
                    _pendingFees.value = fees
                    _isLoading.value = false
                }
        }
    }
    
    fun createFee(
        studentId: String,
        feeType: FeeType,
        amount: Double,
        dueDate: Date,
        academicYear: String,
        term: String,
        remarks: String
    ) {
        val newFee = Fee(
            studentId = studentId,
            feeType = feeType,
            amount = amount,
            dueDate = dueDate,
            paymentStatus = PaymentStatus.PENDING,
            academicYear = academicYear,
            term = term,
            remarks = remarks,
            createdBy = "",  // TODO: Get current user ID
            lastModified = Date()
        )
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val feeId = feeRepository.insertFee(newFee)
                Log.d(TAG, "Fee created with ID: $feeId")
                loadAllFees()
                loadPendingFees()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error creating fee: ${e.message}"
                Log.e(TAG, "Error creating fee", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateFee(fee: Fee) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = feeRepository.updateFee(fee)
                if (success) {
                    Log.d(TAG, "Fee updated successfully: ${fee.id}")
                    loadAllFees()
                    loadPendingFees()
                    if (_currentFee.value?.id == fee.id) {
                        _currentFee.value = fee
                    }
                    _error.value = null
                } else {
                    _error.value = "Failed to update fee"
                    Log.e(TAG, "Failed to update fee: ${fee.id}")
                }
            } catch (e: Exception) {
                _error.value = "Error updating fee: ${e.message}"
                Log.e(TAG, "Error updating fee", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteFee(fee: Fee) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = feeRepository.deleteFee(fee)
                if (success) {
                    Log.d(TAG, "Fee deleted successfully: ${fee.id}")
                    loadAllFees()
                    loadPendingFees()
                    if (_currentFee.value?.id == fee.id) {
                        _currentFee.value = null
                    }
                    _error.value = null
                } else {
                    _error.value = "Failed to delete fee"
                    Log.e(TAG, "Failed to delete fee: ${fee.id}")
                }
            } catch (e: Exception) {
                _error.value = "Error deleting fee: ${e.message}"
                Log.e(TAG, "Error deleting fee", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun recordPayment(
        fee: Fee,
        paymentDate: Date,
        paymentMethod: String,
        transactionId: String,
        receiptNumber: String,
        newStatus: PaymentStatus
    ) {
        val updatedFee = fee.copy(
            paymentDate = paymentDate,
            paymentMethod = paymentMethod,
            transactionId = transactionId,
            receiptNumber = receiptNumber,
            paymentStatus = newStatus,
            lastModified = Date()
        )
        
        updateFee(updatedFee)
    }
    
    private fun syncWithCloud() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Syncing fees with cloud")
                feeRepository.syncWithCloud()
                loadAllFees()
                loadPendingFees()
                Log.d(TAG, "Fees synced successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing fees with cloud", e)
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 