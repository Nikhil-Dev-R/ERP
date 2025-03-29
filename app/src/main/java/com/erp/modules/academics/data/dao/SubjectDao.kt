package com.erp.modules.academics.data.dao

import androidx.room.*
import com.erp.common.data.BaseDao
import com.erp.modules.academics.data.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao : BaseDao<Subject> {
    @Query("SELECT * FROM subjects")
    override fun getAll(): Flow<List<Subject>>
    
    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    override suspend fun getById(id: String): Subject?
    
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectById(id: String): Flow<Subject>

    @Query("SELECT * FROM subjects WHERE name LIKE :searchQuery")
    fun searchSubjects(searchQuery: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE gradeLevel = :grade")
    fun getSubjectsByGrade(grade: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE teacherId = :teacherId")
    fun getSubjectsByTeacher(teacherId: String): Flow<List<Subject>>
} 