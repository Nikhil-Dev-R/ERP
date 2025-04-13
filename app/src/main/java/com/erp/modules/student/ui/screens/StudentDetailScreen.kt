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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.erp.components.ERPTopBar
import com.erp.data.UserRole
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.ui.viewmodel.StudentDetailUiState
import com.erp.modules.student.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentDetailUiStateFlow: StateFlow<StudentDetailUiState>,
    role: UserRole,
    isHome: Boolean = false,
    deleteStudent: (Student) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {}
) {
    val studentDetailState by studentDetailUiStateFlow.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            ERPTopBar(
                title = "Student Detail",
                navIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavIconClick = onNavigateBack,
                centerAligned = isHome,
                actions = {
                    if (role == UserRole.Admin || role == UserRole.Teacher) {
                        // Delete button
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Student",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == UserRole.Admin || role == UserRole.Teacher) {
                FloatingActionButton(onClick = { onNavigateToEdit() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Student"
                    )
                }
            }
        },
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
                                    deleteStudent(state.student)
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
        InfoCard(
            title = "Personal Information",
            infoRows = listOf(
                Triple(Icons.Default.Cake, "Date of Birth", student.dateOfBirth?.let { formatDate(it) } ?: "Not provided"),
                Triple(Icons.Default.Wc, "Gender", student.gender),
                Triple(Icons.Default.Bloodtype, "Blood Group", student.bloodGroup),
                Triple(Icons.Default.Home, "Address", student.address)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            title = "Contact Information",
            infoRows = listOf(
                Triple(Icons.Default.Phone, "Phone", student.contactNumber),
                Triple(Icons.Default.Email, "Email", student.email),
                Triple(Icons.Default.MedicalServices, "Emergency Contact", student.emergencyContact)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            title = "Parent Information",
            infoRows = listOf(
                Triple(Icons.Default.Person, "Parent Name", student.parentName),
                Triple(Icons.Default.Call, "Parent Contact", student.parentContact),
                Triple(Icons.Default.Email, "Parent Email", student.parentEmail)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            title = "Academic Information",
            infoRows = listOf(
                Triple(Icons.Default.DateRange, "Admission Date", student.admissionDate?.let { formatDate(it) } ?: "Not provided"),
                Triple(Icons.Default.School, "Previous School", student.previousSchool),
                Triple(Icons.Default.HealthAndSafety, "Health Notes", student.healthNotes),
                Triple(Icons.Default.CheckCircle, "Status", if (student.active) "Active" else "Inactive")
            )
        )
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
    icon: ImageVector,
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
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    SuggestionChip(
        onClick = onClick,
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

@Composable
fun InfoCard(
    title: String,
    infoRows: List<Triple<ImageVector, String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SectionTitle(title = title)

            infoRows.forEach {
                InfoRow(icon = it.first, label = it.second, value = it.third)
            }
        }
    }
}

// Helper function to format dates
private fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
}

@Preview
@Composable
fun SDSPreview() {
    StudentDetailScreen(
        studentDetailUiStateFlow = MutableStateFlow<StudentDetailUiState>(
            StudentDetailUiState.Success(Student())
        ).asStateFlow(),
        role = UserRole.Admin
    )
}