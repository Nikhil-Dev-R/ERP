package com.erp.modules.exam.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.exam.model.*
import com.erp.core.navigation.ERPDestinations
import com.erp.modules.exam.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreateScreen(
    viewModel: ExamViewModel,
    navController: NavController,
    quizId: String? = null
) {
    val isEditing = quizId != null
    var quiz by remember { mutableStateOf(Quiz()) }
    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    var currentQuestion by remember { mutableStateOf<QuizQuestion?>(null) }
    var isQuestionDialogVisible by remember { mutableStateOf(false) }
    
    // Load quiz if editing
    LaunchedEffect(quizId) {
        if (isEditing && quizId != null) {
            viewModel.getQuizById(quizId)?.let {
                quiz = it
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Quiz" else "Create Quiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(ERPDestinations.QUIZ_MANAGEMENT_ROUTE) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveQuiz(quiz)
                        navController.navigate(ERPDestinations.QUIZ_MANAGEMENT_ROUTE)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic quiz details section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Quiz Details",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        // Title
                        OutlinedTextField(
                            value = quiz.title,
                            onValueChange = { quiz = quiz.copy(title = it) },
                            label = { Text("Quiz Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        // Description
                        OutlinedTextField(
                            value = quiz.description,
                            onValueChange = { quiz = quiz.copy(description = it) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4
                        )
                        
                        // Subject dropdown
                        SubjectDropdown(
                            subjects = subjects,
                            selectedSubjectId = quiz.subjectId,
                            onSubjectSelected = { subjectId ->
                                quiz = quiz.copy(subjectId = subjectId)
                            }
                        )
                        
                        // Grade Level
                        OutlinedTextField(
                            value = quiz.gradeLevel,
                            onValueChange = { quiz = quiz.copy(gradeLevel = it) },
                            label = { Text("Grade/Class Level") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        // Marks
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = quiz.totalMarks.toString(),
                                onValueChange = { 
                                    val marks = it.toIntOrNull() ?: 0
                                    quiz = quiz.copy(totalMarks = marks)
                                },
                                label = { Text("Total Marks") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = quiz.passingMarks.toString(),
                                onValueChange = { 
                                    val marks = it.toIntOrNull() ?: 0
                                    quiz = quiz.copy(passingMarks = marks)
                                },
                                label = { Text("Passing Marks") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true
                            )
                        }
                        
                        // Duration
                        OutlinedTextField(
                            value = quiz.duration.toString(),
                            onValueChange = { 
                                val duration = it.toIntOrNull() ?: 0
                                quiz = quiz.copy(duration = duration)
                            },
                            label = { Text("Duration (minutes)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true
                        )
                        
                        // Quiz settings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = quiz.isRandomized,
                                onCheckedChange = { quiz = quiz.copy(isRandomized = it) }
                            )
                            
                            Text(
                                text = "Randomize Questions",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = quiz.showResultImmediately,
                                onCheckedChange = { quiz = quiz.copy(showResultImmediately = it) }
                            )
                            
                            Text(
                                text = "Show Results Immediately",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        
                        // Instructions
                        OutlinedTextField(
                            value = quiz.instructions,
                            onValueChange = { quiz = quiz.copy(instructions = it) },
                            label = { Text("Instructions") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }
            }
            
            // Questions section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Questions (${quiz.questions.size})",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Button(
                                onClick = {
                                    currentQuestion = null
                                    isQuestionDialogVisible = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Add Question")
                            }
                        }
                        
                        if (quiz.questions.isEmpty()) {
                            EmptyQuestionsState()
                        } else {
                            QuestionsList(
                                questions = quiz.questions,
                                onQuestionClick = { question ->
                                    currentQuestion = question
                                    isQuestionDialogVisible = true
                                },
                                onDeleteQuestion = { question ->
                                    quiz = quiz.copy(
                                        questions = quiz.questions.filter { it.id != question.id }
                                    )
                                }
                            )
                        }
                    }
                }
            }
            
            // Scheduling section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Quiz Schedule",
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        
                        // Start time
                        Button(
                            onClick = {
                                // Show date picker - simplified for this example
                                val now = System.currentTimeMillis()
                                quiz = quiz.copy(startTime = now)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                if (quiz.startTime > 0) 
                                    "Start: ${dateFormat.format(Date(quiz.startTime))}" 
                                else 
                                    "Set Start Time"
                            )
                        }
                        
                        // End time
                        Button(
                            onClick = {
                                // Show date picker - simplified for this example
                                val now = System.currentTimeMillis()
                                quiz = quiz.copy(endTime = now)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                if (quiz.endTime > 0) 
                                    "End: ${dateFormat.format(Date(quiz.endTime))}" 
                                else 
                                    "Set End Time"
                            )
                        }
                    }
                }
            }
        }
        
        // Question dialog
        if (isQuestionDialogVisible) {
            QuestionDialog(
                question = currentQuestion,
                onDismiss = { isQuestionDialogVisible = false },
                onSave = { question ->
                    quiz = if (currentQuestion != null) {
                        // Update existing question
                        quiz.copy(
                            questions = quiz.questions.map {
                                if (it.id == question.id) question else it
                            }
                        )
                    } else {
                        // Add new question
                        quiz.copy(
                            questions = quiz.questions + question.copy(quizId = quiz.id)
                        )
                    }
                    isQuestionDialogVisible = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDropdown(
    subjects: List<Any>, // Replace with your Subject model
    selectedSubjectId: String,
    onSubjectSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedSubjectId, // Ideally this would show subject name
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Subject") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            // Mock subjects - in a real app, you'd use your actual subjects
            listOf("Mathematics", "Science", "English", "History").forEach { subject ->
                DropdownMenuItem(
                    text = { Text(subject) },
                    onClick = {
                        onSubjectSelected(subject) // In a real app, you'd use subject.id
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyQuestionsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No questions added yet. Click the 'Add Question' button to start.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuestionsList(
    questions: List<QuizQuestion>,
    onQuestionClick: (QuizQuestion) -> Unit,
    onDeleteQuestion: (QuizQuestion) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        questions.forEachIndexed { index, question ->
            QuestionListItem(
                index = index + 1,
                question = question,
                onClick = { onQuestionClick(question) },
                onDelete = { onDeleteQuestion(question) }
            )
        }
    }
}

@Composable
fun QuestionListItem(
    index: Int,
    question: QuizQuestion,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Text(
                    text = "Type: ${question.questionType.name}, Marks: ${question.marks}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDialog(
    question: QuizQuestion?,
    onDismiss: () -> Unit,
    onSave: (QuizQuestion) -> Unit
) {
    val isEditing = question != null
    var questionText by remember { mutableStateOf(question?.questionText ?: "") }
    var questionType by remember { mutableStateOf(question?.questionType ?: QuestionType.MULTIPLE_CHOICE) }
    var marks by remember { mutableStateOf(question?.marks?.toString() ?: "1") }
    var options by remember { mutableStateOf(question?.options ?: emptyList()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Question" else "Add Question") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Question text
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                // Question type
                Text(
                    text = "Question Type",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuestionType.values().forEach { type ->
                        FilterChip(
                            selected = questionType == type,
                            onClick = { questionType = type },
                            label = { Text(type.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) }
                        )
                    }
                }
                
                // Marks
                OutlinedTextField(
                    value = marks,
                    onValueChange = { marks = it },
                    label = { Text("Marks") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
                
                // Options (for MCQ, Single Choice, T/F)
                if (questionType in listOf(QuestionType.MULTIPLE_CHOICE, QuestionType.SINGLE_CHOICE, QuestionType.TRUE_FALSE)) {
                    Text(
                        text = "Options",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (questionType == QuestionType.TRUE_FALSE) {
                        // For T/F, always have fixed True/False options
                        options = listOf(
                            QuizOption(id = "true", optionText = "True", isCorrect = false),
                            QuizOption(id = "false", optionText = "False", isCorrect = false)
                        )
                    }
                    
                    options.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = option.isCorrect,
                                onCheckedChange = { isCorrect ->
                                    val updatedOptions = options.toMutableList()
                                    updatedOptions[index] = option.copy(isCorrect = isCorrect)
                                    options = updatedOptions
                                }
                            )
                            
                            OutlinedTextField(
                                value = option.optionText,
                                onValueChange = { optionText ->
                                    val updatedOptions = options.toMutableList()
                                    updatedOptions[index] = option.copy(optionText = optionText)
                                    options = updatedOptions
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                readOnly = questionType == QuestionType.TRUE_FALSE
                            )
                            
                            if (questionType != QuestionType.TRUE_FALSE) {
                                IconButton(
                                    onClick = {
                                        options = options.filterIndexed { i, _ -> i != index }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Option"
                                    )
                                }
                            }
                        }
                    }
                    
                    if (questionType != QuestionType.TRUE_FALSE && options.size < 6) {
                        Button(
                            onClick = {
                                options = options + QuizOption(
                                    id = UUID.randomUUID().toString(),
                                    optionText = "",
                                    isCorrect = false
                                )
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Add Option")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newQuestion = (question ?: QuizQuestion(id = UUID.randomUUID().toString())).copy(
                        questionText = questionText,
                        questionType = questionType,
                        marks = marks.toIntOrNull() ?: 1,
                        options = options,
                        correctAnswers = options.filter { it.isCorrect }.map { it.id }
                    )
                    onSave(newQuestion)
                },
                enabled = questionText.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 