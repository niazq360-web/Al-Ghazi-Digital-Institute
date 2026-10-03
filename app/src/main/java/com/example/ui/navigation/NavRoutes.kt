package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Students : Screen("students")
    data object StudentDetail : Screen("student_detail/{studentId}") {
        fun createRoute(studentId: Long) = "student_detail/$studentId"
    }
    data object AddEditStudent : Screen("add_edit_student?studentId={studentId}") {
        fun createRoute(studentId: Long? = null) = if (studentId != null) "add_edit_student?studentId=$studentId" else "add_edit_student"
    }
    data object Courses : Screen("courses")
    data object Fees : Screen("fees")
    data object CollectFee : Screen("collect_fee?studentId={studentId}") {
        fun createRoute(studentId: Long? = null) = if (studentId != null) "collect_fee?studentId=$studentId" else "collect_fee"
    }
    data object ReceiptAction : Screen("receipt_action/{paymentId}") {
        fun createRoute(paymentId: Long) = "receipt_action/$paymentId"
    }
    data object Attendance : Screen("attendance")
    data object Teachers : Screen("teachers")
    data object Expenses : Screen("expenses")
    data object Certificates : Screen("certificates")
    data object Reports : Screen("reports")
    data object Settings : Screen("settings")
}
