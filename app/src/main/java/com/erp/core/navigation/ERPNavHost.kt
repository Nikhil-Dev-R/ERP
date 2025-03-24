package com.erp.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.erp.core.auth.AuthManager
import com.erp.core.ui.screens.HomeScreen
import com.erp.core.ui.screens.LoginScreen
import com.erp.modules.finance.ui.screens.FinanceDashboardScreen
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.hr.ui.screens.HRDashboardScreen
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import com.erp.modules.inventory.ui.screens.InventoryDashboardScreen
import com.erp.modules.inventory.ui.screens.ProductsScreen
import com.erp.modules.inventory.ui.screens.VendorsScreen
import com.erp.modules.inventory.ui.screens.ProductDetailScreen
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel

object ERPDestinations {
    const val LOGIN_ROUTE = "login"
    const val HOME_ROUTE = "home"
    
    // Finance module
    const val FINANCE_DASHBOARD_ROUTE = "finance_dashboard"
    const val TRANSACTIONS_ROUTE = "transactions"
    const val TRANSACTION_DETAIL_ROUTE = "transaction_detail"
    const val INVOICES_ROUTE = "invoices"
    const val INVOICE_DETAIL_ROUTE = "invoice_detail"
    
    // HR module
    const val HR_DASHBOARD_ROUTE = "hr_dashboard"
    const val EMPLOYEES_ROUTE = "employees"
    const val EMPLOYEE_DETAIL_ROUTE = "employee_detail"
    const val LEAVE_REQUESTS_ROUTE = "leave_requests"
    
    // Inventory module
    const val INVENTORY_DASHBOARD_ROUTE = "inventory_dashboard"
    const val PRODUCTS_ROUTE = "products"
    const val PRODUCT_DETAIL_ROUTE = "product_detail"
    const val VENDORS_ROUTE = "vendors"
    
    // CRM module
    const val CRM_DASHBOARD_ROUTE = "crm_dashboard"
    const val CUSTOMERS_ROUTE = "customers"
    const val CUSTOMER_DETAIL_ROUTE = "customer_detail"
    const val LEADS_ROUTE = "leads"
    
    // Project module
    const val PROJECT_DASHBOARD_ROUTE = "project_dashboard"
    const val PROJECTS_ROUTE = "projects"
    const val PROJECT_DETAIL_ROUTE = "project_detail"
    const val TASKS_ROUTE = "tasks"
}

@Composable
fun ERPNavHost(
    navController: NavHostController = rememberNavController(),
    authManager: AuthManager,
    // ViewModels will be injected here through DI
    financeViewModel: FinanceViewModel,
    hrViewModel: HRViewModel,
    inventoryViewModel: InventoryViewModel
) {
    // Determine start destination based on authentication status
    val isLoggedIn by authManager.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedIn) ERPDestinations.HOME_ROUTE else ERPDestinations.LOGIN_ROUTE
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ERPDestinations.LOGIN_ROUTE) {
            LoginScreen(
                authManager = authManager,
                onLoginSuccess = {
                    navController.navigate(ERPDestinations.HOME_ROUTE) {
                        popUpTo(ERPDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        composable(ERPDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToFinance = {
                    navController.navigate(ERPDestinations.FINANCE_DASHBOARD_ROUTE)
                },
                onNavigateToHR = {
                    navController.navigate(ERPDestinations.HR_DASHBOARD_ROUTE)
                },
                onNavigateToInventory = {
                    navController.navigate(ERPDestinations.INVENTORY_DASHBOARD_ROUTE)
                },
                onNavigateToCRM = {
                    navController.navigate(ERPDestinations.CRM_DASHBOARD_ROUTE)
                },
                onNavigateToProjects = {
                    navController.navigate(ERPDestinations.PROJECT_DASHBOARD_ROUTE)
                },
                onLogout = {
                    authManager.signOut()
                    navController.navigate(ERPDestinations.LOGIN_ROUTE) {
                        popUpTo(ERPDestinations.HOME_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        // Finance module navigation
        composable(ERPDestinations.FINANCE_DASHBOARD_ROUTE) {
            FinanceDashboardScreen(
                viewModel = financeViewModel,
                onNavigateToTransactions = {
                    navController.navigate(ERPDestinations.TRANSACTIONS_ROUTE)
                },
                onNavigateToInvoices = {
                    navController.navigate(ERPDestinations.INVOICES_ROUTE)
                },
                onAddTransaction = {
                    navController.navigate(ERPDestinations.TRANSACTION_DETAIL_ROUTE)
                }
            )
        }
        
        // Add the missing transaction detail route
        composable(ERPDestinations.TRANSACTION_DETAIL_ROUTE) {
            PlaceholderScreen(
                title = "Transaction Detail", 
                message = "This screen is under development"
            )
        }
        
        composable(ERPDestinations.TRANSACTIONS_ROUTE) {
            PlaceholderScreen(
                title = "Transactions", 
                message = "This screen is under development"
            )
        }
        
        composable(ERPDestinations.INVOICES_ROUTE) {
            PlaceholderScreen(
                title = "Invoices", 
                message = "This screen is under development"
            )
        }
        
        // HR module navigation
        composable(ERPDestinations.HR_DASHBOARD_ROUTE) {
            HRDashboardScreen(
                viewModel = hrViewModel,
                onNavigateToEmployees = {
                    navController.navigate(ERPDestinations.EMPLOYEES_ROUTE)
                },
                onNavigateToLeaveRequests = {
                    navController.navigate(ERPDestinations.LEAVE_REQUESTS_ROUTE)
                },
                onAddEmployee = {
                    navController.navigate(ERPDestinations.EMPLOYEE_DETAIL_ROUTE)
                }
            )
        }
        
        composable(ERPDestinations.EMPLOYEES_ROUTE) {
            PlaceholderScreen(
                title = "Employees", 
                message = "This screen is under development"
            )
        }
        
        composable(ERPDestinations.EMPLOYEE_DETAIL_ROUTE) {
            PlaceholderScreen(
                title = "Employee Detail", 
                message = "This screen is under development"
            )
        }
        
        composable(ERPDestinations.LEAVE_REQUESTS_ROUTE) {
            PlaceholderScreen(
                title = "Leave Requests", 
                message = "This screen is under development"
            )
        }
        
        // Inventory module navigation
        composable(ERPDestinations.INVENTORY_DASHBOARD_ROUTE) {
            InventoryDashboardScreen(
                viewModel = inventoryViewModel,
                onNavigateToProducts = {
                    navController.navigate(ERPDestinations.PRODUCTS_ROUTE)
                },
                onNavigateToVendors = {
                    navController.navigate(ERPDestinations.VENDORS_ROUTE)
                },
                onAddProduct = {
                    // Navigate to product detail screen from Inventory Dashboard
                    navController.navigate(ERPDestinations.PRODUCT_DETAIL_ROUTE)
                },
                onAddVendor = {
                    navController.navigate(ERPDestinations.VENDORS_ROUTE)
                }
            )
        }
        
        composable(ERPDestinations.PRODUCTS_ROUTE) {
            ProductsScreen(
                viewModel = inventoryViewModel,
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
                viewModel = inventoryViewModel,
                productId = productId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(ERPDestinations.VENDORS_ROUTE) {
            VendorsScreen(
                viewModel = inventoryViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        // CRM module placeholder
        composable(ERPDestinations.CRM_DASHBOARD_ROUTE) {
            PlaceholderScreen("CRM Module", "This module is under development")
        }
        
        // Project module placeholder
        composable(ERPDestinations.PROJECT_DASHBOARD_ROUTE) {
            PlaceholderScreen("Projects Module", "This module is under development")
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