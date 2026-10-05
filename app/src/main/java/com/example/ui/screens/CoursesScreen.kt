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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
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
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AcademyViewModel
import java.util.Locale

@Composable
fun CoursesScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val courses by viewModel.courses.collectAsState()
    val courseFeeSummaries by viewModel.courseFeeSummaries.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCourse by remember { mutableStateOf<Course?>(null) }
    var courseToDelete by remember { mutableStateOf<Course?>(null) }
    var expandedCourseId by remember { mutableStateOf<Long?>(null) }

    if (courseToDelete != null) {
        AppConfirmationDialog(
            title = "Delete Course?",
            message = "Are you sure you want to delete ${courseToDelete?.name}?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                courseToDelete?.let { viewModel.deleteCourse(it) }
                courseToDelete = null
            },
            onDismiss = { courseToDelete = null }
        )
    }

    if (showAddDialog || editingCourse != null) {
        val isEdit = editingCourse != null
        var name by remember { mutableStateOf(editingCourse?.name ?: "") }
        var description by remember { mutableStateOf(editingCourse?.description ?: "") }
        var duration by remember { mutableStateOf(editingCourse?.duration ?: "3 Months") }
        var totalFee by remember { mutableDoubleStateOf(editingCourse?.totalFee ?: 15000.0) }
        var monthlyFee by remember { mutableDoubleStateOf(editingCourse?.monthlyFee ?: 5000.0) }
        var instructor by remember { mutableStateOf(editingCourse?.instructor ?: "Al Ghazi Faculty") }
        var classTiming by remember { mutableStateOf(editingCourse?.classTiming ?: "10:00 AM - 12:00 PM") }
        var batchName by remember { mutableStateOf(editingCourse?.batchName ?: "Batch-2026-A") }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingCourse = null
            },
            title = {
                Text(
                    text = if (isEdit) "Edit Course" else "Add New Course",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Course Name *") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("course_name_input")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description & Syllabus Overview") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("course_desc_input")
                        )
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
                                value = if (totalFee == 0.0) "" else totalFee.toInt().toString(),
                                onValueChange = { totalFee = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("Total Fee (Rs.)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("course_fee_input")
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = instructor,
                                onValueChange = { instructor = it },
                                label = { Text("Instructor") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = batchName,
                                onValueChange = { batchName = it },
                                label = { Text("Batch") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = classTiming,
                            onValueChange = { classTiming = it },
                            label = { Text("Class Timing") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        if (isEdit) {
                            viewModel.updateCourse(
                                editingCourse!!.copy(
                                    name = name.trim(),
                                    description = description.trim(),
                                    duration = duration.trim(),
                                    totalFee = totalFee,
                                    monthlyFee = monthlyFee,
                                    instructor = instructor.trim(),
                                    classTiming = classTiming.trim(),
                                    batchName = batchName.trim()
                                )
                            )
                        } else {
                            viewModel.addCourse(
                                Course(
                                    name = name.trim(),
                                    description = description.trim(),
                                    duration = duration.trim(),
                                    totalFee = totalFee,
                                    monthlyFee = monthlyFee,
                                    instructor = instructor.trim(),
                                    classTiming = classTiming.trim(),
                                    batchName = batchName.trim()
                                )
                            )
                        }
                        showAddDialog = false
                        editingCourse = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                    modifier = Modifier.testTag("save_course_dialog_btn")
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingCourse = null
                }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurpleAccent,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_course_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
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
                tagline = "Course Catalog & Curriculums",
                compact = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(courses) { course ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                            .testTag("course_card_${course.id}"),
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
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(PurpleAccent.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = PurpleAccent, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(course.name, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${course.duration} • ${course.batchName}", color = GoldSecondary, fontSize = 11.sp)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { editingCourse = course }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { courseToDelete = course }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            if (course.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(course.description, color = TextSecondaryDark, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val summary = courseFeeSummaries.find { it.courseName.trim().equals(course.name.trim(), ignoreCase = true) }
                            val enrolledCount = summary?.studentCount ?: 0
                            val isExpanded = expandedCourseId == course.id

                            Spacer(modifier = Modifier.height(10.dp))

                            // Fee & Enrollment Stats
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLight, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Enrolled: $enrolledCount Students", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Course Fee: Rs. ${String.format(Locale.US, "%,.0f", course.totalFee)}", color = TextSecondaryDark, fontSize = 10.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    val paid = summary?.paidFee ?: 0.0
                                    val bal = summary?.remainingFee ?: 0.0
                                    Text("Paid: Rs. ${String.format(Locale.US, "%,.0f", paid)}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Balance: Rs. ${String.format(Locale.US, "%,.0f", bal)}", color = if (bal > 0) DangerRed else SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action Row: View Students & Admit Student
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        expandedCourseId = if (isExpanded) null else course.id
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isExpanded) "Hide Students" else "View Students ($enrolledCount)",
                                        color = GoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.setFilterCourse(course.name)
                                        onNavigate(Screen.AddEditStudent.createRoute())
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Admit Student", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Expanded Student Records for this Course
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(6.dp))

                                val studentsInCourse = summary?.students ?: emptyList()
                                if (studentsInCourse.isEmpty()) {
                                    Text(
                                        text = "No students enrolled in ${course.name} yet.",
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(6.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Students in ${course.name}:",
                                        color = GoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    studentsInCourse.forEach { st ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(NavyDark, RoundedCornerShape(8.dp))
                                                .clickable { onNavigate(Screen.StudentDetail.createRoute(st.id)) }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(st.name, color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("Roll: ${st.studentId} • Mob: ${st.studentMobile}", color = TextSecondaryDark, fontSize = 9.sp)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text("Paid: Rs. ${st.paidFee.toInt()}", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    Text(
                                                        text = if (st.remainingFee <= 0) "Cleared" else "Bal: Rs. ${st.remainingFee.toInt()}",
                                                        color = if (st.remainingFee > 0) DangerRed else SuccessGreen,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Button(
                                                    onClick = { onNavigate(Screen.CollectFee.createRoute(st.id)) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = NavyDark),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(26.dp)
                                                ) {
                                                    Text("Fee", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
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
