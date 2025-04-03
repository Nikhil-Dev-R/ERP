package com.erp.modules.teacher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestDetailScreen(
    leaveRequestId: String,
    viewModel: HRViewModel,
    onNavigateBack: () -> Unit
) {
    val employees by viewModel.employees.collectAsState()
    
    // Fetch the leave request
    val selectedLeaveRequest by viewModel.selectedLeaveRequest.collectAsState()
    var comments by remember { mutableStateOf("") }
    
    LaunchedEffect(leaveRequestId) {
        // Get the leave request
        val leaveRequest = viewModel.leaveRequests.value.find { it.id == leaveRequestId }
        if (leaveRequest != null) {
            viewModel.selectLeaveRequest(leaveRequest)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leave Request Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearSelectedLeaveRequest() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (selectedLeaveRequest == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val leaveRequest = selectedLeaveRequest!!
            val employee = employees.find { it.id == leaveRequest.employeeId }
            
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val daysDiff = TimeUnit.DAYS.convert(
                leaveRequest.endDate.time - leaveRequest.startDate.time,
                TimeUnit.MILLISECONDS
            ) + 1 // Include both start and end dates
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (leaveRequest.status) {
                            LeaveStatus.PENDING -> Color(0xFFFFF8E1)
                            LeaveStatus.APPROVED -> Color(0xFFE8F5E9)
                            LeaveStatus.REJECTED -> Color(0xFFFFEBEE)
                            LeaveStatus.CANCELLED -> Color(0xFFF5F5F5)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (leaveRequest.status) {
                                LeaveStatus.PENDING -> Icons.Default.HourglassEmpty
                                LeaveStatus.APPROVED -> Icons.Default.CheckCircle
                                LeaveStatus.REJECTED -> Icons.Default.Cancel
                                LeaveStatus.CANCELLED -> Icons.Default.Close
                            },
                            contentDescription = "Status",
                            tint = when (leaveRequest.status) {
                                LeaveStatus.PENDING -> Color(0xFFF57C00)
                                LeaveStatus.APPROVED -> Color(0xFF2E7D32)
                                LeaveStatus.REJECTED -> Color(0xFFC62828)
                                LeaveStatus.CANCELLED -> Color(0xFF9E9E9E)
                            }
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Status: ${leaveRequest.status.name}",
                            style = MaterialTheme.typography.titleLarge,
                            color = when (leaveRequest.status) {
                                LeaveStatus.PENDING -> Color(0xFFF57C00)
                                LeaveStatus.APPROVED -> Color(0xFF2E7D32)
                                LeaveStatus.REJECTED -> Color(0xFFC62828)
                                LeaveStatus.CANCELLED -> Color(0xFF9E9E9E)
                            }
                        )
                    }
                }
                
                // Employee details
                if (employee != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Employee Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Divider()
                            
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Name:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = "${employee.firstName} ${employee.lastName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "ID:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = employee.employeeId,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Department:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = employee.department,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                // Leave details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Leave Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Divider()
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Type:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = leaveRequest.leaveType.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Duration:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(100.dp)
                            )
                            Text(
                                text = "$daysDiff days (${dateFormat.format(leaveRequest.startDate)} - ${dateFormat.format(leaveRequest.endDate)})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        if (leaveRequest.reason != null && leaveRequest.reason.isNotBlank()) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Reason:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = leaveRequest.reason.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        if (!leaveRequest.comments.isNullOrBlank()) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Comments:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = leaveRequest.comments!!,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                
                // Action section (only for pending requests)
                if (leaveRequest.status == LeaveStatus.PENDING) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Divider()
                            
                            OutlinedTextField(
                                value = comments,
                                onValueChange = { comments = it },
                                label = { Text("Comments (Optional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                maxLines = 5
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.rejectLeaveRequest(leaveRequest, "admin", comments.ifBlank { null })
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF44336)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Reject")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reject")
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Button(
                                    onClick = {
                                        viewModel.approveLeaveRequest(leaveRequest, "admin", comments.ifBlank { null })
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Approve")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Approve")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 