package com.erp.modules.attendance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.attendance.data.model.Attendance
import com.erp.modules.attendance.data.repository.AttendanceRepository
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {
    // UI state for attendance list
    private val _attendanceState = MutableStateFlow<AttendanceState>(AttendanceState.Loading)
    val attendanceState = _attendanceState.asStateFlow()
    
    // UI state for class attendance (all students in a class for a specific date)
    private val _classAttendanceState = MutableStateFlow<ClassAttendanceUiState>(ClassAttendanceUiState.Loading)
    val classAttendanceState: StateFlow<ClassAttendanceUiState> = _classAttendanceState
    
    // UI state for student attendance (attendance history for a specific student)
    private val _studentAttendanceState = MutableStateFlow<StudentAttendanceUiState>(StudentAttendanceUiState.Loading)
    val studentAttendanceState: StateFlow<StudentAttendanceUiState> = _studentAttendanceState
    
    // For filtering
    private val _selectedDate = MutableStateFlow<Date>(Calendar.getInstance().time)
    val selectedDate: StateFlow<Date> = _selectedDate
    
    private val _selectedClass = MutableStateFlow<String>("")
    val selectedClass: StateFlow<String> = _selectedClass
    
    private val _selectedSection = MutableStateFlow<String>("")
    val selectedSection: StateFlow<String> = _selectedSection
    
    // Today's attendance records
    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
    
    val attendanceRecords: Flow<List<Attendance>> = attendanceRepository.getAttendanceByDate(today)
        .onEach { records ->
            _attendanceState.value = if (records.isEmpty()) {
                AttendanceState.Empty
            } else {
                AttendanceState.Success(records)
            }
        }
        .catch { e ->
            _attendanceState.value = AttendanceState.Error(e.message ?: "Unknown error")
        }
    
    // Student list for attendance
    private val _studentsState = MutableStateFlow<StudentsState>(StudentsState.Loading)
    val studentsState = _studentsState.asStateFlow()
    
    val students: Flow<List<Student>> = studentRepository.getAllStudents()
        .onEach { studentList ->
            _studentsState.value = if (studentList.isEmpty()) {
                StudentsState.Empty
            } else {
                StudentsState.Success(studentList)
            }
        }
        .catch { e ->
            _studentsState.value = StudentsState.Error(e.message ?: "Unknown error")
        }
    
    // Attendance statistics
    private val _attendanceStats = MutableStateFlow<AttendanceStats?>(null)
    val attendanceStats = _attendanceStats.asStateFlow()
    
    // Load mock data initially
    init {
        loadMockData()
    }
    
    private fun loadMockData() {
        val mockStudents = listOf(
            Student(
                firstName = "John",
                lastName = "Doe",
                enrollmentNumber = "S001",
                grade = "9",
                section = "A"
            ).apply {
                id = "student1"
            },
            Student(
                firstName = "Jane",
                lastName = "Smith",
                enrollmentNumber = "S002",
                grade = "9",
                section = "A"
            ).apply {
                id = "student2"
            },
            Student(
                firstName = "Bob",
                lastName = "Johnson",
                enrollmentNumber = "S003",
                grade = "9",
                section = "A"
            ).apply {
                id = "student3"
            }
        )
        
        val mockAttendance = mockStudents.map { student ->
            Attendance(
                studentId = student.id,
                date = today,
                status = "PRESENT",
                classId = "9", 
                sectionId = "A"
            ).apply {
                id = UUID.randomUUID().toString()
            }
        }
        
        _attendanceState.value = AttendanceState.Success(mockAttendance)
        
        val classAttendanceData = mockStudents.map { student ->
            StudentAttendanceRecord(
                student = student,
                attendance = mockAttendance.find { it.studentId == student.id }
            )
        }
        
        _classAttendanceState.value = ClassAttendanceUiState.Success(
            ClassAttendanceData(
                date = today,
                classId = "9",
                sectionId = "A",
                records = classAttendanceData
            )
        )
    }
    
    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
        loadAttendanceForDate(date, _selectedClass.value, _selectedSection.value)
    }
    
    fun setSelectedClass(classId: String, sectionId: String) {
        _selectedClass.value = classId
        _selectedSection.value = sectionId
        loadAttendanceForDate(_selectedDate.value, classId, sectionId)
    }
    
    private fun loadAttendanceForDate(date: Date, classId: String, sectionId: String) {
        if (classId.isBlank() || sectionId.isBlank()) {
            _classAttendanceState.value = ClassAttendanceUiState.Empty
            return
        }
        
        viewModelScope.launch {
            _classAttendanceState.value = ClassAttendanceUiState.Loading
            
            // In a real app, we would fetch from repository
            // For now, just use the mock data if class and section match
            val attendance = (_attendanceState.value as? AttendanceState.Success)?.attendanceList
                ?.filter { 
                    isSameDay(it.date, date) && 
                    it.classId == classId && 
                    it.sectionId == sectionId 
                } ?: emptyList()
                
            if (attendance.isEmpty()) {
                _classAttendanceState.value = ClassAttendanceUiState.Empty
            } else {
                // In a real app, we would fetch students for the class
                val mockStudents = listOf(
                    Student(
                        firstName = "John",
                        lastName = "Doe",
                        enrollmentNumber = "S001",
                        grade = classId,
                        section = sectionId
                    ).apply {
                        id = "student1"
                    },
                    Student(
                        firstName = "Jane",
                        lastName = "Smith",
                        enrollmentNumber = "S002",
                        grade = classId,
                        section = sectionId
                    ).apply {
                        id = "student2"
                    },
                    Student(
                        firstName = "Bob",
                        lastName = "Johnson",
                        enrollmentNumber = "S003",
                        grade = classId,
                        section = sectionId
                    ).apply {
                        id = "student3"
                    }
                )
                
                val records = mockStudents.map { student ->
                    StudentAttendanceRecord(
                        student = student,
                        attendance = attendance.find { it.studentId == student.id }
                    )
                }
                
                _classAttendanceState.value = ClassAttendanceUiState.Success(
                    ClassAttendanceData(
                        date = date,
                        classId = classId,
                        sectionId = sectionId,
                        records = records
                    )
                )
            }
        }
    }
    
    fun loadAttendanceForStudent(studentId: String) {
        viewModelScope.launch {
            _studentAttendanceState.value = StudentAttendanceUiState.Loading
            
            // In a real app, we would fetch from repository
            val attendance = (_attendanceState.value as? AttendanceState.Success)?.attendanceList
                ?.filter { it.studentId == studentId }
                ?.sortedByDescending { it.date }
                ?: emptyList()
                
            if (attendance.isEmpty()) {
                _studentAttendanceState.value = StudentAttendanceUiState.Empty
            } else {
                // For the demo, we'll just use the first attendance's student
                val mockStudent = Student(
                    firstName = "John",
                    lastName = "Doe",
                    enrollmentNumber = "S001",
                    grade = "9",
                    section = "A"
                ).apply {
                    id = studentId
                }
                
                _studentAttendanceState.value = StudentAttendanceUiState.Success(
                    StudentAttendanceData(
                        student = mockStudent,
                        attendanceHistory = attendance
                    )
                )
            }
        }
    }
    
    fun markAttendance(studentId: String, isPresent: Boolean) {
        viewModelScope.launch {
            val status = if (isPresent) "PRESENT" else "ABSENT"
            
            // Check if attendance record already exists for this student today
            val existingRecord = attendanceRepository.getAttendanceByStudentAndDate(studentId, today)
                .first()
                ?.firstOrNull()
            
            if (existingRecord != null) {
                // Update existing attendance record
                val updatedAttendance = Attendance(
                    studentId = existingRecord.studentId,
                    date = existingRecord.date,
                    status = status,
                    subjectId = existingRecord.subjectId,
                    classId = existingRecord.classId,
                    sectionId = existingRecord.sectionId,
                    remarks = existingRecord.remarks,
                    recordedBy = existingRecord.recordedBy,
                    lastModified = Date()
                ).apply {
                    // Keep the same ID as the existing record
                    id = existingRecord.id
                }
                attendanceRepository.updateAttendance(updatedAttendance)
            } else {
                // Create new attendance record
                val attendance = Attendance(
                    studentId = studentId,
                    date = today,
                    status = status,
                    remarks = "",
                    recordedBy = "Current User", // In a real app, this would be the current user's ID
                    lastModified = Date()
                )
                attendanceRepository.insertAttendance(attendance)
            }
        }
    }
    
    fun markClassAttendance(classId: String, studentIds: List<String>, status: String) {
        viewModelScope.launch {
            val attendanceList = studentIds.map { studentId ->
                Attendance(
                    studentId = studentId,
                    classId = classId,
                    date = today,
                    status = status,
                    remarks = "",
                    recordedBy = "Current User", // In a real app, this would be the current user's ID
                    lastModified = Date()
                )
            }
            
            attendanceRepository.markAttendanceForClass(classId, today, attendanceList)
        }
    }
    
    fun loadAttendanceStats(classId: String? = null, fromDate: Date? = null, toDate: Date? = null) {
        viewModelScope.launch {
            try {
                // This would be a repository call in a real app
                // For now, we'll simulate with mock data
                _attendanceStats.value = AttendanceStats(
                    totalStudents = 120,
                    presentCount = 102,
                    absentCount = 12,
                    lateCount = 6,
                    presentPercentage = 85.0f,
                    absentPercentage = 10.0f,
                    latePercentage = 5.0f
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun getStudentAttendanceRecord(studentId: String) {
        viewModelScope.launch {
            try {
                val attendanceByStudent = attendanceRepository.getAttendanceByStudent(studentId).first()
                // Process and update UI with the attendance record
                if (attendanceByStudent.isNotEmpty()) {
                    // Fetch student details (in a real app would come from repository)
                    val student = Student(
                        firstName = "John",
                        lastName = "Doe",
                        enrollmentNumber = "S001",
                        grade = "9",
                        section = "A"
                    ).apply {
                        id = studentId
                    }
                    
                    _studentAttendanceState.value = StudentAttendanceUiState.Success(
                        StudentAttendanceData(
                            student = student,
                            attendanceHistory = attendanceByStudent
                        )
                    )
                } else {
                    _studentAttendanceState.value = StudentAttendanceUiState.Empty
                }
            } catch (e: Exception) {
                _studentAttendanceState.value = StudentAttendanceUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun syncAttendanceData() {
        viewModelScope.launch {
            attendanceRepository.syncWithCloud()
        }
    }
    
    fun saveAttendance() {
        // In a real app, this would commit all pending attendance changes to the repository
        // For now, we'll just show a success state
        val currentState = _classAttendanceState.value as? ClassAttendanceUiState.Success ?: return
        
        // Update the main attendance list
        val currentAttendanceList = (_attendanceState.value as? AttendanceState.Success)?.attendanceList ?: emptyList()
        val updatedAttendanceList = currentAttendanceList.toMutableList()
        
        // Add or update attendance records
        currentState.data.records.forEach { record ->
            if (record.attendance != null) {
                val existingIndex = updatedAttendanceList.indexOfFirst { 
                    it.id == record.attendance.id 
                }
                
                if (existingIndex >= 0) {
                    updatedAttendanceList[existingIndex] = record.attendance
                } else {
                    updatedAttendanceList.add(record.attendance)
                }
            }
        }
        
        _attendanceState.value = AttendanceState.Success(updatedAttendanceList)
    }
    
    private fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) return false
        
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
    
    fun getFormattedDate(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}

// UI state for attendance list
sealed class AttendanceState {
    object Loading : AttendanceState()
    data class Success(val attendanceList: List<Attendance>) : AttendanceState()
    object Empty : AttendanceState()
    data class Error(val message: String) : AttendanceState()
}

// UI state for class attendance (all students in a class)
sealed class ClassAttendanceUiState {
    object Loading : ClassAttendanceUiState()
    object Empty : ClassAttendanceUiState()
    data class Success(val data: ClassAttendanceData) : ClassAttendanceUiState()
    data class Error(val message: String) : ClassAttendanceUiState()
}

data class ClassAttendanceData(
    val date: Date,
    val classId: String,
    val sectionId: String,
    val records: List<StudentAttendanceRecord>
)

data class StudentAttendanceRecord(
    val student: Student,
    val attendance: Attendance?
)

// UI state for student attendance (attendance history for a specific student)
sealed class StudentAttendanceUiState {
    object Loading : StudentAttendanceUiState()
    object Empty : StudentAttendanceUiState()
    data class Success(val data: StudentAttendanceData) : StudentAttendanceUiState()
    data class Error(val message: String) : StudentAttendanceUiState()
}

data class StudentAttendanceData(
    val student: Student,
    val attendanceHistory: List<Attendance>
)

// State classes
sealed class StudentsState {
    object Loading : StudentsState()
    data class Success(val students: List<Student>) : StudentsState()
    object Empty : StudentsState()
    data class Error(val message: String) : StudentsState()
}

// Data class for attendance statistics
data class AttendanceStats(
    val totalStudents: Int,
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val presentPercentage: Float,
    val absentPercentage: Float,
    val latePercentage: Float
) 