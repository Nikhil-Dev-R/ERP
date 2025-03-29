package com.erp.modules.attendance.data.repository

import com.erp.data.remote.FirebaseService
import com.erp.modules.attendance.data.dao.AttendanceDao
import com.erp.modules.attendance.data.model.Attendance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val firebaseService: FirebaseService<Attendance>
) {
    // Local database operations
    fun getAllAttendance(): Flow<List<Attendance>> = attendanceDao.getAll()
    suspend fun getAttendanceById(id: String): Attendance? = attendanceDao.getById(id)
    
    fun getAttendanceByStudent(studentId: String): Flow<List<Attendance>> = attendanceDao.getAttendanceByStudent(studentId)
    
    fun getAttendanceByClass(classId: String): Flow<List<Attendance>> = attendanceDao.getAttendanceByClass(classId)
    
    fun getAttendanceByDate(date: Date): Flow<List<Attendance>> = attendanceDao.getAttendanceByDate(date)
    
    fun getAttendanceByClassAndDate(classId: String, date: Date): Flow<List<Attendance>> = 
        attendanceDao.getAttendanceByClassAndDate(classId, date)
    
    fun getAttendanceByStudentAndDate(studentId: String, date: Date): Flow<List<Attendance>> = 
        attendanceDao.getAttendanceByStudentAndDate(studentId, date)
    
    fun getAttendanceByStatus(status: String): Flow<List<Attendance>> = attendanceDao.getAttendanceByStatus(status)
    
    fun countPresentDaysForStudent(studentId: String): Flow<Int> = attendanceDao.countPresentDaysForStudent(studentId)
    
    fun countAbsentDaysForStudent(studentId: String): Flow<Int> = attendanceDao.countAbsentDaysForStudent(studentId)
    
    // Operations with cloud synchronization
    suspend fun insertAttendance(attendance: Attendance) {
        attendanceDao.insert(attendance)
        firebaseService.insert(attendance)
    }
    
    suspend fun updateAttendance(attendance: Attendance) {
        attendanceDao.update(attendance)
        firebaseService.update(attendance)
    }
    
    suspend fun deleteAttendance(attendance: Attendance) {
        attendanceDao.delete(attendance)
        firebaseService.delete(attendance.id)
    }
    
    suspend fun markAttendanceForClass(classId: String, date: Date, attendanceList: List<Attendance>) {
        attendanceList.forEach { attendance ->
            insertAttendance(attendance)
        }
    }
    
    suspend fun markAttendanceForStudent(attendance: Attendance) {
        insertAttendance(attendance)
    }
    
    // Cloud synchronization
    suspend fun syncWithCloud() {
        val attendanceRecords = firebaseService.getAll()
        attendanceRecords.forEach { attendance ->
            attendanceDao.insert(attendance)
        }
    }
    
    // Analytics
    fun getAttendancePercentage(studentId: String, totalDays: Int): Flow<Float> {
        return countPresentDaysForStudent(studentId).map { presentDays ->
            if (totalDays > 0) (presentDays.toFloat() / totalDays) * 100 else 0f
        }
    }
} 