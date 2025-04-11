package com.erp.modules.exam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.components.DashboardCard
import com.erp.core.navigation.ERPDestinations
import com.erp.modules.exam.viewmodel.ExamViewModel

@Composable
fun ExamScreen(
    viewModel: ExamViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val quizzes by viewModel.quizzes.collectAsState()
    val results by viewModel.examResults.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Exam & Result Management",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardCard(
                    title = "Quiz Management",
                    description = "Create, edit and manage quizzes and assessments",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    count = quizzes.size,
                    onClick = {
                        navController.navigate(ERPDestinations.QUIZ_MANAGEMENT_ROUTE)
                    }
                )
            }
            
            item {
                DashboardCard(
                    title = "Exam Schedule",
                    description = "Schedule and manage upcoming exams",
                    icon = Icons.Default.DateRange,
                    count = 0, // Will be updated with actual exam count
                    onClick = {
                        navController.navigate(ERPDestinations.EXAM_LIST_ROUTE)
                    }
                )
            }
            
            item {
                DashboardCard(
                    title = "Results Management",
                    description = "View and manage student exam results",
                    icon = Icons.Default.Assessment,
                    count = results.size,
                    onClick = {
                        navController.navigate(ERPDestinations.RESULTS_VIEW_ROUTE)
                    }
                )
            }
            
            item {
                DashboardCard(
                    title = "Upload Results",
                    description = "Upload and process exam results",
                    icon = Icons.Default.CloudUpload,
                    count = null,
                    onClick = {
                        navController.navigate(ERPDestinations.RESULTS_UPLOAD_ROUTE)
                    }
                )
            }
        }
    }
} 