package com.erp.modules.exam.data.dao

import androidx.room.*
import com.erp.common.data.BaseDao
import com.erp.modules.exam.data.model.ExamResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao : BaseDao<ExamResult> {
    @Query("SELECT * FROM results")
    override fun getAll(): Flow<List<ExamResult>>

    @Query("SELECT * FROM results WHERE id = :id")
    override suspend fun getById(id: String): ExamResult?

    @Query("SELECT * FROM results WHERE studentId = :studentId")
    fun getResultsByStudent(studentId: String): Flow<List<ExamResult>>

    @Query("SELECT * FROM results WHERE examId = :examId")
    fun getResultsByExam(examId: String): Flow<List<ExamResult>>

    @Query("SELECT * FROM results WHERE subjectId = :subjectId")
    fun getResultsBySubject(subjectId: String): Flow<List<ExamResult>>

    @Query("SELECT * FROM results WHERE studentId = :studentId AND examId = :examId")
    fun getResultByStudentAndExam(studentId: String, examId: String): Flow<ExamResult>

    @Query("SELECT * FROM results WHERE studentId = :studentId AND subjectId = :subjectId")
    fun getResultsByStudentAndSubject(studentId: String, subjectId: String): Flow<List<ExamResult>>

    @Query("SELECT AVG(marksObtained) FROM results WHERE studentId = :studentId")
    fun getAverageMarksForStudent(studentId: String): Flow<Float>

    @Query("SELECT AVG(marksObtained) FROM results WHERE examId = :examId")
    fun getAverageMarksForExam(examId: String): Flow<Float>
} 