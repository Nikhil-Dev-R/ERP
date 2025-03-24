package com.erp.modules.hr.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

enum class EmploymentType {
    FULL_TIME, PART_TIME, CONTRACT, INTERN, CONSULTANT
}

enum class EmploymentStatus {
    ACTIVE, ON_LEAVE, TERMINATED, SUSPENDED
}

@Entity(
    tableName = "employees",
    indices = [
        Index("employeeId", unique = true),
        Index("email", unique = true),
        Index("status"),
        Index("id", unique = true)
    ]
)
data class Employee(
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val position: String,
    val department: String,
    val employmentType: EmploymentType,
    val status: EmploymentStatus,
    val hireDate: Date,
    val terminationDate: Date? = null,
    val reportingTo: String? = null,
    val salary: Double = 0.0,
    val address: String? = null,
    val profilePictureUrl: String? = null,
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity() 