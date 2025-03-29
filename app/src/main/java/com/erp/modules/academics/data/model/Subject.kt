package com.erp.modules.academics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.UUID

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    val name: String = "",
    val code: String = "",
    val description: String = "",
    val gradeLevel: String = "",  // Which grade/class this subject is for
    val credits: Int = 0,
    val isElective: Boolean = false,
    val departmentId: String = "",  // Reference to a department if needed
    val teacherId: String = "",     // Primary teacher for this subject
    val syllabus: String = "",      // Brief overview or document reference
    val imageUrl: String = "",      // Subject icon or image
    val active: Boolean = true
) : BaseEntity()