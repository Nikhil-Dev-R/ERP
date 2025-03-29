package com.erp.modules.student.data.repository

import android.util.Log
import com.erp.data.remote.FirebaseService
import com.erp.modules.student.data.dao.StudentDao
import com.erp.modules.student.data.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StudentRepository(
    private val studentDao: StudentDao,
    private val firebaseService: FirebaseService<Student>
) {
    private val TAG = "StudentRepository"
    
    // Improved getAllStudents to fetch from both local and cloud
    fun getAllStudents(): Flow<List<Student>> = flow {
        // First emit from local database
        Log.d(TAG, "Getting students from local database")
        emit(studentDao.getAllStudentsSync())
        
        try {
            // Then fetch from Firebase and update local database
            Log.d(TAG, "Fetching students from Firebase")
            val cloudStudents = firebaseService.getAll()
            Log.d(TAG, "Fetched ${cloudStudents.size} students from Firebase")
            
            if (cloudStudents.isNotEmpty()) {
                studentDao.insertAllStudents(cloudStudents)
                emit(studentDao.getAllStudentsSync())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching students from Firebase: ${e.message}", e)
            // Already emitted local data, so we don't throw the exception
        }
    }
    
    fun getStudentById(id: String): Flow<Student?> = flow {
        // First try local
        val localStudent = studentDao.getStudentByIdSync(id)
        emit(localStudent)
        
        try {
            // Then try Firebase
            Log.d(TAG, "Fetching student $id from Firebase")
            val remoteStudent = firebaseService.getById(id)
            
            if (remoteStudent != null) {
                Log.d(TAG, "Found student in Firebase, updating local database")
                studentDao.insertStudent(remoteStudent)
                emit(remoteStudent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching student $id from Firebase: ${e.message}", e)
            // Already emitted local data, so we don't throw the exception
        }
    }
    
    fun getStudentsByClass(grade: String, section: String): Flow<List<Student>> = 
        studentDao.getStudentsByClass(grade, section)
    
    fun getStudentByEnrollmentNumber(enrollmentNumber: String): Flow<Student> =
        studentDao.getStudentByEnrollmentNumber(enrollmentNumber)
    
    fun searchStudents(query: String): Flow<List<Student>> = studentDao.searchStudents(query)
    
    suspend fun insertStudent(student: Student): String {
        Log.d(TAG, "Inserting student: ${student.firstName} ${student.lastName}")
        
        try {
            // First save to Firebase to get an ID
            val id = firebaseService.insert(student)
            Log.d(TAG, "Student saved to Firebase with ID: $id")
            
            if (id.isNotEmpty()) {
                // Update the student with the Firebase ID
                student.id = id
                studentDao.insertStudent(student)
                Log.d(TAG, "Student saved to local database")
            } else {
                Log.e(TAG, "Failed to get ID from Firebase, saving only to local database")
                studentDao.insertStudent(student)
            }
            return id
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting student: ${e.message}", e)
            // Save to local database anyway
            studentDao.insertStudent(student)
            return student.id
        }
    }
    
    suspend fun updateStudent(student: Student): Boolean {
        Log.d(TAG, "Updating student: ${student.firstName} ${student.lastName}, ID: ${student.id}")
        
        try {
            // Update in Firebase
            val success = firebaseService.update(student)
            Log.d(TAG, "Student update in Firebase success: $success")
            
            // Update local database regardless of Firebase result
            studentDao.updateStudent(student)
            Log.d(TAG, "Student updated in local database")
            
            return success
        } catch (e: Exception) {
            Log.e(TAG, "Error updating student in Firebase: ${e.message}", e)
            // Update local database anyway
            studentDao.updateStudent(student)
            return false
        }
    }
    
    suspend fun deleteStudent(student: Student): Boolean {
        Log.d(TAG, "Deleting student with ID: ${student.id}")
        
        try {
            // Delete from Firebase
            val success = firebaseService.delete(student.id)
            Log.d(TAG, "Student deletion from Firebase success: $success")
            
            // Delete from local database regardless of Firebase result
            studentDao.deleteStudent(student)
            Log.d(TAG, "Student deleted from local database")
            
            return success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting student from Firebase: ${e.message}", e)
            // Delete from local database anyway
            studentDao.deleteStudent(student)
            return false
        }
    }
    
    suspend fun deleteStudentById(id: String): Boolean {
        Log.d(TAG, "Deleting student by ID: $id")
        
        try {
            // Delete from Firebase
            val success = firebaseService.delete(id)
            Log.d(TAG, "Student deletion from Firebase success: $success")
            
            // Delete from local database regardless of Firebase result
            studentDao.deleteStudentById(id)
            Log.d(TAG, "Student deleted from local database")
            
            return success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting student from Firebase: ${e.message}", e)
            // Delete from local database anyway
            studentDao.deleteStudentById(id)
            return false
        }
    }
    
    // Cloud operations
    suspend fun syncWithCloud() {
        Log.d(TAG, "Syncing students with Firebase")
        try {
            val cloudStudents = firebaseService.getAll()
            Log.d(TAG, "Fetched ${cloudStudents.size} students from Firebase")
            studentDao.insertAllStudents(cloudStudents)
            Log.d(TAG, "Sync complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing with Firebase: ${e.message}", e)
        }
    }
} 