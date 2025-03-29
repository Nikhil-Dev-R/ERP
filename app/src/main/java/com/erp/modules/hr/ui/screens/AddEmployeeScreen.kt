package com.erp.modules.hr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.EmployeeRole
import com.erp.modules.hr.data.model.EmployeeStatus
import com.erp.modules.hr.data.model.EmploymentType
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(
    viewModel: HRViewModel,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Form fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(EmployeeRole.TEACHER) }
    var department by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("MALE") }
    var status by remember { mutableStateOf(EmployeeStatus.ACTIVE) }
    var photoUrl by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var emergencyContactName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var previousExperience by remember { mutableStateOf("0") }
    var employmentType by remember { mutableStateOf(EmploymentType.FULL_TIME) }
    var position by remember { mutableStateOf("") }
    var reportingTo by remember { mutableStateOf("") }
    
    // Date pickers
    var dateOfBirth by remember { mutableStateOf<Date?>(null) }
    var showDateOfBirthPicker by remember { mutableStateOf(false) }
    val dateOfBirthState = rememberDatePickerState()
    
    var hireDate by remember { mutableStateOf<Date>(Date()) }
    var showHireDatePicker by remember { mutableStateOf(false) }
    val hireDateState = rememberDatePickerState(initialSelectedDateMillis = hireDate.time)
    
    // Dropdowns
    var expandedRole by remember { mutableStateOf(false) }
    var expandedGender by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }
    var expandedEmploymentType by remember { mutableStateOf(false) }
    
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Employee") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (validateForm(
                        firstName = firstName,
                        lastName = lastName,
                        employeeId = employeeId,
                        contactNumber = contactNumber,
                        email = email,
                        department = department,
                        position = position
                    )) {
                        val employee = Employee(
                            firstName = firstName,
                            lastName = lastName,
                            employeeId = employeeId,
                            role = role,
                            department = department,
                            qualification = qualification,
                            specialization = specialization,
                            contactNumber = contactNumber,
                            email = email,
                            address = address,
                            gender = gender,
                            dateOfBirth = dateOfBirth,
                            status = status,
                            photoUrl = photoUrl,
                            emergencyContact = emergencyContact,
                            emergencyContactName = emergencyContactName,
                            bloodGroup = bloodGroup,
                            previousExperience = previousExperience.toIntOrNull() ?: 0,
                            employmentType = employmentType,
                            position = position,
                            reportingTo = reportingTo,
                            hireDate = hireDate,
                            // Initialize empty lists for teaching fields
                            subjectsIds = emptyList(),
                            classesTaught = emptyList()
                        )
                        // Set ID separately since it's not a constructor parameter
                        employee.id = UUID.randomUUID().toString()
                        
                        scope.launch {
                            viewModel.saveEmployee(employee)
                            snackbarHostState.showSnackbar("Employee saved successfully")
                            onNavigateBack()
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill all required fields correctly")
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleMedium
            )
            
            // First Name
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Last Name
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Employee ID
            OutlinedTextField(
                value = employeeId,
                onValueChange = { employeeId = it },
                label = { Text("Employee ID*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // Gender Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = !expandedGender }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    listOf("MALE", "FEMALE", "OTHER").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                expandedGender = false
                            }
                        )
                    }
                }
            }
            
            // Date of Birth
            OutlinedTextField(
                value = dateOfBirth?.let { dateFormatter.format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Date of Birth") },
                trailingIcon = {
                    IconButton(onClick = { showDateOfBirthPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (showDateOfBirthPicker) {
                DatePickerDialog(
                    onDismissRequest = { showDateOfBirthPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            dateOfBirthState.selectedDateMillis?.let {
                                dateOfBirth = Date(it)
                            }
                            showDateOfBirthPicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateOfBirthPicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = dateOfBirthState)
                }
            }
            
            // Contact Information
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Contact Information",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Contact Number
            OutlinedTextField(
                value = contactNumber,
                onValueChange = { contactNumber = it },
                label = { Text("Contact Number*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )
            
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            
            // Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Emergency Contact
            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )
            
            // Emergency Contact Name
            OutlinedTextField(
                value = emergencyContactName,
                onValueChange = { emergencyContactName = it },
                label = { Text("Emergency Contact Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Employment Information
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Employment Information",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Role Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedRole,
                onExpandedChange = { expandedRole = !expandedRole }
            ) {
                OutlinedTextField(
                    value = role.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedRole,
                    onDismissRequest = { expandedRole = false }
                ) {
                    EmployeeRole.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name.replace("_", " ")) },
                            onClick = {
                                role = option
                                expandedRole = false
                            }
                        )
                    }
                }
            }
            
            // Department
            OutlinedTextField(
                value = department,
                onValueChange = { department = it },
                label = { Text("Department*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Position
            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Position*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Employment Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedEmploymentType,
                onExpandedChange = { expandedEmploymentType = !expandedEmploymentType }
            ) {
                OutlinedTextField(
                    value = employmentType.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Employment Type*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmploymentType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedEmploymentType,
                    onDismissRequest = { expandedEmploymentType = false }
                ) {
                    EmploymentType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name.replace("_", " ")) },
                            onClick = {
                                employmentType = option
                                expandedEmploymentType = false
                            }
                        )
                    }
                }
            }
            
            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = status.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    EmployeeStatus.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                status = option
                                expandedStatus = false
                            }
                        )
                    }
                }
            }
            
            // Hire Date
            OutlinedTextField(
                value = dateFormatter.format(hireDate),
                onValueChange = {},
                readOnly = true,
                label = { Text("Hire Date*") },
                trailingIcon = {
                    IconButton(onClick = { showHireDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (showHireDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showHireDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            hireDateState.selectedDateMillis?.let {
                                hireDate = Date(it)
                            }
                            showHireDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showHireDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = hireDateState)
                }
            }
            
            // Reporting To
            OutlinedTextField(
                value = reportingTo,
                onValueChange = { reportingTo = it },
                label = { Text("Reports To") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Additional Information
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium
            )
            
            // Qualification
            OutlinedTextField(
                value = qualification,
                onValueChange = { qualification = it },
                label = { Text("Qualification") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Specialization
            OutlinedTextField(
                value = specialization,
                onValueChange = { specialization = it },
                label = { Text("Specialization") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Previous Experience
            OutlinedTextField(
                value = previousExperience,
                onValueChange = { previousExperience = it },
                label = { Text("Previous Experience (Years)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            
            // Blood Group
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                )
            )
            
            // Submit Button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (validateForm(
                        firstName = firstName,
                        lastName = lastName,
                        employeeId = employeeId,
                        contactNumber = contactNumber,
                        email = email,
                        department = department,
                        position = position
                    )) {
                        val employee = Employee(
                            firstName = firstName,
                            lastName = lastName,
                            employeeId = employeeId,
                            role = role,
                            department = department,
                            qualification = qualification,
                            specialization = specialization,
                            contactNumber = contactNumber,
                            email = email,
                            address = address,
                            gender = gender,
                            dateOfBirth = dateOfBirth,
                            status = status,
                            photoUrl = photoUrl,
                            emergencyContact = emergencyContact,
                            emergencyContactName = emergencyContactName,
                            bloodGroup = bloodGroup,
                            previousExperience = previousExperience.toIntOrNull() ?: 0,
                            employmentType = employmentType,
                            position = position,
                            reportingTo = reportingTo,
                            hireDate = hireDate,
                            // Initialize empty lists for teaching fields
                            subjectsIds = emptyList(),
                            classesTaught = emptyList()
                        )
                        // Set ID separately since it's not a constructor parameter
                        employee.id = UUID.randomUUID().toString()
                        
                        scope.launch {
                            viewModel.saveEmployee(employee)
                            snackbarHostState.showSnackbar("Employee saved successfully")
                            onNavigateBack()
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill all required fields correctly")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Save Employee")
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}

private fun validateForm(
    firstName: String,
    lastName: String,
    employeeId: String,
    contactNumber: String,
    email: String,
    department: String,
    position: String
): Boolean {
    // Check required fields
    if (firstName.isBlank() || 
        lastName.isBlank() || 
        employeeId.isBlank() || 
        contactNumber.isBlank() || 
        email.isBlank() || 
        department.isBlank() ||
        position.isBlank()) {
        return false
    }
    
    // Email validation
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    if (!email.matches(emailPattern.toRegex())) {
        return false
    }
    
    // Phone number validation (basic check)
    if (contactNumber.length < 10) {
        return false
    }
    
    return true
} 