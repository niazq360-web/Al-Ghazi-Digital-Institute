package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.AcademySettings
import com.example.data.model.Attendance
import com.example.data.model.Certificate
import com.example.data.model.Course
import com.example.data.model.CourseProgress
import com.example.data.model.Expense
import com.example.data.model.FeePayment
import com.example.data.model.SalaryPayment
import com.example.data.model.SmsTemplate
import com.example.data.model.Student
import com.example.data.model.Teacher
import com.example.data.model.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AcademyRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val settingsDao = db.settingsDao()
    private val courseDao = db.courseDao()
    private val studentDao = db.studentDao()
    private val feePaymentDao = db.feePaymentDao()
    private val teacherDao = db.teacherDao()
    private val salaryPaymentDao = db.salaryPaymentDao()
    private val expenseDao = db.expenseDao()
    private val attendanceDao = db.attendanceDao()
    private val courseProgressDao = db.courseProgressDao()
    private val testResultDao = db.testResultDao()
    private val certificateDao = db.certificateDao()
    private val smsTemplateDao = db.smsTemplateDao()

    val settingsFlow: Flow<AcademySettings?> = settingsDao.getSettingsFlow()
    val coursesFlow: Flow<List<Course>> = courseDao.getAllCoursesFlow()
    val activeCoursesFlow: Flow<List<Course>> = courseDao.getActiveCoursesFlow()
    val studentsFlow: Flow<List<Student>> = studentDao.getAllStudentsFlow()
    val paymentsFlow: Flow<List<FeePayment>> = feePaymentDao.getAllPaymentsFlow()
    val teachersFlow: Flow<List<Teacher>> = teacherDao.getAllTeachersFlow()
    val salariesFlow: Flow<List<SalaryPayment>> = salaryPaymentDao.getAllSalariesFlow()
    val expensesFlow: Flow<List<Expense>> = expenseDao.getAllExpensesFlow()
    val certificatesFlow: Flow<List<Certificate>> = certificateDao.getAllCertificatesFlow()
    val templatesFlow: Flow<List<SmsTemplate>> = smsTemplateDao.getAllTemplatesFlow()

    val studentCountFlow: Flow<Int> = studentDao.getStudentCountFlow()
    val activeStudentCountFlow: Flow<Int> = studentDao.getActiveStudentCountFlow()
    val courseCountFlow: Flow<Int> = courseDao.getCourseCountFlow()
    val totalCollectedFeeFlow: Flow<Double?> = feePaymentDao.getTotalCollectedFeeFlow()
    val totalPendingFeeFlow: Flow<Double?> = studentDao.getTotalPendingFeeFlow()
    val totalExpenseFlow: Flow<Double?> = expenseDao.getTotalExpenseAmountFlow()
    val totalSalaryFlow: Flow<Double?> = salaryPaymentDao.getTotalSalaryPaidFlow()

    fun searchStudents(query: String): Flow<List<Student>> {
        return studentDao.searchStudentsFlow(query.trim())
    }

    fun getStudentByIdFlow(id: Long): Flow<Student?> = studentDao.getStudentByIdFlow(id)
    suspend fun getStudentById(id: Long): Student? = studentDao.getStudentById(id)
    fun getPaymentsForStudent(studentId: Long): Flow<List<FeePayment>> =
        feePaymentDao.getPaymentsForStudentFlow(studentId)

    suspend fun getPaymentById(id: Long): FeePayment? = feePaymentDao.getPaymentById(id)

    suspend fun getSettings(): AcademySettings {
        return settingsDao.getSettings() ?: AcademySettings()
    }

    suspend fun updateSettings(settings: AcademySettings) {
        settingsDao.insertOrUpdate(settings)
    }

    // Courses
    suspend fun insertCourse(course: Course): Long = courseDao.insertCourse(course)
    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)
    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)

    // Students
    suspend fun registerStudent(student: Student): Long = withContext(Dispatchers.IO) {
        val existingStudents = studentDao.getAllStudentsFlow().first()
        val nextNumber = existingStudents.size + 1
        val finalRollNo = if (student.studentId.isBlank()) {
            val year = Calendar.getInstance().get(Calendar.YEAR)
            String.format(Locale.US, "AG-%d-%04d", year, nextNumber)
        } else {
            student.studentId
        }

        val calculatedRemaining = student.finalFee - student.paidFee
        val finalStudent = student.copy(
            studentId = finalRollNo,
            remainingFee = calculatedRemaining.coerceAtLeast(0.0)
        )
        val newId = studentDao.insertStudent(finalStudent)

        // If initial paid fee > 0, generate receipt automatically
        if (finalStudent.paidFee > 0.0) {
            collectFee(
                studentId = newId,
                paidAmount = finalStudent.paidFee,
                paymentMethod = "Cash",
                remarks = "Admission Fee",
                receivedBy = "Admin",
                isAdvanceAllowed = true
            )
        }

        newId
    }

    suspend fun updateStudent(student: Student) = withContext(Dispatchers.IO) {
        val calculatedRemaining = student.finalFee - student.paidFee
        val updated = student.copy(
            remainingFee = calculatedRemaining.coerceAtLeast(0.0)
        )
        studentDao.updateStudent(updated)
    }

    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)

    // Fee Collection
    suspend fun collectFee(
        studentId: Long,
        paidAmount: Double,
        paymentMethod: String,
        remarks: String,
        receivedBy: String,
        feeMonth: String = "",
        isAdvanceAllowed: Boolean = false
    ): FeePayment = withContext(Dispatchers.IO) {
        val student = studentDao.getStudentById(studentId)
            ?: throw IllegalArgumentException("Student not found")

        val currentSettings = getSettings()
        val seq = currentSettings.nextReceiptSeq
        val receiptNumber = String.format(Locale.US, "%s%05d", currentSettings.receiptPrefix, seq)

        val previousPaid = student.paidFee
        val newTotalPaid = previousPaid + paidAmount
        val newRemaining = (student.finalFee - newTotalPaid).coerceAtLeast(0.0)

        val resolvedMonth = if (feeMonth.isNotBlank()) feeMonth else SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())

        val payment = FeePayment(
            receiptNo = receiptNumber,
            studentId = student.id,
            studentRollNo = student.studentId,
            studentName = student.name,
            fatherName = student.fatherName,
            courseName = student.courseName,
            batchName = student.batchName,
            totalFee = student.finalFee,
            previousPaid = previousPaid,
            paidAmount = paidAmount,
            newTotalPaid = newTotalPaid,
            remainingFee = newRemaining,
            paymentDate = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            remarks = remarks,
            feeMonth = resolvedMonth,
            receivedBy = receivedBy,
            smsStatus = "Pending"
        )

        val paymentId = feePaymentDao.insertPayment(payment)
        // Update student fee totals
        studentDao.updateStudent(
            student.copy(
                paidFee = newTotalPaid,
                remainingFee = newRemaining
            )
        )

        // Increment settings sequence
        updateSettings(currentSettings.copy(nextReceiptSeq = seq + 1))

        payment.copy(id = paymentId)
    }

    suspend fun updatePayment(payment: FeePayment) = feePaymentDao.updatePayment(payment)
    suspend fun deletePayment(payment: FeePayment) = feePaymentDao.deletePayment(payment)

    // Teachers
    suspend fun insertTeacher(teacher: Teacher): Long = teacherDao.insertTeacher(teacher)
    suspend fun updateTeacher(teacher: Teacher) = teacherDao.updateTeacher(teacher)
    suspend fun deleteTeacher(teacher: Teacher) = teacherDao.deleteTeacher(teacher)

    // Salary & Automatic Expense Booking
    suspend fun payTeacherSalary(
        teacherId: Long,
        teacherName: String,
        monthYear: String,
        salaryAmount: Double,
        bonus: Double,
        deduction: Double,
        paymentMethod: String,
        remarks: String,
        paidBy: String
    ): SalaryPayment = withContext(Dispatchers.IO) {
        val netPaid = (salaryAmount + bonus - deduction).coerceAtLeast(0.0)
        val existingSalaries = salaryPaymentDao.getAllSalariesFlow().first()
        val nextSeq = existingSalaries.size + 1
        val voucher = String.format(Locale.US, "SAL-2026-%04d", nextSeq)

        val salary = SalaryPayment(
            voucherNo = voucher,
            teacherId = teacherId,
            teacherName = teacherName,
            monthYear = monthYear,
            salaryAmount = salaryAmount,
            bonus = bonus,
            deduction = deduction,
            netPaid = netPaid,
            paymentDate = System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            remarks = remarks,
            paidBy = paidBy
        )
        val id = salaryPaymentDao.insertSalary(salary)

        // Automatically record this salary payment in Expenses as "Staff Salary"
        val existingExpenses = expenseDao.getAllExpensesFlow().first()
        val expSeq = existingExpenses.size + 1
        val expNo = String.format(Locale.US, "EXP-2026-%04d", expSeq)
        expenseDao.insertExpense(
            Expense(
                expenseNo = expNo,
                title = "Salary: $teacherName ($monthYear)",
                category = "Staff Salary",
                amount = netPaid,
                expenseDate = System.currentTimeMillis(),
                description = "Teacher Salary $voucher. Bonus: $bonus, Deduction: $deduction. $remarks",
                paidTo = teacherName,
                paymentMethod = paymentMethod
            )
        )

        salary.copy(id = id)
    }

    // Expenses
    suspend fun insertExpense(
        title: String,
        category: String,
        amount: Double,
        description: String,
        paidTo: String,
        paymentMethod: String
    ): Expense = withContext(Dispatchers.IO) {
        val existingExpenses = expenseDao.getAllExpensesFlow().first()
        val expSeq = existingExpenses.size + 1
        val expNo = String.format(Locale.US, "EXP-2026-%04d", expSeq)
        val expense = Expense(
            expenseNo = expNo,
            title = title,
            category = category,
            amount = amount,
            expenseDate = System.currentTimeMillis(),
            description = description,
            paidTo = paidTo,
            paymentMethod = paymentMethod
        )
        val id = expenseDao.insertExpense(expense)
        expense.copy(id = id)
    }

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    // Attendance
    fun getAttendanceForDateAndCourse(dateString: String, courseId: Long): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForDateAndCourseFlow(dateString, courseId)

    fun getAttendanceForDate(dateString: String): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForDateFlow(dateString)

    fun getAttendanceForStudent(studentId: Long): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForStudentFlow(studentId)

    suspend fun saveAttendanceList(records: List<Attendance>) =
        attendanceDao.insertAll(records)

    // Course Progress
    fun getCourseProgressForStudent(studentId: Long): Flow<CourseProgress?> =
        courseProgressDao.getProgressForStudentFlow(studentId)

    suspend fun saveCourseProgress(progress: CourseProgress) =
        courseProgressDao.insertOrUpdate(progress)

    // Tests / Results
    fun getTestResultsForStudent(studentId: Long): Flow<List<TestResult>> =
        testResultDao.getResultsForStudentFlow(studentId)

    val allTestResultsFlow: Flow<List<TestResult>> = testResultDao.getAllResultsFlow()

    suspend fun insertTestResult(testResult: TestResult) =
        testResultDao.insertResult(testResult)

    suspend fun deleteTestResult(testResult: TestResult) =
        testResultDao.deleteResult(testResult)

    // Certificates
    suspend fun issueCertificate(
        studentId: Long,
        studentRollNo: String,
        studentName: String,
        fatherName: String,
        courseName: String,
        duration: String,
        startDate: String,
        completionDate: String,
        grade: String,
        issueDate: String,
        customDescription: String,
        directorName: String,
        teacherName: String
    ): Certificate = withContext(Dispatchers.IO) {
        val settings = getSettings()
        val seq = settings.nextCertSeq
        val certNo = String.format(Locale.US, "%s%04d", settings.certificatePrefix, seq)
        val cert = Certificate(
            certificateNo = certNo,
            studentId = studentId,
            studentRollNo = studentRollNo,
            studentName = studentName,
            fatherName = fatherName,
            courseName = courseName,
            duration = duration,
            startDate = startDate,
            completionDate = completionDate,
            grade = grade,
            issueDate = issueDate,
            customDescription = customDescription,
            directorName = directorName,
            teacherName = teacherName
        )
        val id = certificateDao.insertCertificate(cert)
        updateSettings(settings.copy(nextCertSeq = seq + 1))
        cert.copy(id = id)
    }

    suspend fun deleteCertificate(certificate: Certificate) =
        certificateDao.deleteCertificate(certificate)

    // SMS Templates
    suspend fun getTemplate(languageCode: String): SmsTemplate? =
        smsTemplateDao.getTemplate(languageCode)

    suspend fun saveTemplate(template: SmsTemplate) =
        smsTemplateDao.insertOrUpdate(template)

    // 12-Month Session Renewal & Retention
    suspend fun extendSession12Months() = withContext(Dispatchers.IO) {
        val current = getSettings()
        val updated = current.copy(
            sessionStartDate = System.currentTimeMillis(),
            sessionMonthsDuration = current.sessionMonthsDuration + 12,
            sessionRenewalPrompted = false
        )
        updateSettings(updated)
    }

    // Export complete institute data to JSON format
    suspend fun exportDataToJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("academyName", "Al Ghazi Digital Institute")
        root.put("exportDate", System.currentTimeMillis())
        root.put("version", 1)

        val settings = getSettings()
        val settingsObj = JSONObject().apply {
            put("academyName", settings.academyName)
            put("tagline", settings.tagline)
            put("ownerName", settings.ownerName)
            put("phoneNumber", settings.phoneNumber)
            put("whatsappNumber", settings.whatsappNumber)
            put("email", settings.email)
            put("address", settings.address)
            put("website", settings.website)
        }
        root.put("settings", settingsObj)

        val courses = courseDao.getAllCoursesFlow().first()
        val coursesArr = JSONArray()
        courses.forEach { c ->
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("description", c.description)
                put("duration", c.duration)
                put("totalFee", c.totalFee)
                put("monthlyFee", c.monthlyFee)
                put("instructor", c.instructor)
                put("classTiming", c.classTiming)
                put("batchName", c.batchName)
            }
            coursesArr.put(obj)
        }
        root.put("courses", coursesArr)

        val students = studentDao.getAllStudentsFlow().first()
        val studentsArr = JSONArray()
        students.forEach { s ->
            val obj = JSONObject().apply {
                put("studentId", s.studentId)
                put("name", s.name)
                put("fatherName", s.fatherName)
                put("gender", s.gender)
                put("studentMobile", s.studentMobile)
                put("courseName", s.courseName)
                put("batchName", s.batchName)
                put("finalFee", s.finalFee)
                put("paidFee", s.paidFee)
                put("remainingFee", s.remainingFee)
                put("status", s.status)
            }
            studentsArr.put(obj)
        }
        root.put("students", studentsArr)

        val payments = feePaymentDao.getAllPaymentsFlow().first()
        val paymentsArr = JSONArray()
        payments.forEach { p ->
            val obj = JSONObject().apply {
                put("receiptNo", p.receiptNo)
                put("studentRollNo", p.studentRollNo)
                put("studentName", p.studentName)
                put("paidAmount", p.paidAmount)
                put("paymentDate", p.paymentDate)
                put("paymentMethod", p.paymentMethod)
                put("remainingFee", p.remainingFee)
            }
            paymentsArr.put(obj)
        }
        root.put("payments", paymentsArr)

        val teachers = teacherDao.getAllTeachersFlow().first()
        val teachersArr = JSONArray()
        teachers.forEach { t ->
            val obj = JSONObject().apply {
                put("name", t.name)
                put("phone", t.phone)
                put("subjectOrCourse", t.subjectOrCourse)
                put("monthlySalary", t.monthlySalary)
            }
            teachersArr.put(obj)
        }
        root.put("teachers", teachersArr)

        val expenses = expenseDao.getAllExpensesFlow().first()
        val expensesArr = JSONArray()
        expenses.forEach { e ->
            val obj = JSONObject().apply {
                put("expenseNo", e.expenseNo)
                put("title", e.title)
                put("category", e.category)
                put("amount", e.amount)
                put("expenseDate", e.expenseDate)
                put("paidTo", e.paidTo)
            }
            expensesArr.put(obj)
        }
        root.put("expenses", expensesArr)

        root.toString(2)
    }

    // Import from JSON format
    suspend fun importDataFromJson(jsonStr: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonStr)
            if (root.has("students")) {
                val sArr = root.getJSONArray("students")
                for (i in 0 until sArr.length()) {
                    val s = sArr.getJSONObject(i)
                    val rollNo = s.optString("studentId", "AG-2026-9999")
                    val existing = studentDao.getStudentByRollNo(rollNo)
                    if (existing == null) {
                        studentDao.insertStudent(
                            Student(
                                studentId = rollNo,
                                name = s.optString("name", "Student"),
                                fatherName = s.optString("fatherName", ""),
                                courseName = s.optString("courseName", "General"),
                                finalFee = s.optDouble("finalFee", 10000.0),
                                paidFee = s.optDouble("paidFee", 0.0),
                                remainingFee = s.optDouble("remainingFee", 10000.0)
                            )
                        )
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun clearAllRecords() = withContext(Dispatchers.IO) {
        studentDao.deleteAllStudents()
        feePaymentDao.deleteAllPayments()
        expenseDao.deleteAllExpenses()
        attendanceDao.deleteAllAttendance()
        courseProgressDao.deleteAllProgress()
        testResultDao.deleteAllTestResults()
        certificateDao.deleteAllCertificates()
        salaryPaymentDao.deleteAllSalaries()

        // Clean up any uploaded student photos from internal storage
        try {
            val photoDir = java.io.File(context.filesDir, "student_photos")
            if (photoDir.exists()) {
                photoDir.deleteRecursively()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val s = getSettings()
        updateSettings(s.copy(nextReceiptSeq = 1, nextCertSeq = 1))
    }
}
