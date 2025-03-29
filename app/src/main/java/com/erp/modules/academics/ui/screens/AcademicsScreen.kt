package com.erp.modules.academics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicsScreen(
    viewModel: AcademicsViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Academics") },
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
        ) {
            // Title
            Text(
                text = "Academic Resources",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
            
            // Main menu cards
            AcademicsMenuCards(
                onSubjectsClick = {
                    navController.navigate("academics/class_selection")
                },
                onTimetableClick = {
                    navController.navigate("academics/section_selection")
                },
                onSectionFilesClick = {
                    navController.navigate("academics/section_files/A")
                },
                onExamsClick = {
                    // Will be implemented in a future module
                },
                onAttendanceClick = {
                    // Will be implemented in a future module
                }
            )
        }
    }
}

@Composable
fun AcademicsMenuCards(
    onSubjectsClick: () -> Unit,
    onTimetableClick: () -> Unit,
    onSectionFilesClick: () -> Unit,
    onExamsClick: () -> Unit,
    onAttendanceClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subjects Card
        AcademicsFeatureCard(
            title = "Subjects & Syllabus",
            description = "View subjects and detailed syllabus by class",
            icon = Icons.Default.Book,
            onClick = onSubjectsClick
        )
        
        // Timetable Card
        AcademicsFeatureCard(
            title = "Class Timetable",
            description = "View the timetable for different sections",
            icon = Icons.Default.DateRange,
            onClick = onTimetableClick
        )
        
        // Section Files Card
        AcademicsFeatureCard(
            title = "Section Files",
            description = "Manage and upload files for class sections",
            icon = Icons.Default.CloudUpload,
            onClick = onSectionFilesClick
        )
        
        // Exams Card
        AcademicsFeatureCard(
            title = "Examinations",
            description = "View exam schedules and past results",
            icon = Icons.AutoMirrored.Filled.Assignment,
            onClick = onExamsClick
        )
        
        // Attendance Card
        AcademicsFeatureCard(
            title = "Attendance",
            description = "View attendance records and statistics",
            icon = Icons.Default.CheckCircle,
            onClick = onAttendanceClick
        )
    }
}

@Composable
fun AcademicsFeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
} 