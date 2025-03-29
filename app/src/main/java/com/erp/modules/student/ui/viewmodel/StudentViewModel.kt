package com.erp.modules.student.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class StudentViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {
    
    private val TAG = "StudentViewModel"
    
    // UI state for student list
    private val _studentsState = MutableStateFlow<StudentsUiState>(StudentsUiState.Loading)
    val studentsState: StateFlow<StudentsUiState> = _studentsState
    
    // UI state for single student details
    private val _studentDetailState = MutableStateFlow<StudentDetailUiState>(StudentDetailUiState.Loading)
    val studentDetailState: StateFlow<StudentDetailUiState> = _studentDetailState
    
    // For new/edit student form
    private val _currentStudent = MutableStateFlow<Student?>(null)
    val currentStudent: StateFlow<Student?> = _currentStudent
    
    init {
        Log.d(TAG, "Initializing StudentViewModel")
        loadAllStudents()
    }
    
    fun loadAllStudents() {
        Log.d(TAG, "Loading all students")
        viewModelScope.launch {
            _studentsState.value = StudentsUiState.Loading
            
            studentRepository.getAllStudents()
                .catch { error ->
                    Log.e(TAG, "Error loading students: ${error.message}", error)
                    _studentsState.value = StudentsUiState.Error(error.message ?: "Unknown error")
                }
                .collect { students ->
                    Log.d(TAG, "Loaded ${students.size} students")
                    _studentsState.value = if (students.isEmpty()) {
                        Log.d(TAG, "No students found")
                        StudentsUiState.Empty
                    } else {
                        StudentsUiState.Success(students)
                    }
                }
        }
    }
    
    fun getStudentsByClass(grade: String, section: String) {
        Log.d(TAG, "Getting students by class: $grade-$section")
        viewModelScope.launch {
            _studentsState.value = StudentsUiState.Loading
            
            studentRepository.getStudentsByClass(grade, section)
                .catch { error ->
                    Log.e(TAG, "Error loading students by class: ${error.message}", error)
                    _studentsState.value = StudentsUiState.Error(error.message ?: "Unknown error")
                }
                .collect { students ->
                    Log.d(TAG, "Loaded ${students.size} students for class $grade-$section")
                    _studentsState.value = if (students.isEmpty()) {
                        StudentsUiState.Empty
                    } else {
                        StudentsUiState.Success(students)
                    }
                }
        }
    }
    
    fun getStudentDetail(id: String) {
        Log.d(TAG, "Getting student details for ID: $id")
        viewModelScope.launch {
            _studentDetailState.value = StudentDetailUiState.Loading
            
            studentRepository.getStudentById(id)
                .catch { error ->
                    Log.e(TAG, "Error loading student detail: ${error.message}", error)
                    _studentDetailState.value = StudentDetailUiState.Error(
                        error.message ?: "Unknown error"
                    )
                }
                .collect { student ->
                    Log.d(TAG, "Loaded student detail: ${student?.firstName} ${student?.lastName}")
                    student?.let { _studentDetailState.value = StudentDetailUiState.Success(it) }
                    _currentStudent.value = student
                }
        }
    }
    
    fun searchStudents(query: String) {
        Log.d(TAG, "Searching students with query: $query")
        if (query.isBlank()) {
            loadAllStudents()
            return
        }
        
        viewModelScope.launch {
            _studentsState.value = StudentsUiState.Loading
            
            studentRepository.searchStudents(query)
                .catch { error ->
                    Log.e(TAG, "Error searching students: ${error.message}", error)
                    _studentsState.value = StudentsUiState.Error(error.message ?: "Unknown error")
                }
                .collect { students ->
                    Log.d(TAG, "Found ${students.size} students matching query: $query")
                    _studentsState.value = if (students.isEmpty()) {
                        StudentsUiState.Empty
                    } else {
                        StudentsUiState.Success(students)
                    }
                }
        }
    }
    
    fun createNewStudent() {
        Log.d(TAG, "Creating new student")
        _currentStudent.value = Student(
            admissionDate = Date()
        )
    }
    
    fun saveStudent(student: Student) {
        Log.d(TAG, "Saving student: ${student.firstName} ${student.lastName}, ID: ${student.id}")
        viewModelScope.launch {
            try {
                if (student.id.isBlank()) {
                    Log.d(TAG, "Inserting new student")
                    val id = studentRepository.insertStudent(student)
                    Log.d(TAG, "Student inserted with ID: $id")
                } else {
                    Log.d(TAG, "Updating existing student with ID: ${student.id}")
                    val success = studentRepository.updateStudent(student)
                    Log.d(TAG, "Student update success: $success")
                }
                loadAllStudents()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving student: ${e.message}", e)
                // Handle error - in a real app, we'd use a dedicated error channel
            }
        }
    }
    
    fun deleteStudent(student: Student) {
        Log.d(TAG, "Deleting student with ID: ${student.id}")
        viewModelScope.launch {
            try {
                val success = studentRepository.deleteStudent(student)
                Log.d(TAG, "Student deletion success: $success")
                loadAllStudents()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting student: ${e.message}", e)
                // Handle error
            }
        }
    }
}

// UI states for students list
sealed class StudentsUiState {
    object Loading : StudentsUiState()
    object Empty : StudentsUiState()
    data class Success(val students: List<Student>) : StudentsUiState()
    data class Error(val message: String) : StudentsUiState()
}

// UI states for student detail
sealed class StudentDetailUiState {
    object Loading : StudentDetailUiState()
    data class Success(val student: Student) : StudentDetailUiState()
    data class Error(val message: String) : StudentDetailUiState()
} 