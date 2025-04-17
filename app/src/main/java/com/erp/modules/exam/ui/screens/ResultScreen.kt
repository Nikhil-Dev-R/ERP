package com.erp.modules.exam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.core.navigation.ERPDestinations
import com.erp.modules.exam.model.ExamResult
import com.erp.modules.exam.model.Quiz
import com.erp.modules.exam.model.ResultStatus
import com.erp.modules.exam.viewmodel.ExamViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class UploadMethod {
    INDIVIDUAL,
    BULK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: ExamViewModel,
    navController: NavController
) {
    val results by viewModel.examResults.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()
    
    var selectedExamId by remember { mutableStateOf("") }
    
    LaunchedEffect(selectedExamId) {
        if (selectedExamId.isNotEmpty()) {
            viewModel.loadResultsByExam(selectedExamId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (results.isEmpty()) {
                EmptyResultsState()
            } else {
                Text(
                    text = "Exam Results",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                
                // Dropdown to select exam
                ExamSelectionDropdown(
                    exams = quizzes,
                    selectedExamId = selectedExamId,
                    onExamSelected = { selectedExamId = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Results list
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        if (selectedExamId.isEmpty()) results 
                        else results.filter { it.examId == selectedExamId }
                    ) { result ->
                        ResultItem(result = result)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSelectionDropdown(
    exams: List<Quiz>,
    selectedExamId: String,
    onExamSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = exams.find { it.id == selectedExamId }?.title ?: "All Exams",
            onValueChange = { },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
//                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Add an "All Exams" option
            DropdownMenuItem(
                text = { Text("All Exams") },
                onClick = {
                    onExamSelected("")
                    expanded = false
                }
            )
            
            // Add exam options
            exams.forEach { exam ->
                DropdownMenuItem(
                    text = { Text(exam.title) },
                    onClick = {
                        onExamSelected(exam.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDropdown(
    exams: List<Quiz>,
    selectedExam: Quiz?,
    onExamSelected: (Quiz) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedExam?.title ?: "Select an exam",
            onValueChange = { },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
//                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            exams.forEach { exam ->
                DropdownMenuItem(
                    text = { Text(exam.title) },
                    onClick = {
                        onExamSelected(exam)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyResultsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Assessment,
            contentDescription = "No Results",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No Results Available",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Upload some results or create a new exam",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResultItem(result: ExamResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
                    text = result.studentName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                StatusChip(status = result.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${result.studentId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Score: ${result.score}/${result.totalMarks}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = result.examTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatusChip(status: ResultStatus) {
    val (color, text) = when (status) {
        ResultStatus.PASS -> MaterialTheme.colorScheme.primary to "Pass"
        ResultStatus.FAIL -> MaterialTheme.colorScheme.error to "Fail"
        ResultStatus.PENDING -> MaterialTheme.colorScheme.tertiary to "Pending"
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