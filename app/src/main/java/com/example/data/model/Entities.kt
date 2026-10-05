package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "academy_settings")
data class AcademySettings(
    @PrimaryKey val id: Int = 1,
    val academyName: String = "Al Ghazi Digital Institute",
    val tagline: String = "LEARN • PRACTICE • GROW",
    val ownerName: String = "Prof. Al Ghazi",
    val phoneNumber: String = "+92 300 1234567",
    val whatsappNumber: String = "+92 300 1234567",
    val email: String = "info@alghazi.edu.pk",
    val address: String = "Main Campus, Al Ghazi Complex, Pakistan",
    val website: String = "www.alghazi.edu.pk",
    val facebook: String = "fb.com/alghazidigital",
    val instagram: String = "@alghazi_digital",
    val youtube: String = "youtube.com/@alghazidigital",
    val receiptPrefix: String = "REC-2026-",
    val nextReceiptSeq: Int = 1,
    val certificatePrefix: String = "AG-CERT-2026-",
    val nextCertSeq: Int = 1,
    val receiptFooterText: String = "Thank you for choosing Al Ghazi Digital Institute.",
    val printerPaperWidth: String = "58mm", // "58mm" or "80mm"
    val preferredSimSlot: Int = -1, // -1: System default, 0: SIM 1, 1: SIM 2
    val smsSimNumber: String = "",
    val smsEnabled: Boolean = true,
    val defaultLanguage: String = "en", // "en", "ur", "sd"
    val darkThemeEnabled: Boolean = true,
    val adminPasswordHash: String = "admin123", // default secure admin pass
    val sessionStartDate: Long = System.currentTimeMillis(),
    val sessionMonthsDuration: Int = 12, // 12-month record retention guarantee
    val sessionRenewalPrompted: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val cloudSyncEnabled: Boolean = true,
    val registeredMobile: String = "+92 300 1234567",
    val isAccountRegistered: Boolean = false
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val duration: String = "3 Months",
    val totalFee: Double = 15000.0,
    val monthlyFee: Double = 5000.0,
    val instructor: String = "Al Ghazi Faculty",
    val classTiming: String = "10:00 AM - 12:00 PM",
    val batchName: String = "Batch-2026-A",
    val startDate: String = "2026-01-15",
    val endDate: String = "2026-04-15",
    val isActive: Boolean = true
)

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String, // e.g. AG-2026-0001
    val name: String,
    val fatherName: String,
    val gender: String = "Male",
    val dob: String = "",
    val cnicOrBForm: String = "",
    val studentMobile: String = "",
    val parentMobile: String = "",
    val whatsappNumber: String = "",
    val address: String = "",
    val city: String = "Karachi",
    val photoUri: String = "",
    val courseId: Long = 0L,
    val courseName: String,
    val batchName: String = "Batch A",
    val timing: String = "10:00 AM - 12:00 PM",
    val admissionDate: String = "2026-10-01",
    val courseFee: Double = 15000.0,
    val discount: Double = 0.0,
    val finalFee: Double = 15000.0,
    val paidFee: Double = 0.0,
    val remainingFee: Double = 15000.0,
    val status: String = "Active", // "Active", "Completed", "Dropped"
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "fee_payments")
data class FeePayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNo: String, // e.g. REC-2026-00001
    val studentId: Long,
    val studentRollNo: String,
    val studentName: String,
    val fatherName: String,
    val courseName: String,
    val batchName: String,
    val totalFee: Double,
    val previousPaid: Double,
    val paidAmount: Double,
    val newTotalPaid: Double,
    val remainingFee: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Cash", // Cash, EasyPaisa, JazzCash, Bank Transfer, Online
    val remarks: String = "Monthly installment",
    val receivedBy: String = "Admin",
    val simUsed: String = "Default SIM",
    val smsStatus: String = "Pending" // "Sent", "Failed", "Pending", "Fallback SMS App", "Skipped"
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val fatherOrHusbandName: String = "",
    val phone: String,
    val email: String = "",
    val qualification: String = "BS Computer Science",
    val subjectOrCourse: String = "Android App Development",
    val monthlySalary: Double = 45000.0,
    val joiningDate: String = "2026-01-01",
    val status: String = "Active", // "Active", "On Leave", "Resigned"
    val address: String = ""
)

@Entity(tableName = "salary_payments")
data class SalaryPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val voucherNo: String, // e.g. SAL-2026-0001
    val teacherId: Long,
    val teacherName: String,
    val monthYear: String, // e.g. October 2026
    val salaryAmount: Double,
    val bonus: Double = 0.0,
    val deduction: Double = 0.0,
    val netPaid: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Cash",
    val remarks: String = "",
    val paidBy: String = "Director"
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expenseNo: String, // e.g. EXP-2026-0001
    val title: String,
    val category: String, // "Rent", "Electricity / Utilities", "Staff Salary", "Marketing / Ads", "Equipment & Maintenance", "Internet & Tech", "Refreshments / Tea", "Miscellaneous"
    val amount: Double,
    val expenseDate: Long = System.currentTimeMillis(),
    val description: String = "",
    val paidTo: String = "",
    val paymentMethod: String = "Cash"
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentRollNo: String,
    val studentName: String,
    val courseId: Long,
    val courseName: String,
    val batchName: String,
    val dateString: String, // "yyyy-MM-dd"
    val status: String, // "Present", "Absent", "Leave"
    val remarks: String = ""
)

@Entity(tableName = "course_progress")
data class CourseProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val courseId: Long,
    val courseName: String,
    val topicCompleted: String = "",
    val topicPending: String = "",
    val assignmentTitle: String = "",
    val assignmentMarks: Double = 0.0,
    val marksObtained: Double = 0.0,
    val progressPercentage: Int = 0, // 0 to 100
    val remarks: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_results")
data class TestResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testName: String, // e.g. Midterm Test 1
    val courseId: Long,
    val courseName: String,
    val studentId: Long,
    val studentName: String,
    val testDate: String = "2026-10-01",
    val totalMarks: Double = 100.0,
    val obtainedMarks: Double = 85.0,
    val percentage: Double = 85.0,
    val grade: String = "A+",
    val remarks: String = "Excellent performance"
)

@Entity(tableName = "certificates")
data class Certificate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val certificateNo: String, // e.g. AG-CERT-2026-001
    val studentId: Long,
    val studentRollNo: String,
    val studentName: String,
    val fatherName: String,
    val courseName: String,
    val duration: String,
    val startDate: String,
    val completionDate: String,
    val grade: String = "A+",
    val issueDate: String = "2026-10-01",
    val customDescription: String = "has successfully completed the comprehensive training program with exemplary dedication and outstanding performance.",
    val directorName: String = "Engr. Al Ghazi",
    val teacherName: String = "Lead Faculty Instructor"
)

@Entity(tableName = "sms_templates")
data class SmsTemplate(
    @PrimaryKey val languageCode: String, // "en", "ur", "sd"
    val templateName: String,
    val content: String
)
