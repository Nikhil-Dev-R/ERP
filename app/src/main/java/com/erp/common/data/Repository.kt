package com.erp.common.data

import com.erp.common.model.BaseEntity
import kotlinx.coroutines.flow.Flow

interface Repository<T : BaseEntity> {
    suspend fun getById(id: String): T?
    fun getAll(): Flow<List<T>>
    suspend fun insert(item: T): String
    suspend fun update(item: T)
    suspend fun delete(item: T)
    suspend fun deleteById(id: String)
} 