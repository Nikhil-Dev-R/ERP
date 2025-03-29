package com.erp.modules.exam.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.exam.viewmodel.ExamViewModel
import com.erp.modules.exam.model.ExamResult
import com.erp.modules.exam.model.ResultStatus
import kotlinx.coroutines.launch
import com.erp.core.navigation.ERPDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsViewScreen(
    viewModel: ExamViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val results by viewModel.examResults.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<ResultStatus?>(null) }
    var selectedExamId by remember { mutableStateOf<String?>(null) }
    val quizzes by viewModel.quizzes.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Filter results based on search and filters
    val filteredResults = results.filter { result ->
        (searchQuery.isEmpty() || 
            result.studentName.contains(searchQuery, ignoreCase = true) ||
            result.studentId.contains(searchQuery, ignoreCase = true) ||
            result.examTitle.contains(searchQuery, ignoreCase = true)) &&
        (selectedStatus == null || result.status == selectedStatus) &&
        (selectedExamId == null || result.examId == selectedExamId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exam Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        navController.navigate(ERPDestinations.RESULTS_UPLOAD_ROUTE)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Upload Results")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search by student name, ID or exam") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )
            
            // Filters section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status filter
                FilterChip(
                    selected = selectedStatus != null,
                    onClick = { 
                        selectedStatus = if (selectedStatus == null) ResultStatus.PASS else null 
                    },
                    label = { 
                        Text(selectedStatus?.name?.lowercase()?.replaceFirstChar { 
                            it.uppercase() 
                        } ?: "Status") 
                    },
                    leadingIcon = {
                        if (selectedStatus != null) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                )
                
                // Exam filter
                FilterChip(
                    selected = selectedExamId != null,
                    onClick = { selectedExamId = if (selectedExamId == null) 
                        quizzes.firstOrNull()?.id else null 
                    },
                    label = { 
                        Text(quizzes.find { it.id == selectedExamId }?.title ?: "Exam") 
                    },
                    leadingIcon = {
                        if (selectedExamId != null) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                )
                
                // Reset filters
                if (selectedStatus != null || selectedExamId != null || searchQuery.isNotEmpty()) {
                    FilledTonalButton(
                        onClick = {
                            selectedStatus = null
                            selectedExamId = null
                            searchQuery = ""
                        },
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Reset")
                    }
                }
            }
            
            if (results.isEmpty()) {
                EmptyResultsState(onUploadClick = { navController.navigate(ERPDestinations.RESULTS_UPLOAD_ROUTE) })
            } else if (filteredResults.isEmpty()) {
                NoMatchingResultsState()
            } else {
                // Results list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredResults) { result ->
                        ResultItem(
                            result = result,
                            onClick = { 
                                navController.navigate("${ERPDestinations.RESULT_DETAIL_ROUTE}/${result.id}") 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultItem(
    result: ExamResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                
                ResultStatusChip(status = result.status)
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
private fun ResultStatusChip(status: ResultStatus) {
    val backgroundColor = when (status) {
        ResultStatus.PASS -> MaterialTheme.colorScheme.primary
        ResultStatus.FAIL -> MaterialTheme.colorScheme.error
        ResultStatus.PENDING -> MaterialTheme.colorScheme.tertiary
    }
    
    Surface(
        color = backgroundColor.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelMedium,
            color = backgroundColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyResultsState(onUploadClick: () -> Unit) {
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
            text = "Upload exam results to view them here",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onUploadClick) {
            Icon(Icons.Default.Upload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload Results")
        }
    }
}

@Composable
fun NoMatchingResultsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FilterAlt,
            contentDescription = "No Matching Results",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No matching results found",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Try adjusting your filters",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
} 