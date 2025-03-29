package com.erp.modules.attendance.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "attendance")
class Attendance(
    // We don't override id here to inherit from BaseEntity
    val studentId: String = "",
    val date: Date? = null,
    val status: String = "PRESENT",      // Changed to String to be more flexible: "PRESENT", "ABSENT", "LATE", etc.
    val subjectId: String? = null,       // Optional: for subject-wise attendance
    val classId: String = "",            // Class/grade reference
    val sectionId: String = "",          // Section reference
    val remarks: String = "",            // Any notes about the attendance
    val recordedBy: String = "",         // Teacher/staff who marked attendance
    val lastModified: Date = Date()      // When the attendance was last modified
) : BaseEntity() {
    // Room needs a primary key, so we annotate the inherited id property
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
}

// These constants can be used instead of enum to allow more flexibility
object AttendanceStatus {
    const val PRESENT = "PRESENT"
    const val ABSENT = "ABSENT"
    const val LATE = "LATE"
    const val EXCUSED = "EXCUSED"
    const val HALF_DAY = "HALF_DAY"
} 