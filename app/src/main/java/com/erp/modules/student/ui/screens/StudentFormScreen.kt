package com.erp.modules.student.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(
    viewModel: StudentViewModel,
    studentId: String?,
    onNavigateBack: () -> Unit
) {
    val currentStudent by viewModel.currentStudent.collectAsState()
    var student by remember { mutableStateOf(Student()) }
    var isLoading by remember { mutableStateOf(studentId != null) }
    
    // Load student data when the screen launches
    LaunchedEffect(studentId) {
        if (studentId != null) {
            viewModel.getStudentDetail(studentId)
        } else {
            viewModel.createNewStudent()
        }
    }
    
    // Update student when currentStudent changes
    LaunchedEffect(currentStudent) {
        currentStudent?.let {
            student = it
            isLoading = false
        }
    }
    
    val title = if (studentId == null) "Add Student" else "Edit Student"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveStudent(student)
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Student"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StudentForm(
                student = student,
                onStudentChange = { student = it },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun StudentForm(
    student: Student,
    onStudentChange: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Personal Information Section
        SectionHeader(title = "Personal Information")
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // First Name
            OutlinedTextField(
                value = student.firstName,
                onValueChange = { onStudentChange(student.copy(firstName = it)) },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
            
            // Last Name
            OutlinedTextField(
                value = student.lastName,
                onValueChange = { onStudentChange(student.copy(lastName = it)) },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Enrollment Number
        OutlinedTextField(
            value = student.enrollmentNumber,
            onValueChange = { onStudentChange(student.copy(enrollmentNumber = it)) },
            label = { Text("Enrollment/Registration Number") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Date of Birth
        SimpleDatePickerField(
            date = student.dateOfBirth,
            onDateSelected = { onStudentChange(student.copy(dateOfBirth = it)) },
            label = "Date of Birth"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gender
        GenderSelection(
            selectedGender = student.gender,
            onGenderSelected = { onStudentChange(student.copy(gender = it)) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Blood Group
        OutlinedTextField(
            value = student.bloodGroup,
            onValueChange = { onStudentChange(student.copy(bloodGroup = it)) },
            label = { Text("Blood Group") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Bloodtype, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Class Information Section
        SectionHeader(title = "Class Information")
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Grade/Class
            OutlinedTextField(
                value = student.grade,
                onValueChange = { onStudentChange(student.copy(grade = it)) },
                label = { Text("Grade/Class") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            // Section
            OutlinedTextField(
                value = student.section,
                onValueChange = { onStudentChange(student.copy(section = it)) },
                label = { Text("Section") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Next
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Admission Date
        SimpleDatePickerField(
            date = student.admissionDate,
            onDateSelected = { onStudentChange(student.copy(admissionDate = it)) },
            label = "Admission Date"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Previous School
        OutlinedTextField(
            value = student.previousSchool,
            onValueChange = { onStudentChange(student.copy(previousSchool = it)) },
            label = { Text("Previous School") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Contact Information
        SectionHeader(title = "Contact Information")
        
        // Phone
        OutlinedTextField(
            value = student.contactNumber,
            onValueChange = { onStudentChange(student.copy(contactNumber = it)) },
            label = { Text("Contact Number") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email
        OutlinedTextField(
            value = student.email,
            onValueChange = { onStudentChange(student.copy(email = it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Address
        OutlinedTextField(
            value = student.address,
            onValueChange = { onStudentChange(student.copy(address = it)) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Emergency Contact
        OutlinedTextField(
            value = student.emergencyContact,
            onValueChange = { onStudentChange(student.copy(emergencyContact = it)) },
            label = { Text("Emergency Contact") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Parent Information
        SectionHeader(title = "Parent/Guardian Information")
        
        // Parent Name
        OutlinedTextField(
            value = student.parentName,
            onValueChange = { onStudentChange(student.copy(parentName = it)) },
            label = { Text("Parent Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Parent Contact
        OutlinedTextField(
            value = student.parentContact,
            onValueChange = { onStudentChange(student.copy(parentContact = it)) },
            label = { Text("Parent Contact") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Parent Email
        OutlinedTextField(
            value = student.parentEmail,
            onValueChange = { onStudentChange(student.copy(parentEmail = it)) },
            label = { Text("Parent Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Additional Information
        SectionHeader(title = "Additional Information")
        
        // Health Notes
        OutlinedTextField(
            value = student.healthNotes,
            onValueChange = { onStudentChange(student.copy(healthNotes = it)) },
            label = { Text("Health Notes") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.HealthAndSafety, contentDescription = null) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Photo URL
        OutlinedTextField(
            value = student.photoUrl,
            onValueChange = { onStudentChange(student.copy(photoUrl = it)) },
            label = { Text("Photo URL") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        
        if (student.photoUrl.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = student.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Active Status
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = student.active,
                onCheckedChange = { onStudentChange(student.copy(active = it)) }
            )
            
            Text(
                text = "Student is active",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun GenderSelection(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    val genderOptions = listOf("Male", "Female", "Other")
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Gender",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            genderOptions.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    RadioButton(
                        selected = gender == selectedGender,
                        onClick = { onGenderSelected(gender) }
                    )
                    Text(
                        text = gender,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerField(
    date: Date?,
    onDateSelected: (Date) -> Unit,
    label: String
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val formattedDate = date?.let { dateFormatter.format(it) } ?: "Select Date"
    
    // Create date picker state with initial date if available
    val initialSelectedDateMillis = date?.time
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)
    
    OutlinedTextField(
        value = formattedDate,
        onValueChange = { },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.Event, contentDescription = "Select Date")
            }
        },
        readOnly = true
    )
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
} 