package com.example.ui.screens

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.PrinterStatus
import com.example.service.SimCardInfo
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptActionScreen(
    paymentId: Long,
    viewModel: AcademyViewModel,
    onBack: () -> Unit
) {
    val allPayments by viewModel.allPayments.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val printerStatus by viewModel.printerStatus.collectAsState()
    val availablePrinters by viewModel.availablePrinters.collectAsState()
    val availableSims by viewModel.availableSims.collectAsState()
    val selectedSim by viewModel.selectedSim.collectAsState()

    val payment = allPayments.find { it.id == paymentId }
    val student = payment?.let { p -> allStudents.find { it.id == p.studentId } }

    var showSimDialog by remember { mutableStateOf(false) }
    var showPrinterDialog by remember { mutableStateOf(false) }
    var printerErrorNotice by remember { mutableStateOf<String?>(null) }

    if (payment == null) {
        Box(modifier = Modifier.fillMaxSize().background(NavyDark), contentAlignment = Alignment.Center) {
            Text("Receipt not found", color = TextPrimaryDark)
        }
        return
    }

    // SIM Selection Dialog for Real SMS
    if (showSimDialog) {
        AlertDialog(
            onDismissRequest = { showSimDialog = false },
            title = { Text("Select SIM for SMS", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Choose the SIM card from which to dispatch the fee confirmation message:",
                        color = TextPrimaryDark,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    availableSims.forEach { sim ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSelectedSim(sim)
                                    showSimDialog = false
                                    viewModel.sendFeeSms(payment, student?.studentMobile ?: payment.studentRollNo)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSim?.subscriptionId == sim.subscriptionId,
                                onClick = {
                                    viewModel.setSelectedSim(sim)
                                    showSimDialog = false
                                    viewModel.sendFeeSms(payment, student?.studentMobile ?: payment.studentRollNo)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(sim.displayName, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                                Text("${sim.carrierName} ${sim.number}", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSimDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    // Printer Connection Dialog
    if (showPrinterDialog) {
        AlertDialog(
            onDismissRequest = { showPrinterDialog = false },
            title = { Text("Connect Thermal Printer", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Paired Bluetooth thermal printers (${settings.printerPaperWidth}):",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (availablePrinters.isEmpty()) {
                        Text(
                            "No paired Bluetooth printers found. Please pair your POS thermal printer in Android Bluetooth Settings first.",
                            color = WarningAmber,
                            fontSize = 12.sp
                        )
                    } else {
                        availablePrinters.forEach { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.connectPrinter(device)
                                        showPrinterDialog = false
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bluetooth, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(device.name ?: "Unknown Printer", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                                    Text(device.address, color = TextSecondaryDark, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPrinterDialog = false }) {
                    Text("Close", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fee Receipt Action", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("receipt_action_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        },
        containerColor = NavyDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Payment Successful Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SuccessGreen, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PAYMENT SUCCESSFUL", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                        Text(payment.receiptNo, color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Receipt Preview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AcademyHeaderLogo(
                            academyName = settings.academyName,
                            tagline = settings.tagline,
                            compact = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NavyBorder))
                        Spacer(modifier = Modifier.height(10.dp))

                        ReceiptRow(label = "Receipt No:", value = payment.receiptNo, isBold = true)
                        val dateStr = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US).format(Date(payment.paymentDate))
                        ReceiptRow(label = "Date & Time:", value = dateStr)
                        ReceiptRow(label = "Student Name:", value = payment.studentName, isBold = true)
                        ReceiptRow(label = "Father Name:", value = payment.fatherName)
                        ReceiptRow(label = "Roll No / ID:", value = payment.studentRollNo)
                        ReceiptRow(label = "Course:", value = payment.courseName)
                        ReceiptRow(label = "Batch:", value = payment.batchName)

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NavyBorder))
                        Spacer(modifier = Modifier.height(8.dp))

                        ReceiptRow(label = "Total Course Fee:", value = "Rs. ${String.format(Locale.US, "%,.0f", payment.totalFee)}")
                        ReceiptRow(label = "Previous Paid:", value = "Rs. ${String.format(Locale.US, "%,.0f", payment.previousPaid)}")
                        ReceiptRow(
                            label = "CURRENT PAYMENT:",
                            value = "Rs. ${String.format(Locale.US, "%,.0f", payment.paidAmount)}",
                            isBold = true,
                            isGreen = true
                        )
                        ReceiptRow(label = "Total Paid:", value = "Rs. ${String.format(Locale.US, "%,.0f", payment.newTotalPaid)}")
                        ReceiptRow(
                            label = "Remaining Fee:",
                            value = "Rs. ${String.format(Locale.US, "%,.0f", payment.remainingFee)}",
                            isBold = true,
                            isRed = payment.remainingFee > 0
                        )

                        ReceiptRow(label = "Payment Method:", value = payment.paymentMethod)
                        ReceiptRow(label = "Received By:", value = payment.receivedBy)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = settings.receiptFooterText,
                            color = TextSecondaryDark,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // PRINT RECEIPT (Thermal / Bluetooth)
                    Button(
                        onClick = {
                            if (printerStatus is PrinterStatus.Connected) {
                                viewModel.printReceipt(payment)
                            } else {
                                showPrinterDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("action_print_receipt")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🖨 PRINT RECEIPT (Thermal ${settings.printerPaperWidth})", fontWeight = FontWeight.Bold)
                    }

                    // DOWNLOAD PDF
                    Button(
                        onClick = { viewModel.downloadOrShareReceiptPdf(payment, share = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = InfoBlue, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("action_download_pdf")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📄 DOWNLOAD PDF RECEIPT", fontWeight = FontWeight.Bold)
                    }

                    // SEND SMS
                    Button(
                        onClick = {
                            val phone = student?.studentMobile ?: ""
                            if (availableSims.size > 1) {
                                showSimDialog = true
                            } else {
                                viewModel.sendFeeSms(payment, phone)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("action_send_sms")
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📱 SEND FEE CONFIRMATION SMS", fontWeight = FontWeight.Bold)
                    }

                    // SHARE RECEIPT
                    OutlinedButton(
                        onClick = { viewModel.downloadOrShareReceiptPdf(payment, share = true) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("action_share_receipt")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("↗ SHARE RECEIPT", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isGreen: Boolean = false,
    isRed: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextSecondaryDark,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            color = when {
                isGreen -> SuccessGreen
                isRed -> DangerRed
                isBold -> TextPrimaryDark
                else -> TextPrimaryDark
            },
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}
