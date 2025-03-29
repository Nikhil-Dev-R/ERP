package com.erp.modules.academics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.TimeTableEntry
import com.erp.modules.academics.ui.viewmodel.AcademicsViewModel
import com.erp.modules.academics.ui.viewmodel.ClassRoomDetailState
import com.erp.modules.academics.ui.viewmodel.TimeTableUiState
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(
    viewModel: AcademicsViewModel,
    navController: NavController,
    classRoomId: String
) {
    var selectedDay by remember { mutableIntStateOf(1) } // Default to Monday (1)
    val classRoomState = remember { mutableStateOf<ClassRoomDetailState>(ClassRoomDetailState.Loading) }
    val timetableState = remember { mutableStateOf<TimeTableUiState>(TimeTableUiState.Loading) }
    
    // Track subjects for displaying timetable details
    val subjectsMap = remember { mutableStateMapOf<String, Subject>() }
    
    // Map of day numbers to day names
    val dayNames = mapOf(
        1 to "Monday",
        2 to "Tuesday",
        3 to "Wednesday",
        4 to "Thursday",
        5 to "Friday",
        6 to "Saturday",
        7 to "Sunday"
    )
    
    // Load classroom details
    LaunchedEffect(classRoomId) {
        viewModel.getClassRoomById(classRoomId).collect { classRoom ->
            classRoomState.value = ClassRoomDetailState.Success(classRoom)
        }
    }
    
    // Load timetable entries for the selected day
    LaunchedEffect(classRoomId, selectedDay) {
        viewModel.getTimeTableEntriesByClassAndDay(classRoomId, selectedDay.toString()).collect { entries ->
            timetableState.value = (if (entries.isEmpty()) {
                TimeTableUiState.Empty
            } else {
                TimeTableUiState.Success(entries)

                // Load subject details for each entry
                entries.forEach { entry ->
                    if (!subjectsMap.containsKey(entry.subjectId)) {
                        try {
                            val subject = viewModel.getSubjectById(entry.subjectId).first()
                            subjectsMap[entry.subjectId] = subject
                        } catch (e: Exception) {
                            // Handle error if subject not found
                        }
                    }
                }
            }) as TimeTableUiState
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = classRoomState.value) {
                        is ClassRoomDetailState.Success -> {
                            Text("Timetable - ${state.classRoom.name} ${state.classRoom.section}")
                        }
                        else -> {
                            Text("Timetable")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Day selector
            DaySelectionTabs(
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                dayNames = dayNames
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (val state = timetableState.value) {
                    is TimeTableUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is TimeTableUiState.Success -> {
                        TimeTableContent(
                            timeTableEntries = state.timeTableEntries,
                            subjectsMap = subjectsMap,
                            viewModel = viewModel
                        )
                    }
                    is TimeTableUiState.Empty -> {
                        EmptyTimeTable(dayNames[selectedDay] ?: "Selected day")
                    }
                    is TimeTableUiState.Error -> {
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
}

@Composable
fun DaySelectionTabs(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    dayNames: Map<Int, String>
) {
    ScrollableTabRow(selectedTabIndex = selectedDay - 1) {
        dayNames.forEach { (day, name) ->
            Tab(
                selected = selectedDay == day,
                onClick = { onDaySelected(day) },
                text = { Text(name) }
            )
        }
    }
}

@Composable
fun TimeTableContent(
    timeTableEntries: List<TimeTableEntry>,
    subjectsMap: Map<String, Subject>,
    viewModel: AcademicsViewModel
) {
    // Sort entries by period number
    val sortedEntries = remember(timeTableEntries) {
        timeTableEntries.sortedBy { it.periodNumber }
    }
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(sortedEntries) { entry ->
            TimeTableEntryCard(
                entry = entry,
                subject = subjectsMap[entry.subjectId]
            )
        }
    }
}

@Composable
fun TimeTableEntryCard(
    entry: TimeTableEntry,
    subject: Subject?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Period and time
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Period ${entry.periodNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${entry.startTime} - ${entry.endTime}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Vertical divider
            Divider(
                modifier = Modifier
                    .height(64.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )
            
            // Subject details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                if (subject != null) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Code: ${subject.code}",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "Unknown Subject",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Room: ${entry.roomId}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Note: ${entry.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyTimeTable(dayName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No classes scheduled for $dayName",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Check another day or contact administration for information",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
} 