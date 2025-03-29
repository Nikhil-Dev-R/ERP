package com.erp.modules.exam.data.dao

import androidx.room.*
import com.erp.modules.exam.data.model.Quiz
import com.erp.modules.exam.data.model.QuizStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY startTime DESC")
    fun getAll(): Flow<List<Quiz>>
    
    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getById(id: String): Quiz?
    
    @Query("SELECT * FROM quizzes WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    fun searchQuizzes(searchQuery: String): Flow<List<Quiz>>
    
    @Query("SELECT * FROM quizzes WHERE subjectId = :subjectId")
    fun getQuizzesBySubject(subjectId: String): Flow<List<Quiz>>
    
    @Query("SELECT * FROM quizzes WHERE gradeLevel = :gradeLevel")
    fun getQuizzesByGradeLevel(gradeLevel: String): Flow<List<Quiz>>
    
    @Query("SELECT * FROM quizzes WHERE status = :status")
    fun getQuizzesByStatus(status: QuizStatus): Flow<List<Quiz>>
    
    @Query("SELECT * FROM quizzes WHERE startTime >= :startDate AND endTime <= :endDate")
    fun getQuizzesBetweenDates(startDate: Date, endDate: Date): Flow<List<Quiz>>
    
    @Query("SELECT * FROM quizzes WHERE createdBy = :teacherId")
    fun getQuizzesByTeacher(teacherId: String): Flow<List<Quiz>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quiz: Quiz)
    
    @Update
    suspend fun update(quiz: Quiz)
    
    @Delete
    suspend fun delete(quiz: Quiz)
} 