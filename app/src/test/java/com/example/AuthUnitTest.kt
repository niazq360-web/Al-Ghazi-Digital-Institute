package com.example

import com.example.data.model.AcademySettings
import com.example.ui.viewmodel.ActiveOtpState
import com.example.ui.viewmodel.OtpDeliveryChannel
import com.example.ui.viewmodel.OtpFlowType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUnitTest {

    private fun normalizePhone(phone: String): String {
        return phone.replace("+", "")
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .trimStart('0')
    }

    @Test
    fun testPhoneNormalization() {
        val phone1 = "+92 300 1234567"
        val phone2 = "03001234567"
        val phone3 = "923001234567"

        val norm1 = normalizePhone(phone1)
        val norm2 = normalizePhone(phone2)
        val norm3 = normalizePhone(phone3)

        assertTrue(norm1.endsWith(norm2.takeLast(7)))
        assertTrue(norm3.endsWith(norm1.takeLast(7)))
    }

    @Test
    fun testOtpValidation() {
        val activeOtp = ActiveOtpState(
            code = "786432",
            mobileNumber = "03001234567",
            channel = OtpDeliveryChannel.SMS,
            flowType = OtpFlowType.REGISTRATION,
            expiresAt = System.currentTimeMillis() + 60000
        )

        // Valid code
        assertEquals("786432", activeOtp.code)
        assertTrue(System.currentTimeMillis() < activeOtp.expiresAt)

        // Expired test
        val expiredOtp = activeOtp.copy(expiresAt = System.currentTimeMillis() - 1000)
        assertTrue(System.currentTimeMillis() > expiredOtp.expiresAt)
    }

    @Test
    fun testPasswordChangeModel() {
        val originalSettings = AcademySettings(
            registeredMobile = "03001234567",
            adminPasswordHash = "oldPass123",
            isAccountRegistered = true
        )

        val updatedSettings = originalSettings.copy(
            adminPasswordHash = "newSecret456"
        )

        assertEquals("newSecret456", updatedSettings.adminPasswordHash)
        assertEquals("03001234567", updatedSettings.registeredMobile)
        assertTrue(updatedSettings.isAccountRegistered)
    }
}
