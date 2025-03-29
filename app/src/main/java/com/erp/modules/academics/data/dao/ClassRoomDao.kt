package com.erp.modules.academics.data.dao

import androidx.room.*
import com.erp.common.data.BaseDao
import com.erp.modules.academics.data.model.ClassRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassRoomDao : BaseDao<ClassRoom> {
    @Query("SELECT * FROM classrooms")
    override fun getAll(): Flow<List<ClassRoom>>
    
    @Query("SELECT * FROM classrooms")
    fun getAllClassRooms(): Flow<List<ClassRoom>>

    @Query("SELECT * FROM classrooms WHERE id = :id")
    override suspend fun getById(id: String): ClassRoom?
    
    @Query("SELECT * FROM classrooms WHERE id = :id")
    fun getClassRoomById(id: String): Flow<ClassRoom>

    @Query("SELECT * FROM classrooms WHERE name LIKE :gradeName")
    fun getClassRoomsByGrade(gradeName: String): Flow<List<ClassRoom>>

    @Query("SELECT * FROM classrooms WHERE classTeacherId = :teacherId")
    fun getClassRoomsByTeacher(teacherId: String): Flow<List<ClassRoom>>

    @Query("SELECT * FROM classrooms WHERE section = :section")
    fun getClassRoomsBySection(section: String): Flow<List<ClassRoom>>
} 