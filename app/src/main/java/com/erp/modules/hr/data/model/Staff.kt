package com.erp.modules.hr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "staff")
data class Staff(
    val firstName: String = "",
    val lastName: String = "",
    val employeeId: String = "",
    val staffRole: StaffRole = StaffRole.ADMIN_STAFF,
    val department: String = "",
    val joiningDate: Date? = null,
    val qualification: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val address: String = "",
    val gender: String = "",
    val dateOfBirth: Date? = null,
    val status: EmployeeStatus = EmployeeStatus.ACTIVE,
    val photoUrl: String = "",
    
    // Staff-specific fields
    val emergencyContact: String = "",
    val emergencyContactName: String = "",
    val bloodGroup: String = "",
    val previousExperience: Int = 0,
    
    // Employment details
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val position: String = "",
    val reportingTo: String = "",
    val workingHours: String = "",
    val hireDate: Date = Date()
) : BaseEntity() {
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
}

enum class StaffRole {
    ADMIN_STAFF,
    PRINCIPAL,
    VICE_PRINCIPAL,
    COORDINATOR,
    LIBRARIAN,
    LAB_ASSISTANT,
    COUNSELOR,
    SPORTS_COACH,
    SECURITY,
    MAINTENANCE
} 