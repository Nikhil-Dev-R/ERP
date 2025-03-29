package com.erp.modules.hr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "employees")
data class Employee(
    val firstName: String = "",
    val lastName: String = "",
    val employeeId: String = "",
    val role: EmployeeRole = EmployeeRole.TEACHER,
    val department: String = "",
    val joiningDate: Date? = null,
    val qualification: String = "",
    val specialization: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val address: String = "",
    val gender: String = "",
    val dateOfBirth: Date? = null,
    val status: EmployeeStatus = EmployeeStatus.ACTIVE,
    val photoUrl: String = "",
    
    // Additional fields for school staff
    val subjectsIds: List<String> = emptyList(),
    val classesTaught: List<String> = emptyList(),
    val emergencyContact: String = "",
    val emergencyContactName: String = "",
    val bloodGroup: String = "",
    val previousExperience: Int = 0,   // Experience in years
    
    // Employment details
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val position: String = "",
    val reportingTo: String = "",
    val hireDate: Date = Date()
) : BaseEntity() {
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
}

enum class EmployeeRole {
    TEACHER,
    PRINCIPAL,
    VICE_PRINCIPAL,
    COORDINATOR,
    ADMIN_STAFF,
    LIBRARIAN,
    LAB_ASSISTANT,
    COUNSELOR,
    SPORTS_COACH,
    SECURITY,
    MAINTENANCE
}

enum class EmployeeStatus {
    ACTIVE,
    ON_LEAVE,
    TERMINATED,
    RETIRED,
    SABBATICAL
}

enum class EmploymentStatus {
    ACTIVE,
    ON_LEAVE,
    PROBATION,
    SUSPENDED,
    TERMINATED
}

enum class EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    TEMPORARY,
    INTERN
} 