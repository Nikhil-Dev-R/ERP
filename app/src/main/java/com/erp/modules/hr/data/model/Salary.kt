package com.erp.modules.hr.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

enum class SalaryStatus {
    PENDING, PROCESSED, PAID, CANCELLED
}

enum class PaymentMethod {
    BANK_TRANSFER, CHECK, CASH, ONLINE
}

@Entity(
    tableName = "salary_payments",
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
        Index("paymentDate")
    ]
)
data class Salary(
    val employeeId: String,
    val amount: Double,
    val baseSalary: Double,
    val allowances: Double = 0.0,
    val deductions: Double = 0.0,
    val bonus: Double = 0.0,
    val tax: Double = 0.0,
    val payPeriodStart: Date,
    val payPeriodEnd: Date,
    val paymentDate: Date? = null,
    val status: SalaryStatus = SalaryStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.BANK_TRANSFER,
    val transactionId: String? = null,
    val notes: String? = null,
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity() 