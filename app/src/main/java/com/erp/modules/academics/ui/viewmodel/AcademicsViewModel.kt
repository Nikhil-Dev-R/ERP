package com.erp.modules.academics.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erp.modules.academics.data.SampleData
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.data.model.TimeTableEntry
import com.erp.modules.academics.data.repository.AcademicsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

// Placeholder for academics viewmodel - to be implemented with full features
class AcademicsViewModel(
    private val academicsRepository: AcademicsRepository
) : ViewModel() {
    // State flows for ClassRoom
    private val _classRoomState = MutableStateFlow<ClassRoomState>(ClassRoomState.Loading)
    val classRoomState = _classRoomState.asStateFlow()
    
    val classRooms: Flow<List<ClassRoom>> = academicsRepository.getAllClassRooms()
        .onEach { classRooms ->
            _classRoomState.value = if (classRooms.isEmpty()) {
                ClassRoomState.Empty
            } else {
                ClassRoomState.Success(classRooms)
            }
        }
        .catch { e ->
            _classRoomState.value = ClassRoomState.Error(e.message ?: "Unknown error")
        }
    
    // State flows for Subject
    private val _subjectState = MutableStateFlow<SubjectState>(SubjectState.Loading)
    val subjectState = _subjectState.asStateFlow()
    
    val subjects: Flow<List<Subject>> = academicsRepository.getAllSubjects()
        .onEach { subjects ->
            _subjectState.value = if (subjects.isEmpty()) {
                SubjectState.Empty
            } else {
                SubjectState.Success(subjects)
            }
        }
        .catch { e ->
            _subjectState.value = SubjectState.Error(e.message ?: "Unknown error")
        }
    
    // State flows for TimeTableEntry
    private val _timetableState = MutableStateFlow<TimetableState>(TimetableState.Loading)
    val timetableState = _timetableState.asStateFlow()
    
    val timetableEntries: Flow<List<TimeTableEntry>> = academicsRepository.getAllTimeTableEntries()
        .onEach { entries ->
            _timetableState.value = if (entries.isEmpty()) {
                TimetableState.Empty
            } else {
                TimetableState.Success(entries)
            }
        }
        .catch { e ->
            _timetableState.value = TimetableState.Error(e.message ?: "Unknown error")
        }
    
    // State flows for SubjectAttachment
    private val _attachmentState = MutableStateFlow<AttachmentState>(AttachmentState.Loading)
    val attachmentState = _attachmentState.asStateFlow()
    
    init {
        // Load sample data for testing
        loadSampleData()
    }
    
    // Function to populate repository with sample data
    private fun loadSampleData() = viewModelScope.launch {
        // Insert sample subjects
        SampleData.sampleSubjects.forEach { subject ->
            academicsRepository.insertSubject(subject)
        }
        
        // Insert sample classrooms
        SampleData.sampleClassRooms.forEach { classRoom ->
            academicsRepository.insertClassRoom(classRoom)
        }
        
        // Insert sample timetable entries
        SampleData.sampleTimetableEntries.forEach { entry ->
            academicsRepository.insertTimeTableEntry(entry)
        }
    }
    
    // ClassRoom operations
    fun getClassRoomById(id: String) = academicsRepository.getClassRoomById(id)
    
    fun getClassRoomsByGrade(grade: String) = academicsRepository.getClassRoomsByGrade(grade)
    
    fun getClassRoomsByTeacher(teacherId: String) = academicsRepository.getClassRoomsByTeacher(teacherId)
    
    fun getClassRoomsBySection(section: String): Flow<List<ClassRoom>> = 
        academicsRepository.getClassRoomsBySection(section)
    
    fun insertClassRoom(classRoom: ClassRoom) = viewModelScope.launch {
        academicsRepository.insertClassRoom(classRoom)
    }
    
    fun updateClassRoom(classRoom: ClassRoom) = viewModelScope.launch {
        academicsRepository.updateClassRoom(classRoom)
    }
    
    fun deleteClassRoom(classRoom: ClassRoom) = viewModelScope.launch {
        academicsRepository.deleteClassRoom(classRoom)
    }
    
    // Subject operations
    fun getSubjectById(id: String) = academicsRepository.getSubjectById(id)
    
    fun searchSubjects(query: String) = academicsRepository.searchSubjects(query)
    
    fun getSubjectsByGrade(grade: String) = academicsRepository.getSubjectsByGrade(grade)
    
    fun getSubjectsByTeacher(teacherId: String) = academicsRepository.getSubjectsByTeacher(teacherId)
    
    fun insertSubject(subject: Subject) = viewModelScope.launch {
        academicsRepository.insertSubject(subject)
    }
    
    fun updateSubject(subject: Subject) = viewModelScope.launch {
        academicsRepository.updateSubject(subject)
    }
    
    fun deleteSubject(subject: Subject) = viewModelScope.launch {
        academicsRepository.deleteSubject(subject)
    }
    
    // TimeTableEntry operations
    fun getTimeTableEntryById(id: String) = academicsRepository.getTimeTableEntryById(id)
    
    fun getTimeTableEntriesByClass(classId: String) = academicsRepository.getTimeTableEntriesByClass(classId)
    
    fun getTimeTableEntriesBySubject(subjectId: String) = academicsRepository.getTimeTableEntriesBySubject(subjectId)
    
    fun getTimeTableEntriesByTeacher(teacherId: String) = academicsRepository.getTimeTableEntriesByTeacher(teacherId)
    
    fun getTimeTableEntriesByDay(dayOfWeek: String) = academicsRepository.getTimeTableEntriesByDay(dayOfWeek)
    
    fun getTimeTableEntriesByClassAndDay(classId: String, dayOfWeek: String) = 
        academicsRepository.getTimeTableEntriesByClassAndDay(classId, dayOfWeek)
    
    fun insertTimeTableEntry(timeTableEntry: TimeTableEntry) = viewModelScope.launch {
        academicsRepository.insertTimeTableEntry(timeTableEntry)
    }
    
    fun updateTimeTableEntry(timeTableEntry: TimeTableEntry) = viewModelScope.launch {
        academicsRepository.updateTimeTableEntry(timeTableEntry)
    }
    
    fun deleteTimeTableEntry(timeTableEntry: TimeTableEntry) = viewModelScope.launch {
        academicsRepository.deleteTimeTableEntry(timeTableEntry)
    }
    
    // Attachment operations
    fun getAllAttachments(): Flow<List<SubjectAttachment>> = academicsRepository.getAllAttachments()
    
    fun getAttachmentById(id: String): Flow<SubjectAttachment> = academicsRepository.getAttachmentById(id)
    
    fun getAttachmentsBySubject(subjectId: String): Flow<List<SubjectAttachment>> = 
        academicsRepository.getAttachmentsBySubject(subjectId)
            .onEach { attachments ->
                _attachmentState.value = if (attachments.isEmpty()) {
                    AttachmentState.Empty
                } else {
                    AttachmentState.Success(attachments)
                }
            }
            .catch { e ->
                _attachmentState.value = AttachmentState.Error(e.message ?: "Unknown error")
            }
    
    fun searchAttachments(query: String): Flow<List<SubjectAttachment>> = 
        academicsRepository.searchAttachments(query)
    
    fun insertAttachment(attachment: SubjectAttachment) = viewModelScope.launch {
        academicsRepository.insertAttachment(attachment)
    }
    
    fun updateAttachment(attachment: SubjectAttachment) = viewModelScope.launch {
        academicsRepository.updateAttachment(attachment)
    }
    
    fun deleteAttachment(attachment: SubjectAttachment) = viewModelScope.launch {
        academicsRepository.deleteAttachment(attachment)
    }
    
    fun uploadAttachment(
        subjectId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long,
        description: String = ""
    ) = viewModelScope.launch {
        try {
            _attachmentState.value = AttachmentState.Uploading
            
            // Add delay to show progress indicator for demo purposes
            kotlinx.coroutines.delay(500)
            
            val attachment = academicsRepository.uploadAttachment(
                subjectId, fileName, fileUri, fileType, fileSize, description
            )
            
            // Update local list immediately regardless of Firebase success
            _attachmentState.value = AttachmentState.UploadSuccess(attachment)
        } catch (e: Exception) {
            _attachmentState.value = AttachmentState.Error(e.message ?: "Upload failed")
        }
    }
    
    // Sync operations
    fun syncAcademicsData() = viewModelScope.launch {
        academicsRepository.syncAllAcademicsDataWithCloud()
    }
}

// State classes
sealed class ClassRoomState {
    object Loading : ClassRoomState()
    data class Success(val classRooms: List<ClassRoom>) : ClassRoomState()
    object Empty : ClassRoomState()
    data class Error(val message: String) : ClassRoomState()
}

sealed class SubjectState {
    object Loading : SubjectState()
    data class Success(val subjects: List<Subject>) : SubjectState()
    object Empty : SubjectState()
    data class Error(val message: String) : SubjectState()
}

sealed class TimetableState {
    object Loading : TimetableState()
    data class Success(val entries: List<TimeTableEntry>) : TimetableState()
    object Empty : TimetableState()
    data class Error(val message: String) : TimetableState()
}

// UI State classes for subjects
sealed class SubjectsUiState {
    object Loading : SubjectsUiState()
    object Empty : SubjectsUiState()
    data class Success(val subjects: List<Subject>) : SubjectsUiState()
    data class Error(val message: String) : SubjectsUiState()
}

sealed class SubjectDetailState {
    object Loading : SubjectDetailState()
    data class Success(val subject: Subject) : SubjectDetailState()
    data class Error(val message: String) : SubjectDetailState()
}

// UI State classes for classrooms
sealed class ClassRoomsUiState {
    object Loading : ClassRoomsUiState()
    object Empty : ClassRoomsUiState()
    data class Success(val classRooms: List<ClassRoom>) : ClassRoomsUiState()
    data class Error(val message: String) : ClassRoomsUiState()
}

sealed class ClassRoomDetailState {
    object Loading : ClassRoomDetailState()
    data class Success(val classRoom: ClassRoom) : ClassRoomDetailState()
    data class Error(val message: String) : ClassRoomDetailState()
}

// UI State classes for timetable
sealed class TimeTableUiState {
    object Loading : TimeTableUiState()
    object Empty : TimeTableUiState()
    data class Success(val timeTableEntries: List<TimeTableEntry>) : TimeTableUiState()
    data class Error(val message: String) : TimeTableUiState()
}

// UI State classes for attachments
sealed class AttachmentState {
    object Loading : AttachmentState()
    object Uploading : AttachmentState()
    data class Success(val attachments: List<SubjectAttachment>) : AttachmentState()
    data class UploadSuccess(val attachment: SubjectAttachment) : AttachmentState()
    object Empty : AttachmentState()
    data class Error(val message: String) : AttachmentState()
}

sealed class AttachmentsUiState {
    object Loading : AttachmentsUiState()
    object Uploading : AttachmentsUiState()
    object Empty : AttachmentsUiState()
    data class Success(val attachments: List<SubjectAttachment>) : AttachmentsUiState()
    data class Error(val message: String) : AttachmentsUiState()
}