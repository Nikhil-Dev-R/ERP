package com.erp.modules.inventory.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.erp.common.model.BaseEntity
import java.util.Date
import java.util.UUID

enum class ProductCategory {
    GENERAL,
    ELECTRONICS,
    CLOTHING,
    FURNITURE,
    FOOD,
    BOOKS,
    TOOLS,
    SPORTS,
    AUTOMOTIVE,
    SOFTWARE,
    HARDWARE,
    OFFICE_SUPPLIES,
    RAW_MATERIALS
}

enum class ProductStatus {
    ACTIVE,
    INACTIVE,
    LOW_STOCK,
    OUT_OF_STOCK
}

@Entity(
    tableName = "products",
    indices = [
        Index("sku", unique = true),
        Index("name"),
        Index("category"),
        Index("status"),
        Index("vendorId")
    ]
)
data class Product(
    val name: String,
    val sku: String,
    val description: String = "",
    val price: Double,
    val category: ProductCategory = ProductCategory.GENERAL,
    val status: ProductStatus = ProductStatus.ACTIVE,
    val stockQuantity: Int = 0,
    val reorderLevel: Int = 5,
    val vendorId: String = "",
    val lastRestockDate: Date? = null,
    override var createdAt: Date = Date(),
    override var updatedAt: Date = Date(),
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString()
) : BaseEntity() 