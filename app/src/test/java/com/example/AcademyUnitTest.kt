package com.example

import com.example.data.model.Course
import com.example.data.model.FeePayment
import com.example.data.model.Student
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AcademyUnitTest {

    @Test
    fun testFeeCalculation() {
        val courseFee = 35000.0
        val discount = 3000.0
        val finalFee = courseFee - discount
        assertEquals(32000.0, finalFee, 0.001)

        val paidAmount = 20000.0
        val remainingFee = finalFee - paidAmount
        assertEquals(12000.0, remainingFee, 0.001)
    }

    @Test
    fun testCourseModel() {
        val course = Course(
            id = 1,
            name = "Android App Development",
            duration = "4 Months",
            totalFee = 35000.0,
            monthlyFee = 9000.0
        )
        assertEquals("Android App Development", course.name)
        assertTrue(course.isActive)
    }

    @Test
    fun testPaymentModel() {
        val payment = FeePayment(
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
            remainingFee = 12000.0
        )
        assertEquals("REC-2026-00001", payment.receiptNo)
        assertEquals(12000.0, payment.remainingFee, 0.001)
    }
}
