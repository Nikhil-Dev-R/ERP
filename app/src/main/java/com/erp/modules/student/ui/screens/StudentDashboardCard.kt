package com.erp.modules.student.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erp.modules.student.data.model.Student
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    onNavigateToStudentsList: () -> Unit,
    onNavigateToAddStudent: () -> Unit,
    onNavigateToStudentsByClass: () -> Unit,
    onNavigateToStudentAttendance: () -> Unit,
    onNavigateBack: () -> Unit,
    onViewAllClick: () -> Unit = {},
    recentAddedStudents: List<Student> = emptyList()
) {
    val cardList = listOf(
        StudentDashboardCard(
            title = "All Students",
            icon = Icons.Default.Group,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = onNavigateToStudentsList
        ),
        StudentDashboardCard(
            title = "Add Student",
            icon = Icons.Default.PersonAdd,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onNavigateToAddStudent
        ),
        StudentDashboardCard(
            title = "Students By Class",
            icon = Icons.Default.School,
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = onNavigateToStudentsByClass
        ),
        StudentDashboardCard(
            title = "Attendance",
            icon = Icons.Default.CheckCircle,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            onClick = onNavigateToStudentAttendance
        )
    )
    val scrollState = rememberScrollState()
    var showAllRecentStudents by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Student Management System",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            
            // Student dashboard cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(cardList) { card ->
                    DashboardCard(
                        title = card.title,
                        icon = card.icon,
                        backgroundColor = card.backgroundColor,
                        onClick = card.onClick,
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Quick stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Quick Stats",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RecentStatCard(
                            title = "Total Students",
                            value = "237",
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                        
                        RecentStatCard(
                            title = "Classes",
                            value = "12",
                            icon = Icons.Default.Class,
                            modifier = Modifier.weight(1f)
                        )
                        
                        RecentStatCard(
                            title = "Attendance",
                            value = "94%",
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recent students card
            if (recentAddedStudents.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Recently Added Students",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Recent students list
                        if (!showAllRecentStudents) {
                            // Show atMost 3 recent students
                            items(
                                recentAddedStudents.take(min(2, recentAddedStudents.size))
                            ) { student ->
                                Text(
                                    text = "${student.firstName} ${student.lastName} (Class ${student.grade} ${student.section})",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        } else {
                            items(recentAddedStudents) { student ->
                                Text(
                                    text = "${student.firstName} ${student.lastName} (Class ${student.grade} ${student.section})",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { showAllRecentStudents = true },
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "View All")
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "View All"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RecentStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

data class StudentDashboardCard(
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier
)

@Preview
@Composable
fun StudentPreview() {
    StudentDashboardScreen(
        {}, {}, {}, {}, {},
    )
}