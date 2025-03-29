package com.erp.modules.hr.data.repository

import com.erp.data.remote.FirebaseService
import com.erp.modules.hr.data.dao.TeacherDao
import com.erp.modules.hr.data.model.Teacher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TeacherRepository(
    private val teacherDao: TeacherDao,
    private val firebaseService: FirebaseService<Teacher>
) {
    suspend fun getAllTeachers(): Flow<List<Teacher>> {
        return flow {
            // First emit from local database
            emit(teacherDao.getAllTeachers())
            
            // Then fetch from Firebase and update local database
            try {
                val teachers = firebaseService.getAll()
                teacherDao.insertAll(teachers)
                emit(teacherDao.getAllTeachers())
            } catch (e: Exception) {
                // Error handling
            }
        }
    }
    
    suspend fun getTeacherById(id: String): Teacher? {
        val localTeacher = teacherDao.getTeacherById(id)
        
        return localTeacher ?: try {
            val remoteTeacher = firebaseService.getById(id)
            remoteTeacher?.let { teacherDao.insert(it) }
            remoteTeacher
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun insertTeacher(teacher: Teacher): String {
        val id = firebaseService.insert(teacher)
        if (id.isNotEmpty()) {
            teacher.id = id
            teacherDao.insert(teacher)
        }
        return id
    }
    
    suspend fun updateTeacher(teacher: Teacher): Boolean {
        val success = firebaseService.update(teacher)
        if (success) {
            teacherDao.update(teacher)
        }
        return success
    }
    
    suspend fun deleteTeacher(id: String): Boolean {
        val success = firebaseService.delete(id)
        if (success) {
            teacherDao.deleteById(id)
        }
        return success
    }
} 