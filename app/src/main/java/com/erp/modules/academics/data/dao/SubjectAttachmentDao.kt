package com.erp.modules.academics.data.dao

import androidx.room.*
import com.erp.modules.academics.data.model.SubjectAttachment
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectAttachmentDao {
    @Query("SELECT * FROM subject_attachments WHERE active = 1")
    fun getAllAttachments(): Flow<List<SubjectAttachment>>
    
    @Query("SELECT * FROM subject_attachments WHERE id = :id AND active = 1")
    fun getAttachmentById(id: String): Flow<SubjectAttachment>
    
    @Query("SELECT * FROM subject_attachments WHERE subjectId = :subjectId AND active = 1")
    fun getAttachmentsBySubject(subjectId: String): Flow<List<SubjectAttachment>>
    
    @Query("SELECT * FROM subject_attachments WHERE fileName LIKE :query AND active = 1")
    fun searchAttachments(query: String): Flow<List<SubjectAttachment>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachment: SubjectAttachment)
    
    @Update
    suspend fun update(attachment: SubjectAttachment)
    
    @Delete
    suspend fun delete(attachment: SubjectAttachment)
    
    @Query("UPDATE subject_attachments SET active = 0 WHERE id = :id")
    suspend fun softDelete(id: String)
} 