package com.erp.modules.academics.data.repository

import android.net.Uri
import com.erp.data.remote.FirebaseService
import com.erp.modules.academics.data.dao.ClassRoomDao
import com.erp.modules.academics.data.dao.SubjectAttachmentDao
import com.erp.modules.academics.data.dao.SubjectDao
import com.erp.modules.academics.data.dao.TimeTableEntryDao
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.data.model.TimeTableEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

class AcademicsRepository(
    private val classRoomDao: ClassRoomDao,
    private val subjectDao: SubjectDao,
    private val timeTableEntryDao: TimeTableEntryDao,
    private val subjectAttachmentDao: SubjectAttachmentDao,
    private val firebaseService: FirebaseService<*>,
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    // ClassRoom operations
    fun getAllClassRooms(): Flow<List<ClassRoom>> = classRoomDao.getAllClassRooms()
    
    fun getClassRoomById(id: String): Flow<ClassRoom> = classRoomDao.getClassRoomById(id)
    
    fun getClassRoomsByGrade(grade: String): Flow<List<ClassRoom>> = classRoomDao.getClassRoomsByGrade(grade)
    
    fun getClassRoomsByTeacher(teacherId: String): Flow<List<ClassRoom>> = classRoomDao.getClassRoomsByTeacher(teacherId)
    
    fun getClassRoomsBySection(section: String): Flow<List<ClassRoom>> = classRoomDao.getClassRoomsBySection(section)
    
    suspend fun insertClassRoom(classRoom: ClassRoom) {
        classRoomDao.insert(classRoom)
        firebaseService.saveClassRoom(classRoom)
    }
    
    suspend fun updateClassRoom(classRoom: ClassRoom) {
        classRoomDao.update(classRoom)
        firebaseService.updateClassRoom(classRoom)
    }
    
    suspend fun deleteClassRoom(classRoom: ClassRoom) {
        classRoomDao.delete(classRoom)
        firebaseService.deleteClassRoom(classRoom.id)
    }
    
    // Subject operations
    fun getAllSubjects(): Flow<List<Subject>> = subjectDao.getAllSubjects()
    
    fun getSubjectById(id: String): Flow<Subject> = subjectDao.getSubjectById(id)
    
    fun searchSubjects(query: String): Flow<List<Subject>> = subjectDao.searchSubjects("%$query%")
    
    fun getSubjectsByGrade(grade: String): Flow<List<Subject>> = subjectDao.getSubjectsByGrade(grade)
    
    fun getSubjectsByTeacher(teacherId: String): Flow<List<Subject>> = subjectDao.getSubjectsByTeacher(teacherId)
    
    suspend fun insertSubject(subject: Subject) {
        subjectDao.insert(subject)
        firebaseService.saveSubject(subject)
    }
    
    suspend fun updateSubject(subject: Subject) {
        subjectDao.update(subject)
        firebaseService.updateSubject(subject)
    }
    
    suspend fun deleteSubject(subject: Subject) {
        subjectDao.delete(subject)
        firebaseService.deleteSubject(subject.id)
    }
    
    // TimeTableEntry operations
    fun getAllTimeTableEntries(): Flow<List<TimeTableEntry>> = timeTableEntryDao.getAllTimeTableEntries()
    
    fun getTimeTableEntryById(id: String): Flow<TimeTableEntry> = timeTableEntryDao.getTimeTableEntryById(id)
    
    fun getTimeTableEntriesByClass(classId: String): Flow<List<TimeTableEntry>> = timeTableEntryDao.getTimeTableEntriesByClass(classId)
    
    fun getTimeTableEntriesBySubject(subjectId: String): Flow<List<TimeTableEntry>> = timeTableEntryDao.getTimeTableEntriesBySubject(subjectId)
    
    fun getTimeTableEntriesByTeacher(teacherId: String): Flow<List<TimeTableEntry>> = timeTableEntryDao.getTimeTableEntriesByTeacher(teacherId)
    
    fun getTimeTableEntriesByDay(dayOfWeek: String): Flow<List<TimeTableEntry>> = timeTableEntryDao.getTimeTableEntriesByDay(dayOfWeek)
    
    fun getTimeTableEntriesByClassAndDay(classId: String, dayOfWeek: String): Flow<List<TimeTableEntry>> =
        timeTableEntryDao.getTimeTableEntriesByClassAndDay(classId, dayOfWeek)
    
    suspend fun insertTimeTableEntry(timeTableEntry: TimeTableEntry) {
        timeTableEntryDao.insert(timeTableEntry)
        firebaseService.saveTimeTableEntry(timeTableEntry)
    }
    
    suspend fun updateTimeTableEntry(timeTableEntry: TimeTableEntry) {
        timeTableEntryDao.update(timeTableEntry)
        firebaseService.updateTimeTableEntry(timeTableEntry)
    }
    
    suspend fun deleteTimeTableEntry(timeTableEntry: TimeTableEntry) {
        timeTableEntryDao.delete(timeTableEntry)
        firebaseService.deleteTimeTableEntry(timeTableEntry.id)
    }
    
    // Subject Attachment operations
    fun getAllAttachments(): Flow<List<SubjectAttachment>> = subjectAttachmentDao.getAllAttachments()
    
    fun getAttachmentById(id: String): Flow<SubjectAttachment> = subjectAttachmentDao.getAttachmentById(id)
    
    fun getAttachmentsBySubject(subjectId: String): Flow<List<SubjectAttachment>> = 
        subjectAttachmentDao.getAttachmentsBySubject(subjectId)
    
    fun searchAttachments(query: String): Flow<List<SubjectAttachment>> = 
        subjectAttachmentDao.searchAttachments("%$query%")
    
    suspend fun insertAttachment(attachment: SubjectAttachment) {
        subjectAttachmentDao.insert(attachment)
        firebaseService.saveAttachment(attachment)
    }
    
    suspend fun updateAttachment(attachment: SubjectAttachment) {
        subjectAttachmentDao.update(attachment)
        firebaseService.updateAttachment(attachment)
    }
    
    suspend fun deleteAttachment(attachment: SubjectAttachment) {
        subjectAttachmentDao.delete(attachment)
        firebaseService.deleteAttachment(attachment.id)
        
        // Delete file from Firebase Storage
        try {
            val storageRef = firebaseStorage.getReference("subject_attachments/${attachment.id}")
            storageRef.delete().await()
        } catch (e: Exception) {
            // Log error but don't throw to ensure DB consistency
            e.printStackTrace()
        }
    }
    
    suspend fun uploadAttachment(
        subjectId: String,
        fileName: String,
        fileUri: Uri,
        fileType: String,
        fileSize: Long,
        description: String = ""
    ): SubjectAttachment {
        val attachmentId = UUID.randomUUID().toString()
        var fileUrl = ""
        var attemptCount = 0
        
        // Ensure folders exist in Firebase Storage
        try {
            // Check if Firebase Auth is ready
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                // Try to sign in anonymously if not already signed in
                val authTask = auth.signInAnonymously().await()
                println("Firebase signed in anonymously: ${authTask.user?.uid}")
            }
            
            // Make sure the base folder exists
            val baseFolder = "subject_attachments"
            
            // Create a reference to the file location in Firebase Storage
            val storageRef = firebaseStorage.getReference("$baseFolder/$attachmentId")
            
            // Try upload with retry logic
            while (attemptCount < 3) {
                try {
                    println("Attempting upload: attempt ${attemptCount + 1}")
                    // Upload file to Firebase Storage
                    val uploadTask = storageRef.putFile(fileUri).await()
                    
                    // Get download URL
                    fileUrl = storageRef.downloadUrl.await().toString()
                    println("Upload successful: $fileUrl")
                    break // Exit retry loop on success
                } catch (e: Exception) {
                    attemptCount++
                    println("Firebase Storage upload attempt $attemptCount failed: ${e.message}")
                    
                    if (attemptCount >= 3) {
                        // After max retries, fall back to local URL
                        fileUrl = "file://local/placeholder_${System.currentTimeMillis()}"
                        println("Using local fallback URL: $fileUrl")
                    } else {
                        // Wait before retrying
                        kotlinx.coroutines.delay(1000)
                    }
                }
            }
        } catch (e: Exception) {
            // Log the error
            println("Firebase Storage setup failed: ${e.message}")
            
            // Use a placeholder URL for development/testing
            fileUrl = "file://local/placeholder_${System.currentTimeMillis()}"
        }
        
        // Create and save attachment metadata
        val attachment = SubjectAttachment(
            id = attachmentId,
            subjectId = subjectId,
            fileName = fileName,
            fileUrl = fileUrl,
            fileType = fileType,
            fileSize = fileSize,
            description = description
        )
        
        // Save to local database
        subjectAttachmentDao.insert(attachment)
        
        // Try to save to Firebase Firestore with retry logic
        attemptCount = 0
        while (attemptCount < 3) {
            try {
                firebaseService.saveAttachment(attachment)
                println("Firestore save successful")
                break // Exit retry loop on success
            } catch (e: Exception) {
                attemptCount++
                println("Firebase Firestore save attempt $attemptCount failed: ${e.message}")
                
                if (attemptCount < 3) {
                    // Wait before retrying
                    kotlinx.coroutines.delay(1000)
                }
            }
        }
        
        return attachment
    }
    
    // Cloud sync operations for attachments
    suspend fun syncAttachmentsWithCloud() {
        val attachments: List<SubjectAttachment> = firebaseService.getAllAttachments()
        attachments.forEach { attachment -> 
            subjectAttachmentDao.insert(attachment) 
        }
    }
    
    // Cloud sync operations
    suspend fun syncClassRoomsWithCloud() {
        val classRooms = firebaseService.getAllClassRooms()
        classRooms.forEach { classRoom -> 
            classRoomDao.insert(classRoom) 
        }
    }
    
    suspend fun syncSubjectsWithCloud() {
        val subjects = firebaseService.getAllSubjects()
        subjects.forEach { subject -> 
            subjectDao.insert(subject) 
        }
    }
    
    suspend fun syncTimeTableEntriesWithCloud() {
        val timeTableEntries = firebaseService.getAllTimeTableEntries()
        timeTableEntries.forEach { entry -> 
            timeTableEntryDao.insert(entry) 
        }
    }
    
    // Sync all operations
    suspend fun syncAllAcademicsDataWithCloud() {
        syncClassRoomsWithCloud()
        syncSubjectsWithCloud()
        syncTimeTableEntriesWithCloud()
        syncAttachmentsWithCloud()
    }
} 