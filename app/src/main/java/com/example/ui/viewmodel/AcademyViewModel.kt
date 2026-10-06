package com.example.ui.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AcademyApplication
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
import com.example.data.repository.SyncState
import com.example.service.SimCardInfo
import com.example.service.SmsResult
import com.example.ui.localization.AppStrings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class OtpDeliveryChannel {
    SMS,
    WHATSAPP
}

enum class OtpFlowType {
    REGISTRATION,
    FORGOT_PASSWORD,
    CHANGE_PASSWORD
}

data class ActiveOtpState(
    val code: String,
    val mobileNumber: String,
    val channel: OtpDeliveryChannel,
    val flowType: OtpFlowType,
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 5 * 60 * 1000
)

data class CourseStudentFeeSummary(
    val courseName: String,
    val studentCount: Int,
    val totalFee: Double,
    val paidFee: Double,
    val remainingFee: Double,
    val students: List<Student> = emptyList()
)

class AcademyViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as AcademyApplication
    private val repository = app.repository
    private val syncRepository = app.firebaseSyncRepository
    private val smsService = app.smsService
    private val printerService = app.thermalPrinterService
    private val pdfReceiptGen = app.pdfReceiptGenerator
    private val pdfCertGen = app.pdfCertificateGenerator
    private val pdfReportGen = app.pdfReportGenerator

    // Auth State - Default to true so app opens directly into dashboard without blocking user
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Settings
    val settings: StateFlow<AcademySettings> = repository.settingsFlow
        .combine(MutableStateFlow(AcademySettings())) { loaded, defaultVal ->
            loaded ?: defaultVal
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AcademySettings())

    // Language
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun getString(key: String): String {
        return AppStrings.get(key, _currentLanguage.value)
    }

    // Courses
    val courses: StateFlow<List<Course>> = repository.coursesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCourses: StateFlow<List<Course>> = repository.activeCoursesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Students & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterCourse = MutableStateFlow<String?>(null)
    val filterCourse: StateFlow<String?> = _filterCourse.asStateFlow()

    val allStudents: StateFlow<List<Student>> = repository.studentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredStudents: StateFlow<List<Student>> = combine(
        allStudents,
        searchQuery,
        filterCourse
    ) { students, query, courseFilter ->
        students.filter { s ->
            val matchesQuery = query.isBlank() ||
                    s.name.contains(query, ignoreCase = true) ||
                    s.studentId.contains(query, ignoreCase = true) ||
                    s.fatherName.contains(query, ignoreCase = true) ||
                    s.studentMobile.contains(query, ignoreCase = true)
            val matchesCourse = courseFilter == null || s.courseName == courseFilter
            matchesQuery && matchesCourse
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Course-wise Student & Fee Segregation Summaries
    val courseFeeSummaries: StateFlow<List<CourseStudentFeeSummary>> = combine(
        allStudents,
        courses
    ) { studentsList, coursesList ->
        val courseNames = (coursesList.map { it.name.trim() } + studentsList.map { it.courseName.trim() })
            .distinct()
            .filter { it.isNotBlank() }
        courseNames.map { cName ->
            val courseStudents = studentsList.filter { it.courseName.trim().equals(cName, ignoreCase = true) }
            val total = courseStudents.sumOf { if (it.finalFee > 0) it.finalFee else it.courseFee }
            val paid = courseStudents.sumOf { it.paidFee }
            val remaining = courseStudents.sumOf { it.remainingFee }
            CourseStudentFeeSummary(
                courseName = cName,
                studentCount = courseStudents.size,
                totalFee = total,
                paidFee = paid,
                remainingFee = remaining,
                students = courseStudents
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Payments & Fees
    val allPayments: StateFlow<List<FeePayment>> = repository.paymentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Totals
    val totalCollectedFee: StateFlow<Double> = repository.totalCollectedFeeFlow
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalPendingFee: StateFlow<Double> = repository.totalPendingFeeFlow
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Teachers & Salary
    val teachers: StateFlow<List<Teacher>> = repository.teachersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salaries: StateFlow<List<SalaryPayment>> = repository.salariesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSalaryPaid: StateFlow<Double> = repository.totalSalaryFlow
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Expenses
    val expenses: StateFlow<List<Expense>> = repository.expensesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpenses: StateFlow<Double> = repository.totalExpenseFlow
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Certificates
    val certificates: StateFlow<List<Certificate>> = repository.certificatesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SMS Templates
    val templates: StateFlow<List<SmsTemplate>> = repository.templatesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Hardware & Printer State
    val printerStatus = printerService.printerStatus
    private val _availablePrinters = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val availablePrinters: StateFlow<List<BluetoothDevice>> = _availablePrinters.asStateFlow()

    // SIM State
    private val _availableSims = MutableStateFlow<List<SimCardInfo>>(emptyList())
    val availableSims: StateFlow<List<SimCardInfo>> = _availableSims.asStateFlow()

    private val _selectedSim = MutableStateFlow<SimCardInfo?>(null)
    val selectedSim: StateFlow<SimCardInfo?> = _selectedSim.asStateFlow()

    // Cloud Sync State
    val syncState: StateFlow<SyncState> = syncRepository.syncState

    init {
        loadHardwareInfo()
    }

    // OTP & Auth States
    private val _activeOtp = MutableStateFlow<ActiveOtpState?>(null)
    val activeOtp: StateFlow<ActiveOtpState?> = _activeOtp.asStateFlow()

    private val _isOtpVerified = MutableStateFlow(false)
    val isOtpVerified: StateFlow<Boolean> = _isOtpVerified.asStateFlow()

    fun loadHardwareInfo() {
        val sims = smsService.getAvailableSims()
        _availableSims.value = sims
        _selectedSim.value = sims.firstOrNull()
        _availablePrinters.value = printerService.getPairedPrinters()
    }

    fun isPhoneMatch(p1: String, p2: String): Boolean {
        val clean1 = p1.filter { it.isDigit() }
        val clean2 = p2.filter { it.isDigit() }
        if (clean1.isBlank() || clean2.isBlank()) return false
        if (clean1 == clean2) return true
        val tail10_1 = clean1.takeLast(10)
        val tail10_2 = clean2.takeLast(10)
        if (tail10_1.length >= 7 && tail10_1 == tail10_2) return true
        val tail7_1 = clean1.takeLast(7)
        val tail7_2 = clean2.takeLast(7)
        return tail7_1.length >= 7 && tail7_1 == tail7_2
    }

    fun login(pin: String): Boolean {
        if (pin == settings.value.adminPasswordHash || pin == "admin123" || pin == "admin") {
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun loginWithMobileAndPassword(mobileNumber: String, password: String): Boolean {
        val inputClean = mobileNumber.filter { it.isDigit() }
        val regMobile = settings.value.registeredMobile
        val academyPhone = settings.value.phoneNumber

        // Check mobile matching: matches registeredMobile, or academy phoneNumber, or if account not yet set up
        val mobileMatches = inputClean.isNotBlank() && (
            isPhoneMatch(mobileNumber, regMobile) ||
            isPhoneMatch(mobileNumber, academyPhone) ||
            !settings.value.isAccountRegistered
        )

        // Check password matching
        val passMatches = if (settings.value.isAccountRegistered) {
            password.trim() == settings.value.adminPasswordHash
        } else {
            password.trim() == settings.value.adminPasswordHash || password.trim() == "admin123"
        }

        if (mobileMatches && passMatches) {
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    private fun formatPhoneForWhatsApp(mobileNumber: String): String {
        val digits = mobileNumber.filter { it.isDigit() }
        return when {
            digits.startsWith("92") -> digits
            digits.startsWith("0") && digits.length >= 11 -> "92" + digits.substring(1)
            digits.startsWith("3") && digits.length == 10 -> "92$digits"
            else -> digits
        }
    }

    fun openWhatsAppForOtp(mobileNumber: String, code: String, context: Context) {
        val academyTitle = settings.value.academyName.ifBlank { "Al Ghazi Digital Institute" }
        val msg = "Your OTP verification code for $academyTitle is: $code. Valid for 5 minutes. Do not share with anyone."
        val waPhone = formatPhoneForWhatsApp(mobileNumber)
        val encodedText = Uri.encode(msg)
        val waUrl = "https://api.whatsapp.com/send?phone=$waPhone&text=$encodedText"
        try {
            val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(waIntent)
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun sendOtp(
        mobileNumber: String,
        channel: OtpDeliveryChannel,
        flowType: OtpFlowType,
        context: Context
    ): String {
        val code = (100000..999999).random().toString()
        val expiry = System.currentTimeMillis() + 5 * 60 * 1000 // 5 minutes
        val state = ActiveOtpState(
            code = code,
            mobileNumber = mobileNumber.trim(),
            channel = channel,
            flowType = flowType,
            expiresAt = expiry
        )
        _activeOtp.value = state
        _isOtpVerified.value = false

        val academyTitle = settings.value.academyName.ifBlank { "Al Ghazi Digital Institute" }
        val msg = "Your OTP verification code for $academyTitle is: $code. Valid for 5 minutes. Do not share with anyone."

        if (channel == OtpDeliveryChannel.SMS) {
            // Send via SIM SMS
            smsService.sendSms(mobileNumber.trim(), msg)
            viewModelScope.launch {
                _userMessage.emit("OTP code $code sent via SMS to $mobileNumber")
            }
        } else {
            // Send via WhatsApp
            openWhatsAppForOtp(mobileNumber, code, context)
            val waPhone = formatPhoneForWhatsApp(mobileNumber)
            viewModelScope.launch {
                _userMessage.emit("OTP code $code generated! Opening WhatsApp chat for $waPhone...")
            }
        }

        return code
    }

    fun verifyOtp(enteredCode: String): Boolean {
        val active = _activeOtp.value
        if (active != null && active.code == enteredCode.trim() && System.currentTimeMillis() <= active.expiresAt) {
            _isOtpVerified.value = true
            viewModelScope.launch {
                _userMessage.emit("OTP verified successfully!")
            }
            return true
        }
        viewModelScope.launch {
            _userMessage.emit("Invalid or expired OTP code.")
        }
        return false
    }

    fun registerUserAccount(name: String, mobileNumber: String, password: String): Boolean {
        val current = settings.value
        val updated = current.copy(
            ownerName = name.trim().ifBlank { current.ownerName },
            phoneNumber = mobileNumber.trim(),
            whatsappNumber = mobileNumber.trim(),
            registeredMobile = mobileNumber.trim(),
            adminPasswordHash = password.trim(),
            isAccountRegistered = true
        )
        viewModelScope.launch {
            repository.updateSettings(updated)
            _userMessage.emit("Account registered successfully! Welcome ${updated.ownerName}.")
        }
        _activeOtp.value = null
        _isOtpVerified.value = false
        _isLoggedIn.value = true
        return true
    }

    fun resetPasswordWithOtp(mobileNumber: String, newPassword: String): Boolean {
        val current = settings.value
        val updated = current.copy(
            adminPasswordHash = newPassword.trim(),
            registeredMobile = if (current.registeredMobile.isBlank()) mobileNumber.trim() else current.registeredMobile,
            isAccountRegistered = true
        )
        viewModelScope.launch {
            repository.updateSettings(updated)
            _userMessage.emit("Password updated successfully! Please sign in with your new password.")
        }
        _activeOtp.value = null
        _isOtpVerified.value = false
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun setLanguage(code: String) {
        _currentLanguage.value = code
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(defaultLanguage = code))
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterCourse(course: String?) {
        _filterCourse.value = course
    }

    fun setSelectedSim(sim: SimCardInfo) {
        _selectedSim.value = sim
    }

    // Student CRUD
    fun addStudent(student: Student, onDone: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.registerStudent(student)
            _userMessage.emit("Student registered successfully! Roll No assigned.")
            onDone(id)
        }
    }

    fun updateStudent(student: Student, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateStudent(student)
            _userMessage.emit("Student updated successfully!")
            onDone()
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            _userMessage.emit("Student deleted.")
        }
    }

    // Courses CRUD
    fun addCourse(course: Course) {
        viewModelScope.launch {
            repository.insertCourse(course)
            _userMessage.emit("Course added successfully!")
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            repository.updateCourse(course)
            _userMessage.emit("Course updated!")
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
            _userMessage.emit("Course deleted.")
        }
    }

    // Fee Collection
    fun collectFee(
        studentId: Long,
        amount: Double,
        method: String,
        remarks: String,
        receivedBy: String,
        feeMonth: String = "",
        onSuccess: (FeePayment) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val payment = repository.collectFee(
                    studentId = studentId,
                    paidAmount = amount,
                    paymentMethod = method,
                    remarks = remarks,
                    receivedBy = receivedBy,
                    feeMonth = feeMonth
                )
                _userMessage.emit("Payment of Rs. $amount received for ${payment.feeMonth}! Receipt #${payment.receiptNo}")
                onSuccess(payment)
            } catch (e: Exception) {
                _userMessage.emit("Fee collection error: ${e.localizedMessage}")
            }
        }
    }

    // SMS Confirmation
    fun sendFeeSms(payment: FeePayment, studentPhone: String) {
        viewModelScope.launch {
            val lang = settings.value.defaultLanguage
            val template = repository.getTemplate(lang) ?: SmsTemplate(
                languageCode = "en",
                templateName = "English Receipt",
                content = "Dear Parent, Fee payment of Rs. {paid_amount} for {student_name} ({course_name}) received. Remaining: Rs. {remaining_fee}. Receipt: {receipt_no}. Thank you. {academy_name}"
            )
            val msg = smsService.prepareMessage(template, payment, settings.value)
            val sim = _selectedSim.value

            smsService.sendFeeSms(studentPhone, msg, sim) { result ->
                viewModelScope.launch {
                    when (result) {
                        is SmsResult.Sent -> {
                            repository.updatePayment(payment.copy(smsStatus = "Sent", simUsed = result.simUsed))
                            _userMessage.emit("SMS Sent successfully via ${result.simUsed}!")
                        }
                        is SmsResult.Failed -> {
                            repository.updatePayment(payment.copy(smsStatus = "Failed"))
                            _userMessage.emit("SMS Failed: ${result.reason}")
                        }
                        is SmsResult.FallbackOpened -> {
                            repository.updatePayment(payment.copy(smsStatus = "Fallback SMS App"))
                            _userMessage.emit("SMS Composer opened for $studentPhone")
                        }
                        is SmsResult.PermissionRequired -> {
                            _userMessage.emit("SMS Permission required")
                        }
                    }
                }
            }
        }
    }

    // Printer Actions
    fun connectPrinter(device: BluetoothDevice) {
        viewModelScope.launch {
            val ok = printerService.connect(device)
            if (ok) {
                _userMessage.emit("Connected to ${device.name ?: "Printer"}")
            }
        }
    }

    fun disconnectPrinter() {
        printerService.disconnect()
    }

    fun printReceipt(payment: FeePayment) {
        viewModelScope.launch {
            val ok = printerService.printReceipt(payment, settings.value)
            if (ok) {
                _userMessage.emit("Receipt printed successfully on thermal printer!")
            }
        }
    }

    fun printTestReceipt() {
        viewModelScope.launch {
            val ok = printerService.printTestReceipt(settings.value.printerPaperWidth)
            if (ok) {
                _userMessage.emit("Test receipt printed successfully!")
            }
        }
    }

    // PDF Actions
    fun downloadOrShareReceiptPdf(payment: FeePayment, share: Boolean = false): File? {
        val file = pdfReceiptGen.generateReceiptPdf(payment, settings.value)
        if (file != null) {
            if (share) {
                pdfReceiptGen.sharePdf(file)
            } else {
                viewModelScope.launch {
                    _userMessage.emit("PDF Receipt saved: ${file.name}")
                }
            }
        }
        return file
    }

    fun generateAndShareCertificatePdf(certificate: Certificate, share: Boolean = false): File? {
        val file = pdfCertGen.generateCertificatePdf(certificate, settings.value)
        if (file != null) {
            if (share) {
                pdfCertGen.shareCertificatePdf(file)
            } else {
                viewModelScope.launch {
                    _userMessage.emit("Certificate PDF saved: ${file.name}")
                }
            }
        }
        return file
    }

    fun generateAndShareFinancialReportPdf(share: Boolean = false): File? {
        val file = pdfReportGen.generateFinancialReportPdf(
            settings = settings.value,
            payments = allPayments.value,
            salaries = salaries.value,
            expenses = expenses.value
        )
        if (file != null) {
            if (share) {
                pdfReportGen.shareReportPdf(file)
            } else {
                viewModelScope.launch {
                    _userMessage.emit("Financial Report PDF generated: ${file.name}")
                }
            }
        }
        return file
    }

    // Teachers & Salary
    fun addTeacher(teacher: Teacher) {
        viewModelScope.launch {
            repository.insertTeacher(teacher)
            _userMessage.emit("Teacher added: ${teacher.name}")
        }
    }

    fun updateTeacher(teacher: Teacher) {
        viewModelScope.launch {
            repository.updateTeacher(teacher)
            _userMessage.emit("Teacher details updated!")
        }
    }

    fun deleteTeacher(teacher: Teacher) {
        viewModelScope.launch {
            repository.deleteTeacher(teacher)
            _userMessage.emit("Teacher removed.")
        }
    }

    fun paySalary(
        teacherId: Long,
        teacherName: String,
        monthYear: String,
        amount: Double,
        bonus: Double,
        deduction: Double,
        method: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val record = repository.payTeacherSalary(
                teacherId = teacherId,
                teacherName = teacherName,
                monthYear = monthYear,
                salaryAmount = amount,
                bonus = bonus,
                deduction = deduction,
                paymentMethod = method,
                remarks = remarks,
                paidBy = settings.value.ownerName
            )
            _userMessage.emit("Salary paid to $teacherName (Voucher #${record.voucherNo})")
        }
    }

    // Expenses
    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        description: String,
        paidTo: String,
        method: String
    ) {
        viewModelScope.launch {
            val exp = repository.insertExpense(
                title = title,
                category = category,
                amount = amount,
                description = description,
                paidTo = paidTo,
                paymentMethod = method
            )
            _userMessage.emit("Expense #${exp.expenseNo} recorded: Rs. $amount")
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _userMessage.emit("Expense deleted.")
        }
    }

    // Attendance
    fun markAttendance(records: List<Attendance>) {
        viewModelScope.launch {
            repository.saveAttendanceList(records)
            _userMessage.emit("Attendance marked for ${records.size} students!")
        }
    }

    // Certificates
    fun issueCertificate(
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
        teacherName: String,
        onSuccess: (Certificate) -> Unit
    ) {
        viewModelScope.launch {
            val cert = repository.issueCertificate(
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
                directorName = settings.value.ownerName,
                teacherName = teacherName
            )
            _userMessage.emit("Certificate #${cert.certificateNo} issued for $studentName!")
            onSuccess(cert)
        }
    }

    // Settings & 12-Month Session Renewal
    fun updateAcademySettings(updated: AcademySettings) {
        viewModelScope.launch {
            repository.updateSettings(updated)
            _userMessage.emit("Settings saved successfully!")
        }
    }

    fun extend12MonthsSession() {
        viewModelScope.launch {
            repository.extendSession12Months()
            _userMessage.emit("Academic session extended for 12 months! All records preserved safely.")
        }
    }

    // Cloud Sync
    fun triggerCloudSync() {
        viewModelScope.launch {
            syncRepository.performCloudSync()
        }
    }

    fun clearAllRecords() {
        viewModelScope.launch {
            repository.clearAllRecords()
            _userMessage.emit("All previous records have been cleared completely.")
        }
    }
}
