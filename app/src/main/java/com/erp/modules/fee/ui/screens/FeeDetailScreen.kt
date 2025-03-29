package com.erp.modules.fee.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erp.modules.fee.ui.viewmodel.FeeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeDetailScreen(
    viewModel: FeeViewModel,
    feeId: String,
    onNavigateBack: () -> Unit
) {
    val fee by viewModel.currentFee.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(feeId) {
        viewModel.loadFeeById(feeId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fee Details") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null) {
                Text(
                    text = error ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                fee?.let { feeData ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        FeeDetailItem("Fee Type", feeData.feeType.toString())
                        FeeDetailItem("Amount", "$${feeData.amount}")
                        FeeDetailItem("Payment Status", feeData.paymentStatus.toString())
                        FeeDetailItem("Academic Year", feeData.academicYear)
                        FeeDetailItem("Term", feeData.term)
                        feeData.dueDate?.let {
                            FeeDetailItem(
                                "Due Date",
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                            )
                        }
                        feeData.paymentDate?.let {
                            FeeDetailItem(
                                "Payment Date",
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                            )
                        }
                        feeData.paymentMethod?.let {
                            FeeDetailItem("Payment Method", it)
                        }
                        FeeDetailItem("Student ID", feeData.studentId)
                        feeData.remarks?.let {
                            FeeDetailItem("Remarks", it)
                        }
                    }
                } ?: run {
                    Text(
                        text = "Fee not found",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeeDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
} 