package com.erp.modules.finance.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.math.BigDecimal
import java.util.Date
import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

enum class TransactionStatus {
    PENDING, COMPLETED, CANCELLED, FAILED
}

@Entity(
    tableName = "transactions",
    indices = [
        Index("date"),
        Index("type"),
        Index("status")
    ]
)
data class Transaction(
    val amount: BigDecimal,
    val description: String,
    val date: Date,
    val type: TransactionType,
    val status: TransactionStatus,
    val accountId: String? = null,
    val categoryId: String? = null,
    val payeeId: String? = null,
    val referenceNumber: String? = null,
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity() 