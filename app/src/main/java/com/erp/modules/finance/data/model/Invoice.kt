package com.erp.modules.finance.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

enum class InvoiceStatus {
    DRAFT, SENT, PAID, OVERDUE, CANCELLED
}

@Entity(
    tableName = "invoices",
    indices = [
        Index("customerId"),
        Index("dueDate"),
        Index("status")
    ]
)
data class Invoice(
    val invoiceNumber: String,
    val customerId: String,
    val issueDate: Date,
    val dueDate: Date,
    val amount: BigDecimal,
    val tax: BigDecimal = BigDecimal.ZERO,
    val status: InvoiceStatus,
    val notes: String? = null,
    val items: List<InvoiceItem> = emptyList(),
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity()

data class InvoiceItem(
    val productId: String,
    val description: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val discount: BigDecimal = BigDecimal.ZERO,
    val tax: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal
) 