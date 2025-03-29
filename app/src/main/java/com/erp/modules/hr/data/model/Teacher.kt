package com.erp.modules.hr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "teachers")
data class Teacher(
    val firstName: String = "",
    val lastName: String = "",
    val employeeId: String = "",
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
    
    // Teacher-specific fields
    val subjectsIds: List<String> = emptyList(),
    val classesTaught: List<String> = emptyList(),
    val emergencyContact: String = "",
    val emergencyContactName: String = "",
    val bloodGroup: String = "",
    val previousExperience: Int = 0,
    
    // Employment details
    val employmentType: EmploymentType = EmploymentType.FULL_TIME,
    val position: String = "",
    val reportingTo: String = "",
    val hireDate: Date = Date()
) : BaseEntity() {
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
} 