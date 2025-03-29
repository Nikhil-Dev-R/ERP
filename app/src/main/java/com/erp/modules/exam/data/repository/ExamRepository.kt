package com.erp.modules.exam.data.repository

import com.erp.common.model.BaseEntity
import com.erp.data.remote.FirebaseService
import com.erp.modules.exam.data.dao.ExamDao
import com.erp.modules.exam.data.dao.ResultDao
import com.erp.modules.exam.data.model.Exam
import com.erp.modules.exam.data.model.ExamResult
import com.erp.modules.exam.data.model.ExamType
import com.erp.modules.exam.data.model.ResultStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ExamRepository(
    private val examDao: ExamDao,
    private val resultDao: ResultDao,
    private val examFirebaseService: FirebaseService<Exam>,
    private val resultFirebaseService: FirebaseService<ExamResult>
) {
    // Exam operations
    fun getAllExams(): Flow<List<Exam>> = examDao.getAll()
    
    suspend fun getExamById(id: String): Exam? = examDao.getById(id)
    
    fun searchExams(query: String): Flow<List<Exam>> = examDao.searchExams("%$query%")
    
    fun getExamsByClass(classId: String): Flow<List<Exam>> = examDao.getExamsByClass(classId)
    
    fun getExamsBySubject(subjectId: String): Flow<List<Exam>> = examDao.getExamsBySubject(subjectId)
    
    fun getExamsByDate(date: Date): Flow<List<Exam>> = examDao.getExamsByDate(date)
    
    fun getExamsBetweenDates(startDate: Date, endDate: Date): Flow<List<Exam>> = 
        examDao.getExamsBetweenDates(startDate, endDate)
    
    fun getExamsByType(type: ExamType): Flow<List<Exam>> = examDao.getExamsByType(type)
    
    suspend fun insertExam(exam: Exam) {
        examDao.insert(exam)
        examFirebaseService.insert(exam)
    }
    
    suspend fun updateExam(exam: Exam) {
        examDao.update(exam)
        examFirebaseService.update(exam)
    }
    
    suspend fun deleteExam(exam: Exam) {
        examDao.delete(exam)
        examFirebaseService.delete(exam.id)
    }
    
    // Result operations
    fun getAllResults(): Flow<List<ExamResult>> = resultDao.getAll()
    
    suspend fun getResultById(id: String): ExamResult? = resultDao.getById(id)
    
    fun getResultsByStudent(studentId: String): Flow<List<ExamResult>> = resultDao.getResultsByStudent(studentId)
    
    fun getResultsByExam(examId: String): Flow<List<ExamResult>> = resultDao.getResultsByExam(examId)
    
    fun getResultsBySubject(subjectId: String): Flow<List<ExamResult>> = resultDao.getResultsBySubject(subjectId)
    
    fun getResultByStudentAndExam(studentId: String, examId: String): Flow<ExamResult> = 
        resultDao.getResultByStudentAndExam(studentId, examId)
    
    fun getResultsByStudentAndSubject(studentId: String, subjectId: String): Flow<List<ExamResult>> = 
        resultDao.getResultsByStudentAndSubject(studentId, subjectId)
    
    fun getAverageMarksForStudent(studentId: String): Flow<Float> = resultDao.getAverageMarksForStudent(studentId)
    
    fun getAverageMarksForExam(examId: String): Flow<Float> = resultDao.getAverageMarksForExam(examId)
    
    suspend fun insertResult(result: ExamResult) {
        resultDao.insert(result)
        resultFirebaseService.insert(result)
    }
    
    suspend fun updateResult(result: ExamResult) {
        resultDao.update(result)
        resultFirebaseService.update(result)
    }
    
    suspend fun deleteResult(result: ExamResult) {
        resultDao.delete(result)
        resultFirebaseService.delete(result.id)
    }
    
    suspend fun publishResults(examId: String, results: List<ExamResult>) {
        results.forEach { result ->
            val updatedResult = result.copy(isPublished = true, publishedDate = Date())
            updateResult(updatedResult)
        }
    }
    
    suspend fun evaluateResult(result: ExamResult, marksObtained: Float, remarks: String, evaluatedBy: String) {
        val exam = examDao.getById(result.examId)
        val passingMarks = exam?.passingMarks ?: 35 // Default passing marks if exam not found
        val status = if (marksObtained >= passingMarks) ResultStatus.PASSED else ResultStatus.FAILED
        val updatedResult = result.copy(
            marksObtained = marksObtained,
            remarks = remarks,
            status = status,
            evaluatedBy = evaluatedBy,
            evaluationDate = Date()
        )
        updateResult(updatedResult)
    }
    
    // Cloud synchronization
    suspend fun syncExamsWithCloud() {
        val exams = examFirebaseService.getAll()
        exams.forEach { examDao.insert(it) }
    }

    suspend fun syncResultsWithCloud() {
        val results = resultFirebaseService.getAll()
        results.forEach { resultDao.insert(it) }
    }
} 