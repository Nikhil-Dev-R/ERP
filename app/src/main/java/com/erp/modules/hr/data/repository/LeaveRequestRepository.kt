package com.erp.modules.hr.data.repository

import com.erp.common.data.Repository
import com.erp.data.remote.FirebaseService
import com.erp.modules.hr.data.dao.LeaveRequestDao
import com.erp.modules.hr.data.model.LeaveRequest
import com.erp.modules.hr.data.model.LeaveStatus
import com.erp.modules.hr.data.model.LeaveType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class LeaveRequestRepository(
    private val leaveRequestDao: LeaveRequestDao,
    private val firebaseService: FirebaseService<LeaveRequest>
) : Repository<LeaveRequest> {
    
    override suspend fun getById(id: String): LeaveRequest? {
        return leaveRequestDao.getById(id) ?: firebaseService.getById(id)?.also {
            leaveRequestDao.insert(it)
        }
    }
    
    override fun getAll(): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getAll()
    }
    
    override suspend fun insert(item: LeaveRequest): String {
        leaveRequestDao.insert(item)
        return firebaseService.insert(item)
    }
    
    override suspend fun update(item: LeaveRequest) {
        val updatedItem = item.copy(updatedAt = Date())
        leaveRequestDao.update(updatedItem)
        firebaseService.update(updatedItem)
    }
    
    override suspend fun delete(item: LeaveRequest) {
        leaveRequestDao.delete(item)
        firebaseService.delete(item.id)
    }
    
    override suspend fun deleteById(id: String) {
        leaveRequestDao.deleteById(id)
        firebaseService.delete(id)
    }
    
    fun getByEmployeeId(employeeId: String): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getByEmployeeId(employeeId)
    }
    
    fun getByStatus(status: LeaveStatus): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getByStatus(status)
    }
    
    fun getByLeaveType(leaveType: LeaveType): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getByLeaveType(leaveType)
    }
    
    fun getByDateRange(startDate: Date, endDate: Date): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getByDateRange(startDate, endDate)
    }
    
    fun getByApprover(approverId: String): Flow<List<LeaveRequest>> {
        return leaveRequestDao.getByApprover(approverId)
    }
    
    suspend fun approveLeaveRequest(id: String, approverId: String, comments: String? = null) {
        val leaveRequest = getById(id) ?: return
        val updatedRequest = leaveRequest.copy(
            status = LeaveStatus.APPROVED,
            approvedById = approverId,
            approvedAt = Date(),
            comments = comments
        )
        update(updatedRequest)
    }
    
    suspend fun rejectLeaveRequest(id: String, approverId: String, comments: String? = null) {
        val leaveRequest = getById(id) ?: return
        val updatedRequest = leaveRequest.copy(
            status = LeaveStatus.REJECTED,
            approvedById = approverId,
            approvedAt = Date(),
            comments = comments
        )
        update(updatedRequest)
    }
} 