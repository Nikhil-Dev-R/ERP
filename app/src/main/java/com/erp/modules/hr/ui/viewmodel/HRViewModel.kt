package com.erp.modules.hr.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmploymentStatus
import com.erp.modules.hr.data.model.EmploymentType
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.data.model.LeaveType
import com.erp.modules.hr.data.repository.EmployeeRepository
import com.erp.modules.hr.data.repository.LeaveRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class HRViewModel(
    private val employeeRepository: EmployeeRepository,
    private val leaveRequestRepository: LeaveRequestRepository
) : ViewModel() {
    
    // Employees
    val employees = employeeRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee = _selectedEmployee.asStateFlow()
    
    // Leave Requests
    val leaveRequests = leaveRequestRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val pendingLeaveRequests = leaveRequestRepository.getByStatus(LeaveStatus.PENDING)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedLeaveRequest = MutableStateFlow<LeaveRequest?>(null)
    val selectedLeaveRequest = _selectedLeaveRequest.asStateFlow()
    
    // Employee operations
    fun getEmployeesByDepartment(department: String): StateFlow<List<Employee>> {
        return employeeRepository.getByDepartment(department)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getEmployeesByStatus(status: EmploymentStatus): StateFlow<List<Employee>> {
        return employeeRepository.getByStatus(status)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getEmployeesByType(type: EmploymentType): StateFlow<List<Employee>> {
        return employeeRepository.getByEmploymentType(type)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun selectEmployee(employee: Employee) {
        _selectedEmployee.value = employee
    }
    
    fun clearSelectedEmployee() {
        _selectedEmployee.value = null
    }
    
    fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            if (employee.id.isEmpty()) {
                employeeRepository.insert(employee)
            } else {
                employeeRepository.update(employee)
            }
        }
    }
    
    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.delete(employee)
        }
    }
    
    // Leave Request operations
    fun getLeaveRequestsByEmployee(employeeId: String): StateFlow<List<LeaveRequest>> {
        return leaveRequestRepository.getByEmployeeId(employeeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getLeaveRequestsByStatus(status: LeaveStatus): StateFlow<List<LeaveRequest>> {
        return leaveRequestRepository.getByStatus(status)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getLeaveRequestsByType(type: LeaveType): StateFlow<List<LeaveRequest>> {
        return leaveRequestRepository.getByLeaveType(type)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun getLeaveRequestsByDateRange(startDate: Date, endDate: Date): StateFlow<List<LeaveRequest>> {
        return leaveRequestRepository.getByDateRange(startDate, endDate)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    fun selectLeaveRequest(leaveRequest: LeaveRequest) {
        _selectedLeaveRequest.value = leaveRequest
    }
    
    fun clearSelectedLeaveRequest() {
        _selectedLeaveRequest.value = null
    }
    
    fun saveLeaveRequest(leaveRequest: LeaveRequest) {
        viewModelScope.launch {
            if (leaveRequest.id.isEmpty()) {
                leaveRequestRepository.insert(leaveRequest)
            } else {
                leaveRequestRepository.update(leaveRequest)
            }
        }
    }
    
    fun approveLeaveRequest(leaveRequest: LeaveRequest, approverId: String, comments: String? = null) {
        viewModelScope.launch {
            leaveRequestRepository.approveLeaveRequest(leaveRequest.id, approverId, comments)
            // Refresh the selected leave request
            _selectedLeaveRequest.value = leaveRequestRepository.getById(leaveRequest.id)
        }
    }
    
    fun rejectLeaveRequest(leaveRequest: LeaveRequest, approverId: String, comments: String? = null) {
        viewModelScope.launch {
            leaveRequestRepository.rejectLeaveRequest(leaveRequest.id, approverId, comments)
            // Refresh the selected leave request
            _selectedLeaveRequest.value = leaveRequestRepository.getById(leaveRequest.id)
        }
    }
    
    fun deleteLeaveRequest(leaveRequest: LeaveRequest) {
        viewModelScope.launch {
            leaveRequestRepository.delete(leaveRequest)
            clearSelectedLeaveRequest()
        }
    }
} 