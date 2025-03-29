package com.erp.modules.student.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.ui.viewmodel.StudentDetailUiState
import com.erp.modules.student.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    viewModel: StudentViewModel,
    studentId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String?) -> Unit
) {
    val studentDetailState by viewModel.studentDetailState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(studentId) {
        if (studentId != null) {
            viewModel.getStudentDetail(studentId)
        } else {
            viewModel.createNewStudent()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    if (studentId != null) {
                        // Edit button
                        IconButton(onClick = { onNavigateToEdit(studentId) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Student"
                            )
                        }
                        
                        // Delete button
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Student"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = studentDetailState) {
            is StudentDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is StudentDetailUiState.Success -> {
                StudentDetailContent(
                    student = state.student,
                    modifier = Modifier.padding(paddingValues)
                )
                
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Student") },
                        text = { Text("Are you sure you want to delete ${state.student.firstName} ${state.student.lastName}?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.deleteStudent(state.student)
                                    showDeleteDialog = false
                                    onNavigateBack()
                                }
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
            
            is StudentDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StudentDetailContent(
    student: Student,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Student Photo
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (student.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = student.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Student Name
        Text(
            text = "${student.firstName} ${student.lastName}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "ID: ${student.enrollmentNumber}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        
        // Class Information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            InfoChip(
                icon = Icons.Default.Class,
                label = "Class: ${student.grade}"
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            InfoChip(
                icon = Icons.Default.Groups,
                label = "Section: ${student.section}"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Student Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SectionTitle(title = "Personal Information")
                
                InfoRow(
                    icon = Icons.Default.Cake,
                    label = "Date of Birth",
                    value = student.dateOfBirth?.let { formatDate(it) } ?: "Not provided"
                )
                
                InfoRow(
                    icon = Icons.Default.Wc,
                    label = "Gender",
                    value = student.gender
                )
                
                InfoRow(
                    icon = Icons.Default.Bloodtype,
                    label = "Blood Group",
                    value = student.bloodGroup
                )
                
                InfoRow(
                    icon = Icons.Default.Home,
                    label = "Address",
                    value = student.address
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle(title = "Contact Information")
                
                InfoRow(
                    icon = Icons.Default.Call,
                    label = "Phone",
                    value = student.contactNumber
                )
                
                InfoRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = student.email
                )
                
                InfoRow(
                    icon = Icons.Default.MedicalServices,
                    label = "Emergency Contact",
                    value = student.emergencyContact
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle(title = "Parent Information")
                
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Parent Name",
                    value = student.parentName
                )
                
                InfoRow(
                    icon = Icons.Default.Call,
                    label = "Parent Contact",
                    value = student.parentContact
                )
                
                InfoRow(
                    icon = Icons.Default.Email,
                    label = "Parent Email",
                    value = student.parentEmail
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle(title = "Academic Information")
                
                InfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Admission Date",
                    value = student.admissionDate?.let { formatDate(it) } ?: "Not provided"
                )
                
                InfoRow(
                    icon = Icons.Default.School,
                    label = "Previous School",
                    value = student.previousSchool
                )
                
                InfoRow(
                    icon = Icons.Default.HealthAndSafety,
                    label = "Health Notes",
                    value = student.healthNotes
                )
                
                InfoRow(
                    icon = if (student.active) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    label = "Status",
                    value = if (student.active) "Active" else "Inactive"
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    SuggestionChip(
        onClick = { },
        label = { Text(text = label) },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

// Helper function to format dates
private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
} 