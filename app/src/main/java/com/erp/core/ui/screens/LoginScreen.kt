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
import androidx.compose.foundation.layout.width
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
import com.erp.core.auth.AuthManager
import com.erp.data.UserRole
import com.erp.data.userRoles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authManager: AuthManager,
    onLoginSuccess: (UserRole, String) -> Unit
) {
    var role by remember { mutableStateOf<UserRole?>(null) }
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDropDown by remember { mutableStateOf(false) }
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

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = role?.value?.uppercase() ?: "",
                onValueChange = {
                    showDropDown = true
                },
                label = { Text("Role") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown, contentDescription = null,
                        modifier = Modifier.clickable {
                            showDropDown = !showDropDown
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
//                readOnly = true
            )

            DropdownMenu(
                expanded = showDropDown,
                onDismissRequest = {
                    showDropDown = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                userRoles.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it.key.uppercase()
                            )
                        },
                        onClick = {
                            role = UserRole.fromString(it.key)
                            showDropDown = false
                        },
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = it.value,
                                contentDescription = it.key,
                            )
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("Access ID") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
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
        
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email and password cannot be empty"
                    return@Button
                }
                
                errorMessage = null
                isLoading = true
                
                coroutineScope.launch {
                    try {
                        // For demo purposes, we'll use a simulated sign-in
                        // In a real app, you would use:
                        // val result = authManager.signInWithEmailAndPassword(email, password)
                        
                        // Simulate a delay for authentication
                        delay(1500)
                        
                        // Simulate successful authentication
                        authManager.simulateSignIn()
                        isLoading = false
                        role?.let {
                            onLoginSuccess(it, id)
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
                        .width(24.dp)
                        .height(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }
        
        // Add Google Sign-In button here
    }
}

@Composable
fun RoleItem(role: String, imageVector: ImageVector) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = role,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = role,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun LoginPreview() {
    LoginScreen(
        authManager = AuthManager(LocalContext.current)
    ) { _, _ -> }
}