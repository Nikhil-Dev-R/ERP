package com.erp.modules.exam.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.exam.model.Quiz
import com.erp.modules.exam.model.QuizStatus
import com.erp.modules.exam.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.erp.core.navigation.ERPDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizManagementScreen(
    viewModel: ExamViewModel,
    navController: NavController
) {
    val quizzes by viewModel.quizzes.collectAsState(initial = emptyList())
    val searchText = remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FloatingActionButton(
                        onClick = { navController.navigate(ERPDestinations.QUIZ_CREATE_ROUTE) },
                        content = {
                            Icon(Icons.Default.Add, contentDescription = "Create Quiz")
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchText.value,
                onValueChange = { 
                    searchText.value = it
                    viewModel.searchQuizzes(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search quizzes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.value.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchText.value = ""
                            viewModel.loadAllQuizzes()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )
            
            if (quizzes.isEmpty()) {
                EmptyQuizState()
            } else {
                QuizList(
                    quizzes = quizzes,
                    onQuizClick = { quiz ->
                        navController.navigate("${ERPDestinations.QUIZ_EDIT_ROUTE}/${quiz.id}")
                    },
                    onEditClick = { quiz ->
                        navController.navigate("${ERPDestinations.QUIZ_EDIT_ROUTE}/${quiz.id}")
                    },
                    onDeleteClick = { quiz ->
                        viewModel.deleteQuiz(quiz.id)
                    },
                    onPublishClick = { quiz ->
                        viewModel.publishQuiz(quiz.id)
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyQuizState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "No quizzes found",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create a new quiz to get started",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // Navigate to create quiz screen
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Create Quiz")
            }
        }
    }
}

@Composable
fun QuizList(
    quizzes: List<Quiz>,
    onQuizClick: (Quiz) -> Unit,
    onEditClick: (Quiz) -> Unit,
    onDeleteClick: (Quiz) -> Unit,
    onPublishClick: (Quiz) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(quizzes) { quiz ->
            QuizListItem(
                quiz = quiz,
                onClick = { onQuizClick(quiz) },
                onEditClick = { onEditClick(quiz) },
                onDeleteClick = { onDeleteClick(quiz) },
                onPublishClick = { onPublishClick(quiz) }
            )
        }
    }
}

@Composable
fun QuizListItem(
    quiz: Quiz,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                StatusChip(status = quiz.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = quiz.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${quiz.duration} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${quiz.totalMarks} marks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = quiz.startTime?.let { dateFormat.format(Date(it)) } ?: "Not scheduled",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (quiz.status == QuizStatus.DRAFT) {
                    TextButton(onClick = onPublishClick) {
                        Text("Publish")
                    }
                }
                
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
                
                TextButton(onClick = onDeleteClick) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: QuizStatus) {
    val (color, text) = when (status) {
        QuizStatus.DRAFT -> MaterialTheme.colorScheme.secondary to "Draft"
        QuizStatus.PUBLISHED -> MaterialTheme.colorScheme.primary to "Published"
        QuizStatus.ACTIVE -> MaterialTheme.colorScheme.tertiary to "Active"
        QuizStatus.COMPLETED -> MaterialTheme.colorScheme.primary to "Completed"
        QuizStatus.CANCELLED -> MaterialTheme.colorScheme.error to "Cancelled"
        QuizStatus.ARCHIVED -> MaterialTheme.colorScheme.outline to "Archived"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
} 