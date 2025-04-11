package com.erp.modules.exam.data.repository

import com.erp.data.remote.FirebaseService
import com.erp.modules.exam.data.dao.QuizAttemptDao
import com.erp.modules.exam.data.dao.QuizDao
import com.erp.modules.exam.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

class QuizRepository(
    private val quizDao: QuizDao,
    private val quizAttemptDao: QuizAttemptDao,
    private val quizFirebaseService: FirebaseService<Quiz>,
    private val quizAttemptFirebaseService: FirebaseService<QuizAttempt>
) {
    // Quiz operations
    fun getAllQuizzes(): Flow<List<Quiz>> = quizDao.getAll()

    suspend fun getQuizById(id: String): Quiz? = quizDao.getById(id)

    fun searchQuizzes(query: String): Flow<List<Quiz>> = quizDao.searchQuizzes("%$query%")

    fun getQuizzesBySubject(subjectId: String): Flow<List<Quiz>> = quizDao.getQuizzesBySubject(subjectId)

    fun getQuizzesByGradeLevel(gradeLevel: String): Flow<List<Quiz>> = quizDao.getQuizzesByGradeLevel(gradeLevel)

    fun getQuizzesByStatus(status: QuizStatus): Flow<List<Quiz>> = quizDao.getQuizzesByStatus(status)

    fun getQuizzesBetweenDates(startDate: Date, endDate: Date): Flow<List<Quiz>> =
        quizDao.getQuizzesBetweenDates(startDate, endDate)

    fun getQuizzesByTeacher(teacherId: String): Flow<List<Quiz>> = quizDao.getQuizzesByTeacher(teacherId)

    suspend fun insertQuiz(quiz: Quiz) {
        quizDao.insert(quiz)
        quizFirebaseService.insert(quiz)
    }

    suspend fun updateQuiz(quiz: Quiz) {
        quizDao.update(quiz)
        quizFirebaseService.update(quiz)
    }

    suspend fun deleteQuiz(quiz: Quiz) {
        quizDao.delete(quiz)
        quizFirebaseService.delete(quiz.id)
    }

    suspend fun publishQuiz(quiz: Quiz) {
        val updatedQuiz = quiz.copy(status = QuizStatus.PUBLISHED)
        updateQuiz(updatedQuiz)
    }

    // Quiz attempt operations
    fun getAllQuizAttempts(): Flow<List<QuizAttempt>> = quizAttemptDao.getAll()

    suspend fun getQuizAttemptById(id: String): QuizAttempt? = quizAttemptDao.getById(id)

    fun getQuizAttemptsByQuiz(quizId: String): Flow<List<QuizAttempt>> = quizAttemptDao.getAttemptsByQuiz(quizId)

    fun getQuizAttemptsByStudent(studentId: String): Flow<List<QuizAttempt>> = quizAttemptDao.getAttemptsByStudent(studentId)

    fun getQuizAttemptByQuizAndStudent(quizId: String, studentId: String): Flow<QuizAttempt?> =
        quizAttemptDao.getAttemptByQuizAndStudent(quizId, studentId)

    fun getAverageScoreForQuiz(quizId: String): Flow<Float> = quizAttemptDao.getAverageScoreForQuiz(quizId)

    fun getAverageScoreForStudent(studentId: String): Flow<Float> = quizAttemptDao.getAverageScoreForStudent(studentId)

    suspend fun insertQuizAttempt(quizAttempt: QuizAttempt) {
        quizAttemptDao.insert(quizAttempt)
        quizAttemptFirebaseService.insert(quizAttempt)
    }

    suspend fun updateQuizAttempt(quizAttempt: QuizAttempt) {
        quizAttemptDao.update(quizAttempt)
        quizAttemptFirebaseService.update(quizAttempt)
    }

    suspend fun deleteQuizAttempt(quizAttempt: QuizAttempt) {
        quizAttemptDao.delete(quizAttempt)
        quizAttemptFirebaseService.delete(quizAttempt.id)
    }

    suspend fun submitQuizAttempt(quizAttempt: QuizAttempt) {
        val updatedAttempt = quizAttempt.copy(
            endTime = Date(),
            status = QuizAttemptStatus.SUBMITTED
        )
        updateQuizAttempt(updatedAttempt)
    }

    suspend fun scoreQuizAttempt(quizAttempt: QuizAttempt, quiz: Quiz): QuizAttempt {
        // Calculate the score based on correct answers
        var totalScore = 0
        val scoredAnswers = quizAttempt.answers.map { answer ->
            val question = quiz.questions.find { it.id == answer.questionId }
            if (question != null) {
                // Calculate score based on question type
                val isCorrect = when (question.questionType) {
                    QuestionType.MULTIPLE_CHOICE, QuestionType.SINGLE_CHOICE -> {
                        // Check if selected options match correct options
                        answer.selectedOptions.toSet() == question.correctAnswers.toSet()
                    }
                    QuestionType.TRUE_FALSE -> {
                        // For T/F, check if the single selected option is correct
                        answer.selectedOptions.isNotEmpty() && answer.selectedOptions.first() == question.correctAnswers.first()
                    }
                    // For text answers, manual evaluation is required
                    QuestionType.SHORT_ANSWER, QuestionType.LONG_ANSWER -> false
                    else -> false
                }

                val marksAwarded = if (isCorrect) question.marks else 0
                totalScore += marksAwarded
                answer.copy(isCorrect = isCorrect, marksAwarded = marksAwarded)
            } else {
                answer
            }
        }

        val updatedAttempt = quizAttempt.copy(
            status = QuizAttemptStatus.EVALUATED,
            score = totalScore,
            totalMarks = quiz.totalMarks,
            answers = scoredAnswers
        )

        updateQuizAttempt(updatedAttempt)
        return updatedAttempt
    }

    // Cloud synchronization
    suspend fun syncQuizzesWithCloud() {
        val quizzes = quizFirebaseService.getAll()
        quizzes.forEach { quizDao.insert(it) }
    }

    suspend fun syncQuizAttemptsWithCloud() {
        val attempts = quizAttemptFirebaseService.getAll()
        attempts.forEach { quizAttemptDao.insert(it) }
    }
} 