package com.erp.modules.academics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.academics.ui.viewmodel.ClassRoomsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionSelectionScreen(
    viewModel: AcademicsViewModel,
    navController: NavController,
    navigateToTimetable: (String) -> Unit
) {
    val classRoomsState = remember { mutableStateOf<ClassRoomsUiState>(ClassRoomsUiState.Loading) }
    
    LaunchedEffect(Unit) {
        viewModel.classRooms.collect { classRooms ->
            classRoomsState.value = if (classRooms.isEmpty()) {
                ClassRoomsUiState.Empty
            } else {
                ClassRoomsUiState.Success(classRooms)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Section") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            when (val state = classRoomsState.value) {
                is ClassRoomsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ClassRoomsUiState.Success -> {
                    SectionsList(
                        classRooms = state.classRooms,
                        onSectionClick = { classRoomId ->
                            navigateToTimetable(classRoomId)
                        }
                    )
                }
                is ClassRoomsUiState.Empty -> {
                    Text(
                        text = "No sections available",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
                is ClassRoomsUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionsList(
    classRooms: List<ClassRoom>,
    onSectionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a section to view its timetable",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(classRooms) { classRoom ->
                SectionCard(
                    classRoom = classRoom,
                    onClick = { onSectionClick(classRoom.id) }
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    classRoom: ClassRoom,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${classRoom.name} - Section ${classRoom.section}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Room: ${classRoom.roomNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Academic Year: ${classRoom.academicYear}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 