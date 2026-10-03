package com.example.data.model

data class AppBackupData(
    val exportDate: Long = System.currentTimeMillis(),
    val academyName: String = "Al Ghazi Digital Institute",
    val version: Int = 1,
    val settings: AcademySettings? = null,
    val courses: List<Course> = emptyList(),
    val students: List<Student> = emptyList(),
    val payments: List<FeePayment> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val salaries: List<SalaryPayment> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val attendance: List<Attendance> = emptyList(),
    val testResults: List<TestResult> = emptyList(),
    val courseProgress: List<CourseProgress> = emptyList(),
    val certificates: List<Certificate> = emptyList()
)
