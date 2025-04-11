
package com.erp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.erp.data.UserRole
import com.erp.data.userRoles

/**
 * Displays a role dropdown with custom icons.
 */
@Composable
fun RoleDropdown(
    selectedRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropDown by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedRole?.value?.uppercase() ?: "",
            onValueChange = {},
            label = { Text("Role") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select role",
                    modifier = Modifier.clickable { showDropDown = !showDropDown }
                )
            },
            readOnly = true, // Make it read-only; selection is via dropdown
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().clickable {
                showDropDown = true
            }
        )

        DropdownMenu(
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            userRoles.forEach { roleEntry ->
                DropdownMenuItem(
                    text = {
                        Text(text = roleEntry.key.uppercase())
                    },
                    onClick = {
                        UserRole.fromString(roleEntry.key)?.let {
                            onRoleSelected(it)
                        }
                        showDropDown = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = roleEntry.value,
                            contentDescription = roleEntry.key
                        )
                    }
                )
            }
        }
    }
}
