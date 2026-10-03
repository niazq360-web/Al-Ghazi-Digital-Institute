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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.Attendance
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
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
fun AttendanceScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val courses by viewModel.activeCourses.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    val courseStudents = allStudents.filter { s ->
        selectedCourse == null || s.courseName == selectedCourse?.name
    }

    val attendanceMap = remember(courseStudents) {
        mutableStateMapOf<Long, String>().apply {
            courseStudents.forEach { s ->
                put(s.id, "Present")
            }
        }
    }

    val presentCount = attendanceMap.values.count { it == "Present" }
    val absentCount = attendanceMap.values.count { it == "Absent" }
    val leaveCount = attendanceMap.values.count { it == "Leave" }
    val percent = if (courseStudents.isNotEmpty()) (presentCount.toDouble() / courseStudents.size * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(horizontal = 16.dp)
    ) {
        AcademyHeaderLogo(
            academyName = settings.academyName,
            tagline = "Daily Student Attendance Register",
            compact = true
        )

        // Date and Course Controls Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        label = { Text("Date (yyyy-MM-dd)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("attendance_date_input")
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedCourse?.name ?: "All Courses",
                            onValueChange = {},
                            label = { Text("Course") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { courseDropdownExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("select_attendance_course")
                        )
                        DropdownMenu(
                            expanded = courseDropdownExpanded,
                            onDismissRequest = { courseDropdownExpanded = false },
                            modifier = Modifier.background(NavyCard)
                        ) {
                            courses.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.name, color = TextPrimaryDark) },
                                    onClick = {
                                        selectedCourse = c
                                        courseDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Stats Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyLight, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total: ${courseStudents.size}", color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Present: $presentCount", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Absent: $absentCount", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Attendance: $percent%", color = GoldBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Mark All Present Button
                Button(
                    onClick = {
                        courseStudents.forEach { s -> attendanceMap[s.id] = "Present" }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyLight, contentColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(38.dp)
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark All Present", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Student Attendance List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(courseStudents) { student ->
                val currentStatus = attendanceMap[student.id] ?: "Present"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                        .testTag("attendance_row_${student.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${student.studentId} • ${student.courseName}", color = TextSecondaryDark, fontSize = 11.sp)
                        }

                        // Status Selector Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            StatusToggleBtn(
                                label = "P",
                                isSelected = currentStatus == "Present",
                                color = SuccessGreen,
                                onClick = { attendanceMap[student.id] = "Present" }
                            )
                            StatusToggleBtn(
                                label = "A",
                                isSelected = currentStatus == "Absent",
                                color = DangerRed,
                                onClick = { attendanceMap[student.id] = "Absent" }
                            )
                            StatusToggleBtn(
                                label = "L",
                                isSelected = currentStatus == "Leave",
                                color = WarningAmber,
                                onClick = { attendanceMap[student.id] = "Leave" }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Save Attendance FAB/Button
        Button(
            onClick = {
                val records = courseStudents.map { s ->
                    Attendance(
                        studentId = s.id,
                        studentRollNo = s.studentId,
                        studentName = s.name,
                        courseId = s.courseId,
                        courseName = s.courseName,
                        batchName = s.batchName,
                        dateString = selectedDate,
                        status = attendanceMap[s.id] ?: "Present"
                    )
                }
                viewModel.markAttendance(records)
            },
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 8.dp)
                .testTag("save_attendance_btn")
        ) {
            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save & Record Attendance", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatusToggleBtn(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isSelected) color else NavyLight)
            .border(1.dp, if (isSelected) color else NavyBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextSecondaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
