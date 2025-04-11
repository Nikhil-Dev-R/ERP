
package com.erp.core.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erp.components.RoleDropdown
import com.erp.core.auth.AuthManager
import com.erp.data.UserRole
import com.erp.data.userRoles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The login screen composable which collects email, password, access ID and role,
 * then executes the sign in logic. The function handles both real sign-in using Firebase
 * authentication and simulated sign in for demo purposes.
 */
@Composable
fun LoginScreen(
    authManager: AuthManager,
    onLoginSuccess: (UserRole, String) -> Unit,
    onRegisterClick: () -> Unit = {},
    useSimulation: Boolean = false // Toggle simulation vs. real authentication
) {
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    var accessId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ERP System",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Login to your account",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Role selection dropdown
        RoleDropdown(
            selectedRole = selectedRole,
            onRoleSelected = { role ->
                selectedRole = role
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = accessId,
            onValueChange = { accessId = it },
            label = { Text("Access ID") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Display an error message if needed
        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Input validation
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email and password cannot be empty"
                    return@Button
                }
                if (selectedRole == null) {
                    errorMessage = "Please select a role"
                    return@Button
                }
                if (accessId.isBlank()) {
                    errorMessage = "Access ID cannot be empty"
                    return@Button
                }

                errorMessage = null
                isLoading = true

                coroutineScope.launch {
                    try {
                        // Use simulation if toggled on; otherwise, perform real authentication.
                        if (useSimulation) {
                            delay(1500)
                            authManager.simulateSignIn()
                        } else {
                            // Use the real authentication method
                            val result = authManager.signInWithEmailAndPassword(email, password)
                            result.getOrElse {
                                throw Exception(it.message ?: "Authentication failed")
                            }
                        }
                        isLoading = false
                        // On successful login, pass the role and access ID to caller
                        selectedRole?.let { role ->
                            onLoginSuccess(role, accessId)
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = e.message ?: "Authentication failed"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(24.dp)
                        .padding(2.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Optional: A clickable text for registration if needed.
        TextButton(
            onClick = { onRegisterClick() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Don't have an account? Register here.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Additional sign-in methods can be added below (e.g., Google Sign-In)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    // For preview purposes we use simulation mode and a dummy AuthManager.
    LoginScreen(
        authManager = AuthManager(LocalContext.current),
        onLoginSuccess = { _, _ -> },
        onRegisterClick = {},
        useSimulation = true
    )
}
