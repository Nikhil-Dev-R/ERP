package com.erp.modules.academics.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.UUID

/**
 * Model class for storing subject attachment information
 */
@Entity(
    tableName = "subject_attachments",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class SubjectAttachment(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    
    val subjectId: String,
    val fileName: String,
    val fileUrl: String,
    val fileType: String,     // "pdf", "jpg", "png", "doc", "docx", "ppt", "pptx", "xls", "xlsx"
    val uploadDate: Long = System.currentTimeMillis(),
    val fileSize: Long = 0,
    val description: String = "",
    val active: Boolean = true
) : BaseEntity() 