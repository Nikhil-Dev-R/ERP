package com.erp.modules.academics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.academics.ui.viewmodel.SubjectsUiState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsByClassScreen(
    viewModel: AcademicsViewModel,
    navController: NavController,
    gradeLevel: String
) {
    val subjectsState = remember { mutableStateOf<SubjectsUiState>(SubjectsUiState.Loading) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(gradeLevel) {
        viewModel.getSubjectsByGrade(gradeLevel).collect { subjects ->
            subjectsState.value = if (subjects.isEmpty()) {
                SubjectsUiState.Empty
            } else {
                SubjectsUiState.Success(subjects)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subjects - $gradeLevel") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSubjectDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = subjectsState.value) {
                is SubjectsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SubjectsUiState.Success -> {
                    SubjectsList(
                        subjects = state.subjects,
                        onSubjectClick = { subjectId ->
                            navController.navigate("subject_detail/$subjectId")
                        }
                    )
                }
                is SubjectsUiState.Empty -> {
                    Text(
                        text = "No subjects found for $gradeLevel\nClick + to add a subject",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
                is SubjectsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        
        if (showAddSubjectDialog) {
            AddSubjectDialog(
                gradeLevel = gradeLevel,
                onDismiss = { showAddSubjectDialog = false },
                onConfirm = { subject ->
                    viewModel.insertSubject(subject)
                    showAddSubjectDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectDialog(
    gradeLevel: String,
    onDismiss: () -> Unit,
    onConfirm: (Subject) -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    var subjectCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isElective by remember { mutableStateOf(false) }
    var credits by remember { mutableStateOf("4") }
    var syllabus by remember { mutableStateOf("") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add New Subject",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = subjectCode,
                    onValueChange = { subjectCode = it },
                    label = { Text("Subject Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Credits:")
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = credits,
                        onValueChange = { 
                            if (it.isEmpty() || it.toIntOrNull() != null) {
                                credits = it 
                            }
                        },
                        modifier = Modifier.width(60.dp),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isElective,
                            onCheckedChange = { isElective = it }
                        )
                        Text("Elective Subject")
                    }
                }
                
                OutlinedTextField(
                    value = syllabus,
                    onValueChange = { syllabus = it },
                    label = { Text("Syllabus Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val newSubject = Subject(
                                id = UUID.randomUUID().toString(),
                                name = subjectName,
                                code = subjectCode,
                                description = description,
                                gradeLevel = gradeLevel,
                                credits = credits.toIntOrNull() ?: 4,
                                isElective = isElective,
                                syllabus = syllabus
                            )
                            onConfirm(newSubject)
                        },
                        enabled = subjectName.isNotBlank() && subjectCode.isNotBlank()
                    ) {
                        Text("Add Subject")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsList(
    subjects: List<Subject>,
    onSubjectClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subjects) { subject ->
            SubjectCard(
                subject = subject,
                onClick = { onSubjectClick(subject.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (subject.imageUrl.isBlank()) {
                    Icon(
                        imageVector = if (subject.isElective) Icons.Filled.Star else Icons.Filled.Book,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    // Image rendering could be added here
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Code: ${subject.code}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (subject.isElective) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Elective",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
} 