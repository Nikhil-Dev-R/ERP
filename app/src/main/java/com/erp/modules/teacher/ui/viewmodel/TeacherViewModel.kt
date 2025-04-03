package com.erp.modules.teacher.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmployeeStatus
import com.erp.modules.hr.data.model.EmploymentType
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.data.model.LeaveType
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.data.model.SalaryStatus
import com.erp.modules.hr.data.model.Staff
import com.erp.modules.hr.data.model.Teacher
import com.erp.modules.hr.data.repository.EmployeeRepository
import com.erp.modules.hr.data.repository.LeaveRequestRepository
import com.erp.modules.hr.data.repository.SalaryRepository
import com.erp.modules.hr.data.repository.StaffRepository
import com.erp.modules.hr.data.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class TeacherViewModel(
    private val employeeRepository: EmployeeRepository,
    private val leaveRequestRepository: LeaveRequestRepository,
    private val salaryRepository: SalaryRepository,
    private val teacherRepository: TeacherRepository? = null,
    private val staffRepository: StaffRepository? = null
) : ViewModel() {
    
    private val TAG = "HRViewModel"
    
    init {
        // Initialize data loading
        Log.d(TAG, "Initializing HRViewModel and loading data")
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading initial employees data")
                // Explicitly trigger a Firebase sync for employees
                employeeRepository.syncWithFirebase()
                
                // Get fresh employees data
                employeeRepository.getAll().collect { employeeList ->
                    Log.d(TAG, "Collected ${employeeList.size} employees")
                }
                
                Log.d(TAG, "Loading initial teachers data")
                loadAllTeachers()
                
                Log.d(TAG, "Loading initial staff data")
                loadAllStaff()
                
                Log.d(TAG, "Loading initial leave requests data")
                leaveRequestRepository.getAll().collect {} // Just trigger the collection
                
                Log.d(TAG, "Loading initial salaries data")
                salaryRepository.getAll().collect {} // Just trigger the collection
                
                Log.d(TAG, "Completed loading initial HR data")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading initial HR data: ${e.message}", e)
            }
        }
    }
    
    // Employees
    val employees = employeeRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedEmployee = MutableStateFlow<Employee?>(null)
    val selectedEmployee = _selectedEmployee.asStateFlow()
    
    // Teachers
    private val _teachers = MutableStateFlow<List<Teacher>>(emptyList())
    val teachers = _teachers.asStateFlow()
    
    private val _selectedTeacher = MutableStateFlow<Teacher?>(null)
    val selectedTeacher = _selectedTeacher.asStateFlow()
    
    // Staff
    private val _staff = MutableStateFlow<List<Staff>>(emptyList())
    val staff = _staff.asStateFlow()
    
    private val _selectedStaff = MutableStateFlow<Staff?>(null)
    val selectedStaff = _selectedStaff.asStateFlow()
    
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
    
    // Salary payments
    val salaries = salaryRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val pendingSalaries = salaryRepository.getPendingSalaries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _selectedSalary = MutableStateFlow<Salary?>(null)
    val selectedSalary = _selectedSalary.asStateFlow()
    

    
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
    
    fun deleteLeaveRequest(leaveRequest: LeaveRequest) {
        viewModelScope.launch {
            leaveRequestRepository.delete(leaveRequest)
            clearSelectedLeaveRequest()
        }
    }

    fun selectSalary(salary: Salary) {
        _selectedSalary.value = salary
    }
    
    fun clearSelectedSalary() {
        _selectedSalary.value = null
    }
    
    fun saveSalary(salary: Salary) {
        viewModelScope.launch {
            if (salary.id.isEmpty()) {
                salaryRepository.insert(salary)
            } else {
                salaryRepository.update(salary)
            }
        }
    }
    
    fun processSalary(salary: Salary, notes: String? = null) {
        viewModelScope.launch {
            salaryRepository.processSalary(salary.id, notes)
            // Refresh the selected salary
            _selectedSalary.value = salaryRepository.getById(salary.id)
        }
    }
    
    fun markSalaryAsPaid(salary: Salary, transactionId: String? = null, paymentDate: Date = Date(), notes: String? = null) {
        viewModelScope.launch {
            salaryRepository.markAsPaid(salary.id, transactionId, paymentDate, notes)
            // Refresh the selected salary
            _selectedSalary.value = salaryRepository.getById(salary.id)
        }
    }
    
    fun cancelSalary(salary: Salary, notes: String? = null) {
        viewModelScope.launch {
            salaryRepository.cancelSalary(salary.id, notes)
            // Refresh the selected salary
            _selectedSalary.value = salaryRepository.getById(salary.id)
        }
    }
    
    fun deleteSalary(salary: Salary) {
        viewModelScope.launch {
            salaryRepository.delete(salary)
            clearSelectedSalary()
        }
    }
    
    // Teacher operations
    fun loadAllTeachers() {
        viewModelScope.launch {
            teacherRepository?.getAllTeachers()?.collect {
                _teachers.value = it
            }
        }
    }
    
    fun selectTeacher(teacher: Teacher) {
        _selectedTeacher.value = teacher
    }
    
    fun clearSelectedTeacher() {
        _selectedTeacher.value = null
    }
    
    fun updateTeacher(teacher: Teacher) {
        viewModelScope.launch {
            teacherRepository?.let {
                it.updateTeacher(teacher)
            }
        }
    }
    
    // Staff operations
    fun loadAllStaff() {
        viewModelScope.launch {
            staffRepository?.getAllStaff()?.collect {
                _staff.value = it
            }
        }
    }
    
    fun selectStaff(staff: Staff) {
        _selectedStaff.value = staff
    }
    
    fun clearSelectedStaff() {
        _selectedStaff.value = null
    }
    
    fun saveStaff(staff: Staff) {
        viewModelScope.launch {
            staffRepository?.let {
                it.insertStaff(staff)
            }
        }
    }
    
    fun updateStaff(staff: Staff) {
        viewModelScope.launch {
            staffRepository?.let {
                it.updateStaff(staff)
            }
        }
    }
    
    fun deleteStaff(staffId: String) {
        viewModelScope.launch {
            staffRepository?.let {
                it.deleteStaff(staffId)
            }
        }
    }
    
    // Force refresh employees from Firebase
    fun forceRefreshEmployees() {
        Log.d(TAG, "Force refreshing employees from Firebase")
        viewModelScope.launch {
            try {
                // Explicitly sync with Firebase
                employeeRepository.syncWithFirebase()
                
                // After sync is complete, collect employees and log count
                employeeRepository.getAll().collect { employeesList ->
                    Log.d(TAG, "After refresh: ${employeesList.size} employees in local cache")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error force refreshing employees: ${e.message}", e)
            }
        }
    }
} 