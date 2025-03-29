package com.erp.modules.exam.data.dao

import androidx.room.*
import com.erp.common.data.BaseDao
import com.erp.modules.exam.data.model.Exam
import com.erp.modules.exam.data.model.ExamType
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ExamDao : BaseDao<Exam> {
    @Query("SELECT * FROM exams")
    override fun getAll(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE id = :id")
    override suspend fun getById(id: String): Exam?

    @Query("SELECT * FROM exams WHERE name LIKE :searchQuery")
    fun searchExams(searchQuery: String): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE gradeLevel = :classId")
    fun getExamsByClass(classId: String): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE subjectId = :subjectId")
    fun getExamsBySubject(subjectId: String): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE startDate = :date")
    fun getExamsByDate(date: Date): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE startDate >= :startDate AND startDate <= :endDate")
    fun getExamsBetweenDates(startDate: Date, endDate: Date): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE examType = :type")
    fun getExamsByType(type: ExamType): Flow<List<Exam>>
} 