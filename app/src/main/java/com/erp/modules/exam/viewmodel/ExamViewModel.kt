package com.erp.modules.exam.viewmodel

import androidx.lifecycle.ViewModel
import com.erp.modules.exam.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ExamViewModel : ViewModel() {
    // State flows for the data
    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()
    
    private val _examResults = MutableStateFlow<List<ExamResult>>(emptyList())
    val examResults: StateFlow<List<ExamResult>> = _examResults.asStateFlow()
    
    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()
    
    init {
        // Initialize with mock data
        loadMockData()
    }
    
    private fun loadMockData() {
        // Create mock subjects
        val mockSubjects = listOf(
            Subject(
                id = "sub1",
                name = "Mathematics",
                code = "MATH101",
                description = "Basic mathematics course"
            ),
            Subject(
                id = "sub2",
                name = "Science",
                code = "SCI101",
                description = "Introduction to science"
            ),
            Subject(
                id = "sub3",
                name = "English",
                code = "ENG101",
                description = "English language basics"
            )
        )
        
        // Create mock quizzes
        val mockQuizzes = listOf(
            Quiz(
                id = "q1",
                title = "Math Quiz 1",
                description = "Basic math operations",
                subjectId = "sub1",
                subjectName = "Mathematics",
                totalMarks = 100,
                passingMarks = 40,
                status = QuizStatus.PUBLISHED,
                questions = listOf(
                    QuizQuestion(
                        id = "que1",
                        quizId = "q1",
                        questionText = "What is 2+2?",
                        questionType = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            QuizOption(id = "opt1", optionText = "3", isCorrect = false),
                            QuizOption(id = "opt2", optionText = "4", isCorrect = true),
                            QuizOption(id = "opt3", optionText = "5", isCorrect = false),
                            QuizOption(id = "opt4", optionText = "6", isCorrect = false)
                        ),
                        correctAnswers = listOf("opt2"),
                        marks = 5
                    ),
                    QuizQuestion(
                        id = "que2",
                        quizId = "q1",
                        questionText = "What is 5x5?",
                        questionType = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            QuizOption(id = "opt5", optionText = "20", isCorrect = false),
                            QuizOption(id = "opt6", optionText = "25", isCorrect = true),
                            QuizOption(id = "opt7", optionText = "30", isCorrect = false),
                            QuizOption(id = "opt8", optionText = "35", isCorrect = false)
                        ),
                        correctAnswers = listOf("opt6"),
                        marks = 5
                    )
                )
            ),
            Quiz(
                id = "q2",
                title = "Science Quiz 1",
                description = "Basic science concepts",
                subjectId = "sub2",
                subjectName = "Science",
                totalMarks = 50,
                passingMarks = 20,
                status = QuizStatus.DRAFT
            ),
            Quiz(
                id = "q3",
                title = "English Grammar",
                description = "English grammar assessment",
                subjectId = "sub3",
                subjectName = "English",
                totalMarks = 80,
                passingMarks = 40,
                status = QuizStatus.PUBLISHED
            )
        )
        
        // Create mock exam results
        val mockResults = listOf(
            ExamResult(
                id = "res1",
                studentId = "std1",
                studentName = "John Doe",
                examId = "q1",
                examTitle = "Math Quiz 1",
                score = 85,
                totalMarks = 100,
                status = ResultStatus.PASS
            ),
            ExamResult(
                id = "res2",
                studentId = "std2",
                studentName = "Jane Smith",
                examId = "q1",
                examTitle = "Math Quiz 1",
                score = 35,
                totalMarks = 100,
                status = ResultStatus.FAIL
            ),
            ExamResult(
                id = "res3",
                studentId = "std3",
                studentName = "Bob Johnson",
                examId = "q3",
                examTitle = "English Grammar",
                score = 75,
                totalMarks = 80,
                status = ResultStatus.PASS
            )
        )
        
        // Update state flows
        _subjects.value = mockSubjects
        _quizzes.value = mockQuizzes
        _examResults.value = mockResults
    }
    
    // Quiz operations
    fun getQuizById(id: String): Quiz? {
        return _quizzes.value.find { it.id == id }
    }
    
    fun saveQuiz(quiz: Quiz) {
        val currentQuizzes = _quizzes.value.toMutableList()
        val index = currentQuizzes.indexOfFirst { it.id == quiz.id }
        
        if (index >= 0) {
            // Update existing quiz
            currentQuizzes[index] = quiz
        } else {
            // Add new quiz
            currentQuizzes.add(quiz)
        }
        
        _quizzes.value = currentQuizzes
    }
    
    fun deleteQuiz(quizId: String) {
        _quizzes.update { quizzes ->
            quizzes.filter { it.id != quizId }
        }
    }
    
    fun publishQuiz(quizId: String) {
        _quizzes.update { quizzes ->
            quizzes.map { 
                if (it.id == quizId) it.copy(status = QuizStatus.PUBLISHED) else it 
            }
        }
    }
    
    fun searchQuizzes(query: String): List<Quiz> {
        if (query.isBlank()) return _quizzes.value
        
        return _quizzes.value.filter { quiz ->
            quiz.title.contains(query, ignoreCase = true) ||
                quiz.description.contains(query, ignoreCase = true) ||
                quiz.subjectName.contains(query, ignoreCase = true)
        }
    }
    
    fun loadAllQuizzes() {
        // In a real app, this would fetch from a repository
        // For now, we're just using the mock data
    }
    
    // Result operations
    fun saveResult(result: ExamResult) {
        val currentResults = _examResults.value.toMutableList()
        val index = currentResults.indexOfFirst { it.id == result.id }
        
        if (index >= 0) {
            // Update existing result
            currentResults[index] = result
        } else {
            // Add new result
            currentResults.add(result)
        }
        
        _examResults.value = currentResults
    }
    
    fun getResultById(id: String): ExamResult? {
        return _examResults.value.find { it.id == id }
    }
    
    fun loadResultsByExam(examId: String): List<ExamResult> {
        return _examResults.value.filter { it.examId == examId }
    }
    
    fun generateResultTemplate(quizId: String, studentId: String, studentName: String): ExamResult {
        val quiz = getQuizById(quizId) ?: throw IllegalArgumentException("Quiz not found")
        
        return ExamResult(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            studentName = studentName,
            examId = quiz.id,
            examTitle = quiz.title,
            score = 0, // Initial score is 0
            totalMarks = quiz.totalMarks,
            status = ResultStatus.PENDING
        )
    }
    
    // Mock user operations
    fun getCurrentUserId(): String = "current_user_id"
} 