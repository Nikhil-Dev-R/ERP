package com.erp.modules.exam.model

import java.util.UUID

enum class QuizStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class ResultStatus {
    PASS,
    FAIL,
    PENDING
}

enum class QuestionType {
    MULTIPLE_CHOICE,
    SINGLE_CHOICE,
    TRUE_FALSE,
    SHORT_ANSWER,
    ESSAY
}

data class Subject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val description: String = "",
    val gradeLevel: String = ""
)

data class Quiz(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val subjectId: String = "",
    val subjectName: String = "",
    val gradeLevel: String = "",
    val timeLimit: Int = 60, // in minutes
    val duration: Int = 60, // in minutes (alias for timeLimit)
    val totalMarks: Int = 100,
    val passingMarks: Int = 40,
    val status: QuizStatus = QuizStatus.DRAFT,
    val questions: List<QuizQuestion> = emptyList(),
    val startTime: Long = 0,
    val endTime: Long = 0,
    val instructions: String = "",
    val isRandomized: Boolean = false,
    val showResultImmediately: Boolean = true,
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Question(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val questionText: String = "", // Alias for text
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE, // Alias for type
    val options: List<QuizOption> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val marks: Int = 1
)

data class QuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val quizId: String = "",
    val questionText: String = "",
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val options: List<QuizOption> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val marks: Int = 1
)

data class QuizOption(
    val id: String = UUID.randomUUID().toString(),
    val optionText: String = "",
    val isCorrect: Boolean = false
)

data class ExamResult(
    val id: String = UUID.randomUUID().toString(),
    val studentId: String,
    val studentName: String,
    val examId: String,
    val examTitle: String,
    val score: Int,
    val totalMarks: Int,
    val status: ResultStatus,
    val submissionDate: Long = System.currentTimeMillis(),
    val answers: List<AnswerResponse> = emptyList()
)

data class AnswerResponse(
    val questionId: String,
    val questionText: String,
    val studentAnswer: String,
    val isCorrect: Boolean,
    val marks: Int
) 