package com.erp.modules.student.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erp.modules.student.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY grade, lastName")
    fun getAllStudents(): Flow<List<Student>>
    
    // Synchronous version for Firebase integration
    @Query("SELECT * FROM students ORDER BY grade, lastName")
    suspend fun getAllStudentsSync(): List<Student>
    
    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: String): Flow<Student>
    
    // Synchronous version for Firebase integration
    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentByIdSync(id: String): Student?
    
    @Query("SELECT * FROM students WHERE grade = :grade AND section = :section ORDER BY lastName")
    fun getStudentsByClass(grade: String, section: String): Flow<List<Student>>
    
    // Synchronous version for Firebase integration
    @Query("SELECT * FROM students WHERE grade = :grade AND section = :section ORDER BY lastName")
    suspend fun getStudentsByClassSync(grade: String, section: String): List<Student>
    
    @Query("SELECT * FROM students WHERE enrollmentNumber = :enrollmentNumber")
    fun getStudentByEnrollmentNumber(enrollmentNumber: String): Flow<Student>
    
    @Query("SELECT * FROM students WHERE LOWER(firstName) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(lastName) LIKE '%' || LOWER(:searchQuery) || '%' OR LOWER(enrollmentNumber) LIKE '%' || LOWER(:searchQuery) || '%'")
    fun searchStudents(searchQuery: String): Flow<List<Student>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<Student>)
    
    @Update
    suspend fun updateStudent(student: Student)
    
    @Delete
    suspend fun deleteStudent(student: Student)
    
    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: String)
} 