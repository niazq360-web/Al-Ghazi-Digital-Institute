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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Certificate
import com.example.ui.components.AcademyHeaderLogo
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

@Composable
fun CertificatesScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val certificates by viewModel.certificates.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showIssueDialog by remember { mutableStateOf(false) }

    if (showIssueDialog) {
        var selectedStudent by remember { mutableStateOf(allStudents.firstOrNull()) }
        var studentDropdownExpanded by remember { mutableStateOf(false) }

        var duration by remember { mutableStateOf("3 Months") }
        var startDate by remember { mutableStateOf(selectedStudent?.admissionDate ?: "2026-06-01") }
        var completionDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
        var grade by remember { mutableStateOf("A+") }
        var customDescription by remember {
            mutableStateOf("has successfully completed the comprehensive training program with exemplary dedication, mastering all curriculum modules and practical capstone projects.")
        }
        var teacherName by remember { mutableStateOf("Lead Faculty Instructor") }

        AlertDialog(
            onDismissRequest = { showIssueDialog = false },
            title = { Text("Issue Professional Certificate", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedStudent?.let { "${it.name} (${it.studentId})" } ?: "Select Student",
                                onValueChange = {},
                                label = { Text("Student *") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { studentDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("select_cert_student")
                            )
                            DropdownMenu(
                                expanded = studentDropdownExpanded,
                                onDismissRequest = { studentDropdownExpanded = false },
                                modifier = Modifier.background(NavyCard)
                            ) {
                                allStudents.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.name} (${s.studentId}) - ${s.courseName}", color = TextPrimaryDark) },
                                        onClick = {
                                            selectedStudent = s
                                            startDate = s.admissionDate
                                            studentDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("Duration") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = grade,
                                onValueChange = { grade = it },
                                label = { Text("Grade") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = { startDate = it },
                                label = { Text("Start Date") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = completionDate,
                                onValueChange = { completionDate = it },
                                label = { Text("Completion Date") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = customDescription,
                            onValueChange = { customDescription = it },
                            label = { Text("Custom Description (Printed on Certificate)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_cert_desc")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = teacherName,
                            onValueChange = { teacherName = it },
                            label = { Text("Teacher / Instructor Name (Signee)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_cert_teacher")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = settings.ownerName,
                            onValueChange = {},
                            label = { Text("Director / Principal Name (Signee)") },
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = selectedStudent ?: return@Button
                        viewModel.issueCertificate(
                            studentId = s.id,
                            studentRollNo = s.studentId,
                            studentName = s.name,
                            fatherName = s.fatherName,
                            courseName = s.courseName,
                            duration = duration,
                            startDate = startDate,
                            completionDate = completionDate,
                            grade = grade,
                            issueDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                            customDescription = customDescription.trim(),
                            teacherName = teacherName.trim(),
                            onSuccess = { cert ->
                                viewModel.generateAndShareCertificatePdf(cert, share = false)
                            }
                        )
                        showIssueDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                    modifier = Modifier.testTag("confirm_issue_cert_btn")
                ) {
                    Text("Generate & Issue Certificate", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIssueDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showIssueDialog = true },
                containerColor = GoldPrimary,
                contentColor = NavyDark,
                modifier = Modifier.testTag("issue_cert_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Issue Certificate")
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
                tagline = "Official Certificate Issuance Center",
                compact = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (certificates.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No certificates issued yet.", color = TextSecondaryDark, fontSize = 14.sp)
                        }
                    }
                }

                items(certificates) { cert ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .testTag("cert_card_${cert.id}"),
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
                                            .background(GoldPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CardMembership, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(cert.studentName, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${cert.certificateNo} • ${cert.courseName}", color = GoldBright, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Grade: ${cert.grade} • Duration: ${cert.duration}", color = TextSecondaryDark, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "\"${cert.customDescription}\"",
                                color = TextSecondaryDark,
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Signatures: ${cert.teacherName} & ${cert.directorName}", color = GoldSecondary, fontSize = 10.sp)
                                    Text("Issued on: ${cert.issueDate}", color = TextSecondaryDark, fontSize = 9.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { viewModel.generateAndShareCertificatePdf(cert, share = false) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = "Download PDF", tint = GoldPrimary)
                                    }
                                    IconButton(
                                        onClick = { viewModel.generateAndShareCertificatePdf(cert, share = true) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share PDF", tint = SuccessGreen)
                                    }
                                }
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
