package com.erp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erp.data.converters.DateConverter
import com.erp.modules.finance.data.dao.InvoiceDao
import com.erp.modules.finance.data.dao.TransactionDao
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.hr.data.dao.EmployeeDao
import com.erp.modules.hr.data.dao.LeaveRequestDao
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.inventory.data.dao.ProductDao
import com.erp.modules.inventory.data.dao.VendorDao
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.Vendor

@Database(
    entities = [
        // Finance entities
        Transaction::class,
        Invoice::class,
        
        // HR entities
        Employee::class,
        LeaveRequest::class,
        
        // Inventory entities
        Product::class,
        Vendor::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // Finance DAOs
    abstract fun transactionDao(): TransactionDao
    abstract fun invoiceDao(): InvoiceDao
    
    // HR DAOs
    abstract fun employeeDao(): EmployeeDao
    abstract fun leaveRequestDao(): LeaveRequestDao
    
    // Inventory DAOs
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
                    "erp_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 