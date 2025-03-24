package com.erp.modules.hr.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.erp.common.data.BaseDao
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.data.model.LeaveType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface LeaveRequestDao : BaseDao<LeaveRequest> {
    @Query("SELECT * FROM leave_requests WHERE id = :id")
    override suspend fun getById(id: String): LeaveRequest?
    
    @Query("SELECT * FROM leave_requests ORDER BY startDate DESC")
    override fun getAll(): Flow<List<LeaveRequest>>
    
    @Query("SELECT * FROM leave_requests WHERE employeeId = :employeeId ORDER BY startDate DESC")
    fun getByEmployeeId(employeeId: String): Flow<List<LeaveRequest>>
    
    @Query("SELECT * FROM leave_requests WHERE status = :status ORDER BY startDate")
    fun getByStatus(status: LeaveStatus): Flow<List<LeaveRequest>>
    
    @Query("SELECT * FROM leave_requests WHERE leaveType = :leaveType ORDER BY startDate DESC")
    fun getByLeaveType(leaveType: LeaveType): Flow<List<LeaveRequest>>
    
    @Query("SELECT * FROM leave_requests WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate")
    fun getByDateRange(startDate: Date, endDate: Date): Flow<List<LeaveRequest>>
    
    @Query("SELECT * FROM leave_requests WHERE approvedById = :approverId ORDER BY approvedAt DESC")
    fun getByApprover(approverId: String): Flow<List<LeaveRequest>>
    
    @Query("DELETE FROM leave_requests WHERE id = :id")
    suspend fun deleteById(id: String)
} 