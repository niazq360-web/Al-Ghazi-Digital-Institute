package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AcademySettings::class,
        Course::class,
        Student::class,
        FeePayment::class,
        Teacher::class,
        SalaryPayment::class,
        Expense::class,
        Attendance::class,
        CourseProgress::class,
        TestResult::class,
        Certificate::class,
        SmsTemplate::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao
    abstract fun courseDao(): CourseDao
    abstract fun studentDao(): StudentDao
    abstract fun feePaymentDao(): FeePaymentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun salaryPaymentDao(): SalaryPaymentDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun courseProgressDao(): CourseProgressDao
    abstract fun testResultDao(): TestResultDao
    abstract fun certificateDao(): CertificateDao
    abstract fun smsTemplateDao(): SmsTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "al_ghazi_academy.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            // Initial Settings
            database.settingsDao().insertOrUpdate(
                AcademySettings(
                    id = 1,
                    academyName = "Al Ghazi Digital Institute",
                    tagline = "LEARN • PRACTICE • GROW",
                    ownerName = "Engr. Al Ghazi",
                    phoneNumber = "+92 300 1234567",
                    whatsappNumber = "+92 300 1234567",
                    email = "info@alghazi.edu.pk",
                    address = "Main Boulevard, Al Ghazi Tech Complex, Pakistan",
                    website = "https://alghazi.edu.pk",
                    facebook = "facebook.com/alghazidigital",
                    instagram = "instagram.com/alghazidigital",
                    youtube = "youtube.com/@alghazidigital",
                    receiptPrefix = "REC-2026-",
                    nextReceiptSeq = 3,
                    certificatePrefix = "AG-CERT-2026-",
                    nextCertSeq = 2,
                    printerPaperWidth = "58mm",
                    adminPasswordHash = "admin123"
                )
            )

            // Initial 9 Courses
            val initialCourses = listOf(
                Course(
                    id = 1,
                    name = "Trading",
                    description = "Professional Crypto, Forex & Stocks Technical Analysis",
                    duration = "3 Months",
                    totalFee = 25000.0,
                    monthlyFee = 8500.0,
                    instructor = "Sir Bilal Ahmed",
                    classTiming = "04:00 PM - 06:00 PM",
                    batchName = "Batch-TR-2026",
                    startDate = "2026-09-01",
                    endDate = "2026-12-01",
                    isActive = true
                ),
                Course(
                    id = 2,
                    name = "Spoken English",
                    description = "Grammar, Accent, Public Speaking & Business Communication",
                    duration = "2 Months",
                    totalFee = 12000.0,
                    monthlyFee = 6000.0,
                    instructor = "Ma'am Ayesha Khan",
                    classTiming = "10:00 AM - 11:30 AM",
                    batchName = "Batch-ENG-Morning",
                    startDate = "2026-09-15",
                    endDate = "2026-11-15",
                    isActive = true
                ),
                Course(
                    id = 3,
                    name = "CIT",
                    description = "Certificate in Information Technology - Office Automation, Hardware & OS",
                    duration = "3 Months",
                    totalFee = 15000.0,
                    monthlyFee = 5000.0,
                    instructor = "Sir Tariq Mehmood",
                    classTiming = "11:30 AM - 01:00 PM",
                    batchName = "Batch-CIT-Morning",
                    startDate = "2026-09-01",
                    endDate = "2026-12-01",
                    isActive = true
                ),
                Course(
                    id = 4,
                    name = "DIT",
                    description = "Diploma in Information Technology - Full 1-Year Government Recognized Program",
                    duration = "1 Year",
                    totalFee = 45000.0,
                    monthlyFee = 4000.0,
                    instructor = "Prof. Al Ghazi & Team",
                    classTiming = "02:00 PM - 04:00 PM",
                    batchName = "Batch-DIT-Annual",
                    startDate = "2026-01-01",
                    endDate = "2026-12-31",
                    isActive = true
                ),
                Course(
                    id = 5,
                    name = "Digital Marketing",
                    description = "SEO, Meta Ads, Google Ads, Content Creation & Funnels",
                    duration = "3 Months",
                    totalFee = 20000.0,
                    monthlyFee = 7000.0,
                    instructor = "Sir Hamza Raza",
                    classTiming = "06:00 PM - 08:00 PM",
                    batchName = "Batch-DM-Evening",
                    startDate = "2026-09-10",
                    endDate = "2026-12-10",
                    isActive = true
                ),
                Course(
                    id = 6,
                    name = "Graphic Designing",
                    description = "Adobe Photoshop, Illustrator, Premiere Pro & Brand Identity",
                    duration = "3 Months",
                    totalFee = 18000.0,
                    monthlyFee = 6000.0,
                    instructor = "Sir Daniyal Ali",
                    classTiming = "12:00 PM - 02:00 PM",
                    batchName = "Batch-GD-Noon",
                    startDate = "2026-09-05",
                    endDate = "2026-12-05",
                    isActive = true
                ),
                Course(
                    id = 7,
                    name = "AI Tools",
                    description = "Generative AI, Prompt Engineering, Automation & AI Workflows",
                    duration = "2 Months",
                    totalFee = 22000.0,
                    monthlyFee = 11000.0,
                    instructor = "Engr. Al Ghazi",
                    classTiming = "08:00 PM - 09:30 PM",
                    batchName = "Batch-AI-Night",
                    startDate = "2026-09-20",
                    endDate = "2026-11-20",
                    isActive = true
                ),
                Course(
                    id = 8,
                    name = "Android App Development",
                    description = "Kotlin, Jetpack Compose, MVVM, Room & Firebase Cloud Architecture",
                    duration = "4 Months",
                    totalFee = 35000.0,
                    monthlyFee = 9000.0,
                    instructor = "Lead Android Architect",
                    classTiming = "03:00 PM - 05:00 PM",
                    batchName = "Batch-AND-Pro",
                    startDate = "2026-08-15",
                    endDate = "2026-12-15",
                    isActive = true
                ),
                Course(
                    id = 9,
                    name = "Web Development",
                    description = "HTML5, CSS3, JavaScript, React, Tailwind & Backend APIs",
                    duration = "4 Months",
                    totalFee = 30000.0,
                    monthlyFee = 8000.0,
                    instructor = "Sir Zeeshan Sheikh",
                    classTiming = "05:00 PM - 07:00 PM",
                    batchName = "Batch-WEB-Pro",
                    startDate = "2026-08-20",
                    endDate = "2026-12-20",
                    isActive = true
                )
            )
            database.courseDao().insertAll(initialCourses)

            // Initial SMS Templates
            val templates = listOf(
                SmsTemplate(
                    languageCode = "en",
                    templateName = "English Receipt SMS",
                    content = "Dear Parent,\nFee payment of Rs. {paid_amount} for {student_name} ({course_name}) has been received successfully.\nTotal Fee: Rs. {total_fee}\nTotal Paid: Rs. {previous_paid}\nRemaining: Rs. {remaining_fee}\nReceipt No: {receipt_no}\nThank you.\n{academy_name}"
                ),
                SmsTemplate(
                    languageCode = "ur",
                    templateName = "Urdu Receipt SMS",
                    content = "محترم والدین، السلام علیکم!\nآپ کے بچے {student_name} کی {course_name} کی فیس Rs. {paid_amount} ادا ہو گئی ہے۔\nکل فیس: Rs. {total_fee}\nکل ادا شدہ: Rs. {previous_paid}\nباقی فیس: Rs. {remaining_fee}\nرسید نمبر: {receipt_no}\n{academy_name} کا شکریہ۔"
                ),
                SmsTemplate(
                    languageCode = "sd",
                    templateName = "Sindhi Receipt SMS",
                    content = "محترم والدين، السلام عليڪم!\nتوهان جي ٻار {student_name} جي {course_name} جي فيس Rs. {paid_amount} ادا ٿي وئي آهي.\nڪل فيس: Rs. {total_fee}\nڪل ادا ٿيل: Rs. {previous_paid}\nباقي فيس: Rs. {remaining_fee}\nرسيد نمبر: {receipt_no}\n{academy_name} جو مهرباني."
                )
            )
            database.smsTemplateDao().insertAll(templates)

            // Initial Teachers / Faculty
            val teachers = listOf(
                Teacher(
                    id = 1,
                    name = "Sir Bilal Ahmed",
                    fatherOrHusbandName = "Ahmed Khan",
                    phone = "+92 312 9876543",
                    email = "bilal@alghazi.edu.pk",
                    qualification = "Chartered Financial Analyst (CFA)",
                    subjectOrCourse = "Trading",
                    monthlySalary = 65000.0,
                    joiningDate = "2025-06-01",
                    status = "Active",
                    address = "Defence Phase 5, Karachi"
                ),
                Teacher(
                    id = 2,
                    name = "Ma'am Ayesha Khan",
                    fatherOrHusbandName = "Nawaz Khan",
                    phone = "+92 333 4567890",
                    email = "ayesha@alghazi.edu.pk",
                    qualification = "M.A. English Linguistics",
                    subjectOrCourse = "Spoken English",
                    monthlySalary = 48000.0,
                    joiningDate = "2025-08-15",
                    status = "Active",
                    address = "Gulshan-e-Iqbal, Karachi"
                ),
                Teacher(
                    id = 3,
                    name = "Sir Daniyal Ali",
                    fatherOrHusbandName = "Ali Asghar",
                    phone = "+92 345 1122334",
                    email = "daniyal@alghazi.edu.pk",
                    qualification = "BFA Visual Communication",
                    subjectOrCourse = "Graphic Designing",
                    monthlySalary = 52000.0,
                    joiningDate = "2025-09-01",
                    status = "Active",
                    address = "North Nazimabad, Karachi"
                )
            )
            database.teacherDao().insertAll(teachers)

            // Initial Expenses (Rent, Utility, Salary, Marketing)
            val expenses = listOf(
                Expense(
                    id = 1,
                    expenseNo = "EXP-2026-0001",
                    title = "Campus Building Rent",
                    category = "Rent",
                    amount = 85000.0,
                    expenseDate = System.currentTimeMillis() - 86400000L * 4,
                    description = "Monthly campus lease payment",
                    paidTo = "Property Trust",
                    paymentMethod = "Bank Transfer"
                ),
                Expense(
                    id = 2,
                    expenseNo = "EXP-2026-0002",
                    title = "High Speed Fiber Internet",
                    category = "Internet & Tech",
                    amount = 12000.0,
                    expenseDate = System.currentTimeMillis() - 86400000L * 3,
                    description = "100Mbps dedicated lab internet",
                    paidTo = "StormFiber",
                    paymentMethod = "Online"
                ),
                Expense(
                    id = 3,
                    expenseNo = "EXP-2026-0003",
                    title = "K-Electric Commercial Bill",
                    category = "Electricity / Utilities",
                    amount = 34500.0,
                    expenseDate = System.currentTimeMillis() - 86400000L * 2,
                    description = "AC & computer labs power bill",
                    paidTo = "K-Electric",
                    paymentMethod = "Bank Transfer"
                ),
                Expense(
                    id = 4,
                    expenseNo = "EXP-2026-0004",
                    title = "Meta Social Media Ads",
                    category = "Marketing / Ads",
                    amount = 25000.0,
                    expenseDate = System.currentTimeMillis() - 86400000L,
                    description = "New admissions campaign Facebook/Instagram",
                    paidTo = "Meta Ads",
                    paymentMethod = "Credit Card"
                )
            )
            database.expenseDao().insertAll(expenses)

            // Initial Sample Students
            val students = listOf(
                Student(
                    id = 1,
                    studentId = "AG-2026-0001",
                    name = "Muhammad Hamza",
                    fatherName = "Tariq Mahmood",
                    gender = "Male",
                    dob = "2004-03-12",
                    cnicOrBForm = "42101-1234567-1",
                    studentMobile = "+92 301 2345678",
                    parentMobile = "+92 300 9876543",
                    whatsappNumber = "+92 301 2345678",
                    address = "House 12, Block 4, Clifton",
                    city = "Karachi",
                    photoUri = "",
                    courseId = 8,
                    courseName = "Android App Development",
                    batchName = "Batch-AND-Pro",
                    timing = "03:00 PM - 05:00 PM",
                    admissionDate = "2026-08-15",
                    courseFee = 35000.0,
                    discount = 3000.0,
                    finalFee = 32000.0,
                    paidFee = 20000.0,
                    remainingFee = 12000.0,
                    status = "Active",
                    notes = "Fast learner, working on final portfolio project."
                ),
                Student(
                    id = 2,
                    studentId = "AG-2026-0002",
                    name = "Zainab Fatima",
                    fatherName = "Rashid Ali",
                    gender = "Female",
                    dob = "2005-07-21",
                    cnicOrBForm = "42201-7654321-2",
                    studentMobile = "+92 334 5678901",
                    parentMobile = "+92 333 1122334",
                    whatsappNumber = "+92 334 5678901",
                    address = "A-45, Gulistan-e-Jauhar",
                    city = "Karachi",
                    photoUri = "",
                    courseId = 6,
                    courseName = "Graphic Designing",
                    batchName = "Batch-GD-Noon",
                    timing = "12:00 PM - 02:00 PM",
                    admissionDate = "2026-09-05",
                    courseFee = 18000.0,
                    discount = 0.0,
                    finalFee = 18000.0,
                    paidFee = 18000.0,
                    remainingFee = 0.0,
                    status = "Active",
                    notes = "Fee fully cleared."
                )
            )
            database.studentDao().insertAll(students)

            // Initial Fee Payments
            val payments = listOf(
                FeePayment(
                    id = 1,
                    receiptNo = "REC-2026-00001",
                    studentId = 1,
                    studentRollNo = "AG-2026-0001",
                    studentName = "Muhammad Hamza",
                    fatherName = "Tariq Mahmood",
                    courseName = "Android App Development",
                    batchName = "Batch-AND-Pro",
                    totalFee = 32000.0,
                    previousPaid = 0.0,
                    paidAmount = 20000.0,
                    newTotalPaid = 20000.0,
                    remainingFee = 12000.0,
                    paymentDate = System.currentTimeMillis() - 86400000L * 10,
                    paymentMethod = "Bank Transfer",
                    remarks = "1st Installment",
                    receivedBy = "Admin",
                    smsStatus = "Sent"
                ),
                FeePayment(
                    id = 2,
                    receiptNo = "REC-2026-00002",
                    studentId = 2,
                    studentRollNo = "AG-2026-0002",
                    studentName = "Zainab Fatima",
                    fatherName = "Rashid Ali",
                    courseName = "Graphic Designing",
                    batchName = "Batch-GD-Noon",
                    totalFee = 18000.0,
                    previousPaid = 0.0,
                    paidAmount = 18000.0,
                    newTotalPaid = 18000.0,
                    remainingFee = 0.0,
                    paymentDate = System.currentTimeMillis() - 86400000L * 5,
                    paymentMethod = "Cash",
                    remarks = "Lump sum complete payment",
                    receivedBy = "Admin",
                    smsStatus = "Sent"
                )
            )
            database.feePaymentDao().insertAll(payments)

            // Initial Certificate for demo/testing
            val cert = Certificate(
                id = 1,
                certificateNo = "AG-CERT-2026-001",
                studentId = 2,
                studentRollNo = "AG-2026-0002",
                studentName = "Zainab Fatima",
                fatherName = "Rashid Ali",
                courseName = "Graphic Designing",
                duration = "3 Months",
                startDate = "2026-06-01",
                completionDate = "2026-09-01",
                grade = "A+",
                issueDate = "2026-09-05",
                customDescription = "has demonstrated outstanding creativity, brand design proficiency, and completed all capstone projects with distinction.",
                directorName = "Engr. Al Ghazi",
                teacherName = "Sir Daniyal Ali"
            )
            database.certificateDao().insertCertificate(cert)
        }
    }
}
