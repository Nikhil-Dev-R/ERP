package com.erp.modules.academics.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.academics.ui.viewmodel.AttachmentState
import com.erp.modules.academics.ui.viewmodel.SubjectDetailState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper function to determine file type from filename
 */
fun getFileTypeFromFileName(fileName: String): String {
    return when {
        fileName.endsWith(".pdf", ignoreCase = true) -> "pdf"
        fileName.endsWith(".jpg", ignoreCase = true) || 
        fileName.endsWith(".jpeg", ignoreCase = true) -> "jpg"
        fileName.endsWith(".png", ignoreCase = true) -> "png"
        fileName.endsWith(".doc", ignoreCase = true) -> "doc"
        fileName.endsWith(".docx", ignoreCase = true) -> "docx"
        fileName.endsWith(".ppt", ignoreCase = true) -> "ppt"
        fileName.endsWith(".pptx", ignoreCase = true) -> "pptx"
        fileName.endsWith(".xls", ignoreCase = true) -> "xls"
        fileName.endsWith(".xlsx", ignoreCase = true) -> "xlsx"
        else -> "unknown"
    }
}

/**
 * Helper function to get icon for file type
 */
fun getIconForFileType(fileType: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (fileType.lowercase()) {
        "pdf" -> Icons.Default.PictureAsPdf
        "jpg", "jpeg", "png" -> Icons.Default.Image
        "doc", "docx" -> Icons.Default.Description
        "ppt", "pptx" -> Icons.Default.Slideshow
        "xls", "xlsx" -> Icons.Default.TableChart
        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }
}

/**
 * Check if a file type is allowed
 */
fun isAllowedFileType(fileName: String): Boolean {
    val fileType = getFileTypeFromFileName(fileName)
    return fileType != "unknown"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    viewModel: AcademicsViewModel,
    navController: NavController,
    subjectId: String
) {
    val subjectDetailState = remember { mutableStateOf<SubjectDetailState>(SubjectDetailState.Loading) }
    val attachmentState = viewModel.attachmentState.collectAsState().value
    val attachments = remember { mutableStateListOf<SubjectAttachment>() }
    var showUploadDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(subjectId) {
        viewModel.getSubjectById(subjectId).collect { subject ->
            subjectDetailState.value = SubjectDetailState.Success(subject)
        }
    }
    
    LaunchedEffect(subjectId) {
        viewModel.getAttachmentsBySubject(subjectId).collect { subjectAttachments ->
            attachments.clear()
            attachments.addAll(subjectAttachments)
        }
    }
    
    // Handle attachment state changes
    LaunchedEffect(attachmentState) {
        when (attachmentState) {
            is AttachmentState.UploadSuccess -> {
                showUploadDialog = false
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subject Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Upload Attachment")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = subjectDetailState.value) {
                is SubjectDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SubjectDetailState.Success -> {
                    SubjectDetailContent(
                        subject = state.subject,
                        attachments = attachments,
                        onDeleteAttachment = { attachment ->
                            viewModel.deleteAttachment(attachment)
                        }
                    )
                }
                is SubjectDetailState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            
            if (attachmentState is AttachmentState.Uploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = "Uploading file...",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 64.dp)
                    )
                }
            }
        }
        
        if (showUploadDialog && subjectDetailState.value is SubjectDetailState.Success) {
            val subject = (subjectDetailState.value as SubjectDetailState.Success).subject
            FileUploadDialog(
                subjectId = subject.id,
                onDismiss = { showUploadDialog = false },
                onUpload = { uri, fileName, fileType, fileSize ->
                    viewModel.uploadAttachment(
                        subjectId = subject.id,
                        fileName = fileName,
                        fileUri = uri,
                        fileType = fileType,
                        fileSize = fileSize
                    )
                }
            )
        }
    }
}

@Composable
fun SubjectDetailContent(
    subject: Subject,
    attachments: List<SubjectAttachment>,
    onDeleteAttachment: (SubjectAttachment) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Subject header section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Code: ${subject.code}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Grade/Class: ${subject.gradeLevel}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (subject.isElective) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Elective Subject",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Credits: ${subject.credits}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Description section
        if (subject.description.isNotBlank()) {
            item {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        
        // Syllabus section
        item {
            Text(
                text = "Syllabus",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (subject.syllabus.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = subject.syllabus,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                Text(
                    text = "No syllabus available for this subject.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        
        // Attachments section
        item {
            Text(
                text = "Attachments",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            if (attachments.isEmpty()) {
                Text(
                    text = "No attachments available. Click + to upload files.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        
        // List of attachments
        items(attachments) { attachment ->
            AttachmentItem(
                attachment = attachment,
                onDelete = { onDeleteAttachment(attachment) }
            )
        }
    }
}

@Composable
fun AttachmentItem(
    attachment: SubjectAttachment,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File icon based on type
            Icon(
                imageVector = getIconForFileType(attachment.fileType),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 16.dp)
            )
            
            // File details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = attachment.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Type: ${attachment.fileType.uppercase()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Uploaded: ${dateFormat.format(Date(attachment.uploadDate))}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (attachment.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = attachment.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileUploadDialog(
    subjectId: String,
    onDismiss: () -> Unit,
    onUpload: (Uri, String, String, Long) -> Unit
) {
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var fileDescription by remember { mutableStateOf("") }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFileUri = it
            
            // Try to get file name from content resolver
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex)
                    }
                }
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Upload Attachment",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Selected file info
                if (selectedFileUri != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getIconForFileType(getFileTypeFromFileName(fileName)),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = fileName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { selectedFileUri = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }
                    }
                }
                
                // File selection button
                Button(
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedFileUri == null) "Select File" else "Change File")
                }
                
                if (selectedFileUri != null) {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = fileDescription,
                        onValueChange = { fileDescription = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                
                // File type note
                Text(
                    text = "Allowed file types: JPG, PNG, PDF, DOCX, PPT, XLSX",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val isValidFile = selectedFileUri != null && isAllowedFileType(fileName)
                    
                    Button(
                        onClick = {
                            selectedFileUri?.let { uri ->
                                val fileType = getFileTypeFromFileName(fileName)
                                
                                // Get file size
                                val fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.use {
                                    it.statSize
                                } ?: 0L
                                
                                onUpload(uri, fileName, fileType, fileSize)
                            }
                        },
                        enabled = isValidFile
                    ) {
                        Text("Upload")
                    }
                }
            }
        }
    }
} 