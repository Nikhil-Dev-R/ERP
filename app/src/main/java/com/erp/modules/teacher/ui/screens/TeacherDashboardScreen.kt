package com.erp.modules.teacher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmployeeStatus
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.data.model.SalaryStatus
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import com.erp.common.util.CurrencyFormatter
import com.erp.modules.teacher.ui.viewmodel.TeacherViewModel

@Composable
fun TeacherDashboardScreen(
    viewModel: TeacherViewModel,
    onNavigateToEmployees: () -> Unit,
    onNavigateToLeaveRequests: () -> Unit,
    onNavigateToPayroll: () -> Unit,
    onAddEmployee: () -> Unit,
    onEmployeeDetails: (String) -> Unit
) {
    val employees by viewModel.employees.collectAsState()
    val leaveRequests by viewModel.leaveRequests.collectAsState()
    val pendingLeaveRequests by viewModel.pendingLeaveRequests.collectAsState()
    val salaries by viewModel.salaries.collectAsState()
    val pendingSalaries by viewModel.pendingSalaries.collectAsState()
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    val tabs = listOf("Overview", "Employees", "Leaves", "Payroll")
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEmployee) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Teacher Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
            
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
                0 -> OverviewTab(
                    employees = employees,
                    pendingLeaveRequests = pendingLeaveRequests,
                    pendingSalaries = pendingSalaries,
                    onNavigateToEmployees = onNavigateToEmployees,
                    onNavigateToLeaveRequests = onNavigateToLeaveRequests,
                    onNavigateToPayroll = onNavigateToPayroll,
                    onEmployeeDetails = onEmployeeDetails
                )
                1 -> EmployeesTab(
                    employees = employees,
                    onEmployeeDetails = onEmployeeDetails
                )
                2 -> LeavesTab(
                    leaveRequests = leaveRequests,
                    onNavigateToLeaveRequests = onNavigateToLeaveRequests,
                    onLeaveDetails = { leaveRequestId -> 
                        onNavigateToLeaveRequests()
                        // The specific leave request will be handled by the LeaveRequestsScreen 
                    }
                )
                3 -> PayrollTab(
                    salaries = salaries,
                    onSalaryDetails = { /* Navigate to salary details */ }
                )
            }
        }
    }
}

@Composable
fun OverviewTab(
    employees: List<Employee>,
    pendingLeaveRequests: List<LeaveRequest>,
    pendingSalaries: List<Salary>,
    onNavigateToEmployees: () -> Unit,
    onNavigateToLeaveRequests: () -> Unit,
    onNavigateToPayroll: () -> Unit,
    onEmployeeDetails: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HRSummaryCard(
                employees = employees,
                pendingLeaves = pendingLeaveRequests.size,
                pendingPayments = pendingSalaries.size
            )
        }
        
        item {
            StatisticsCard(employees)
        }
        
        item {
            Text(
                text = "Recent Employees",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        val recentEmployees = employees.sortedByDescending { it.hireDate }.take(5)
        items(recentEmployees) { employee ->
            EmployeeItem(
                employee = employee,
                onEmployeeClick = { onEmployeeDetails(employee.id) }
            )
        }
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "View All Employees",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { onNavigateToEmployees() }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Pending Leave Requests",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        if (pendingLeaveRequests.isEmpty()) {
            item {
                Text(
                    text = "No pending leave requests",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(pendingLeaveRequests.take(3)) { leaveRequest ->
                LeaveRequestItem(
                    leaveRequest = leaveRequest,
                    onClick = { onNavigateToLeaveRequests() }
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "View All Leave Requests",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { onNavigateToLeaveRequests() }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Pending Payroll",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        if (pendingSalaries.isEmpty()) {
            item {
                Text(
                    text = "No pending salary payments",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(pendingSalaries.take(3)) { salary ->
                SalaryItem(salary)
            }
        }
        
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "View All Payments",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { onNavigateToPayroll() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatisticsCard(employees: List<Employee>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Employee Demographics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Department Distribution
            val departmentMap = employees.groupBy { it.department }
            val maxDeptCount = departmentMap.values.map { it.size }.maxOrNull() ?: 0
            
            Text(
                text = "Department Distribution",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            departmentMap.entries.take(3).forEach { (dept, deptEmployees) ->
                val percentage = (deptEmployees.size.toFloat() / employees.size * 100).toInt()
                val barWidth = (deptEmployees.size.toFloat() / maxDeptCount * 100).toInt()
                
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dept,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth(barWidth / 100f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeesTab(
    employees: List<Employee>,
    onEmployeeDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        EmployeeFilterSection(employees)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All Employees",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Total: ${employees.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            items(employees.sortedBy { "${it.lastName} ${it.firstName}" }) { employee ->
                EmployeeItem(
                    employee = employee,
                    onEmployeeClick = { onEmployeeDetails(employee.id) }
                )
            }
        }
    }
}

@Composable
fun EmployeeFilterSection(employees: List<Employee>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Employee Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val activeCount = employees.count { it.status == EmployeeStatus.ACTIVE }
            val maleCount = employees.count { it.gender.equals("MALE", ignoreCase = true) }
            val femaleCount = employees.count { it.gender.equals("FEMALE", ignoreCase = true) }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(activeCount.toFloat() / employees.size * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(maleCount.toFloat() / employees.size * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Male",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(femaleCount.toFloat() / employees.size * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Female",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun LeavesTab(
    leaveRequests: List<LeaveRequest>,
    onNavigateToLeaveRequests: () -> Unit,
    onLeaveDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LeaveStatisticsCard(leaveRequests)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Leave Requests",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onNavigateToLeaveRequests() }
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            val pendingLeaves = leaveRequests.filter { it.status == LeaveStatus.PENDING }
            if (pendingLeaves.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending Approvals (${pendingLeaves.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(pendingLeaves.sortedByDescending { it.createdAt }) { leaveRequest ->
                    LeaveRequestItem(
                        leaveRequest = leaveRequest,
                        onClick = { onLeaveDetails(leaveRequest.id) }
                    )
                }
            }
            
            val otherLeaves = leaveRequests.filter { it.status != LeaveStatus.PENDING }
            if (otherLeaves.isNotEmpty()) {
                item {
                    Text(
                        text = "Other Requests (${otherLeaves.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                
                items(otherLeaves.sortedByDescending { it.createdAt }.take(10)) { leaveRequest ->
                    LeaveRequestItem(
                        leaveRequest = leaveRequest,
                        onClick = { onLeaveDetails(leaveRequest.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LeaveStatisticsCard(leaveRequests: List<LeaveRequest>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Leave Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val pendingCount = leaveRequests.count { it.status == LeaveStatus.PENDING }
            val approvedCount = leaveRequests.count { it.status == LeaveStatus.APPROVED }
            val rejectedCount = leaveRequests.count { it.status == LeaveStatus.REJECTED }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LeaveStatusItem(
                    count = pendingCount,
                    total = leaveRequests.size,
                    label = "Pending",
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                LeaveStatusItem(
                    count = approvedCount,
                    total = leaveRequests.size,
                    label = "Approved",
                    color = MaterialTheme.colorScheme.primary
                )
                
                LeaveStatusItem(
                    count = rejectedCount,
                    total = leaveRequests.size,
                    label = "Rejected",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun LeaveStatusItem(count: Int, total: Int, label: String, color: Color) {
    val percentage = if (total > 0) (count.toFloat() / total * 100).toInt() else 0
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "$count Requests",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun PayrollTab(
    salaries: List<Salary>,
    onSalaryDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PayrollStatisticsCard(salaries)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payroll Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Total: ${salaries.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            
            val pendingSalaries = salaries.filter { it.status == SalaryStatus.PENDING }
            if (pendingSalaries.isNotEmpty()) {
                item {
                    Text(
                        text = "Pending Payments (${pendingSalaries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(pendingSalaries.sortedByDescending { it.payPeriodEnd }) { salary ->
                    SalaryItem(
                        salary = salary,
                        onClick = { onSalaryDetails(salary.id) }
                    )
                }
            }
            
            val processedSalaries = salaries.filter { it.status == SalaryStatus.PROCESSED }
            if (processedSalaries.isNotEmpty()) {
                item {
                    Text(
                        text = "Ready for Payment (${processedSalaries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                
                items(processedSalaries.sortedByDescending { it.payPeriodEnd }) { salary ->
                    SalaryItem(
                        salary = salary,
                        onClick = { onSalaryDetails(salary.id) }
                    )
                }
            }
            
            val paidSalaries = salaries.filter { it.status == SalaryStatus.PAID }
            if (paidSalaries.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Payments (${paidSalaries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                
                items(paidSalaries.sortedByDescending { it.paymentDate }.take(5)) { salary ->
                    SalaryItem(
                        salary = salary,
                        onClick = { onSalaryDetails(salary.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PayrollStatisticsCard(salaries: List<Salary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Payroll Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Calculate total paid amount
            val totalPaid = salaries
                .filter { it.status == SalaryStatus.PAID }
                .sumOf { it.amount }
            
            // Calculate pending amount
            val pendingAmount = salaries
                .filter { it.status == SalaryStatus.PENDING || it.status == SalaryStatus.PROCESSED }
                .sumOf { it.amount }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Disbursed",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(totalPaid),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = CurrencyFormatter.formatAsRupees(pendingAmount),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HRSummaryCard(
    employees: List<Employee>,
    pendingLeaves: Int,
    pendingPayments: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "HR Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val totalEmployees = employees.size
            val activeEmployees = employees.count { it.status == EmployeeStatus.ACTIVE }
            val onLeaveEmployees = employees.count { it.status == EmployeeStatus.ON_LEAVE }
            val teacherCount = employees.count { it.role.name.contains("TEACHER") }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    icon = Icons.Default.Person,
                    value = totalEmployees.toString(),
                    label = "Total Employees"
                )
                
                SummaryItem(
                    icon = Icons.Default.School,
                    value = teacherCount.toString(),
                    label = "Teachers"
                )
                
                SummaryItem(
                    icon = Icons.Default.AccessTime,
                    value = onLeaveEmployees.toString(),
                    label = "On Leave"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    icon = Icons.Default.AccessTime,
                    value = pendingLeaves.toString(),
                    label = "Pending Leaves",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                
                SummaryItem(
                    icon = Icons.Default.AttachMoney,
                    value = pendingPayments.toString(),
                    label = "Pending Payments",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = tint,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmployeeItem(
    employee: Employee,
    onEmployeeClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEmployeeClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${employee.firstName} ${employee.lastName}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${employee.role.name.lowercase().replaceFirstChar { it.uppercase() }} - ${employee.department}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "ID: ${employee.employeeId} | Hired: ${dateFormat.format(employee.hireDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Text(
                text = employee.status.name,
                style = MaterialTheme.typography.bodyMedium,
                color = when (employee.status) {
                    EmployeeStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                    EmployeeStatus.ON_LEAVE -> MaterialTheme.colorScheme.tertiary
                    EmployeeStatus.TERMINATED -> MaterialTheme.colorScheme.error
                    EmployeeStatus.RETIRED -> MaterialTheme.colorScheme.error
                    EmployeeStatus.SABBATICAL -> MaterialTheme.colorScheme.tertiary
                }
            )
        }
    }
}

@Composable
fun LeaveRequestItem(
    leaveRequest: LeaveRequest,
    onClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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
                    text = leaveRequest.leaveType.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "${leaveRequest.requestedDays} days",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "From: ${dateFormat.format(leaveRequest.startDate)}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Text(
                text = "To: ${dateFormat.format(leaveRequest.endDate)}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = leaveRequest.status.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (leaveRequest.status) {
                        LeaveStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                        LeaveStatus.APPROVED -> MaterialTheme.colorScheme.primary
                        LeaveStatus.REJECTED -> MaterialTheme.colorScheme.error
                        LeaveStatus.CANCELLED -> MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}

@Composable
fun SalaryItem(
    salary: Salary,
    onClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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
                    text = "Salary Payment",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = CurrencyFormatter.formatAsRupees(salary.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Period: ${dateFormat.format(salary.payPeriodStart)} to ${dateFormat.format(salary.payPeriodEnd)}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = salary.paymentMethod.name.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = salary.status.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (salary.status) {
                        SalaryStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                        SalaryStatus.PROCESSED -> MaterialTheme.colorScheme.primary
                        SalaryStatus.PAID -> MaterialTheme.colorScheme.primary
                        SalaryStatus.CANCELLED -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun TDSPreview() {
    TeacherDashboardScreen(
        viewModel = viewModel<TeacherViewModel>(),
        {}, {}, {}, {}, {}
    )
}