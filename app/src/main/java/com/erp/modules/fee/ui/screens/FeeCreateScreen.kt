package com.erp.modules.fee.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erp.modules.fee.data.model.FeeType
import com.erp.modules.fee.ui.viewmodel.FeeViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeCreateScreen(
    viewModel: FeeViewModel,
    onNavigateBack: () -> Unit
) {
    var studentId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedFeeType by remember { mutableStateOf(FeeType.TUITION) }
    var academicYear by remember { mutableStateOf("2023-2024") }
    var term by remember { mutableStateOf("1") }
    var remarks by remember { mutableStateOf("") }
    var showFeeTypeMenu by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Fee") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = studentId,
                onValueChange = { studentId = it },
                label = { Text("Student ID") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedFeeType.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fee Type") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showFeeTypeMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Fee Type"
                            )
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = showFeeTypeMenu,
                    onDismissRequest = { showFeeTypeMenu = false }
                ) {
                    FeeType.values().forEach { feeType ->
                        DropdownMenuItem(
                            text = { Text(feeType.toString()) },
                            onClick = {
                                selectedFeeType = feeType
                                showFeeTypeMenu = false
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = academicYear,
                onValueChange = { academicYear = it },
                label = { Text("Academic Year") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = term,
                onValueChange = { term = it },
                label = { Text("Term") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Remarks") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            Button(
                onClick = {
                    viewModel.createFee(
                        studentId = studentId,
                        feeType = selectedFeeType,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        dueDate = Date(), // You might want to add a date picker here
                        academicYear = academicYear,
                        term = term,
                        remarks = remarks
                    )
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading && studentId.isNotEmpty() && amount.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Fee")
                }
            }
            
            if (error != null) {
                Text(
                    text = error ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
} 