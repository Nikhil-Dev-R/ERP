package com.erp.modules.teacher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.data.model.LeaveType
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestsScreen(
    viewModel: HRViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val leaveRequests by viewModel.leaveRequests.collectAsState()
    val pendingLeaveRequests by viewModel.pendingLeaveRequests.collectAsState()
    val employees by viewModel.employees.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<LeaveStatus?>(null) }
    
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var selectedLeaveType by remember { mutableStateOf(LeaveType.VACATION) }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    var reason by remember { mutableStateOf("") }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leave Requests") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh data */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            // Custom Add Button
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { showAddDialog = true }
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "New Leave Request",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and filter section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search leave requests") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilterChip(
                    selected = statusFilter == LeaveStatus.PENDING,
                    onClick = { 
                        statusFilter = if (statusFilter == LeaveStatus.PENDING) null else LeaveStatus.PENDING 
                    },
                    label = { Text("Pending") }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilterChip(
                    selected = statusFilter == LeaveStatus.APPROVED,
                    onClick = { 
                        statusFilter = if (statusFilter == LeaveStatus.APPROVED) null else LeaveStatus.APPROVED 
                    },
                    label = { Text("Approved") }
                )
            }
            
            // Leave requests list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredRequests = leaveRequests.filter { leaveRequest ->
                    (statusFilter == null || leaveRequest.status == statusFilter) &&
                    (searchQuery.isEmpty() || leaveRequest.employeeId.contains(searchQuery, ignoreCase = true) ||
                     employees.find { it.id == leaveRequest.employeeId }?.let {
                         "${it.firstName} ${it.lastName}".contains(searchQuery, ignoreCase = true)
                     } ?: false)
                }
                
                if (filteredRequests.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No leave requests found",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredRequests) { leaveRequest ->
                        LeaveRequestItem(
                            leaveRequest = leaveRequest,
                            employee = employees.find { it.id == leaveRequest.employeeId },
                            onClick = { onNavigateToDetail(leaveRequest.id) }
                        )
                    }
                }
            }
        }
        
        // Add Leave Request Dialog
        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "New Leave Request",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Employee selection
                        ExposedDropdownMenuBox(
                            expanded = false,
                            onExpandedChange = { /* Handle expansion */ }
                        ) {
                            OutlinedTextField(
                                value = selectedEmployee?.let { "${it.firstName} ${it.lastName}" } ?: "",
                                onValueChange = { /* Handled by dropdown */ },
                                readOnly = true,
                                label = { Text("Select Employee") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = false,
                                onDismissRequest = { /* Handle dismiss */ }
                            ) {
                                employees.forEach { employee ->
                                    DropdownMenuItem(
                                        text = { Text("${employee.firstName} ${employee.lastName}") },
                                        onClick = { 
                                            selectedEmployee = employee
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Leave type selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LeaveType.values().forEach { type ->
                                FilterChip(
                                    selected = selectedLeaveType == type,
                                    onClick = { selectedLeaveType = type },
                                    label = { Text(type.name) }
                                )
                            }
                        }
                        
                        // Date selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = dateFormat.format(startDate),
                                onValueChange = { /* Handled by date picker */ },
                                readOnly = true,
                                label = { Text("Start Date") },
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                                }
                            )
                            
                            OutlinedTextField(
                                value = dateFormat.format(endDate),
                                onValueChange = { /* Handled by date picker */ },
                                readOnly = true,
                                label = { Text("End Date") },
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                                }
                            )
                        }
                        
                        // Reason
                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            label = { Text("Reason") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 5
                        )
                        
                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Cancel")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = {
                                    selectedEmployee?.let { employee ->
                                        val leaveRequest = LeaveRequest(
                                            employeeId = employee.id,
                                            leaveType = selectedLeaveType,
                                            startDate = startDate,
                                            endDate = endDate,
                                            reason = reason,
                                            status = LeaveStatus.PENDING,
                                            requestedDays = calculateDays(startDate, endDate)
                                        )
                                        viewModel.saveLeaveRequest(leaveRequest)
                                        showAddDialog = false
                                    }
                                },
                                enabled = selectedEmployee != null && reason.isNotBlank()
                            ) {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to calculate days between dates
private fun calculateDays(startDate: Date, endDate: Date): Int {
    val diff = endDate.time - startDate.time
    return (diff / (24 * 60 * 60 * 1000)).toInt() + 1 // Add 1 to include both start and end days
}

@Composable
fun LeaveRequestItem(
    leaveRequest: LeaveRequest,
    employee: Employee?,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when (leaveRequest.status) {
                            LeaveStatus.PENDING -> Color(0xFFFFA000)
                            LeaveStatus.APPROVED -> Color(0xFF4CAF50)
                            LeaveStatus.REJECTED -> Color(0xFFF44336)
                            LeaveStatus.CANCELLED -> Color(0xFF9E9E9E)
                        }
                    )
                    .border(1.dp, Color.White, CircleShape)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = employee?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Employee",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = leaveRequest.leaveType.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${dateFormat.format(leaveRequest.startDate)} - ${dateFormat.format(leaveRequest.endDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (leaveRequest.reason != null && leaveRequest.reason.isNotBlank()) {
                    Text(
                        text = leaveRequest.reason,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Status badge
            FilterChip(
                onClick = { /* No action */ },
                selected = false,
                label = {
                    Text(
                        text = leaveRequest.status.name,
                        color = when (leaveRequest.status) {
                            LeaveStatus.PENDING -> Color(0xFFF57C00)
                            LeaveStatus.APPROVED -> Color(0xFF2E7D32)
                            LeaveStatus.REJECTED -> Color(0xFFC62828)
                            LeaveStatus.CANCELLED -> Color(0xFF9E9E9E)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = when (leaveRequest.status) {
                        LeaveStatus.PENDING -> Color(0xFFFFF3E0)
                        LeaveStatus.APPROVED -> Color(0xFFE8F5E9)
                        LeaveStatus.REJECTED -> Color(0xFFFFEBEE)
                        LeaveStatus.CANCELLED -> Color(0xFFF5F5F5)
                    }
                )
            )
        }
    }
} 