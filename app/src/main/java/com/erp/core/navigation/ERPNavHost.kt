package com.erp.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.erp.core.ui.screens.HomeScreen
import com.erp.core.ui.screens.LoginScreen
import com.erp.core.ui.screens.RegisterScreen
import com.erp.data.UserRole
import com.erp.data.UserRole.Admin
import com.erp.data.UserRole.Parent
import com.erp.data.UserRole.Student
import com.erp.data.UserRole.Teacher
import com.erp.modules.finance.ui.screens.FinanceDashboardScreen
import com.erp.modules.hr.ui.screens.HRDashboardScreen
import com.erp.modules.inventory.ui.screens.InventoryDashboardScreen
import com.erp.modules.inventory.ui.screens.ProductsScreen
import com.erp.modules.inventory.ui.screens.ProductDetailScreen
import com.erp.modules.student.ui.screens.StudentsScreen
import com.erp.ui.viewmodel.MainViewModel
import com.erp.modules.attendance.ui.screens.AttendanceScreen
import com.erp.modules.academics.ui.screens.AcademicsScreen
import com.erp.modules.academics.ui.screens.ClassSelectionScreen
import com.erp.modules.academics.ui.screens.SubjectsByClassScreen
import com.erp.modules.academics.ui.screens.SubjectDetailScreen
import com.erp.modules.academics.ui.screens.SectionSelectionScreen
import com.erp.modules.academics.ui.screens.TimeTableScreen
import com.erp.modules.academics.ui.screens.SectionFilesScreen
import com.erp.modules.student.ui.screens.StudentDetailScreen
import com.erp.modules.student.ui.screens.StudentFormScreen
import com.erp.modules.student.ui.screens.StudentDashboardScreen
import com.erp.modules.exam.ui.screens.ExamScreen
import com.erp.modules.exam.ui.screens.QuizManagementScreen
import com.erp.modules.exam.ui.screens.QuizCreateScreen
import com.erp.modules.exam.ui.screens.ResultsUploadScreen
import com.erp.modules.exam.ui.screens.ResultsViewScreen
import com.erp.modules.hr.ui.screens.EmployeeDetailScreen
import com.erp.modules.inventory.ui.screens.VendorsScreen
import com.erp.modules.hr.ui.screens.AddEmployeeScreen
import com.erp.modules.teacher.ui.screens.LeaveRequestsScreen
import com.erp.modules.hr.ui.screens.LeaveRequestDetailScreen
import com.erp.modules.fee.ui.screens.FeeListScreen
import com.erp.modules.fee.ui.screens.FeeDetailScreen
import com.erp.modules.fee.ui.screens.FeeCreateScreen
import com.erp.modules.finance.ui.screens.TransactionDetailScreen
import com.erp.modules.finance.ui.screens.TransactionsScreen
import com.erp.modules.finance.ui.screens.InvoicesScreen
import com.erp.modules.finance.ui.screens.InvoiceDetailScreen
import com.erp.modules.finance.ui.screens.FinancialReportsScreen
import com.erp.modules.finance.ui.screens.BudgetManagementScreen
import com.erp.modules.student.ui.screens.StudentHomeScreen

@Composable
fun ERPNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel
) {
    // Determine start destination based on authentication status
    val startDestination = ERPDestinations.ADMIN_HOME_ROUTE

//        if (viewModel.authManager.isUserSignedIn()) {
//        ERPDestinations.HOME_ROUTE
//    } else {
//        ERPDestinations.LOGIN_ROUTE
//    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ERPDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                authManager = viewModel.authManager,
                onRegisterSuccess = { role, id ->
                    val route = when (role) {
                        is Admin -> ERPDestinations.ADMIN_HOME_ROUTE
                        is Parent, is Student -> "${ERPDestinations.STUDENT_HOME_ROUTE}?studentId=$id"
                        else -> ERPDestinations.ADMIN_HOME_ROUTE
                    }
                    navController.navigate(route) {
                        popUpTo(ERPDestinations.REGISTER_ROUTE) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Navigate back to the login screen.
                    navController.navigate(ERPDestinations.LOGIN_ROUTE) {
                        popUpTo(ERPDestinations.REGISTER_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(ERPDestinations.LOGIN_ROUTE) {
            LoginScreen(
                authManager = viewModel.authManager,
                onLoginSuccess = { role: UserRole, id: String ->
                    val route = when (role) {
                        is Admin -> ERPDestinations.ADMIN_HOME_ROUTE
                        is Teacher, is Parent, is Student -> "${ERPDestinations.STUDENT_DETAIL_ROUTE}?studentId=$id"
                        else -> ERPDestinations.ADMIN_HOME_ROUTE
                    }
                    navController.navigate(route) {
                        popUpTo(ERPDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
//                    navController.navigate(ERPDestinations.HOME_ROUTE) {
//                        popUpTo(ERPDestinations.LOGIN_ROUTE) { inclusive = true }
//                    }
                },
                onRegisterClick = {
                    navController.navigate(ERPDestinations.REGISTER_ROUTE)  {
                        popUpTo(ERPDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        composable(ERPDestinations.ADMIN_HOME_ROUTE) {
            HomeScreen(
                onNavigateToFinance = {
                    navController.navigate(ERPDestinations.FINANCE_DASHBOARD_ROUTE)
                },
                onNavigateToHR = {
                    navController.navigate(ERPDestinations.HR_DASHBOARD_ROUTE)
                },
                onNavigateToStudents = {
                    navController.navigate(ERPDestinations.STUDENTS_DASHBOARD_ROUTE)
                },
                onNavigateToAcademics = {
                    navController.navigate(ERPDestinations.ACADEMICS_DASHBOARD_ROUTE)
                },
                onNavigateToAttendance = {
                    navController.navigate(ERPDestinations.ATTENDANCE_DASHBOARD_ROUTE)
                },
                onNavigateToExams = {
                    navController.navigate(ERPDestinations.EXAM_DASHBOARD_ROUTE)
                },
                onNavigateToInventory = {
                    navController.navigate(ERPDestinations.INVENTORY_DASHBOARD_ROUTE)
                },
                onNavigateToFees = {
                    navController.navigate(ERPDestinations.FEE_MODULE_DASHBOARD_ROUTE)
                },
                onLogout = {
                    viewModel.authManager.signOut()
                    navController.navigate(ERPDestinations.LOGIN_ROUTE) {
                        popUpTo(ERPDestinations.ADMIN_HOME_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        // Finance module navigation
        composable(ERPDestinations.FINANCE_DASHBOARD_ROUTE) {
            FinanceDashboardScreen(
//                viewModel = viewModel.financeViewModel,
                observeUiState = viewModel.financeViewModel.financeUiState,
                onNavigateToTransactions = {
                    navController.navigate(ERPDestinations.TRANSACTIONS_ROUTE)
                },
                onNavigateToInvoices = {
                    navController.navigate(ERPDestinations.INVOICES_ROUTE)
                },
                onNavigateToFees = {
                    navController.navigate(ERPDestinations.FEES_ROUTE)
                },
                onNavigateToReports = {
                    navController.navigate(ERPDestinations.FINANCIAL_REPORTS_ROUTE)
                },
                onNavigateToBudgets = {
                    navController.navigate(ERPDestinations.BUDGET_MANAGEMENT_ROUTE)
                },
                onAddTransaction = {
                    navController.navigate(ERPDestinations.TRANSACTION_DETAIL_ROUTE)
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        // Add the missing transaction detail route
        composable(ERPDestinations.TRANSACTION_DETAIL_ROUTE) {
            TransactionDetailScreen(
                viewModel = viewModel.financeViewModel,
                transactionId = null,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = "${ERPDestinations.TRANSACTION_DETAIL_ROUTE}/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            TransactionDetailScreen(
                viewModel = viewModel.financeViewModel,
                transactionId = transactionId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.TRANSACTIONS_ROUTE) {
            TransactionsScreen(
                viewModel = viewModel.financeViewModel,
                onNavigateToTransactionDetail = { transactionId ->
                    if (transactionId == null) {
                        navController.navigate(ERPDestinations.TRANSACTION_DETAIL_ROUTE)
                    } else {
                        navController.navigate("${ERPDestinations.TRANSACTION_DETAIL_ROUTE}/$transactionId")
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.INVOICES_ROUTE) {
            InvoicesScreen(
                viewModel = viewModel.financeViewModel,
                onNavigateToInvoiceDetail = { invoiceId ->
                    if (invoiceId == null) {
                        navController.navigate(ERPDestinations.INVOICE_DETAIL_ROUTE)
                    } else {
                        navController.navigate("${ERPDestinations.INVOICE_DETAIL_ROUTE}/$invoiceId")
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.INVOICE_DETAIL_ROUTE) {
            InvoiceDetailScreen(
                viewModel = viewModel.financeViewModel,
                invoiceId = null,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = "${ERPDestinations.INVOICE_DETAIL_ROUTE}/{invoiceId}",
            arguments = listOf(
                navArgument("invoiceId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId")
            InvoiceDetailScreen(
                viewModel = viewModel.financeViewModel,
                invoiceId = invoiceId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.FEES_ROUTE) {
            PlaceholderScreen(
                title = "School Fees", 
                message = "Fee management screens are under development"
            )
        }
        
        // Fee module navigation
        composable(ERPDestinations.FEE_MODULE_DASHBOARD_ROUTE) {
            FeeListScreen(
                viewModel = viewModel.feeViewModel,
                onFeeClick = { feeId ->
                    navController.navigate("${ERPDestinations.FEE_MODULE_DETAIL_ROUTE}/$feeId")
                },
                onAddFeeClick = {
                    navController.navigate(ERPDestinations.FEE_MODULE_CREATE_ROUTE)
                }
            )
        }
        
        composable(
            route = "${ERPDestinations.FEE_MODULE_DETAIL_ROUTE}/{feeId}",
            arguments = listOf(
                navArgument("feeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val feeId = backStackEntry.arguments?.getString("feeId") ?: ""
            FeeDetailScreen(
                viewModel = viewModel.feeViewModel,
                feeId = feeId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.FEE_MODULE_CREATE_ROUTE) {
            FeeCreateScreen(
                viewModel = viewModel.feeViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        // HR module navigation (Teachers/Staff)
        composable(ERPDestinations.HR_DASHBOARD_ROUTE) {
            HRDashboardScreen(
                viewModel = viewModel.hrViewModel,
                onNavigateToEmployees = {
                    navController.navigate(ERPDestinations.EMPLOYEES_ROUTE)
                },
                onNavigateToLeaveRequests = {
                    navController.navigate(ERPDestinations.LEAVE_REQUESTS_ROUTE)
                },
                onNavigateToPayroll = {
                    navController.navigate(ERPDestinations.PAYROLL_ROUTE)
                },
                onAddEmployee = {
                    navController.navigate(ERPDestinations.EMPLOYEE_FORM_ROUTE)
                },
                onEmployeeDetails = { employeeId ->
                    navController.navigate("${ERPDestinations.EMPLOYEE_DETAIL_ROUTE}/$employeeId")
                }
            )
        }
        
        // Add new route for employee form/add employee screen
        composable(ERPDestinations.EMPLOYEE_FORM_ROUTE) {
            AddEmployeeScreen(
                viewModel = viewModel.hrViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.EMPLOYEES_ROUTE) {
            PlaceholderScreen(
                title = "Teachers & Staff", 
                message = "This screen is under development"
            )
        }
        
        composable(
            route = "${ERPDestinations.EMPLOYEE_DETAIL_ROUTE}/{employeeId}",
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")
            EmployeeDetailScreen(
                viewModel = viewModel.hrViewModel,
                employeeId = employeeId ?: "",
                onNavigateBack = {
                    navController.navigateUp()
                },
                onEditEmployee = { id ->
                    // Navigate to edit employee form
                    navController.navigate("${ERPDestinations.EMPLOYEE_FORM_ROUTE}/$id")
                }
            )
        }
        
        composable(ERPDestinations.LEAVE_REQUESTS_ROUTE) {
            LeaveRequestsScreen(
                viewModel = viewModel.hrViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToDetail = { leaveRequestId ->
                    navController.navigate("${ERPDestinations.LEAVE_REQUESTS_ROUTE}/$leaveRequestId")
                }
            )
        }
        
        // Leave request detail route
        composable(
            route = "${ERPDestinations.LEAVE_REQUESTS_ROUTE}/{leaveRequestId}",
            arguments = listOf(
                navArgument("leaveRequestId") {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
            val leaveRequestId = backStackEntry.arguments?.getString("leaveRequestId") ?: ""
            LeaveRequestDetailScreen(
                leaveRequestId = leaveRequestId,
                viewModel = viewModel.hrViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.PAYROLL_ROUTE) {
            PlaceholderScreen(
                title = "Payroll Management", 
                message = "This screen is under development"
            )
        }

        composable(
            route = ERPDestinations.STUDENT_HOME_ROUTE + "?studentId={studentId}",
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")

            LaunchedEffect(studentId) {
                if (studentId == null || studentId.isEmpty()) {
                    viewModel.studentViewModel.createNewStudent()
                } else {
                    viewModel.studentViewModel.getStudentDetail(studentId)
                }
            }
            StudentHomeScreen(
                studentDetailUiStateFlow = viewModel.studentViewModel.studentDetailState,
                role = Student,
                deleteStudent = viewModel.studentViewModel::deleteStudent,
                onNavigateToEdit = {
                    navController.navigate("${ERPDestinations.STUDENT_FORM_ROUTE}?studentId=$studentId")
                }
            )
        }
        
        // Student module navigation
        composable(ERPDestinations.STUDENTS_DASHBOARD_ROUTE) {
            StudentDashboardScreen(
                onNavigateToStudentsList = {
                    navController.navigate(ERPDestinations.STUDENTS_ROUTE)
                },
                onNavigateToAddStudent = {
                    navController.navigate(ERPDestinations.STUDENT_FORM_ROUTE)
                },
                onNavigateToStudentsByClass = {
                    // This would ideally navigate to a class selection screen
                    navController.navigate(ERPDestinations.STUDENTS_ROUTE)
                },
                onNavigateToStudentAttendance = {
                    navController.navigate(ERPDestinations.ATTENDANCE_DASHBOARD_ROUTE)
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.STUDENTS_ROUTE) {
            StudentsScreen(
                observeAllStudentsState = viewModel.studentViewModel.allStudents,
                observeStudentSuggestionsState = viewModel.studentViewModel.studentSuggestions,
                searchStudents = viewModel.studentViewModel::searchStudents,
                onNavigateToStudentDetail = { studentId ->
                    if (studentId == null) {
                        navController.navigate(ERPDestinations.STUDENT_DETAIL_ROUTE)
                    } else {
                        navController.navigate("${ERPDestinations.STUDENT_DETAIL_ROUTE}?studentId=$studentId")
                    }
                },
                onFabClick = {
                    navController.navigate(ERPDestinations.STUDENT_FORM_ROUTE)
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = ERPDestinations.STUDENT_DETAIL_ROUTE + "?studentId={studentId}",
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")

            LaunchedEffect(studentId) {
                if (studentId == null || studentId.isEmpty()) {
                    viewModel.studentViewModel.createNewStudent()
                } else {
                    viewModel.studentViewModel.getStudentDetail(studentId)
                }
            }

            StudentDetailScreen(
                studentDetailUiStateFlow = viewModel.studentViewModel.studentDetailState,
                role = Admin,
                deleteStudent = viewModel.studentViewModel::deleteStudent,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToEdit = {
                    navController.navigate("${ERPDestinations.STUDENT_FORM_ROUTE}?studentId=$studentId")
                }
            )
        }
        
        // Add new route for student form
        composable(
            route = ERPDestinations.STUDENT_FORM_ROUTE + "?studentId={studentId}",
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId")

            // Load student data when the screen launches
            LaunchedEffect(studentId) {
                if (studentId != null) {
                    viewModel.studentViewModel.getStudentDetail(studentId)
                } else {
                    viewModel.studentViewModel.createNewStudent()
                }
            }

            StudentFormScreen(
                observeStudent = viewModel.studentViewModel.currentStudent,
                saveStudent = viewModel.studentViewModel::saveStudent,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        // Academics module navigation
        composable(ERPDestinations.ACADEMICS_DASHBOARD_ROUTE) {
            AcademicsScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController
            )
        }
        
        // Class selection screen
        composable(ERPDestinations.CLASS_SELECTION_ROUTE) {
            ClassSelectionScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController,
                navigateToSubjectsByClass = { className ->
                    navController.navigate("${ERPDestinations.SUBJECTS_BY_CLASS_ROUTE}/$className")
                }
            )
        }
        
        // Subjects by class screen
        composable(
            route = "${ERPDestinations.SUBJECTS_BY_CLASS_ROUTE}/{gradeLevel}",
            arguments = listOf(
                navArgument("gradeLevel") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val gradeLevel = backStackEntry.arguments?.getString("gradeLevel") ?: ""
            SubjectsByClassScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController,
                gradeLevel = gradeLevel
            )
        }
        
        // Subject detail screen
        composable(
            route = "subject_detail/{subjectId}",
            arguments = listOf(
                navArgument("subjectId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            SubjectDetailScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController,
                subjectId = subjectId
            )
        }
        
        // Section selection screen
        composable(ERPDestinations.SECTION_SELECTION_ROUTE) {
            SectionSelectionScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController,
                navigateToTimetable = { classRoomId ->
                    navController.navigate("${ERPDestinations.TIMETABLE_ROUTE}/$classRoomId")
                }
            )
        }
        
        // Section Files screen
        composable(
            route = "${ERPDestinations.SECTION_FILES_ROUTE}/{section}",
            arguments = listOf(
                navArgument("section") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val section = backStackEntry.arguments?.getString("section") ?: "A"
            SectionFilesScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController,
                section = section
            )
        }
        
        // TimeTable screen with classroom ID
        composable(
            route = "${ERPDestinations.TIMETABLE_ROUTE}/{classRoomId}",
            arguments = listOf(
                navArgument("classRoomId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val classRoomId = backStackEntry.arguments?.getString("classRoomId") ?: ""
            TimeTableScreen(
                viewModel = viewModel.academicsViewModel,
                navController = navController,
                classRoomId = classRoomId
            )
        }
        
        // Attendance module navigation
        composable(ERPDestinations.ATTENDANCE_DASHBOARD_ROUTE) {
            AttendanceScreen(
                viewModel = viewModel.attendanceViewModel,
                navController = navController
            )
        }
        
        composable(ERPDestinations.ATTENDANCE_MARK_ROUTE) {
            PlaceholderScreen(
                title = "Mark Attendance", 
                message = "This screen is under development"
            )
        }
        
        composable(ERPDestinations.ATTENDANCE_REPORT_ROUTE) {
            PlaceholderScreen(
                title = "Attendance Reports", 
                message = "This screen is under development"
            )
        }
        
        // Exam module
        composable(ERPDestinations.EXAM_DASHBOARD_ROUTE) {
            ExamScreen(
                viewModel = viewModel.examViewModel,
                navController = navController
            )
        }
        
        // Exam List screen
        composable(ERPDestinations.EXAM_LIST_ROUTE) {
            PlaceholderScreen(
                title = "Exam Schedule",
                message = "View of all scheduled exams"
            )
        }
        
        // Create Exam screen
        composable(ERPDestinations.EXAM_CREATE_ROUTE) {
            PlaceholderScreen(
                title = "Create Exam",
                message = "Form to create a new exam"
            )
        }
        
        // Quiz Management screen
        composable(ERPDestinations.QUIZ_MANAGEMENT_ROUTE) {
            QuizManagementScreen(
                viewModel = viewModel.examViewModel,
                navController = navController
            )
        }
        
        // Quiz Create/Edit screen
        composable(ERPDestinations.QUIZ_CREATE_ROUTE) {
            QuizCreateScreen(
                viewModel = viewModel.examViewModel,
                navController = navController
            )
        }
        
        // Quiz Edit screen
        composable(
            route = "${ERPDestinations.QUIZ_EDIT_ROUTE}/{quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            QuizCreateScreen(
                viewModel = viewModel.examViewModel,
                navController = navController,
                quizId = quizId
            )
        }
        
        // Quiz Detail screen
        composable(
            route = "${ERPDestinations.QUIZ_DETAIL_ROUTE}/{quizId}",
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            PlaceholderScreen(
                title = "Quiz Details",
                message = "Details and questions for quiz ID: $quizId"
            )
        }
        
        // Results Upload screen
        composable(ERPDestinations.RESULTS_UPLOAD_ROUTE) {
            ResultsUploadScreen(
                viewModel = viewModel.examViewModel,
                navController = navController
            )
        }
        
        // Results View screen
        composable(ERPDestinations.RESULTS_VIEW_ROUTE) {
            ResultsViewScreen(
                viewModel = viewModel.examViewModel,
                navController = navController
            )
        }
        
        // Result Detail screen
        composable(
            route = "${ERPDestinations.RESULT_DETAIL_ROUTE}/{resultId}",
            arguments = listOf(navArgument("resultId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resultId = backStackEntry.arguments?.getString("resultId")
            PlaceholderScreen(
                title = "Result Detail",
                message = "Detailed view of result ID: $resultId"
            )
        }
        
        // Inventory module navigation
        composable(ERPDestinations.INVENTORY_DASHBOARD_ROUTE) {
            InventoryDashboardScreen(
                viewModel = viewModel.inventoryViewModel,
                onNavigateToProducts = {
                    navController.navigate(ERPDestinations.PRODUCTS_ROUTE)
                },
                onNavigateToVendors = {
                    navController.navigate(ERPDestinations.VENDORS_ROUTE)
                },
                onAddProduct = {
                    navController.navigate(ERPDestinations.PRODUCT_DETAIL_ROUTE)
                },
                onNavigateBack = {
                    navController.navigateUp()
                },
                onAddVendor = {
                    navController.navigate(ERPDestinations.VENDORS_ROUTE)
                }
            )
        }
        
        composable(ERPDestinations.PRODUCTS_ROUTE) {
            ProductsScreen(
                viewModel = viewModel.inventoryViewModel,
                onNavigateToProductDetail = { productId: String? ->
                    navController.navigate("${ERPDestinations.PRODUCT_DETAIL_ROUTE}?productId=$productId")
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = ERPDestinations.PRODUCT_DETAIL_ROUTE + "?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                viewModel = viewModel.inventoryViewModel,
                productId = productId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.VENDORS_ROUTE) {
            VendorsScreen(
                viewModel = viewModel.inventoryViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        // New routes
        composable(ERPDestinations.FINANCIAL_REPORTS_ROUTE) {
            FinancialReportsScreen(
                viewModel = viewModel.financeViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.BUDGET_MANAGEMENT_ROUTE) {
            BudgetManagementScreen(
                viewModel = viewModel.financeViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title\n$message",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
} 