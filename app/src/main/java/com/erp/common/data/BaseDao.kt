package com.erp.common.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.erp.common.model.BaseEntity
import kotlinx.coroutines.flow.Flow

interface BaseDao<T : BaseEntity> {
    /**
     * Get all entities
     * Note: Implementing DAOs must override this method with proper @Query annotation
     */
    fun getAll(): Flow<List<T>>

    /**
     * Get entity by ID
     * Note: Implementing DAOs must override this method with proper @Query annotation
     */
    suspend fun getById(id: String): T?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T): Long

    @Update
    suspend fun update(item: T)

    @Delete
    suspend fun delete(item: T)
} 