package com.erp.modules.academics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.UUID

@Entity(tableName = "classrooms")
data class ClassRoom(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    val name: String = "",          // Example: "Grade 10", "Class 7"
    val section: String = "",       // Example: "A", "B", "Science", "Commerce"
    val roomNumber: String = "",    // Physical room where class is held
    val capacity: Int = 40,
    val academicYear: String = "",  // Example: "2023-2024"
    val classTeacherId: String = "",// Primary teacher responsible for class
    val description: String = "",
    val schedule: String = "",      // JSON string with class schedule info
    val active: Boolean = true
) : BaseEntity()