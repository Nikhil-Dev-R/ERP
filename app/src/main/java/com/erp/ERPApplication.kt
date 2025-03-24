package com.erp

import android.app.Application
import com.erp.core.auth.AuthManager
import com.erp.data.AppDatabase
import com.google.firebase.FirebaseApp

class ERPApplication : Application() {
    // Database instance
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Auth manager
    val authManager by lazy { AuthManager(this) }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
} 