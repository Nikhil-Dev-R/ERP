package com.erp.data.remote

import android.util.Log
import com.erp.common.model.BaseEntity
import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.SubjectAttachment
import com.erp.modules.academics.data.model.TimeTableEntry
import com.erp.modules.attendance.data.model.Attendance
import com.erp.modules.exam.data.model.Exam
import com.erp.modules.exam.data.model.ExamResult
import com.erp.modules.finance.data.model.Fee
import com.erp.modules.finance.data.model.Invoice
import com.erp.modules.finance.data.model.Transaction
import com.erp.modules.hr.data.model.Employee
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.inventory.data.model.Product
import com.erp.modules.inventory.data.model.Vendor
import com.erp.modules.student.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.reflect.KClass

class FirebaseService<T : BaseEntity>(
    private val classType: KClass<T>,
    private val collectionPath: String
) {
    private val db: FirebaseFirestore = Firebase.firestore
    private val collection = db.collection(collectionPath)
    
    suspend fun getAll(): List<T> {
        return try {
            collection.get().await().documents.mapNotNull { doc ->
                try {
                    val item = doc.toObject(classType.java)
                    item?.id = doc.id
                    item
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Error converting document to object: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting all documents: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun getById(id: String): T? {
        return try {
            val document = collection.document(id).get().await()
            if (document.exists()) {
                val item = document.toObject(classType.java)
                item?.id = id
                item
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error getting document by ID: ${e.message}")
            null
        }
    }
    
    suspend fun insert(item: T): String {
        val TAG = "FirebaseService"
        return try {
            // Check if Firestore instance is available
            if (!isFirestoreAvailable()) {
                Log.e(TAG, "Error inserting document: Firebase Firestore not available", Exception("Firebase not initialized"))
                return ""
            }
            
            if (item.id.isNullOrEmpty()) {
                Log.d(TAG, "Inserting new document to collection: $collectionPath")
                val docRef = collection.add(item).await()
                val newId = docRef.id
                
                // Update the local item with the new ID
                item.id = newId
                Log.d(TAG, "Document successfully inserted with ID: $newId in collection: $collectionPath")
                
                // Update the document with its ID to ensure consistency
                collection.document(newId).set(item).await()
                
                return newId
            } else {
                Log.d(TAG, "Inserting document with existing ID ${item.id} to collection: $collectionPath")
                collection.document(item.id!!).set(item).await()
                Log.d(TAG, "Document successfully inserted with ID: ${item.id} in collection: $collectionPath")
                return item.id!!
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting document into $collectionPath: ${e.message}", e)
            ""
        }
    }
    
    suspend fun update(item: T): Boolean {
        val TAG = "FirebaseService"
        return try {
            // Check if Firestore instance is available
            if (!isFirestoreAvailable()) {
                Log.e(TAG, "Error updating document: Firebase Firestore not available", Exception("Firebase not initialized"))
                return false
            }
            
            if (item.id.isNullOrEmpty()) {
                Log.e(TAG, "Cannot update document with empty ID in collection: $collectionPath")
                return false
            }
            
            Log.d(TAG, "Updating document with ID ${item.id} in collection: $collectionPath")
            collection.document(item.id!!).set(item).await()
            Log.d(TAG, "Document successfully updated with ID: ${item.id} in collection: $collectionPath")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating document with ID ${item.id} in $collectionPath: ${e.message}", e)
            false
        }
    }
    
    suspend fun delete(id: String): Boolean {
        return try {
            collection.document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error deleting document: ${e.message}")
            false
        }
    }
    
    // Attendance operations
    suspend fun saveAttendance(attendance: Attendance) {
        db.collection("attendance").document(attendance.id).set(attendance)
    }
    
    suspend fun updateAttendance(attendance: Attendance) {
        db.collection("attendance").document(attendance.id).set(attendance)
    }
    
    suspend fun deleteAttendance(id: String) {
        db.collection("attendance").document(id).delete()
    }
    
    suspend fun getAllAttendance(): List<Attendance> {
        val snapshot = db.collection("attendance").get().await()
        return snapshot.toObjects(Attendance::class.java)
    }
    
    // Academics operations - ClassRooms
    suspend fun saveClassRoom(classRoom: ClassRoom) {
        db.collection("classrooms").document(classRoom.id).set(classRoom)
    }
    
    suspend fun updateClassRoom(classRoom: ClassRoom) {
        db.collection("classrooms").document(classRoom.id).set(classRoom)
    }
    
    suspend fun deleteClassRoom(id: String) {
        db.collection("classrooms").document(id).delete()
    }
    
    suspend fun getAllClassRooms(): List<ClassRoom> {
        val snapshot = db.collection("classrooms").get().await()
        return snapshot.toObjects(ClassRoom::class.java)
    }
    
    // Academics operations - Subjects
    suspend fun saveSubject(subject: Subject) {
        db.collection("subjects").document(subject.id).set(subject)
    }
    
    suspend fun updateSubject(subject: Subject) {
        db.collection("subjects").document(subject.id).set(subject)
    }
    
    suspend fun deleteSubject(id: String) {
        db.collection("subjects").document(id).delete()
    }
    
    suspend fun getAllSubjects(): List<Subject> {
        val snapshot = db.collection("subjects").get().await()
        return snapshot.toObjects(Subject::class.java)
    }
    
    // Academics operations - TimeTableEntries
    suspend fun saveTimeTableEntry(timeTableEntry: TimeTableEntry) {
        db.collection("timetable_entries").document(timeTableEntry.id).set(timeTableEntry)
    }
    
    suspend fun updateTimeTableEntry(timeTableEntry: TimeTableEntry) {
        db.collection("timetable_entries").document(timeTableEntry.id).set(timeTableEntry)
    }
    
    suspend fun deleteTimeTableEntry(id: String) {
        db.collection("timetable_entries").document(id).delete()
    }
    
    suspend fun getAllTimeTableEntries(): List<TimeTableEntry> {
        val snapshot = db.collection("timetable_entries").get().await()
        return snapshot.toObjects(TimeTableEntry::class.java)
    }
    
    // Exam operations
    suspend fun saveExam(exam: Exam) {
        db.collection("exams").document(exam.id).set(exam)
    }
    
    suspend fun updateExam(exam: Exam) {
        db.collection("exams").document(exam.id).set(exam)
    }
    
    suspend fun deleteExam(id: String) {
        db.collection("exams").document(id).delete()
    }
    
    suspend fun getAllExams(): List<Exam> {
        val snapshot = db.collection("exams").get().await()
        return snapshot.toObjects(Exam::class.java)
    }
    
    // Exam Result operations
    suspend fun saveResult(result: ExamResult) {
        db.collection("results").document(result.id).set(result)
    }
    
    suspend fun updateResult(result: ExamResult) {
        db.collection("results").document(result.id).set(result)
    }
    
    suspend fun deleteResult(id: String) {
        db.collection("results").document(id).delete()
    }
    
    suspend fun getAllResults(): List<ExamResult> {
        val snapshot = db.collection("results").get().await()
        return snapshot.toObjects(ExamResult::class.java)
    }
    
    // Academics operations - SubjectAttachments
    suspend fun saveAttachment(attachment: SubjectAttachment) {
        db.collection("subject_attachments").document(attachment.id).set(attachment)
    }
    
    suspend fun updateAttachment(attachment: SubjectAttachment) {
        db.collection("subject_attachments").document(attachment.id).set(attachment)
    }
    
    suspend fun deleteAttachment(id: String) {
        db.collection("subject_attachments").document(id).delete()
    }
    
    suspend fun getAllAttachments(): List<SubjectAttachment> {
        val snapshot = db.collection("subject_attachments").get().await()
        return snapshot.toObjects(SubjectAttachment::class.java)
    }

    private fun isFirestoreAvailable(): Boolean {
        return try {
            FirebaseFirestore.getInstance()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Firestore not available: ${e.message}", e)
            false
        }
    }
} 