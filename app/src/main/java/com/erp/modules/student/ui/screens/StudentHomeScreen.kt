package com.erp.modules.student.ui.screens

import androidx.compose.runtime.Composable
import com.erp.data.UserRole
import com.erp.modules.student.ui.viewmodel.StudentDetailUiState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StudentHomeScreen(
    studentDetailUiStateFlow: StateFlow<StudentDetailUiState>,
    role: UserRole,
    studentId: String? = null,
) {
    StudentDetailScreen(
        studentDetailUiStateFlow = studentDetailUiStateFlow,
        role = role,
        studentId = studentId
    )
}