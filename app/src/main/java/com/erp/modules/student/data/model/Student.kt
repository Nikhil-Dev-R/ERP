package com.erp.modules.student.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "students")
data class Student(
    val firstName: String = "",
    val lastName: String = "",
    val enrollmentNumber: String = "",
    val dateOfBirth: Date? = null,
    val grade: String = "",
    val section: String = "",
    val gender: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val address: String = "",
    val parentName: String = "",
    val parentContact: String = "",
    val parentEmail: String = "",
    val admissionDate: Date? = null,
    val photoUrl: String = "",
    
    // Additional fields for school management
    val bloodGroup: String = "",
    val emergencyContact: String = "",
    val previousSchool: String = "",
    val healthNotes: String = "",
    val active: Boolean = true
) : BaseEntity() {
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
} 