package com.erp.modules.hr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

enum class LeaveType {
    VACATION, SICK, PERSONAL, BEREAVEMENT, MATERNITY, PATERNITY, UNPAID
}

enum class LeaveStatus {
    PENDING, APPROVED, REJECTED, CANCELLED
}

@Entity(
    tableName = "leave_requests",
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("employeeId"),
        Index("status"),
        Index("startDate"),
        Index("endDate")
    ]
)
data class LeaveRequest(
    val employeeId: String,
    val leaveType: LeaveType,
    val startDate: Date,
    val endDate: Date,
    val status: LeaveStatus = LeaveStatus.PENDING,
    val requestedDays: Int,
    val reason: String? = null,
    val approvedById: String? = null,
    val approvedAt: Date? = null,
    val comments: String? = null,
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity() 