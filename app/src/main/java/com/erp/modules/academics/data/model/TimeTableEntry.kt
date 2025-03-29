package com.erp.modules.academics.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.UUID

@Entity(tableName = "timetable_entries")
data class TimeTableEntry(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),

    val classRoomId: String = "",  // Reference to the class
    val subjectId: String = "",    // Subject being taught
    val teacherId: String = "",    // Teacher for this period
    val dayOfWeek: Int = 0,        // 1-7 representing Monday-Sunday
    val periodNumber: Int = 0,     // Which period of the day (1st, 2nd, etc.)
    val startTime: String = "",    // Format: "HH:MM"
    val endTime: String = "",      // Format: "HH:MM"
    val roomId: String = "",       // Where the class is held (can differ from homeroom)
    val academicYear: String = "", // Current academic year
    val term: String = "",         // Term/semester
    val notes: String = "",        // Any special instructions
    val active: Boolean = true
) : BaseEntity()