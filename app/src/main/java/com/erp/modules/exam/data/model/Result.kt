package com.erp.modules.exam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "results")
data class ExamResult(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    
    val examId: String = "",
    val studentId: String = "",
    val subjectId: String = "",
    val marksObtained: Float = 0f,
    val grade: String = "",
    val remarks: String = "",
    val status: ResultStatus = ResultStatus.PENDING,
    val evaluatedBy: String = "",       // Teacher who evaluated
    val evaluationDate: Date? = null,
    val isPublished: Boolean = false,
    val publishedDate: Date? = null,
    val lastModified: Date = Date()
) : BaseEntity()

enum class ResultStatus {
    PENDING,
    PASSED,
    FAILED,
    ABSENT,
    INCOMPLETE
} 