package com.erp.core.navigation

object ERPDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val HOME_ROUTE = "home"

    // Finance module
    const val FINANCE_DASHBOARD_ROUTE = "finance_dashboard"
    const val TRANSACTIONS_ROUTE = "transactions"
    const val TRANSACTION_DETAIL_ROUTE = "transaction_detail"
    const val INVOICES_ROUTE = "invoices"
    const val INVOICE_DETAIL_ROUTE = "invoice_detail"
    const val FEES_ROUTE = "fees"
    const val FEE_DETAIL_ROUTE = "fee_detail"

    // Fee module
    const val FEE_MODULE_DASHBOARD_ROUTE = "fee_module_dashboard"
    const val FEE_MODULE_LIST_ROUTE = "fee_module_list"
    const val FEE_MODULE_DETAIL_ROUTE = "fee_module_detail"
    const val FEE_MODULE_CREATE_ROUTE = "fee_module_create"

    // HR module (Teachers/Staff)
    const val HR_DASHBOARD_ROUTE = "hr_dashboard"
    const val EMPLOYEES_ROUTE = "employees"
    const val EMPLOYEE_DETAIL_ROUTE = "employee_detail"
    const val EMPLOYEE_FORM_ROUTE = "employee_form"
    const val LEAVE_REQUESTS_ROUTE = "leave_requests"
    const val PAYROLL_ROUTE = "payroll"

    // Student module
    const val STUDENT_HOME = "student_home"
    const val STUDENTS_DASHBOARD_ROUTE = "students_dashboard"
    const val STUDENTS_ROUTE = "students"
    const val STUDENT_DETAIL_ROUTE = "student_detail"
    const val STUDENT_FORM_ROUTE = "student_form"

    // Academics module
    const val ACADEMICS_DASHBOARD_ROUTE = "academics_dashboard"
    const val SUBJECTS_ROUTE = "subjects"
    const val SUBJECT_DETAIL_ROUTE = "subject_detail"
    const val CLASSES_ROUTE = "classes"
    const val CLASS_DETAIL_ROUTE = "class_detail"
    const val TIMETABLE_ROUTE = "timetable"
    const val CLASS_SELECTION_ROUTE = "academics/class_selection"
    const val SUBJECTS_BY_CLASS_ROUTE = "academics/subjects_by_class"
    const val SECTION_SELECTION_ROUTE = "academics/section_selection"
    const val SECTION_FILES_ROUTE = "academics/section_files"

    // Attendance module
    const val ATTENDANCE_DASHBOARD_ROUTE = "attendance_dashboard"
    const val ATTENDANCE_MARK_ROUTE = "attendance_mark"
    const val ATTENDANCE_REPORT_ROUTE = "attendance_report"

    // Exam module
    const val EXAM_DASHBOARD_ROUTE = "exam_dashboard"
    const val EXAMS_ROUTE = "exams"
    const val EXAM_DETAIL_ROUTE = "exam_detail"
    const val RESULTS_ROUTE = "results"
    const val RESULT_ENTRY_ROUTE = "result_entry"
    const val RESULT_REPORT_ROUTE = "result_report"

    // Exam module additional routes
    const val EXAM_LIST_ROUTE = "exam/list"
    const val EXAM_CREATE_ROUTE = "exam/create"
    const val QUIZ_MANAGEMENT_ROUTE = "exam/quiz"
    const val QUIZ_CREATE_ROUTE = "exam/quiz/create"
    const val QUIZ_EDIT_ROUTE = "exam/quiz/edit"
    const val QUIZ_DETAIL_ROUTE = "exam/quiz/detail"
    const val RESULTS_UPLOAD_ROUTE = "exam/results/upload"
    const val RESULTS_VIEW_ROUTE = "exam/results/view"
    const val RESULT_DETAIL_ROUTE = "exam/results/detail"

    // Inventory module (for school supplies)
    const val INVENTORY_DASHBOARD_ROUTE = "inventory_dashboard"
    const val PRODUCTS_ROUTE = "products"
    const val PRODUCT_DETAIL_ROUTE = "product_detail"
    const val VENDORS_ROUTE = "vendors"

    // New routes
    const val FINANCIAL_REPORTS_ROUTE = "financial_reports"
    const val BUDGET_MANAGEMENT_ROUTE = "budget_management"
}