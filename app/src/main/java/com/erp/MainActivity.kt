package com.erp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.erp.common.model.BaseEntity
import com.erp.core.navigation.ERPNavHost
import com.erp.data.remote.FirebaseService
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.TimeTableEntry
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.data.repository.AcademicsRepository
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.attendance.data.model.Attendance
import com.erp.modules.attendance.data.repository.AttendanceRepository
import com.erp.modules.attendance.ui.viewmodel.AttendanceViewModel
import com.erp.modules.exam.data.model.Exam
import com.erp.modules.exam.data.model.ExamResult
import com.erp.modules.exam.data.repository.ExamRepository
import com.erp.modules.exam.viewmodel.ExamViewModel
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
import com.erp.ui.theme.ERPTheme
import com.erp.ui.viewmodel.MainViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    
    // Main ViewModel that holds all module ViewModels
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        // Initialize Firebase
        initializeFirebase()
        
        // Initialize repositories and view models
        createRepositories()
        
        setContent {
            ERPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    ERPNavHost(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
    
    // Initialize Firebase with proper settings
    private fun initializeFirebase() {
        // Let the application handle the Firebase initialization
        val app = applicationContext as ERPApplication
        
        // Log connection status
        Log.d("MainActivity", "Checking Firebase connection status")
    }

    // Create repositories
    private fun createRepositories() {
        val application = applicationContext as ERPApplication
        val database = application.database
        val authManager = application.authManager
        
        // Initialize Firebase for the three main tables (Students, Teachers & Staff, Finance)
        
        // 1. Students Table
        val studentRepository = StudentRepository(
            database.studentDao(), 
            createFirebaseService("students", Student::class)
        )
        
        // 2. Teachers & Staff Tables
        val teacherRepository = TeacherRepository(
            database.teacherDao(),
            createFirebaseService("teachers", Teacher::class)
        )
        
        val staffRepository = StaffRepository(
            database.staffDao(),
            createFirebaseService("staff", Staff::class)
        )
        
        // 3. Finance Tables
        val transactionRepository = TransactionRepository(
            database.transactionDao(),
            createFirebaseService("transactions", Transaction::class)
        )
        val invoiceRepository = InvoiceRepository(
            database.invoiceDao(),
            createFirebaseService("invoices", Invoice::class)
        )
        val feeRepository = FeeRepository(
            database.financeFeeDao(),
            createFirebaseService("fees", Fee::class)
        )
        
        // 4. Fee Module Repository
        val feeModuleRepository = com.erp.modules.fee.data.repository.FeeRepository(
            database.feeDao(),
            createFirebaseService("fee_module_fees", com.erp.modules.fee.data.model.Fee::class)
        )
        
        // Create academics repositories with properly typed FirebaseService
        val classRoomFirebaseService = createFirebaseService("classrooms", ClassRoom::class)
        val subjectFirebaseService = createFirebaseService("subjects", Subject::class)
        val timeTableEntryFirebaseService = createFirebaseService("timetable_entries", TimeTableEntry::class)
        val attachmentFirebaseService = createFirebaseService("subject_attachments", SubjectAttachment::class)
        
        // Use a composite pattern or delegate to a service that can handle all three types
        val academicsRepository = AcademicsRepository(
            database.classRoomDao(),
            database.subjectDao(),
            database.timeTableEntryDao(),
            database.subjectAttachmentDao(),
            createFirebaseService("academics", BaseEntity::class)
        )
        val attendanceRepository = AttendanceRepository(
            database.attendanceDao(),
            createFirebaseService<Attendance>("attendance", Attendance::class)
        )
        val examRepository = ExamRepository(
            database.examDao(),
            database.resultDao(),
            createFirebaseService<Exam>("exams", Exam::class),
            createFirebaseService<ExamResult>("results", ExamResult::class)
        )
        
        // Create individual repositories for HR module
        val employeeRepository = EmployeeRepository(
            database.employeeDao(),
            createFirebaseService("employees", Employee::class)
        )
        val leaveRequestRepository = LeaveRequestRepository(
            database.leaveRequestDao(),
            createFirebaseService("leaveRequests", LeaveRequest::class)
        )
        val salaryRepository = SalaryRepository(
            database.salaryDao()
        )
        
        // Create individual repositories for Inventory module
        val productRepository = ProductRepository(
            database.productDao(),
            createFirebaseService("products", Product::class)
        )
        val vendorRepository = VendorRepository(
            database.vendorDao(),
            createFirebaseService("vendors", Vendor::class)
        )
        
        // Initialize ViewModels
        val studentViewModel = StudentViewModel(studentRepository)
        val academicsViewModel = AcademicsViewModel(academicsRepository)
        val attendanceViewModel = AttendanceViewModel(attendanceRepository, studentRepository)
        val examViewModel = ExamViewModel()
        val financeViewModel = FinanceViewModel(transactionRepository, invoiceRepository, feeRepository)
        val hrViewModel = HRViewModel(employeeRepository, leaveRequestRepository, salaryRepository)
        val inventoryViewModel = InventoryViewModel(productRepository, vendorRepository)
        val feeViewModel = com.erp.modules.fee.ui.viewmodel.FeeViewModel(feeModuleRepository)

        // Set ViewModels
        viewModel = MainViewModel(
            authManager,
            studentViewModel,
            academicsViewModel,
            attendanceViewModel,
            examViewModel,
            financeViewModel,
            hrViewModel,
            inventoryViewModel,
            feeViewModel
        )
    }

    companion object {
        // Create a typed FirebaseService - accessible from companion object
        fun <T : BaseEntity> createFirebaseService(
            collectionPath: String, 
            entityClass: KClass<T>
        ): FirebaseService<T> {
            // Add log for debugging
            Log.d("MainActivity", "Creating FirebaseService for collection: $collectionPath")
            
            // Ensure Firebase is already initialized before creating FirebaseService
            // We don't initialize here, as it should be initialized by the Application class
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isEmpty()) {
                Log.w("MainActivity", "Firebase not initialized!")
            }
            
            return FirebaseService(entityClass, collectionPath)
        }
    }
} 