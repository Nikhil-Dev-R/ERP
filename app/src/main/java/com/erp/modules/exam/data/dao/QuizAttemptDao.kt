package com.erp.modules.exam.data.dao

import androidx.room.*
import com.erp.modules.exam.data.model.QuizAttempt
import com.erp.modules.exam.data.model.QuizAttemptStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY startTime DESC")
    fun getAll(): Flow<List<QuizAttempt>>
    
    @Query("SELECT * FROM quiz_attempts WHERE id = :id")
    suspend fun getById(id: String): QuizAttempt?
    
    @Query("SELECT * FROM quiz_attempts WHERE quizId = :quizId")
    fun getAttemptsByQuiz(quizId: String): Flow<List<QuizAttempt>>
    
    @Query("SELECT * FROM quiz_attempts WHERE studentId = :studentId")
    fun getAttemptsByStudent(studentId: String): Flow<List<QuizAttempt>>
    
    @Query("SELECT * FROM quiz_attempts WHERE quizId = :quizId AND studentId = :studentId")
    fun getAttemptByQuizAndStudent(quizId: String, studentId: String): Flow<QuizAttempt?>
    
    @Query("SELECT * FROM quiz_attempts WHERE status = :status")
    fun getAttemptsByStatus(status: QuizAttemptStatus): Flow<List<QuizAttempt>>
    
    @Query("SELECT AVG(score * 1.0 / totalMarks * 100) FROM quiz_attempts WHERE quizId = :quizId AND status = 'COMPLETED'")
    fun getAverageScoreForQuiz(quizId: String): Flow<Float>
    
    @Query("SELECT AVG(score * 1.0 / totalMarks * 100) FROM quiz_attempts WHERE studentId = :studentId AND status = 'COMPLETED'")
    fun getAverageScoreForStudent(studentId: String): Flow<Float>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quizAttempt: QuizAttempt)
    
    @Update
    suspend fun update(quizAttempt: QuizAttempt)
    
    @Delete
    suspend fun delete(quizAttempt: QuizAttempt)
} 