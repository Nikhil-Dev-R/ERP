package com.erp.modules.inventory.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

enum class VendorStatus {
    ACTIVE,
    INACTIVE,
    BLACKLISTED
}

@Entity(
    tableName = "vendors",
    indices = [
        Index("name"),
        Index("email", unique = true),
        Index("status"),
        Index("country")
    ]
)
data class Vendor(
    val name: String,
    val email: String,
    val phone: String,
    val address: String = "",
    val country: String = "",
    val contactPerson: String = "",
    val website: String = "",
    val notes: String = "",
    val rating: Float = 3.0f,
    val totalRatings: Int = 0,
    val status: VendorStatus = VendorStatus.ACTIVE,
    val blacklistReason: String? = null,
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity() 