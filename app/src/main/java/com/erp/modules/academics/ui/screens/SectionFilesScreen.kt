package com.erp.modules.academics.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.academics.ui.viewmodel.AttachmentState
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionFilesScreen(
    viewModel: AcademicsViewModel,
    navController: NavController,
    section: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val classRooms = remember { mutableStateListOf<ClassRoom>() }
    val selectedClassRoom = remember { mutableStateOf<ClassRoom?>(null) }
    val attachmentState = viewModel.attachmentState.collectAsState().value
    
    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            uploadFile(uri, viewModel, selectedClassRoom.value?.id ?: "", context)
        }
    }
    
    // Load classrooms by section
    LaunchedEffect(section) {
        viewModel.getClassRoomsBySection(section).collect { sectionClassRooms ->
            classRooms.clear()
            classRooms.addAll(sectionClassRooms)
            if (sectionClassRooms.isNotEmpty()) {
                selectedClassRoom.value = sectionClassRooms.first()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Section $section Files") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            selectedClassRoom.value?.id?.let {
                                filePickerLauncher.launch("*/*")
                            }
                        },
                        enabled = selectedClassRoom.value != null
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Upload File")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Classes in Section $section",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (classRooms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No classes found for section $section",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Display list of classrooms
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(classRooms) { classRoom ->
                        ClassRoomCard(
                            classRoom = classRoom,
                            isSelected = classRoom.id == selectedClassRoom.value?.id,
                            onClick = { selectedClassRoom.value = classRoom }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Display upload status
                when (attachmentState) {
                    is AttachmentState.Uploading -> {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Uploading file...",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    is AttachmentState.UploadSuccess -> {
                        Text(
                            text = "File uploaded successfully: ${(attachmentState as AttachmentState.UploadSuccess).attachment.fileName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    is AttachmentState.Error -> {
                        Text(
                            text = "Error: ${(attachmentState as AttachmentState.Error).message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    else -> { /* No action needed */ }
                }
            }
        }
    }
}

@Composable
fun ClassRoomCard(
    classRoom: ClassRoom,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.School,
                contentDescription = "Class",
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${classRoom.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Room: ${classRoom.roomNumber}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "Teacher ID: ${classRoom.classTeacherId}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Button(
                onClick = onClick,
                enabled = !isSelected
            ) {
                Text(if (isSelected) "Selected" else "Select")
            }
        }
    }
}

private fun uploadFile(uri: Uri, viewModel: AcademicsViewModel, classRoomId: String, context: android.content.Context) {
    // Get file name from URI
    val fileName = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"
    
    // Determine file type from extension
    val fileType = when {
        fileName.endsWith(".pdf", ignoreCase = true) -> "pdf"
        fileName.endsWith(".jpg", ignoreCase = true) -> "jpg"
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
    
    // Upload the file using ViewModel (estimate 1MB size for example)
    try {
        viewModel.uploadAttachment(
            subjectId = classRoomId, // Using classRoomId as the subjectId for this example
            fileName = fileName,
            fileUri = uri,
            fileType = fileType,
            fileSize = 1024 * 1024, // 1MB placeholder size
            description = "Uploaded from Section Files Screen"
        )
    } catch (e: Exception) {
        // Handle any exceptions from the upload process
        android.util.Log.e("SectionFilesScreen", "Upload failed: ${e.message}", e)
        android.widget.Toast.makeText(
            context,
            "Upload failed: ${e.message}",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
} 