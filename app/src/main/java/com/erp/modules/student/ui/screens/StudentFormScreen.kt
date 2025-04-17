package com.erp.modules.student.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.erp.components.ERPTopBar
import com.erp.modules.student.data.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(
    title: String = "Add Student",
    observeStudent: StateFlow<Student?>,
    saveStudent: (Student) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentStudent by observeStudent.collectAsState()
    var student by remember { mutableStateOf(Student()) }
    
    // Update student when currentStudent changes
    LaunchedEffect(currentStudent) {
        currentStudent?.let {
            student = it
        }
    }
    
    Scaffold(
        topBar = {
            ERPTopBar(
                title = title,
                onNavIconClick = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        saveStudent(student)
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
        StudentForm(
            student = student,
            onStudentChange = { student = it },
            saveStudent = saveStudent,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun StudentForm(
    student: Student,
    onStudentChange: (Student) -> Unit,
    saveStudent: (Student) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        GroupCard {
            // Personal Information Section
            SectionHeader(title = "Personal Information")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // First Name
                OutlinedTextField(
                    value = student.firstName,
                    onValueChange = { onStudentChange(student.copy(firstName = it)) },
                    label = { Text("First Name") },
                    singleLine = true,
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
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Enrollment Number
            OutlinedTextField(
                value = student.enrollmentNumber,
                onValueChange = { onStudentChange(student.copy(enrollmentNumber = it)) },
                label = { Text("Enrollment/Registration Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date of Birth
            SimpleDatePickerField(
                date = student.dateOfBirth,
                onDateSelected = { onStudentChange(student.copy(dateOfBirth = it)) },
                label = "Date of Birth"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gender
            GenderSelection(
                selectedGender = student.gender,
                onGenderSelected = { onStudentChange(student.copy(gender = it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Blood Group
            OutlinedTextField(
                value = student.bloodGroup,
                onValueChange = { onStudentChange(student.copy(bloodGroup = it)) },
                label = { Text("Blood Group") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Bloodtype, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Next
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GroupCard {
            // Class Information Section
            SectionHeader(title = "Class Information")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Grade/Class
                OutlinedTextField(
                    value = student.grade,
                    onValueChange = { onStudentChange(student.copy(grade = it)) },
                    label = { Text("Grade/Class") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Section
                OutlinedTextField(
                    value = student.section,
                    onValueChange = { onStudentChange(student.copy(section = it)) },
                    label = { Text("Section") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Admission Date
            SimpleDatePickerField(
                date = student.admissionDate,
                onDateSelected = { onStudentChange(student.copy(admissionDate = it)) },
                label = "Admission Date"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Previous School
            OutlinedTextField(
                value = student.previousSchool,
                onValueChange = { onStudentChange(student.copy(previousSchool = it)) },
                label = { Text("Previous School") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GroupCard {
            // Contact Information
            SectionHeader(title = "Contact Information")

            // Phone
            OutlinedTextField(
                value = student.contactNumber,
                onValueChange = { onStudentChange(student.copy(contactNumber = it)) },
                label = { Text("Contact Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            OutlinedTextField(
                value = student.email,
                onValueChange = { onStudentChange(student.copy(email = it)) },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

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
        }

        Spacer(modifier = Modifier.height(12.dp))

        GroupCard {
            // Parent Information
            SectionHeader(title = "Parent/Guardian Information")

            // Parent Name
            OutlinedTextField(
                value = student.parentName,
                onValueChange = { onStudentChange(student.copy(parentName = it)) },
                label = { Text("Parent Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Parent Contact
            OutlinedTextField(
                value = student.parentContact,
                onValueChange = { onStudentChange(student.copy(parentContact = it)) },
                label = { Text("Parent Contact") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Parent Email
            OutlinedTextField(
                value = student.parentEmail,
                onValueChange = { onStudentChange(student.copy(parentEmail = it)) },
                label = { Text("Parent Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GroupCard {
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

            Spacer(modifier = Modifier.height(8.dp))

            // Photo URL
            OutlinedTextField(
                value = student.photoUrl,
                onValueChange = { onStudentChange(student.copy(photoUrl = it)) },
                label = { Text("Add Photo") },
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

            Spacer(modifier = Modifier.height(8.dp))

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
        }
        
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = { saveStudent(student) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            Text("Save")
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun GenderSelection(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    val genderOptions = listOf("Male", "Female", "Other")
    
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = 1.dp,
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.outline
            )
    ) {
        Text(
            text = "Gender",
            style = MaterialTheme.typography.labelLarge
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            genderOptions.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    RadioButton(
                        selected = gender == selectedGender,
                        onClick = { onGenderSelected(gender) }
                    )
                    Text(
                        text = gender,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            content = content
        )
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

@Preview
@Composable
fun SFSPreview() {
    StudentFormScreen(
        observeStudent = MutableStateFlow(Student("First")).asStateFlow(),
        onNavigateBack = {},
        saveStudent = {},
    )
}