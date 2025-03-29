package com.erp.modules.attendance.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.attendance.ui.viewmodel.AttendanceViewModel
import com.erp.modules.student.data.model.Student
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    navController: NavController
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Daily Attendance", "Reports", "Student Records")
    val currentDate = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()) }
    var selectedClass by remember { mutableStateOf("All Classes") }
    val classOptions = listOf("All Classes", "Grade 9-A", "Grade 9-B", "Grade 10-A", "Grade 10-B")
    var showClassDropdown by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Open filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            if (tabIndex == 0) {
                FloatingActionButton(
                    onClick = { /* Take attendance action */ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Take Attendance")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Date and Class Selection Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, 
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(currentDate, style = MaterialTheme.typography.bodyMedium)
                }
                
                // Class selection dropdown
                Box {
                    OutlinedButton(
                        onClick = { showClassDropdown = true },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    ) {
                        Text(selectedClass)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    
                    DropdownMenu(
                        expanded = showClassDropdown,
                        onDismissRequest = { showClassDropdown = false },
                        modifier = Modifier.width(200.dp)
                    ) {
                        classOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedClass = option
                                    showClassDropdown = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Content based on selected tab
            when (tabIndex) {
                0 -> DailyAttendanceTab(viewModel)
                1 -> AttendanceReportsTab(viewModel)
                2 -> StudentRecordsTab(viewModel)
            }
        }
    }
}

@Composable
fun DailyAttendanceTab(viewModel: AttendanceViewModel) {
    val attendanceRecords by viewModel.attendanceRecords.collectAsState(initial = emptyList())
    val students by viewModel.students.collectAsState(initial = emptyList())
    
    if (students.isEmpty()) {
        EmptyStateMessage("No students found")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(students) { student ->
                StudentAttendanceItem(
                    student = student,
                    isPresent = attendanceRecords.any { 
                        it.studentId == student.id && it.status == "PRESENT" 
                    },
                    onMarkAttendance = { isPresent ->
                        // Handle marking attendance
                        viewModel.markAttendance(student.id, isPresent)
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceReportsTab(viewModel: AttendanceViewModel) {
    // Placeholder for attendance reports
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Summary Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AttendanceSummaryCard(
                title = "Present",
                count = "85%",
                color = Color(0xFF4CAF50)
            )
            AttendanceSummaryCard(
                title = "Absent",
                count = "10%",
                color = Color(0xFFF44336)
            )
            AttendanceSummaryCard(
                title = "Late",
                count = "5%",
                color = Color(0xFFFF9800)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Monthly Overview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Monthly Overview", 
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Attendance data will be displayed in a chart here",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Placeholder for chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Attendance Chart")
                }
            }
        }
        
        // Class Comparison
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Class Comparison", 
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Sample class comparison data
                ClassComparisonItem("Grade 9-A", 95)
                ClassComparisonItem("Grade 9-B", 88)
                ClassComparisonItem("Grade 10-A", 92)
                ClassComparisonItem("Grade 10-B", 90)
            }
        }
    }
}

@Composable
fun StudentRecordsTab(viewModel: AttendanceViewModel) {
    val students by viewModel.students.collectAsState(initial = emptyList())
    
    if (students.isEmpty()) {
        EmptyStateMessage("No student records found")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(students) { student ->
                StudentAttendanceRecordItem(
                    student = student,
                    presentDays = 18, // Mock data
                    absentDays = 2,    // Mock data
                    totalDays = 20     // Mock data
                )
            }
        }
    }
}

@Composable
fun StudentAttendanceItem(
    student: Student,
    isPresent: Boolean,
    onMarkAttendance: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Student info
            Column {
                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Roll No: ${student.enrollmentNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Attendance toggle
            Row {
                AttendanceToggleButton(
                    text = "Present",
                    selected = isPresent,
                    color = Color(0xFF4CAF50),
                    onClick = { onMarkAttendance(true) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                AttendanceToggleButton(
                    text = "Absent",
                    selected = !isPresent,
                    color = Color(0xFFF44336),
                    onClick = { onMarkAttendance(false) }
                )
            }
        }
    }
}

@Composable
fun AttendanceToggleButton(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) color else Color.Transparent
    val contentColor = if (selected) Color.White else Color.Black
    val borderColor = if (selected) color else Color.Gray
    
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(text)
    }
}

@Composable
fun AttendanceSummaryCard(
    title: String,
    count: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .height(120.dp)
            .width(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ClassComparisonItem(className: String, attendancePercentage: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(className)
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = attendancePercentage / 100f,
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp),
                color = when {
                    attendancePercentage >= 90 -> Color(0xFF4CAF50)
                    attendancePercentage >= 75 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("$attendancePercentage%")
        }
    }
}

@Composable
fun StudentAttendanceRecordItem(
    student: Student,
    presentDays: Int,
    absentDays: Int,
    totalDays: Int
) {
    val attendancePercentage = (presentDays.toFloat() / totalDays) * 100
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* View detailed record */ }
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
                Column {
                    Text(
                        text = "${student.firstName} ${student.lastName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Roll No: ${student.enrollmentNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = "${attendancePercentage.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = when {
                        attendancePercentage >= 90 -> Color(0xFF4CAF50)
                        attendancePercentage >= 75 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = attendancePercentage / 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    attendancePercentage >= 90 -> Color(0xFF4CAF50)
                    attendancePercentage >= 75 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatItem("Present", presentDays, Color(0xFF4CAF50))
                AttendanceStatItem("Absent", absentDays, Color(0xFFF44336))
                AttendanceStatItem("Total", totalDays, MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun AttendanceStatItem(label: String, count: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Event,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
} 