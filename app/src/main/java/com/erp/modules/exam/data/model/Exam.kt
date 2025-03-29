package com.erp.modules.exam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    val name: String = "",
    val description: String = "",
    val examType: ExamType = ExamType.UNIT_TEST,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val academicYear: String = "",
    val gradeLevel: String = "",      // Which class/grade this exam is for
    val subjectId: String = "",       // Subject reference
    val totalMarks: Int = 100,
    val passingMarks: Int = 35,
    val instructions: String = "",
    val createdBy: String = "",       // Teacher/admin who created the exam
    val status: ExamStatus = ExamStatus.SCHEDULED
) : BaseEntity()

enum class ExamType {
    UNIT_TEST,
    CLASS_TEST,
    MID_TERM,
    FINAL,
    QUARTERLY,
    HALF_YEARLY,
    PRACTICAL,
    PROJECT,
    ASSIGNMENT
}

enum class ExamStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    RESULTS_PUBLISHED
} 