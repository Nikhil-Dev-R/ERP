package com.erp.modules.exam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.exam.viewmodel.ExamViewModel
import com.erp.modules.exam.model.ExamResult
import com.erp.modules.exam.model.ResultStatus
import com.erp.core.navigation.ERPDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsUploadScreen(
    viewModel: ExamViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val quizzes by viewModel.quizzes.collectAsState()
    var selectedQuizId by remember { mutableStateOf("") }
    var fileUploaded by remember { mutableStateOf(false) }
    var resultsPreview by remember { mutableStateOf<List<ExamResult>>(emptyList()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quizzes.isEmpty()) {
                EmptyQuizzesState()
            } else {
                // Quiz selection dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { },
                ) {
                    OutlinedTextField(
                        value = quizzes.find { it.id == selectedQuizId }?.title ?: "Select a quiz",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    // Mock dropdown menu
                    // This would be implemented with actual functionality in a real app
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Upload section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!fileUploaded) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = "Upload",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "Drag and drop or click to upload results file",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { fileUploaded = true }
                                ) {
                                    Text("Select File")
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "File Ready",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "results.csv uploaded successfully",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Results preview or upload button
                if (fileUploaded) {
                    // Generate some mock preview data
                    LaunchedEffect(fileUploaded) {
                        resultsPreview = List(5) { index ->
                            ExamResult(
                                id = "result_$index",
                                studentId = "student_${index + 1}",
                                studentName = "Student ${index + 1}",
                                examId = selectedQuizId,
                                examTitle = quizzes.find { it.id == selectedQuizId }?.title ?: "",
                                score = (70..95).random(),
                                totalMarks = 100,
                                status = ResultStatus.PASS,
                                submissionDate = System.currentTimeMillis()
                            )
                        }
                    }
                    
                    Text(
                        text = "Results Preview",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(resultsPreview) { result ->
                            ResultPreviewItem(result)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            // Save results
                            resultsPreview.forEach { viewModel.saveResult(it) }
                            navController.navigate(ERPDestinations.RESULTS_VIEW_ROUTE)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload Results")
                    }
                } else {
                    Button(
                        onClick = { /* Just to be enabled after file selection */ },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Preview Results")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultPreviewItem(result: ExamResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.studentName,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Student ID: ${result.studentId}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${result.score}/${result.totalMarks}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = result.status.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (result.status == ResultStatus.PASS) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyQuizzesState() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = "No Quizzes",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No Quizzes Available",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Create quizzes first to upload their results",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
 