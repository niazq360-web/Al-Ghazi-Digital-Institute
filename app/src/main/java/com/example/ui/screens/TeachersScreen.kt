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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Teacher
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.AppConfirmationDialog
import com.example.ui.components.StatsCard
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
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

@Composable
fun TeachersScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val teachers by viewModel.teachers.collectAsState()
    val salaries by viewModel.salaries.collectAsState()
    val totalSalaries by viewModel.totalSalaryPaid.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }
    var payingSalaryTeacher by remember { mutableStateOf<Teacher?>(null) }
    var teacherToDelete by remember { mutableStateOf<Teacher?>(null) }

    if (teacherToDelete != null) {
        AppConfirmationDialog(
            title = "Remove Teacher?",
            message = "Are you sure you want to remove ${teacherToDelete?.name}?",
            confirmText = "Remove",
            isDestructive = true,
            onConfirm = {
                teacherToDelete?.let { viewModel.deleteTeacher(it) }
                teacherToDelete = null
            },
            onDismiss = { teacherToDelete = null }
        )
    }

    // Pay Salary Dialog
    if (payingSalaryTeacher != null) {
        val teacher = payingSalaryTeacher!!
        var monthYear by remember { mutableStateOf(SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())) }
        var salaryAmount by remember { mutableDoubleStateOf(teacher.monthlySalary) }
        var bonus by remember { mutableDoubleStateOf(0.0) }
        var deduction by remember { mutableDoubleStateOf(0.0) }
        var method by remember { mutableStateOf("Cash") }
        var remarks by remember { mutableStateOf("Monthly teacher compensation") }

        val netPayable = (salaryAmount + bonus - deduction).coerceAtLeast(0.0)

        AlertDialog(
            onDismissRequest = { payingSalaryTeacher = null },
            title = { Text("Disburse Teacher Salary", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("Teacher: ${teacher.name} (${teacher.subjectOrCourse})", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    item {
                        OutlinedTextField(
                            value = monthYear,
                            onValueChange = { monthYear = it },
                            label = { Text("Salary Month & Year") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = if (salaryAmount == 0.0) "" else salaryAmount.toInt().toString(),
                                onValueChange = { salaryAmount = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Base Salary (Rs.)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = if (bonus == 0.0) "" else bonus.toInt().toString(),
                                onValueChange = { bonus = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Bonus (Rs.)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = if (deduction == 0.0) "" else deduction.toInt().toString(),
                            onValueChange = { deduction = it.toDoubleOrNull() ?: 0.0 },
                            label = { Text("Deduction (Rs.)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = method,
                            onValueChange = { method = it },
                            label = { Text("Payment Method (Cash / Bank / JazzCash)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyLight, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Net Payable Amount:", color = TextSecondaryDark, fontSize = 12.sp)
                                Text("Rs. ${String.format(Locale.US, "%,.0f", netPayable)}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.paySalary(
                            teacherId = teacher.id,
                            teacherName = teacher.name,
                            monthYear = monthYear,
                            amount = salaryAmount,
                            bonus = bonus,
                            deduction = deduction,
                            method = method,
                            remarks = remarks
                        )
                        payingSalaryTeacher = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_pay_salary_btn")
                ) {
                    Text("Confirm Payout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { payingSalaryTeacher = null }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    // Add Teacher Dialog
    if (showAddTeacherDialog) {
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var qualification by remember { mutableStateOf("BS Software Engineering") }
        var subject by remember { mutableStateOf("Android App Development") }
        var monthlySalary by remember { mutableDoubleStateOf(50000.0) }

        AlertDialog(
            onDismissRequest = { showAddTeacherDialog = false },
            title = { Text("Add Faculty / Teacher", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Teacher Name *") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_teacher_name")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_teacher_phone")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Subject / Assigned Course") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_teacher_subject")
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = qualification,
                                onValueChange = { qualification = it },
                                label = { Text("Qualification") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = if (monthlySalary == 0.0) "" else monthlySalary.toInt().toString(),
                                onValueChange = { monthlySalary = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Monthly Salary (Rs.)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("input_teacher_salary")
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) return@Button
                        viewModel.addTeacher(
                            Teacher(
                                name = name.trim(),
                                phone = phone.trim(),
                                email = email.trim(),
                                qualification = qualification.trim(),
                                subjectOrCourse = subject.trim(),
                                monthlySalary = monthlySalary,
                                joiningDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                                status = "Active"
                            )
                        )
                        showAddTeacherDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                    modifier = Modifier.testTag("save_teacher_btn")
                ) {
                    Text("Save Teacher", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTeacherDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTeacherDialog = true },
                containerColor = InfoBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_teacher_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Teacher")
            }
        },
        containerColor = NavyDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            AcademyHeaderLogo(
                academyName = settings.academyName,
                tagline = "Faculty & Teacher Salary Management",
                compact = true
            )

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsCard(
                    title = "Faculty Teachers",
                    value = "${teachers.size}",
                    icon = Icons.Default.Groups,
                    accentColor = InfoBlue,
                    modifier = Modifier.weight(1f),
                    testTag = "stats_teachers_count"
                )
                StatsCard(
                    title = "Total Salaries Paid",
                    value = "Rs. ${String.format(Locale.US, "%,.0f", totalSalaries)}",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "stats_salaries_paid"
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavyCard,
                contentColor = GoldPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Teachers List (${teachers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Salary History (${salaries.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(teachers) { teacher ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                                .testTag("teacher_card_${teacher.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(InfoBlue.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(teacher.name, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("${teacher.qualification} • ${teacher.subjectOrCourse}", color = GoldSecondary, fontSize = 11.sp)
                                            Text(teacher.phone, color = TextSecondaryDark, fontSize = 10.sp)
                                        }
                                    }

                                    IconButton(onClick = { teacherToDelete = teacher }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(NavyLight, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Monthly Salary", color = TextSecondaryDark, fontSize = 10.sp)
                                        Text("Rs. ${String.format(Locale.US, "%,.0f", teacher.monthlySalary)}", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Button(
                                        onClick = { payingSalaryTeacher = teacher },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("pay_salary_btn_${teacher.id}")
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Pay Salary", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(salaries) { salary ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyBorder, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(salary.teacherName, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${salary.voucherNo} • ${salary.monthYear}", color = GoldPrimary, fontSize = 11.sp)
                                    val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(salary.paymentDate))
                                    Text("$dateStr • Method: ${salary.paymentMethod}", color = TextSecondaryDark, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Rs. ${String.format(Locale.US, "%,.0f", salary.netPaid)}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Disbursed", color = TextSecondaryDark, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }
}
