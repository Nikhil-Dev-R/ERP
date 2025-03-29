package com.erp.modules.hr.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erp.modules.hr.data.model.Teacher

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers")
    suspend fun getAllTeachers(): List<Teacher>
    
    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: String): Teacher?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(teacher: Teacher): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teachers: List<Teacher>)
    
    @Update
    suspend fun update(teacher: Teacher)
    
    @Delete
    suspend fun delete(teacher: Teacher)
    
    @Query("DELETE FROM teachers WHERE id = :id")
    suspend fun deleteById(id: String)
} 