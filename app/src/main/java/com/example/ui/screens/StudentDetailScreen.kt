package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.AppConfirmationDialog
import com.example.ui.navigation.Screen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: Long,
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val student = allStudents.find { it.id == studentId }
    val allPayments by viewModel.allPayments.collectAsState()
    val studentPayments = allPayments.filter { it.studentId == studentId }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (student == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(NavyDark),
            contentAlignment = Alignment.Center
        ) {
            Text("Student not found", color = TextPrimaryDark)
        }
        return
    }

    if (showDeleteConfirm) {
        AppConfirmationDialog(
            title = "Delete Student?",
            message = "Are you sure you want to delete ${student.name}? This will remove all their records.",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteStudent(student)
                onBack()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(student.name, color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("student_detail_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onNavigate(Screen.AddEditStudent.createRoute(student.id)) },
                        modifier = Modifier.testTag("edit_student_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary)
                    }
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.testTag("delete_student_btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
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
            // Header Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(NavyLight)
                                    .border(2.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(2).uppercase(),
                                    color = GoldBright,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.name,
                                    color = TextPrimaryDark,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Roll No: ${student.studentId}",
                                    color = GoldSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "S/D of ${student.fatherName}",
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Details Grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailItem(label = "Course", value = student.courseName)
                            DetailItem(label = "Batch", value = student.batchName)
                            DetailItem(label = "Timing", value = student.timing)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailItem(label = "Mobile", value = student.studentMobile)
                            DetailItem(label = "Parent Mobile", value = student.parentMobile.ifBlank { "N/A" })
                            DetailItem(label = "City", value = student.city)
                        }
                    }
                }
            }

            // Fee Progress Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fee Status",
                                color = GoldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (student.remainingFee <= 0.0) "COMPLETED" else "PENDING",
                                color = if (student.remainingFee <= 0.0) SuccessGreen else DangerRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val progress = if (student.finalFee > 0) {
                            (student.paidFee / student.finalFee).toFloat().coerceIn(0f, 1f)
                        } else 1f

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = GoldPrimary,
                            trackColor = NavyLight
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Fee", color = TextSecondaryDark, fontSize = 11.sp)
                                Text(
                                    "Rs. ${String.format(Locale.US, "%,.0f", student.finalFee)}",
                                    color = TextPrimaryDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Paid Fee", color = TextSecondaryDark, fontSize = 11.sp)
                                Text(
                                    "Rs. ${String.format(Locale.US, "%,.0f", student.paidFee)}",
                                    color = SuccessGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Remaining", color = TextSecondaryDark, fontSize = 11.sp)
                                Text(
                                    "Rs. ${String.format(Locale.US, "%,.0f", student.remainingFee)}",
                                    color = if (student.remainingFee > 0) DangerRed else SuccessGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigate(Screen.CollectFee.createRoute(student.id)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_collect_fee_detail")
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Collect Fee", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.issueCertificate(
                                studentId = student.id,
                                studentRollNo = student.studentId,
                                studentName = student.name,
                                fatherName = student.fatherName,
                                courseName = student.courseName,
                                duration = "3 Months",
                                startDate = student.admissionDate,
                                completionDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                grade = "A+",
                                issueDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                customDescription = "has successfully completed all coursework, practical lab tests, and portfolio milestones with distinction.",
                                teacherName = "Lead Instructor",
                                onSuccess = { cert ->
                                    viewModel.generateAndShareCertificatePdf(cert, share = false)
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = NavyDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_issue_certificate")
                    ) {
                        Icon(Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Certificate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Tabs for History
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = NavyCard,
                    contentColor = GoldPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Payment History (${studentPayments.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Attendance & Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedTab == 0) {
                if (studentPayments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No fee payments recorded yet.", color = TextSecondaryDark, fontSize = 13.sp)
                        }
                    }
                }

                items(studentPayments) { payment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                            .clickable { onNavigate(Screen.ReceiptAction.createRoute(payment.id)) }
                            .testTag("payment_row_${payment.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(payment.receiptNo, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                val date = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(payment.paymentDate))
                                Text("$date • ${payment.paymentMethod}", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Rs. ${String.format(Locale.US, "%,.0f", payment.paidAmount)}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Rem: Rs. ${String.format(Locale.US, "%,.0f", payment.remainingFee)}", color = DangerRed, fontSize = 11.sp)
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Academic Notes & Remarks", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = student.notes.ifBlank { "No special notes recorded. Student is regularly attending lectures." },
                                color = TextPrimaryDark,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextSecondaryDark, fontSize = 10.sp)
        Text(text = value, color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
