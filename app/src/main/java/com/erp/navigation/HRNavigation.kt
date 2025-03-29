package com.erp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.erp.modules.hr.ui.screens.AddEmployeeScreen
import com.erp.modules.hr.ui.screens.EmployeeDetailScreen
import com.erp.modules.hr.ui.screens.HRDashboardScreen
import com.erp.modules.hr.ui.viewmodel.HRViewModel

object HRDestinations {
    const val HR_ROUTE = "hr"
    const val HR_DASHBOARD = "hr_dashboard"
    const val EMPLOYEE_DETAILS = "employee_details/{employeeId}"
    const val ADD_EMPLOYEE = "add_employee"
    
    // Helper functions for constructing route strings
    fun employeeDetailsRoute(employeeId: String): String = "employee_details/$employeeId"
}

@Composable
fun HRNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: HRViewModel,
    startDestination: String = HRDestinations.HR_DASHBOARD
) {
    NavHost(
        navController = navController,
        startDestination = HRDestinations.HR_ROUTE
    ) {
        hrGraph(
            navController = navController,
            viewModel = viewModel
        )
    }
}

fun NavGraphBuilder.hrGraph(
    navController: NavHostController,
    viewModel: HRViewModel
) {
    navigation(
        startDestination = HRDestinations.HR_DASHBOARD,
        route = HRDestinations.HR_ROUTE
    ) {
        composable(HRDestinations.HR_DASHBOARD) {
            HRDashboardScreen(
                viewModel = viewModel,
                onNavigateToEmployees = { /* Implement if needed */ },
                onNavigateToLeaveRequests = { /* Implement if needed */ },
                onNavigateToPayroll = { /* Implement if needed */ },
                onAddEmployee = {
                    navController.navigate(HRDestinations.ADD_EMPLOYEE)
                },
                onEmployeeDetails = { employeeId ->
                    navController.navigate(HRDestinations.employeeDetailsRoute(employeeId))
                }
            )
        }
        
        composable(HRDestinations.EMPLOYEE_DETAILS) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            EmployeeDetailScreen(
                viewModel = viewModel,
                employeeId = employeeId,
                onNavigateBack = { navController.popBackStack() },
                onEditEmployee = { /* Implement if needed */ }
            )
        }
        
        composable(HRDestinations.ADD_EMPLOYEE) {
            AddEmployeeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 