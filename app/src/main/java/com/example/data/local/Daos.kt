package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM academy_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AcademySettings?>

    @Query("SELECT * FROM academy_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AcademySettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AcademySettings)
}

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY id DESC")
    fun getAllCoursesFlow(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveCoursesFlow(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Long): Course?

    @Query("SELECT COUNT(*) FROM courses")
    fun getCourseCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY id DESC")
    fun getAllStudentsFlow(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentByIdFlow(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE studentId = :studentRollNo LIMIT 1")
    suspend fun getStudentByRollNo(studentRollNo: String): Student?

    @Query("SELECT * FROM students WHERE courseId = :courseId ORDER BY name ASC")
    fun getStudentsByCourseFlow(courseId: Long): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students WHERE status = 'Active'")
    fun getActiveStudentCountFlow(): Flow<Int>

    @Query("SELECT SUM(remainingFee) FROM students")
    fun getTotalPendingFeeFlow(): Flow<Double?>

    @Query("""
        SELECT * FROM students 
        WHERE name LIKE '%' || :query || '%' 
           OR fatherName LIKE '%' || :query || '%' 
           OR studentId LIKE '%' || :query || '%' 
           OR studentMobile LIKE '%' || :query || '%'
           OR courseName LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchStudentsFlow(query: String): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}

@Dao
interface FeePaymentDao {
    @Query("SELECT * FROM fee_payments ORDER BY id DESC")
    fun getAllPaymentsFlow(): Flow<List<FeePayment>>

    @Query("SELECT * FROM fee_payments WHERE studentId = :studentId ORDER BY id DESC")
    fun getPaymentsForStudentFlow(studentId: Long): Flow<List<FeePayment>>

    @Query("SELECT * FROM fee_payments WHERE id = :id LIMIT 1")
    suspend fun getPaymentById(id: Long): FeePayment?

    @Query("SELECT * FROM fee_payments WHERE receiptNo = :receiptNo LIMIT 1")
    suspend fun getPaymentByReceiptNo(receiptNo: String): FeePayment?

    @Query("SELECT SUM(paidAmount) FROM fee_payments")
    fun getTotalCollectedFeeFlow(): Flow<Double?>

    @Query("SELECT * FROM fee_payments WHERE paymentDate >= :startOfDayTimestamp ORDER BY id DESC")
    fun getPaymentsSinceFlow(startOfDayTimestamp: Long): Flow<List<FeePayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: FeePayment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(payments: List<FeePayment>)

    @Update
    suspend fun updatePayment(payment: FeePayment)

    @Delete
    suspend fun deletePayment(payment: FeePayment)

    @Query("DELETE FROM fee_payments")
    suspend fun deleteAllPayments()
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY id DESC")
    fun getAllTeachersFlow(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
    suspend fun getTeacherById(id: Long): Teacher?

    @Query("SELECT COUNT(*) FROM teachers")
    fun getTeacherCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teachers: List<Teacher>)

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    @Query("DELETE FROM teachers")
    suspend fun deleteAllTeachers()
}

@Dao
interface SalaryPaymentDao {
    @Query("SELECT * FROM salary_payments ORDER BY id DESC")
    fun getAllSalariesFlow(): Flow<List<SalaryPayment>>

    @Query("SELECT * FROM salary_payments WHERE teacherId = :teacherId ORDER BY id DESC")
    fun getSalariesForTeacherFlow(teacherId: Long): Flow<List<SalaryPayment>>

    @Query("SELECT SUM(netPaid) FROM salary_payments")
    fun getTotalSalaryPaidFlow(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalary(salary: SalaryPayment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(salaries: List<SalaryPayment>)

    @Delete
    suspend fun deleteSalary(salary: SalaryPayment)

    @Query("DELETE FROM salary_payments")
    suspend fun deleteAllSalaries()
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC")
    fun getAllExpensesFlow(): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenseAmountFlow(): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE expenseDate >= :startOfDayTimestamp ORDER BY expenseDate DESC")
    fun getExpensesSinceFlow(startOfDayTimestamp: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<Expense>)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE dateString = :dateString AND courseId = :courseId")
    fun getAttendanceForDateAndCourseFlow(dateString: String, courseId: Long): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE dateString = :dateString")
    fun getAttendanceForDateFlow(dateString: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudentFlow(studentId: Long): Flow<List<Attendance>>

    @Query("SELECT COUNT(*) FROM attendance WHERE dateString = :dateString AND status = 'Present'")
    fun getPresentCountForDateFlow(dateString: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendanceList: List<Attendance>)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance")
    suspend fun deleteAllAttendance()
}

@Dao
interface CourseProgressDao {
    @Query("SELECT * FROM course_progress WHERE studentId = :studentId LIMIT 1")
    fun getProgressForStudentFlow(studentId: Long): Flow<CourseProgress?>

    @Query("SELECT * FROM course_progress")
    fun getAllProgressFlow(): Flow<List<CourseProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: CourseProgress): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CourseProgress>)

    @Query("DELETE FROM course_progress")
    suspend fun deleteAllProgress()
}

@Dao
interface TestResultDao {
    @Query("SELECT * FROM test_results WHERE studentId = :studentId ORDER BY id DESC")
    fun getResultsForStudentFlow(studentId: Long): Flow<List<TestResult>>

    @Query("SELECT * FROM test_results ORDER BY id DESC")
    fun getAllResultsFlow(): Flow<List<TestResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TestResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<TestResult>)

    @Delete
    suspend fun deleteResult(result: TestResult)

    @Query("DELETE FROM test_results")
    suspend fun deleteAllTestResults()
}

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificates ORDER BY id DESC")
    fun getAllCertificatesFlow(): Flow<List<Certificate>>

    @Query("SELECT * FROM certificates WHERE id = :id LIMIT 1")
    suspend fun getCertificateById(id: Long): Certificate?

    @Query("SELECT * FROM certificates WHERE studentId = :studentId")
    fun getCertificatesForStudentFlow(studentId: Long): Flow<List<Certificate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: Certificate): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(certificates: List<Certificate>)

    @Delete
    suspend fun deleteCertificate(certificate: Certificate)

    @Query("DELETE FROM certificates")
    suspend fun deleteAllCertificates()
}

@Dao
interface SmsTemplateDao {
    @Query("SELECT * FROM sms_templates")
    fun getAllTemplatesFlow(): Flow<List<SmsTemplate>>

    @Query("SELECT * FROM sms_templates WHERE languageCode = :languageCode LIMIT 1")
    suspend fun getTemplate(languageCode: String): SmsTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(template: SmsTemplate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<SmsTemplate>)
}
