package com.erp.components

import android.media.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ERPTopBar(
    modifier: Modifier = Modifier,
    centerAligned: Boolean = false,
    title: String = "Title",
    navIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    onNavIconClick: () -> Unit = {},
    actions: @Composable () -> Unit = {},
) {
    if (centerAligned) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge
                )
            },
            actions = { actions },
            modifier = modifier,
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavIconClick) {
                    Icon(
                        navIcon,
                        contentDescription = "Go Back"
                    )
                }
            },
            actions = { actions },
            modifier = modifier,
        )
    }
}