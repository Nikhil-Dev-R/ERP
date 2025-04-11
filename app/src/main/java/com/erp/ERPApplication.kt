package com.erp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.erp.core.auth.AuthManager
import com.erp.data.AppDatabase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.LocalCacheSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class ERPApplication : Application() {
    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    // Firestore instance
    private lateinit var firestore: FirebaseFirestore

    // Database instance with safe initialization
    val database by lazy {
        try {
            // Use the force rebuild method to ensure clean database state
            AppDatabase.forceRebuildDatabase(this)
        } catch (e: Exception) {
            // Log the error
            Log.e("ERPApplication", "Database initialization failed: ${e.message}", e)

            // Check for schema mismatch error
            if (e.message?.contains("Room cannot verify the data integrity") == true) {
                Log.w("ERPApplication", "Schema mismatch detected, deleting database")

                // Retry database initialization with force rebuild
                return@lazy AppDatabase.forceRebuildDatabase(this)
            } else {
                // Try to recover by forcing rebuild
                try {
                    AppDatabase.forceRebuildDatabase(this)
                } catch (e2: Exception) {
                    Log.e("ERPApplication", "Recovery failed: ${e2.message}", e2)
                    throw e2 // Rethrow if recovery fails
                }
            }
        }
    }

    // Auth manager
    val authManager by lazy { AuthManager(this) }

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        initializeFirebase()

        // Sign in anonymously for Firebase Storage access
        signInAnonymously()
    }

    private fun initializeFirebase() {
        try {
            // Initialize Firebase
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("ERPApplication", "Firebase app initialized successfully")
            } else {
                Log.d("ERPApplication", "Firebase app already initialized")
            }

            // Initialize Firebase Auth
            auth = Firebase.auth
            Log.d("ERPApplication", "Firebase Auth initialized: ${auth.currentUser != null}")

            // Initialize Firestore with settings
            firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
//                .setLocalCacheSettings(LocalCacheSettings)
                .setPersistenceEnabled(true)
                .build()
            firestore.firestoreSettings = settings
            Log.d("ERPApplication", "Firestore initialized with persistence enabled")

            // Initialize specific collections
            initializeInventoryCollections()
            initializeFeeModule() // Initialize the Fee module

            // Test connection to Firestore
            CoroutineScope(Dispatchers.IO).launch {
                if (isFirestoreAvailable()) {
                    Log.d("ERPApplication", "Firestore connection test successful")
                } else {
                    Log.e("ERPApplication", "Firestore connection test failed")
                }
            }

        } catch (e: Exception) {
            Log.e("ERPApplication", "Error initializing Firebase: ${e.message}", e)
        }
    }

    private fun signInAnonymously() {
        // Only sign in if not already signed in
        if (auth.currentUser == null) {
            Log.d("ERPApplication", "Attempting anonymous sign-in")
            // Use blocking approach on a background thread
            Thread {
                try {
                    // Use Tasks.await to make the operation blocking
                    val authResult = com.google.android.gms.tasks.Tasks.await(
                        auth.signInAnonymously()
                    )
                    Log.d("ERPApplication", "Anonymous sign-in successful: ${authResult.user?.uid}")
                } catch (e: Exception) {
                    Log.e("ERPApplication", "Anonymous sign-in failed: ${e.message}")
                }
            }.start()
        } else {
            Log.d("ERPApplication", "Already signed in as: ${auth.currentUser?.uid}")
        }
    }

    /**
     * Checks if Firestore is available
     */
    suspend fun isFirestoreAvailable(): Boolean {
        return try {
            if (!isNetworkAvailable()) {
                Log.e("ERPApplication", "Network not available")
                return false
            }

            // Simple Firestore operation to test connection
            val document = com.google.android.gms.tasks.Tasks.await(
                firestore.collection("test").document("test").get()
            )

            Log.d("ERPApplication", "Firestore test completed successfully")
            true
        } catch (e: Exception) {
            Log.e("ERPApplication", "Firestore not available: ${e.message}", e)
            false
        }
    }

    /**
     * Checks if network is available
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }

    /**
     * Initializes the Inventory Management collections in Firebase Firestore
     * Creates the necessary collections with sample documents if they don't exist
     */
    private fun initializeInventoryCollections() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = FirebaseFirestore.getInstance()

                // Create Products collection if it doesn't exist
                val productsCollection = db.collection("products")
                val productsSnapshot = productsCollection.limit(1).get().await()

                if (productsSnapshot.isEmpty) {
                    Log.d("ERPApplication", "Creating products collection with initial schema")

                    // Add a sample product to establish schema
                    val sampleProduct = hashMapOf(
                        "id" to UUID.randomUUID().toString(),
                        "name" to "Sample Product",
                        "description" to "This is a sample product to initialize the schema",
                        "sku" to "SAMPLE-001",
                        "price" to 0.0,
                        "category" to "GENERAL",
                        "status" to "ACTIVE",
                        "stockQuantity" to 0,
                        "reorderLevel" to 5,
                        "vendorId" to "",
                        "lastRestockDate" to null,
                        "createdAt" to java.util.Date(),
                        "updatedAt" to java.util.Date()
                    )

                    productsCollection.document("sample-product").set(sampleProduct).await()

                    // Create indexes on commonly queried fields
                    // Note: Firebase indexes are actually created through the Firebase console or 
                    // using Firebase CLI with firestore.indexes.json, not programmatically in code
                    Log.d("ERPApplication", "Consider creating indexes on sku, category, and status fields")
                }

                // Create Vendors collection if it doesn't exist
                val vendorsCollection = db.collection("vendors")
                val vendorsSnapshot = vendorsCollection.limit(1).get().await()

                if (vendorsSnapshot.isEmpty) {
                    Log.d("ERPApplication", "Creating vendors collection with initial schema")

                    // Add a sample vendor to establish schema
                    val sampleVendor = hashMapOf(
                        "id" to UUID.randomUUID().toString(),
                        "name" to "Sample Vendor",
                        "email" to "sample@vendor.com",
                        "phone" to "+1234567890",
                        "address" to "123 Vendor Street",
                        "city" to "Vendor City",
                        "state" to "VS",
                        "country" to "Sample Country",
                        "postalCode" to "12345",
                        "contactPerson" to "John Doe",
                        "notes" to "This is a sample vendor to initialize the schema",
                        "status" to "ACTIVE",
                        "rating" to 3.0,
                        "createdAt" to java.util.Date(),
                        "updatedAt" to java.util.Date()
                    )

                    vendorsCollection.document("sample-vendor").set(sampleVendor).await()

                    // Create indexes on commonly queried fields
                    Log.d("ERPApplication", "Consider creating indexes on name, status, and country fields")
                }

                Log.d("ERPApplication", "Inventory collections initialization completed")

            } catch (e: Exception) {
                Log.e("ERPApplication", "Error initializing inventory collections: ${e.message}", e)
            }
        }
    }

    /**
     * Initializes the Fee Module collections in Firebase Firestore
     */
    private fun initializeFeeModule() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = FirebaseFirestore.getInstance()

                // Create Fees collection if it doesn't exist
                val feesCollection = db.collection("fee_module_fees")
                val feesSnapshot = feesCollection.limit(1).get().await()

                if (feesSnapshot.isEmpty) {
                    Log.d("ERPApplication", "Creating fee_module_fees collection with initial schema")

                    // Add a sample fee to establish schema
                    val sampleFee = hashMapOf(
                        "id" to UUID.randomUUID().toString(),
                        "studentId" to "sample-student",
                        "feeType" to "TUITION",
                        "amount" to 500.0,
                        "dueDate" to java.util.Date(),
                        "paymentStatus" to "PENDING",
                        "paymentDate" to null,
                        "paymentMethod" to "",
                        "transactionId" to "",
                        "receiptNumber" to "",
                        "academicYear" to "2023-2024",
                        "term" to "Fall 2023",
                        "remarks" to "Sample fee record to initialize schema",
                        "createdBy" to "",
                        "lastModified" to java.util.Date(),
                        "createdAt" to java.util.Date(),
                        "updatedAt" to java.util.Date()
                    )

                    feesCollection.document("sample-fee").set(sampleFee).await()

                    // Create indexes on commonly queried fields
                    Log.d("ERPApplication", "Consider creating indexes on studentId, feeType, and paymentStatus fields")
                }

                Log.d("ERPApplication", "Fee module collection initialization completed")

            } catch (e: Exception) {
                Log.e("ERPApplication", "Error initializing fee module collections: ${e.message}", e)
            }
        }
    }
} 