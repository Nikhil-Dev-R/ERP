package com.erp.core.auth

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        Log.d("AuthManager", "Initializing AuthManager")
        
        // Check if user was previously logged in
        val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        val savedLoginState = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        Log.d("AuthManager", "Saved login state: $savedLoginState")
        
        // Update current state based on SharedPreferences
        _isLoggedIn.value = savedLoginState
        
        // Listen for authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            val firebaseLoggedIn = firebaseAuth.currentUser != null
            
            // Only update SharedPreferences if Firebase auth state changes
            if (firebaseLoggedIn != _isLoggedIn.value && firebaseLoggedIn) {
                Log.d("AuthManager", "Firebase auth state changed, updating prefs: $firebaseLoggedIn")
                prefs.edit().putBoolean(KEY_IS_LOGGED_IN, firebaseLoggedIn).apply()
                _isLoggedIn.value = firebaseLoggedIn
            }
        }
    }
    
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Authentication failed")
            
            // Store login state in SharedPreferences
            val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply()
            
            _isLoggedIn.value = true
            Log.d("AuthManager", "Sign in successful, updated login state")
            
            Result.success(user)
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign in failed: ${e.message}")
            Result.failure(e)
        }
    }
    
    // For demo purposes: simulate sign in without Firebase
    fun simulateSignIn() {
        Log.d("AuthManager", "Simulating sign in")
        
        val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
        
        _isLoggedIn.value = true
        Log.d("AuthManager", "Simulated sign in complete, login state: ${_isLoggedIn.value}")
    }
    
    fun signOut() {
        Log.d("AuthManager", "Signing out")
        
        auth.signOut()
        
        // Clear SharedPreferences
        val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
        
        _isLoggedIn.value = false
        Log.d("AuthManager", "Sign out complete")
    }
    
    fun isUserSignedIn(): Boolean {
        val prefs = context.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        Log.d("AuthManager", "Checking if user is signed in: $isLoggedIn")
        return isLoggedIn
    }
    
    companion object {
        private const val AUTH_PREFS = "auth_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
} 