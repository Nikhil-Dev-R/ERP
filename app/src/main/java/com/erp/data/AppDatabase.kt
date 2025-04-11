package com.erp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import com.erp.data.converters.DateConverter
import com.erp.data.converters.ListConverter
import com.erp.modules.academics.data.dao.ClassRoomDao
import com.erp.modules.academics.data.dao.SubjectAttachmentDao
import com.erp.modules.academics.data.dao.SubjectDao
import com.erp.modules.academics.data.dao.TimeTableEntryDao
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.data.model.TimeTableEntry
import com.erp.modules.attendance.data.dao.AttendanceDao
import com.erp.modules.attendance.data.model.Attendance
import com.erp.modules.exam.data.dao.ExamDao
import com.erp.modules.exam.data.dao.ResultDao
import com.erp.modules.exam.data.model.Exam
import com.erp.modules.exam.data.model.ExamResult
import com.erp.modules.fee.data.dao.FeeDao
import com.erp.modules.fee.data.model.Fee
import com.erp.modules.finance.data.dao.FeeDao as FinanceFeeDao
import com.erp.modules.finance.data.dao.InvoiceDao
import com.erp.modules.finance.data.dao.TransactionDao
import com.erp.modules.finance.data.model.Fee as FinanceFee
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.hr.data.dao.EmployeeDao
import com.erp.modules.hr.data.dao.LeaveRequestDao
import com.erp.modules.hr.data.dao.SalaryDao
import com.erp.modules.hr.data.dao.TeacherDao
import com.erp.modules.hr.data.dao.StaffDao
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.Salary
import com.erp.modules.hr.data.model.Teacher
import com.erp.modules.hr.data.model.Staff
import com.erp.modules.inventory.data.dao.ProductDao
import com.erp.modules.inventory.data.dao.VendorDao
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.student.data.dao.StudentDao
import com.erp.modules.student.data.model.Student
import java.io.File

@Database(
    entities = [
        // Finance entities
        Transaction::class,
        Invoice::class,
        FinanceFee::class,

        // Fee module entity (using different table name)
        Fee::class,

        // HR entities (Staff/Teachers)
        Employee::class,
        LeaveRequest::class,
        Salary::class,
        Teacher::class,
        Staff::class,

        // Student entities
        Student::class,

        // Academics entities
        Subject::class,
        ClassRoom::class,
        TimeTableEntry::class,
        SubjectAttachment::class,

        // Attendance entities
        Attendance::class,

        // Exam entities
        Exam::class,
        ExamResult::class,

        // Inventory entities (for school supplies)
        Product::class,
        Vendor::class
    ],
    version = 8, // Increment version to ensure clean migration
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // Finance DAOs
    abstract fun transactionDao(): TransactionDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun financeFeeDao(): FinanceFeeDao

    // Fee module DAO
    abstract fun feeDao(): FeeDao

    // HR DAOs (Staff/Teachers)
    abstract fun employeeDao(): EmployeeDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    abstract fun salaryDao(): SalaryDao
    abstract fun teacherDao(): TeacherDao
    abstract fun staffDao(): StaffDao

    // Student DAOs
    abstract fun studentDao(): StudentDao

    // Academics DAOs
    abstract fun subjectDao(): SubjectDao
    abstract fun classRoomDao(): ClassRoomDao
    abstract fun timeTableEntryDao(): TimeTableEntryDao
    abstract fun subjectAttachmentDao(): SubjectAttachmentDao

    // Attendance DAOs
    abstract fun attendanceDao(): AttendanceDao

    // Exam DAOs
    abstract fun examDao(): ExamDao
    abstract fun resultDao(): ResultDao

    // Inventory DAOs (for school supplies)
    abstract fun productDao(): ProductDao
    abstract fun vendorDao(): VendorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "school_management_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Log when database is opened successfully
                            Log.d("AppDatabase", "Database opened successfully")
                        }

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Log when database is created
                            Log.d("AppDatabase", "Database created successfully")
                        }
                    })
                    // Add a destructive migration strategy for ALL migrations
                    .addMigrations(object : Migration(1, 8) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            // This is an empty migration that will force a complete rebuild
                            // between any versions up to the current one
                            Log.d("AppDatabase", "Applying destructive migration from v1 to v8")
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Forces a complete rebuild of the database by deleting existing database files.
         * Use this method only when standard migration strategies fail.
         */
        fun forceRebuildDatabase(context: Context): AppDatabase {
            synchronized(this) {
                // Clear the current instance
                INSTANCE = null

                // Get database file path
                val dbFile = context.getDatabasePath("school_management_database")

                // Delete main database file
                if (dbFile.exists()) {
                    val deleted = dbFile.delete()
                    Log.d("AppDatabase", "Database file deleted: $deleted")
                }

                // Delete associated WAL and SHM files
                val shmFile = File(dbFile.path + "-shm")
                if (shmFile.exists()) {
                    val deleted = shmFile.delete()
                    Log.d("AppDatabase", "SHM file deleted: $deleted")
                }

                val walFile = File(dbFile.path + "-wal")
                if (walFile.exists()) {
                    val deleted = walFile.delete()
                    Log.d("AppDatabase", "WAL file deleted: $deleted")
                }

                // Create a new database instance
                return getDatabase(context)
            }
        }
    }
}