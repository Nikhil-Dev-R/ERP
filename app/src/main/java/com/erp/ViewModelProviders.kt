package com.erp

import androidx.compose.runtime.Composable
import com.erp.modules.finance.data.dao.FeeDao
import com.erp.modules.finance.data.model.Fee
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.finance.data.repository.FeeRepository
import com.erp.modules.finance.data.repository.InvoiceRepository
import com.erp.modules.finance.data.repository.TransactionRepository
import com.erp.modules.finance.ui.viewmodel.FinanceViewModel
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.data.model.Staff
import com.erp.modules.hr.data.model.Teacher
import com.erp.modules.hr.data.repository.EmployeeRepository
import com.erp.modules.hr.data.repository.LeaveRequestRepository
import com.erp.modules.hr.data.repository.SalaryRepository
import com.erp.modules.hr.data.repository.StaffRepository
import com.erp.modules.hr.data.repository.TeacherRepository
import com.erp.modules.hr.ui.viewmodel.HRViewModel
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.inventory.data.repository.ProductRepository
import com.erp.modules.inventory.data.repository.VendorRepository
import com.erp.modules.inventory.ui.viewmodel.InventoryViewModel
import com.erp.modules.student.data.model.Student
import com.erp.modules.student.data.repository.StudentRepository
import com.erp.modules.student.ui.viewmodel.StudentViewModel

/**
 * Collection of functions to provide ViewModels for use in Composable functions
 */

@Composable
fun provideFinanceViewModel(application: ERPApplication): FinanceViewModel {
    // Create repositories
    val transactionDao = application.database.transactionDao()
    val invoiceDao = application.database.invoiceDao()
    val feeDao = application.database.feeDao()
    
    return FinanceViewModel(
        transactionRepository = TransactionRepository(
            transactionDao = transactionDao,
            firebaseService = MainActivity.createFirebaseService("transactions", Transaction::class)
        ),
        invoiceRepository = InvoiceRepository(
            invoiceDao = invoiceDao,
            firebaseService = MainActivity.createFirebaseService("invoices", Invoice::class)
        ),
        feeRepository = FeeRepository(
            feeDao = feeDao as FeeDao,
            firebaseService = MainActivity.createFirebaseService("fees", Fee::class)
        )
    )
}

@Composable
fun provideHRViewModel(application: ERPApplication): HRViewModel {
    // Create repositories
    val employeeDao = application.database.employeeDao()
    val leaveRequestDao = application.database.leaveRequestDao()
    val salaryDao = application.database.salaryDao()
    val teacherDao = application.database.teacherDao()
    val staffDao = application.database.staffDao()
    
    return HRViewModel(
        employeeRepository = EmployeeRepository(
            employeeDao = employeeDao,
            firebaseService = MainActivity.createFirebaseService("employees", Employee::class)
        ),
        leaveRequestRepository = LeaveRequestRepository(
            leaveRequestDao = leaveRequestDao,
            firebaseService = MainActivity.createFirebaseService("leaveRequests", LeaveRequest::class)
        ),
        salaryRepository = SalaryRepository(
            salaryDao = salaryDao
        ),
        teacherRepository = TeacherRepository(
            teacherDao = teacherDao,
            firebaseService = MainActivity.createFirebaseService("teachers", Teacher::class)
        ),
        staffRepository = StaffRepository(
            staffDao = staffDao,
            firebaseService = MainActivity.createFirebaseService("staff", Staff::class)
        )
    )
}

@Composable
fun provideStudentViewModel(application: ERPApplication): StudentViewModel {
    // Create repository
    val studentDao = application.database.studentDao()
    
    return StudentViewModel(
        studentRepository = StudentRepository(
            studentDao = studentDao,
            firebaseService = MainActivity.createFirebaseService("students", Student::class)
        )
    )
}

@Composable
fun provideInventoryViewModel(application: ERPApplication): InventoryViewModel {
    // Create repositories
    val productDao = application.database.productDao()
    val vendorDao = application.database.vendorDao()
    
    return InventoryViewModel(
        productRepository = ProductRepository(
            productDao = productDao,
            firebaseService = MainActivity.createFirebaseService("products", Product::class)
        ),
        vendorRepository = VendorRepository(
            vendorDao = vendorDao,
            firebaseService = MainActivity.createFirebaseService("vendors", Vendor::class)
        )
    )
}

@Composable
fun provideFeeViewModel(application: ERPApplication): com.erp.modules.fee.ui.viewmodel.FeeViewModel {
    // Create repository
    val feeDao = application.database.feeDao()
    
    return com.erp.modules.fee.ui.viewmodel.FeeViewModel(
        feeRepository = com.erp.modules.fee.data.repository.FeeRepository(
            feeDao = feeDao,
            firebaseService = MainActivity.createFirebaseService("fee_module_fees", com.erp.modules.fee.data.model.Fee::class)
        )
    )
} 