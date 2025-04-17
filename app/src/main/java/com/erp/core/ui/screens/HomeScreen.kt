package com.erp.core.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToFinance: () -> Unit,
    onNavigateToHR: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToAcademics: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToExams: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToFees: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Management System") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select a Module",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // First row: Student and Academic Management
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModuleCard(
                    title = "Students",
                    icon = Icons.Default.School,
                    onClick = onNavigateToStudents,
                    modifier = Modifier.weight(1f)
                )
                
                ModuleCard(
                    title = "Academics",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    onClick = onNavigateToAcademics,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Second row: Attendance and Exams
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModuleCard(
                    title = "Attendance",
                    icon = Icons.Default.CalendarToday,
                    onClick = onNavigateToAttendance,
                    modifier = Modifier.weight(1f)
                )
                
                ModuleCard(
                    title = "Exams & Results",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    onClick = onNavigateToExams,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Third row: HR (Teachers) and Finance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModuleCard(
                    title = "Teachers & Staff",
                    icon = Icons.Default.Person,
                    onClick = onNavigateToHR,
                    modifier = Modifier.weight(1f)
                )
                
                ModuleCard(
                    title = "Finance",
                    icon = Icons.Default.AttachMoney,
                    onClick = onNavigateToFinance,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Fourth row: Inventory and Fees
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModuleCard(
                    title = "Inventory",
                    icon = Icons.Default.Inventory,
                    onClick = onNavigateToInventory,
                    modifier = Modifier.weight(1f)
                )
                
                ModuleCard(
                    title = "Fee Management",
                    icon = Icons.Default.AttachMoney,
                    onClick = onNavigateToFees,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(120.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
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
                modifier = Modifier.height(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onNavigateToFinance = {},
        onNavigateToHR = {},
        onNavigateToStudents = {},
        onNavigateToAcademics = {},
        onNavigateToAttendance = {},
        onNavigateToExams = {},
        onNavigateToInventory = {},
        onNavigateToFees = {},
        onLogout = {}
    )
}