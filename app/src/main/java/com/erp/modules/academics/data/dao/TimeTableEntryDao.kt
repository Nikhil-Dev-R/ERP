package com.erp.modules.academics.data.dao

import androidx.room.*
import com.erp.common.data.BaseDao
import com.erp.modules.academics.data.model.TimeTableEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeTableEntryDao : BaseDao<TimeTableEntry> {
    @Query("SELECT * FROM timetable_entries")
    override fun getAll(): Flow<List<TimeTableEntry>>
    
    @Query("SELECT * FROM timetable_entries")
    fun getAllTimeTableEntries(): Flow<List<TimeTableEntry>>

    @Query("SELECT * FROM timetable_entries WHERE id = :id")
    override suspend fun getById(id: String): TimeTableEntry?
    
    @Query("SELECT * FROM timetable_entries WHERE id = :id")
    fun getTimeTableEntryById(id: String): Flow<TimeTableEntry>

    @Query("SELECT * FROM timetable_entries WHERE classRoomId = :classId")
    fun getTimeTableEntriesByClass(classId: String): Flow<List<TimeTableEntry>>

    @Query("SELECT * FROM timetable_entries WHERE subjectId = :subjectId")
    fun getTimeTableEntriesBySubject(subjectId: String): Flow<List<TimeTableEntry>>

    @Query("SELECT * FROM timetable_entries WHERE teacherId = :teacherId")
    fun getTimeTableEntriesByTeacher(teacherId: String): Flow<List<TimeTableEntry>>

    @Query("SELECT * FROM timetable_entries WHERE dayOfWeek = :dayOfWeek")
    fun getTimeTableEntriesByDay(dayOfWeek: String): Flow<List<TimeTableEntry>>

    @Query("SELECT * FROM timetable_entries WHERE classRoomId = :classId AND dayOfWeek = :dayOfWeek")
    fun getTimeTableEntriesByClassAndDay(classId: String, dayOfWeek: String): Flow<List<TimeTableEntry>>
} 