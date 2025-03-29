package com.erp.modules.hr.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmployeeStatus
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    viewModel: HRViewModel,
    employeeId: String,
    onNavigateBack: () -> Unit,
    onEditEmployee: (String) -> Unit
) {
    val employee by viewModel.selectedEmployee.collectAsState()
    val leaveRequests by viewModel.getLeaveRequestsByEmployee(employeeId).collectAsState(initial = emptyList())
    val salaries by viewModel.getSalariesByEmployee(employeeId).collectAsState(initial = emptyList())
    
    // Load employee if not already selected
    if (employee?.id != employeeId) {
        viewModel.employees.collectAsState().value.find { it.id == employeeId }?.let {
            viewModel.selectEmployee(it)
        }
    }
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val tabs = listOf("Profile", "Payroll", "Leaves", "Documents")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { employee?.let { onEditEmployee(it.id) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Employee")
                    }
                }
            )
        }
    ) { padding ->
        employee?.let { emp ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Employee header
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (emp.photoUrl.isNotEmpty()) {
                                // Load image from URL
                                // AsyncImage implementation here
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "${emp.firstName} ${emp.lastName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = emp.position,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Text(
                            text = "ID: ${emp.employeeId}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = emp.status.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = when (emp.status) {
                                EmployeeStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                                EmployeeStatus.ON_LEAVE -> MaterialTheme.colorScheme.tertiary
                                EmployeeStatus.TERMINATED -> MaterialTheme.colorScheme.error
                                EmployeeStatus.RETIRED -> MaterialTheme.colorScheme.error
                                EmployeeStatus.SABBATICAL -> MaterialTheme.colorScheme.tertiary
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
                
                when (selectedTabIndex) {
                    0 -> ProfileTab(employee = emp)
                    1 -> PayrollTab(salaries = salaries)
                    2 -> LeavesTab(leaveRequests = leaveRequests)
                    3 -> DocumentsTab()
                }
            }
        } ?: run {
            // Employee not found
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Employee not found")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
    }
}

@Composable
fun ProfileTab(employee: Employee) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Personal Information
        SectionHeader("Personal Information")
        
        InfoRow(
            icon = Icons.Default.Person,
            label = "Employee ID",
            value = employee.employeeId
        )
        
        InfoRow(
            icon = Icons.Default.Person,
            label = "Gender",
            value = employee.gender
        )
        
        InfoRow(
            icon = Icons.Default.CalendarMonth,
            label = "Date of Birth",
            value = dateFormat.format(employee.dateOfBirth)
        )
        
        InfoRow(
            icon = Icons.Default.LocationOn,
            label = "Address",
            value = employee.address
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contact Information
        SectionHeader("Contact Information")
        
        InfoRow(
            icon = Icons.Default.Phone,
            label = "Phone",
            value = employee.contactNumber
        )
        
        InfoRow(
            icon = Icons.Default.Email,
            label = "Email",
            value = employee.email
        )
        
        InfoRow(
            icon = Icons.Default.Phone,
            label = "Emergency Contact",
            value = "${employee.emergencyContactName} (${employee.emergencyContact})"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Employment Information
        SectionHeader("Employment Information")
        
        InfoRow(
            icon = Icons.Default.Work,
            label = "Position",
            value = employee.position
        )
        
        InfoRow(
            icon = Icons.Default.School,
            label = "Department",
            value = employee.department
        )
        
        InfoRow(
            icon = Icons.Default.CalendarMonth,
            label = "Hire Date",
            value = dateFormat.format(employee.hireDate)
        )
        
        InfoRow(
            icon = Icons.Default.Work,
            label = "Employment Type",
            value = employee.employmentType.name.replace("_", " ")
        )
        
        InfoRow(
            icon = Icons.Default.Person,
            label = "Reports To",
            value = employee.reportingTo.takeIf { it.isNotEmpty() } ?: "N/A"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Educational Information
        SectionHeader("Educational Information")
        
        InfoRow(
            icon = Icons.Default.School,
            label = "Qualification",
            value = employee.qualification
        )
        
        InfoRow(
            icon = Icons.Default.School,
            label = "Specialization",
            value = employee.specialization
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Additional Information
        SectionHeader("Additional Information")
        
        InfoRow(
            icon = Icons.Default.Work,
            label = "Previous Experience",
            value = "${employee.previousExperience} years"
        )
        
        InfoRow(
            icon = Icons.Default.Person,
            label = "Blood Group",
            value = employee.bloodGroup.takeIf { it.isNotEmpty() } ?: "Not specified"
        )
        
        if (employee.role.name.contains("TEACHER")) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Teacher specific information
            SectionHeader("Teaching Information")
            
            Text(
                text = "Subjects Taught:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            if (employee.subjectsIds.isEmpty()) {
                Text(
                    text = "No subjects assigned",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                )
            } else {
                employee.subjectsIds.forEach { subject ->
                    Text(
                        text = "• $subject",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)                    )
                }
            }
            
            Text(
                text = "Classes Taught:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            if (employee.classesTaught.isEmpty()) {
                Text(
                    text = "No classes assigned",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                )
            } else {
                employee.classesTaught.forEach { className ->
                    Text(
                        text = "• $className",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PayrollTab(salaries: List<Salary>) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader("Salary History")
        
        if (salaries.isEmpty()) {
            Text(
                text = "No salary records found",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            for (salary in salaries.sortedByDescending { it.payPeriodEnd }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = dateFormat.format(salary.payPeriodStart),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "to",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = dateFormat.format(salary.payPeriodEnd),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Base Salary",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "$${String.format("%.2f", salary.baseSalary)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Allowances",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "+$${String.format("%.2f", salary.allowances)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Bonus",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "+$${String.format("%.2f", salary.bonus)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Deductions",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "-$${String.format("%.2f", salary.deductions)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tax",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "-$${String.format("%.2f", salary.tax)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Net Pay",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "$${String.format("%.2f", salary.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (salary.paymentDate != null) {
                                Text(
                                    text = "Paid on ${dateFormat.format(salary.paymentDate)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            Text(
                                text = salary.status.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = when (salary.status) {
                                    com.erp.modules.hr.data.model.SalaryStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                                    com.erp.modules.hr.data.model.SalaryStatus.PROCESSED -> MaterialTheme.colorScheme.primary
                                    com.erp.modules.hr.data.model.SalaryStatus.PAID -> MaterialTheme.colorScheme.primary
                                    com.erp.modules.hr.data.model.SalaryStatus.CANCELLED -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeavesTab(leaveRequests: List<LeaveRequest>) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader("Leave History")
        
        if (leaveRequests.isEmpty()) {
            Text(
                text = "No leave records found",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            val pendingLeaves = leaveRequests.filter { it.status == com.erp.modules.hr.data.model.LeaveStatus.PENDING }
            if (pendingLeaves.isNotEmpty()) {
                Text(
                    text = "Pending Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                for (leave in pendingLeaves.sortedByDescending { it.createdAt }) {
                    LeaveCard(leave)
                }
            }
            
            val approvedLeaves = leaveRequests.filter { it.status == com.erp.modules.hr.data.model.LeaveStatus.APPROVED }
            if (approvedLeaves.isNotEmpty()) {
                Text(
                    text = "Approved Leaves",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                
                for (leave in approvedLeaves.sortedByDescending { it.startDate }) {
                    LeaveCard(leave)
                }
            }
            
            val otherLeaves = leaveRequests.filter { 
                it.status != com.erp.modules.hr.data.model.LeaveStatus.PENDING && 
                it.status != com.erp.modules.hr.data.model.LeaveStatus.APPROVED 
            }
            if (otherLeaves.isNotEmpty()) {
                Text(
                    text = "Past Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                
                for (leave in otherLeaves.sortedByDescending { it.createdAt }) {
                    LeaveCard(leave)
                }
            }
        }
    }
}

@Composable
fun LeaveCard(leave: LeaveRequest) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = leave.leaveType.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "${leave.requestedDays} days",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "From: ${dateFormat.format(leave.startDate)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            Text(
                text = "To: ${dateFormat.format(leave.endDate)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            if (!leave.reason.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reason: ${leave.reason}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            
            if (!leave.comments.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Comments: ${leave.comments}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = leave.status.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = when (leave.status) {
                        com.erp.modules.hr.data.model.LeaveStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                        com.erp.modules.hr.data.model.LeaveStatus.APPROVED -> MaterialTheme.colorScheme.primary
                        com.erp.modules.hr.data.model.LeaveStatus.REJECTED -> MaterialTheme.colorScheme.error
                        com.erp.modules.hr.data.model.LeaveStatus.CANCELLED -> MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}

@Composable
fun DocumentsTab() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader("Document Management")
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No documents uploaded yet",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(onClick = { /* Add document */ }) {
                Text("Upload Document")
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
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
            modifier = Modifier.padding(end = 12.dp)
        )
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 