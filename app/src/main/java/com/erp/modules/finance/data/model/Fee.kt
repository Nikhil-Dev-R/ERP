package com.erp.modules.finance.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

@Entity(tableName = "fees")
data class Fee(
    val studentId: String = "",
    val feeType: FeeType = FeeType.TUITION,
    val amount: Double = 0.0,
    val dueDate: Date? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentDate: Date? = null,
    val paymentMethod: String = "",
    val transactionId: String = "",
    val receiptNumber: String = "",
    val academicYear: String = "",
    val term: String = "",        // Term or semester (e.g., "Fall 2023")
    val remarks: String = "",
    val createdBy: String = "",
    val lastModified: Date = Date()
) : BaseEntity() {
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
}

enum class FeeType {
    TUITION,
    EXAMINATION,
    TRANSPORTATION,
    LIBRARY,
    LABORATORY,
    ADMISSION,
    HOSTEL,
    SPORTS,
    UNIFORM,
    DEVELOPMENT,
    MISCELLANEOUS
}

enum class PaymentStatus {
    PENDING,
    PARTIAL,
    PAID,
    OVERDUE,
    WAIVED
} 