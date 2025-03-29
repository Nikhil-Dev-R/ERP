package com.erp.modules.exam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    
    val title: String = "",
    val description: String = "",
    val subjectId: String = "",
    val gradeLevel: String = "",
    val totalMarks: Int = 100,
    val passingMarks: Int = 35,
    val duration: Int = 30, // in minutes
    val startTime: Date? = null,
    val endTime: Date? = null,
    val instructions: String = "",
    val createdBy: String = "",
    val status: QuizStatus = QuizStatus.DRAFT,
    val isRandomized: Boolean = false,
    val showResultImmediately: Boolean = true,
    val questions: List<QuizQuestion> = emptyList()
) : BaseEntity()

data class QuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val quizId: String = "",
    val questionText: String = "",
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val options: List<QuizOption> = emptyList(),
    val correctAnswers: List<String> = emptyList(), // IDs of correct options
    val marks: Int = 1
)

data class QuizOption(
    val id: String = UUID.randomUUID().toString(),
    val optionText: String = "",
    val isCorrect: Boolean = false
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    SINGLE_CHOICE,
    TRUE_FALSE,
    SHORT_ANSWER,
    LONG_ANSWER
}

enum class QuizStatus {
    DRAFT,
    PUBLISHED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    
    val quizId: String = "",
    val studentId: String = "",
    val startTime: Date = Date(),
    val endTime: Date? = null,
    val score: Int = 0,
    val totalMarks: Int = 0,
    val status: QuizAttemptStatus = QuizAttemptStatus.IN_PROGRESS,
    val answers: List<QuizAnswer> = emptyList()
) : BaseEntity()

data class QuizAnswer(
    val questionId: String = "",
    val selectedOptions: List<String> = emptyList(), // IDs of selected options
    val textAnswer: String = "", // For short/long answer type questions
    val isCorrect: Boolean = false,
    val marksAwarded: Int = 0
)

enum class QuizAttemptStatus {
    IN_PROGRESS,
    COMPLETED,
    TIMED_OUT,
    SUBMITTED,
    EVALUATED
} 