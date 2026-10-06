package com.example.service

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.model.AcademySettings
import com.example.data.model.FeePayment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class PrinterStatus {
    data object Disconnected : PrinterStatus()
    data class Connecting(val deviceName: String) : PrinterStatus()
    data class Connected(val deviceName: String, val address: String) : PrinterStatus()
    data class Error(val message: String) : PrinterStatus()
}

class ThermalPrinterService(private val context: Context) {

    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val _printerStatus = MutableStateFlow<PrinterStatus>(PrinterStatus.Disconnected)
    val printerStatus: StateFlow<PrinterStatus> = _printerStatus

    val isConnected: Boolean
        get() = bluetoothSocket?.isConnected == true

    fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    fun getPairedPrinters(): List<BluetoothDevice> {
        if (!hasBluetoothPermission()) return emptyList()
        val adapter = BluetoothAdapter.getDefaultAdapter() ?: return emptyList()
        return try {
            adapter.bondedDevices.filter { device ->
                val name = device.name?.lowercase() ?: ""
                name.contains("printer") || name.contains("pos") || name.contains("thermal") ||
                        name.contains("rpp") || name.contains("bt") || name.contains("mpt") ||
                        device.bluetoothClass?.deviceClass == 1664 // Imaging printer class
            }.ifEmpty {
                adapter.bondedDevices.toList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        if (!hasBluetoothPermission()) {
            _printerStatus.value = PrinterStatus.Error("Bluetooth permission not granted")
            return@withContext false
        }

        disconnect()
        val devName = try { device.name ?: "Thermal Printer" } catch (e: Exception) { "Thermal Printer" }
        _printerStatus.value = PrinterStatus.Connecting(devName)

        try {
            val socket = device.createRfcommSocketToServiceRecord(sppUuid)
            socket.connect()
            bluetoothSocket = socket
            outputStream = socket.outputStream
            _printerStatus.value = PrinterStatus.Connected(devName, device.address)
            true
        } catch (e: Exception) {
            disconnect()
            _printerStatus.value = PrinterStatus.Error("Could not connect to $devName: ${e.localizedMessage}")
            false
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream = null
            bluetoothSocket = null
            _printerStatus.value = PrinterStatus.Disconnected
        }
    }

    suspend fun printTestReceipt(paperWidth: String = "58mm"): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected || outputStream == null) {
            _printerStatus.value = PrinterStatus.Error("Thermal printer is not connected.")
            return@withContext false
        }
        val cols = if (paperWidth == "80mm") 48 else 32
        val hr = "=".repeat(cols)

        val escInit = byteArrayOf(0x1B, 0x40)
        val escCenter = byteArrayOf(0x1B, 0x61, 0x01)
        val escLeft = byteArrayOf(0x1B, 0x61, 0x00)
        val escBoldOn = byteArrayOf(0x1B, 0x45, 0x01)
        val escBoldOff = byteArrayOf(0x1B, 0x45, 0x00)
        val feedAndCut = byteArrayOf(0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x41, 0x10)

        try {
            outputStream?.apply {
                write(escInit)
                write(escCenter)
                write(escBoldOn)
                write("AL GHAZI DIGITAL INSTITUTE\n".toByteArray(Charsets.UTF_8))
                write(escBoldOff)
                write("TEST PRINT SUCCESSFUL\n".toByteArray(Charsets.UTF_8))
                write("$hr\n".toByteArray(Charsets.UTF_8))
                write(escLeft)
                write("Paper Size: $paperWidth ($cols cols)\n".toByteArray(Charsets.UTF_8))
                write("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())}\n".toByteArray(Charsets.UTF_8))
                write("Status: Thermal Printer Connected OK\n".toByteArray(Charsets.UTF_8))
                write("$hr\n".toByteArray(Charsets.UTF_8))
                write(escCenter)
                write("LEARN • PRACTICE • GROW\n".toByteArray(Charsets.UTF_8))
                write(feedAndCut)
                flush()
            }
            true
        } catch (e: Exception) {
            disconnect()
            _printerStatus.value = PrinterStatus.Error("Print failed: ${e.localizedMessage}")
            false
        }
    }

    suspend fun printReceipt(
        payment: FeePayment,
        settings: AcademySettings
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isConnected || outputStream == null) {
            _printerStatus.value = PrinterStatus.Error("Thermal printer is not connected.")
            return@withContext false
        }

        val paperWidth = settings.printerPaperWidth
        val cols = if (paperWidth == "80mm") 48 else 32
        val hr = "-".repeat(cols)
        val dhr = "=".repeat(cols)

        val escInit = byteArrayOf(0x1B, 0x40)
        val escCenter = byteArrayOf(0x1B, 0x61, 0x01)
        val escLeft = byteArrayOf(0x1B, 0x61, 0x00)
        val escBoldOn = byteArrayOf(0x1B, 0x45, 0x01)
        val escBoldOff = byteArrayOf(0x1B, 0x45, 0x00)
        val feedAndCut = byteArrayOf(0x0A, 0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x41, 0x10)

        fun twoCols(left: String, right: String): String {
            val space = cols - left.length - right.length
            return if (space > 0) left + " ".repeat(space) + right else "$left $right"
        }

        try {
            outputStream?.apply {
                write(escInit)
                write(escCenter)
                write(escBoldOn)
                write("${settings.academyName.uppercase()}\n".toByteArray(Charsets.UTF_8))
                write(escBoldOff)
                write("${settings.tagline}\n".toByteArray(Charsets.UTF_8))
                write("${settings.address}\n".toByteArray(Charsets.UTF_8))
                write("Tel: ${settings.phoneNumber}\n".toByteArray(Charsets.UTF_8))
                write("$dhr\n".toByteArray(Charsets.UTF_8))
                write(escBoldOn)
                write("FEE RECEIPT\n".toByteArray(Charsets.UTF_8))
                write(escBoldOff)
                write(escLeft)
                write("${twoCols("Receipt No:", payment.receiptNo)}\n".toByteArray(Charsets.UTF_8))
                val dateStr = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US).format(Date(payment.paymentDate))
                write("${twoCols("Date/Time:", dateStr)}\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Fee Month:", payment.feeMonth.ifBlank { "Current Month" })}\n".toByteArray(Charsets.UTF_8))
                write("$hr\n".toByteArray(Charsets.UTF_8))
                write("Student: ${payment.studentName}\n".toByteArray(Charsets.UTF_8))
                write("Father : ${payment.fatherName}\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Roll No:", payment.studentRollNo)}\n".toByteArray(Charsets.UTF_8))
                write("Course : ${payment.courseName}\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Batch :", payment.batchName)}\n".toByteArray(Charsets.UTF_8))
                write("$hr\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Total Fee:", "Rs. ${String.format(Locale.US, "%.0f", payment.totalFee)}")}\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Previous Paid:", "Rs. ${String.format(Locale.US, "%.0f", payment.previousPaid)}")}\n".toByteArray(Charsets.UTF_8))
                write(escBoldOn)
                write("${twoCols("AMOUNT PAID:", "Rs. ${String.format(Locale.US, "%.0f", payment.paidAmount)}")}\n".toByteArray(Charsets.UTF_8))
                write(escBoldOff)
                write("${twoCols("Total Paid:", "Rs. ${String.format(Locale.US, "%.0f", payment.newTotalPaid)}")}\n".toByteArray(Charsets.UTF_8))
                write(escBoldOn)
                write("${twoCols("REMAINING FEE:", "Rs. ${String.format(Locale.US, "%.0f", payment.remainingFee)}")}\n".toByteArray(Charsets.UTF_8))
                write(escBoldOff)
                write("$hr\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Method:", payment.paymentMethod)}\n".toByteArray(Charsets.UTF_8))
                write("${twoCols("Received By:", payment.receivedBy)}\n".toByteArray(Charsets.UTF_8))
                write("$dhr\n".toByteArray(Charsets.UTF_8))
                write(escCenter)
                write("${settings.receiptFooterText}\n".toByteArray(Charsets.UTF_8))
                write(feedAndCut)
                flush()
            }
            true
        } catch (e: Exception) {
            disconnect()
            _printerStatus.value = PrinterStatus.Error("Printing error: ${e.localizedMessage}")
            false
        }
    }
}
