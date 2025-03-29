package com.erp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.erp.core.auth.AuthManager
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.attendance.ui.viewmodel.AttendanceViewModel
import com.erp.modules.exam.viewmodel.ExamViewModel
import com.erp.modules.fee.ui.viewmodel.FeeViewModel
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import com.erp.modules.student.ui.viewmodel.StudentViewModel

class MainViewModel(
    val authManager: AuthManager,
    val studentViewModel: StudentViewModel,
    val academicsViewModel: AcademicsViewModel, 
    val attendanceViewModel: AttendanceViewModel,
    val examViewModel: ExamViewModel,
    val financeViewModel: FinanceViewModel,
    val hrViewModel: HRViewModel,
    val inventoryViewModel: InventoryViewModel,
    val feeViewModel: FeeViewModel
) : ViewModel() {
    // Main app level state and functions can go here
} 