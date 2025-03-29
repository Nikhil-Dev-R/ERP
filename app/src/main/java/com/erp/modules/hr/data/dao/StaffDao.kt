package com.erp.modules.hr.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erp.modules.hr.data.model.Staff

@Dao
interface StaffDao {
    @Query("SELECT * FROM staff")
    suspend fun getAllStaff(): List<Staff>
    
    @Query("SELECT * FROM staff WHERE id = :id")
    suspend fun getStaffById(id: String): Staff?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(staff: Staff): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(staffList: List<Staff>)
    
    @Update
    suspend fun update(staff: Staff)
    
    @Delete
    suspend fun delete(staff: Staff)
    
    @Query("DELETE FROM staff WHERE id = :id")
    suspend fun deleteById(id: String)
} 