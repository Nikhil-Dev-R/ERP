package com.erp.modules.student.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.erp.data.UserRole
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.ui.viewmodel.StudentDetailUiState
import com.erp.modules.student.ui.viewmodel.StudentDetailUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun StudentHomeScreen(
    studentDetailUiStateFlow: StateFlow<StudentDetailUiState>,
    role: UserRole,
    studentId: String? = null,
    deleteStudent: (Student) -> Unit = {},
    onNavigateToEdit: (String?) -> Unit = {},
) {
    StudentDetailScreen(
        studentDetailUiStateFlow = studentDetailUiStateFlow,
        role = role,
        studentId = studentId,
        isHome = true,
        deleteStudent = deleteStudent,
        onNavigateToEdit = onNavigateToEdit,
    )
}

@Preview
@Composable
fun SHSPreview() {
    StudentHomeScreen(
        studentDetailUiStateFlow = MutableStateFlow(Success(Student())).asStateFlow(),
        role = UserRole.Student
    )

}