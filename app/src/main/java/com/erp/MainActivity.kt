package com.erp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erp.common.model.BaseEntity
import com.erp.core.navigation.ERPNavHost
import com.erp.data.remote.FirebaseService
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.repository.InvoiceRepository
import com.erp.modules.finance.data.repository.TransactionRepository
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.hr.data.repository.EmployeeRepository
import com.erp.modules.hr.data.repository.LeaveRequestRepository
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.data.repository.ProductRepository
import com.erp.modules.inventory.data.repository.VendorRepository
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import com.erp.ui.theme.ERPTheme
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // In a real app, we would use Dependency Injection (Hilt/Dagger) here
        val erpApplication = application as ERPApplication
        
        setContent {
            ERPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This should be provided by a ViewModel Factory in a real app
                    val financeViewModel = provideSampleFinanceViewModel(erpApplication)
                    val hrViewModel = provideSampleHRViewModel(erpApplication)
                    val inventoryViewModel = provideSampleInventoryViewModel(erpApplication)
                    
                    ERPNavHost(
                        authManager = erpApplication.authManager,
                        financeViewModel = financeViewModel,
                        hrViewModel = hrViewModel,
                        inventoryViewModel = inventoryViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun provideSampleFinanceViewModel(application: ERPApplication): FinanceViewModel {
    // Create repositories
    val transactionDao = application.database.transactionDao()
    val invoiceDao = application.database.invoiceDao()
    
    return FinanceViewModel(
        transactionRepository = TransactionRepository(
            transactionDao = transactionDao,
            firebaseService = createFirebaseService("transactions", Transaction::class)
        ),
        invoiceRepository = InvoiceRepository(
            invoiceDao = invoiceDao,
            firebaseService = createFirebaseService("invoices", Invoice::class)
        )
    )
}

@Composable
fun provideSampleHRViewModel(application: ERPApplication): HRViewModel {
    // Create repositories
    val employeeDao = application.database.employeeDao()
    val leaveRequestDao = application.database.leaveRequestDao()
    
    return HRViewModel(
        employeeRepository = EmployeeRepository(
            employeeDao = employeeDao,
            firebaseService = createFirebaseService("employees", com.erp.modules.hr.data.model.Employee::class)
        ),
        leaveRequestRepository = LeaveRequestRepository(
            leaveRequestDao = leaveRequestDao,
            firebaseService = createFirebaseService("leaveRequests", com.erp.modules.hr.data.model.LeaveRequest::class)
        )
    )
}

@Composable
fun provideSampleInventoryViewModel(application: ERPApplication): InventoryViewModel {
    // Create repositories
    val productDao = application.database.productDao()
    val vendorDao = application.database.vendorDao()
    
    return InventoryViewModel(
        productRepository = ProductRepository(
            productDao = productDao,
            firebaseService = createFirebaseService("products", Product::class)
        ),
        vendorRepository = VendorRepository(
            vendorDao = vendorDao,
            firebaseService = createFirebaseService("vendors", Vendor::class)
        )
    )
}

fun <T : BaseEntity> createFirebaseService(
    collectionPath: String, 
    entityClass: KClass<T>
): FirebaseService<T> {
    return FirebaseService(entityClass, collectionPath)
}