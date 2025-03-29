package com.erp.modules.attendance.data.dao

import androidx.room.*
import com.erp.common.data.BaseDao
import com.erp.modules.attendance.data.model.Attendance
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface AttendanceDao : BaseDao<Attendance> {
    @Query("SELECT * FROM attendance")
    override fun getAll(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE id = :id")
    override suspend fun getById(id: String): Attendance?

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceByStudent(studentId: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE classId = :classId")
    fun getAttendanceByClass(classId: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceByDate(date: Date): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE classId = :classId AND date = :date")
    fun getAttendanceByClassAndDate(classId: String, date: Date): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId AND date = :date")
    fun getAttendanceByStudentAndDate(studentId: String, date: Date): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE status = :status")
    fun getAttendanceByStatus(status: String): Flow<List<Attendance>>

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'PRESENT'")
    fun countPresentDaysForStudent(studentId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance WHERE studentId = :studentId AND status = 'ABSENT'")
    fun countAbsentDaysForStudent(studentId: String): Flow<Int>
} 