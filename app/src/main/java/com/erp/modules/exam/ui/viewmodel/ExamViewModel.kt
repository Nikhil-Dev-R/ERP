package com.erp.modules.exam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.exam.data.model.*
import com.erp.modules.exam.data.repository.ExamRepository
import com.erp.modules.exam.data.repository.QuizRepository
import com.erp.modules.student.data.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

class ExamViewModel(
    private val examRepository: ExamRepository,
    private val quizRepository: QuizRepository? = null
) : ViewModel() {
    // UI state for exams list
    private val _examsState = MutableStateFlow<ExamsUiState>(ExamsUiState.Loading)
    val examsState: StateFlow<ExamsUiState> = _examsState
    
    // UI state for exam details
    private val _examDetailState = MutableStateFlow<ExamDetailState>(ExamDetailState.Loading)
    val examDetailState: StateFlow<ExamDetailState> = _examDetailState
    
    // UI state for results
    private val _resultsState = MutableStateFlow<ResultsUiState>(ResultsUiState.Loading)
    val resultsState: StateFlow<ResultsUiState> = _resultsState
    
    // UI state for student results
    private val _studentResultsState = MutableStateFlow<StudentResultsUiState>(StudentResultsUiState.Loading)
    val studentResultsState: StateFlow<StudentResultsUiState> = _studentResultsState
    
    // Current editing exam
    private val _currentExam = MutableStateFlow<Exam?>(null)
    val currentExam: StateFlow<Exam?> = _currentExam

    // Exams list
    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams
    
    // Results list
    private val _examResults = MutableStateFlow<List<ExamResult>>(emptyList())
    val examResults: StateFlow<List<ExamResult>> = _examResults
    
    // Quizzes list
    private val _quizzes = MutableStateFlow<List<Quiz>>(emptyList())
    val quizzes: StateFlow<List<Quiz>> = _quizzes
    
    // Subjects list (mock data for now)
    private val _subjects = MutableStateFlow<List<Any>>(emptyList())
    val subjects: StateFlow<List<Any>> = _subjects
    
    // Initialize with mock data
    init {
        loadMockData()
        loadAllQuizzes()
    }
    
    private fun loadMockData() {
        val calendar = Calendar.getInstance()
        
        // Set start date to yesterday
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val startDate = calendar.time
        
        // Set end date to tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        val endDate = calendar.time
        
        // Create mock exams
        val mockExams = listOf(
            Exam(
                id = "exam1",
                name = "Mid-Term Mathematics",
                description = "Mid-term mathematics examination covering algebra and geometry",
                examType = ExamType.MID_TERM,
                startDate = startDate,
                endDate = endDate,
                academicYear = "2023-2024",
                gradeLevel = "9",
                subjectId = "MATH101",
                totalMarks = 100,
                passingMarks = 35,
                status = ExamStatus.IN_PROGRESS
            ),
            Exam(
                id = "exam2",
                name = "English Quiz",
                description = "Weekly English grammar quiz",
                examType = ExamType.CLASS_TEST,
                startDate = Date(),
                endDate = Date(),
                academicYear = "2023-2024",
                gradeLevel = "9",
                subjectId = "ENG101",
                totalMarks = 20,
                passingMarks = 8,
                status = ExamStatus.COMPLETED
            ),
            Exam(
                id = "exam3",
                name = "Science Project",
                description = "Term project for science class",
                examType = ExamType.PROJECT,
                startDate = Date(),
                endDate = calendar.time,
                academicYear = "2023-2024",
                gradeLevel = "9",
                subjectId = "SCI101",
                totalMarks = 50,
                passingMarks = 20,
                status = ExamStatus.SCHEDULED
            )
        )
        
        _examsState.value = ExamsUiState.Success(mockExams)
        _exams.value = mockExams
        
        // Create mock subjects
        val mockSubjects = listOf(
            "MATH101" to "Mathematics",
            "ENG101" to "English",
            "SCI101" to "Science"
        )
        _subjects.value = mockSubjects
        
        // Create mock students
        val mockStudents = listOf(
            Student(
                firstName = "John",
                lastName = "Doe",
                enrollmentNumber = "S001",
                grade = "9",
                section = "A"
            ).apply {
                id = "student1"
            },
            Student(
                firstName = "Jane",
                lastName = "Smith",
                enrollmentNumber = "S002",
                grade = "9",
                section = "A"
            ).apply {
                id = "student2"
            },
            Student(
                firstName = "Bob",
                lastName = "Johnson",
                enrollmentNumber = "S003",
                grade = "9",
                section = "A"
            ).apply {
                id = "student3"
            }
        )
        
        // Create mock results for English Quiz (exam2)
        val mockResults = mockStudents.map { student ->
            ExamResult(
                examId = "exam2",
                studentId = student.id,
                subjectId = "ENG101",
                marksObtained = (10..20).random().toFloat(),
                grade = "A",
                status = ResultStatus.PASSED,
                evaluatedBy = "teacher1",
                evaluationDate = Date(),
                isPublished = true,
                publishedDate = Date()
            ).apply {
                id = UUID.randomUUID().toString()
            }
        }
        
        _resultsState.value = ResultsUiState.Success(mockResults)
        _examResults.value = mockResults
        
        // Create mock quizzes
        val mockQuizzes = listOf(
            Quiz(
                id = "quiz1",
                title = "Mathematics Quiz 1",
                description = "Basic algebra quiz",
                subjectId = "MATH101",
                gradeLevel = "9",
                totalMarks = 20,
                passingMarks = 10,
                duration = 30,
                startTime = startDate,
                endTime = endDate,
                instructions = "Answer all questions. Use calculator if needed.",
                createdBy = "teacher1",
                status = QuizStatus.PUBLISHED,
                questions = listOf(
                    QuizQuestion(
                        id = "q1",
                        quizId = "quiz1",
                        questionText = "What is 2 + 2?",
                        questionType = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            QuizOption(id = "o1", optionText = "3", isCorrect = false),
                            QuizOption(id = "o2", optionText = "4", isCorrect = true),
                            QuizOption(id = "o3", optionText = "5", isCorrect = false)
                        ),
                        correctAnswers = listOf("o2"),
                        marks = 5
                    )
                )
            ),
            Quiz(
                id = "quiz2",
                title = "English Grammar Quiz",
                description = "Parts of speech quiz",
                subjectId = "ENG101",
                gradeLevel = "9",
                status = QuizStatus.DRAFT
            )
        )
        
        _quizzes.value = mockQuizzes
    }
    
    // Exam functions
    fun loadAllExams() {
        viewModelScope.launch {
            _examsState.value = ExamsUiState.Loading
            
            // In a real app, we would fetch from repository
            // For now, just show the mock data again
            val exams = (_examsState.value as? ExamsUiState.Success)?.exams
            
            if (exams != null) {
                _examsState.value = ExamsUiState.Success(exams)
                _exams.value = exams
            } else {
                _examsState.value = ExamsUiState.Empty
            }
        }
    }
    
    fun getExamDetail(id: String) {
        viewModelScope.launch {
            _examDetailState.value = ExamDetailState.Loading
            
            val exam = (_examsState.value as? ExamsUiState.Success)?.exams?.find { it.id == id }
            
            if (exam != null) {
                _examDetailState.value = ExamDetailState.Success(exam)
                _currentExam.value = exam
                
                // Also load results for this exam
                loadResultsByExam(id)
            } else {
                _examDetailState.value = ExamDetailState.Error("Exam not found")
            }
        }
    }
    
    fun createNewExam() {
        _currentExam.value = Exam(
            academicYear = "2023-2024",
            startDate = Date(),
            endDate = Date()
        )
    }
    
    fun saveExam(exam: Exam) {
        viewModelScope.launch {
            // In a real app, we would save to repository
            val currentExams = (_examsState.value as? ExamsUiState.Success)?.exams ?: emptyList()
            val updatedExams = currentExams.toMutableList()
            
            val existingIndex = updatedExams.indexOfFirst { it.id == exam.id }
            if (existingIndex >= 0) {
                updatedExams[existingIndex] = exam
            } else {
                updatedExams.add(exam)
            }
            
            _examsState.value = ExamsUiState.Success(updatedExams)
            _exams.value = updatedExams
            
            // Update the current exam
            if (_currentExam.value?.id == exam.id) {
                _currentExam.value = exam
            }
        }
    }
    
    // Result functions
    fun loadResultsByExam(examId: String) {
        viewModelScope.launch {
            _resultsState.value = ResultsUiState.Loading
            
            // In a real app, we would fetch from repository
            val results = (_resultsState.value as? ResultsUiState.Success)?.results
                ?.filter { it.examId == examId }
                
            if (results != null && results.isNotEmpty()) {
                _resultsState.value = ResultsUiState.Success(results)
                _examResults.value = results
            } else {
                _resultsState.value = ResultsUiState.Empty
                _examResults.value = emptyList()
            }
        }
    }
    
    fun loadResultsForStudent(studentId: String) {
        viewModelScope.launch {
            _studentResultsState.value = StudentResultsUiState.Loading
            
            // In a real app, we would fetch from repository
            val results = (_resultsState.value as? ResultsUiState.Success)?.results
                ?.filter { it.studentId == studentId }
                
            if (results != null && results.isNotEmpty()) {
                _studentResultsState.value = StudentResultsUiState.Success(results)
            } else {
                _studentResultsState.value = StudentResultsUiState.Empty
            }
        }
    }
    
    fun saveResult(result: ExamResult) {
        viewModelScope.launch {
            // In a real app, we would save to repository
            val currentResults = (_resultsState.value as? ResultsUiState.Success)?.results ?: emptyList()
            val updatedResults = currentResults.toMutableList()
            
            val existingIndex = updatedResults.indexOfFirst { it.id == result.id }
            if (existingIndex >= 0) {
                updatedResults[existingIndex] = result
            } else {
                updatedResults.add(result)
            }
            
            _resultsState.value = ResultsUiState.Success(updatedResults)
            _examResults.value = updatedResults
        }
    }
    
    // Quiz functions
    fun loadAllQuizzes() {
        viewModelScope.launch {
            try {
                if (quizRepository != null) {
                    val quizzes = quizRepository.getAllQuizzes().first()
                    _quizzes.value = quizzes
                }
            } catch (e: Exception) {
                // Use mock data instead
                // _quizzes already has mock data from init
            }
        }
    }
    
    fun searchQuizzes(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadAllQuizzes()
                return@launch
            }
            
            try {
                if (quizRepository != null) {
                    val quizzes = quizRepository.searchQuizzes(query).first()
                    _quizzes.value = quizzes
                } else {
                    // Filter mock data
                    val filteredQuizzes = _quizzes.value.filter {
                        it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                    }
                    _quizzes.value = filteredQuizzes
                }
            } catch (e: Exception) {
                // Keep current quizzes
            }
        }
    }
    
    suspend fun getQuizById(id: String): Quiz? {
        return quizRepository?.getQuizById(id) ?: _quizzes.value.find { it.id == id }
    }
    
    fun saveQuiz(quiz: Quiz) {
        viewModelScope.launch {
            try {
                if (quizRepository != null) {
                    if (quiz.id.isBlank() || getQuizById(quiz.id) == null) {
                        quizRepository.insertQuiz(quiz)
                    } else {
                        quizRepository.updateQuiz(quiz)
                    }
                    loadAllQuizzes()
                } else {
                    // Update mock data
                    val currentQuizzes = _quizzes.value.toMutableList()
                    val existingIndex = currentQuizzes.indexOfFirst { it.id == quiz.id }
                    
                    if (existingIndex >= 0) {
                        currentQuizzes[existingIndex] = quiz
                    } else {
                        currentQuizzes.add(quiz)
                    }
                    
                    _quizzes.value = currentQuizzes
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteQuiz(quiz: Quiz) {
        viewModelScope.launch {
            try {
                if (quizRepository != null) {
                    quizRepository.deleteQuiz(quiz)
                    loadAllQuizzes()
                } else {
                    // Update mock data
                    val updatedQuizzes = _quizzes.value.filter { it.id != quiz.id }
                    _quizzes.value = updatedQuizzes
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun publishQuiz(quiz: Quiz) {
        viewModelScope.launch {
            try {
                val updatedQuiz = quiz.copy(status = QuizStatus.PUBLISHED)
                
                if (quizRepository != null) {
                    quizRepository.publishQuiz(updatedQuiz)
                    loadAllQuizzes()
                } else {
                    // Update mock data
                    val currentQuizzes = _quizzes.value.toMutableList()
                    val existingIndex = currentQuizzes.indexOfFirst { it.id == quiz.id }
                    
                    if (existingIndex >= 0) {
                        currentQuizzes[existingIndex] = updatedQuiz
                    }
                    
                    _quizzes.value = currentQuizzes
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    // Utility functions
    fun getCurrentUserId(): String {
        // In a real app, this would come from authentication
        return "teacher1"
    }
    
    fun generateResultTemplate(exam: Exam) {
        // In a real app, this would generate and download a template file
        // For now, this is just a placeholder
    }
}

// UI States
sealed class ExamsUiState {
    object Loading : ExamsUiState()
    data class Success(val exams: List<Exam>) : ExamsUiState()
    object Empty : ExamsUiState()
    data class Error(val message: String) : ExamsUiState()
}

sealed class ExamDetailState {
    object Loading : ExamDetailState()
    data class Success(val exam: Exam) : ExamDetailState()
    data class Error(val message: String) : ExamDetailState()
}

sealed class ResultsUiState {
    object Loading : ResultsUiState()
    data class Success(val results: List<ExamResult>) : ResultsUiState()
    object Empty : ResultsUiState()
    data class Error(val message: String) : ResultsUiState()
}

sealed class StudentResultsUiState {
    object Loading : StudentResultsUiState()
    data class Success(val results: List<ExamResult>) : StudentResultsUiState()
    object Empty : StudentResultsUiState()
    data class Error(val message: String) : StudentResultsUiState()
} 