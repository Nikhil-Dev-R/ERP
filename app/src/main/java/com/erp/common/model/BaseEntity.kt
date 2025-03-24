package com.erp.common.model

import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

open class BaseEntity {
    @PrimaryKey
    open var id: String = UUID.randomUUID().toString()
    open var createdAt: Date = Date()
    open var updatedAt: Date = Date()
} 