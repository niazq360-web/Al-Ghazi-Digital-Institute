package com.example.service

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import com.example.data.model.AcademySettings
import com.example.data.model.FeePayment
import com.example.data.model.SmsTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SimCardInfo(
    val slotIndex: Int,
    val subscriptionId: Int,
    val displayName: String,
    val carrierName: String,
    val number: String
)

sealed class SmsResult {
    data class Sent(val simUsed: String) : SmsResult()
    data class Failed(val reason: String) : SmsResult()
    data class FallbackOpened(val phone: String) : SmsResult()
    data class PermissionRequired(val permission: String) : SmsResult()
}

class SmsService(private val context: Context) {

    fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getAvailableSims(): List<SimCardInfo> {
        val simList = mutableListOf<SimCardInfo>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val hasPhoneState = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPhoneState) {
                try {
                    val subManager =
                        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                    val subInfoList: List<SubscriptionInfo>? =
                        subManager?.activeSubscriptionInfoList
                    subInfoList?.forEach { sub ->
                        simList.add(
                            SimCardInfo(
                                slotIndex = sub.simSlotIndex,
                                subscriptionId = sub.subscriptionId,
                                displayName = sub.displayName?.toString() ?: "SIM ${sub.simSlotIndex + 1}",
                                carrierName = sub.carrierName?.toString() ?: "Carrier",
                                number = sub.number ?: ""
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        if (simList.isEmpty()) {
            simList.add(
                SimCardInfo(
                    slotIndex = 0,
                    subscriptionId = -1,
                    displayName = "Default SIM (SIM 1)",
                    carrierName = "Default",
                    number = ""
                )
            )
        }
        return simList
    }

    fun prepareMessage(
        template: SmsTemplate,
        payment: FeePayment,
        settings: AcademySettings
    ): String {
        val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(payment.paymentDate))
        return template.content
            .replace("{student_name}", payment.studentName)
            .replace("{father_name}", payment.fatherName)
            .replace("{course_name}", payment.courseName)
            .replace("{paid_amount}", String.format(Locale.US, "%.0f", payment.paidAmount))
            .replace("{total_fee}", String.format(Locale.US, "%.0f", payment.totalFee))
            .replace("{previous_paid}", String.format(Locale.US, "%.0f", payment.previousPaid))
            .replace("{remaining_fee}", String.format(Locale.US, "%.0f", payment.remainingFee))
            .replace("{receipt_no}", payment.receiptNo)
            .replace("{payment_date}", dateStr)
            .replace("{academy_name}", settings.academyName)
    }

    fun sendFeeSms(
        recipientPhone: String,
        messageText: String,
        selectedSim: SimCardInfo?,
        onResult: (SmsResult) -> Unit
    ) {
        if (recipientPhone.isBlank()) {
            onResult(SmsResult.Failed("Recipient phone number is missing"))
            return
        }

        if (!hasSmsPermission()) {
            // Fallback to opening system SMS app
            openSmsAppFallback(recipientPhone, messageText)
            onResult(SmsResult.FallbackOpened(recipientPhone))
            return
        }

        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (selectedSim != null && selectedSim.subscriptionId != -1) {
                    val subManager =
                        context.getSystemService(SubscriptionManager::class.java)
                    context.getSystemService(SmsManager::class.java)
                        .createForSubscriptionId(selectedSim.subscriptionId)
                } else {
                    context.getSystemService(SmsManager::class.java)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && selectedSim != null && selectedSim.subscriptionId != -1) {
                @Suppress("DEPRECATION")
                SmsManager.getSmsManagerForSubscriptionId(selectedSim.subscriptionId)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(messageText)
            val simLabel = selectedSim?.displayName ?: "SIM 1"

            smsManager.sendMultipartTextMessage(
                recipientPhone,
                null,
                parts,
                null,
                null
            )
            onResult(SmsResult.Sent(simLabel))
        } catch (e: Exception) {
            e.printStackTrace()
            // Provide fallback if programmatic sending failed
            openSmsAppFallback(recipientPhone, messageText)
            onResult(SmsResult.FallbackOpened(recipientPhone))
        }
    }

    fun openSmsAppFallback(recipientPhone: String, messageText: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:${recipientPhone.trim()}")
                putExtra("sms_body", messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendSms(
        recipientPhone: String,
        messageText: String,
        selectedSim: SimCardInfo? = null,
        onResult: (SmsResult) -> Unit = {}
    ) {
        sendFeeSms(recipientPhone, messageText, selectedSim, onResult)
    }
}
