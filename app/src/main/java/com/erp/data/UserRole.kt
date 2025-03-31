package com.erp.data

sealed class UserRole(val value: String) {
    object Admin : UserRole("admin") {
        val canManageUsers = true
        val canManageSettings = true
        val canManageCourses = true
        val canViewAllData = true
    }

    object Teacher : UserRole("teacher") {
        val canCreateExams = true
        val canManageGrades = true
        val canMarkAttendance = true
        val canViewStudentDetails = true
        val assignedClasses = mutableListOf<String>()
        val assignedCourses = mutableListOf<String>()
    }

    object Student : UserRole("student") {
        val canViewOwnGrades = true
        val canViewOwnAttendance = true
        val classId: String? = null
        val enrolledCourses = mutableListOf<String>()
        val rollNumber: String? = null
    }

    object Parent : UserRole("parent") {
        val canViewChildrenData = true
        val canPayFees = true
        val childrenIds = mutableListOf<String>()
    }

    object Staff : UserRole("staff") {
        val canManageTransport = true
        val canManageInventory = true
        val canManageHostel = true
        val staffType: String? = null
    }

    object Unknown : UserRole("unknown")

    companion object {
        fun fromString(role: String?): UserRole {
            return when(role?.lowercase()) {
                "admin" -> Admin
                "teacher" -> Teacher
                "student" -> Student
                "parent" -> Parent
                "staff" -> Staff
                else -> Unknown
            }
        }
    }
}