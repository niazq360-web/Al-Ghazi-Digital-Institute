package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewList
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Student
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.AppConfirmationDialog
import com.example.ui.navigation.Screen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AcademyViewModel
import java.io.File
import java.util.Locale

@Composable
fun StudentsScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val filteredStudents by viewModel.filteredStudents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterCourse by viewModel.filterCourse.collectAsState()
    val courses by viewModel.activeCourses.collectAsState()
    val courseFeeSummaries by viewModel.courseFeeSummaries.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showWipeDialog by remember { mutableStateOf(false) }
    var isGroupedView by remember { mutableStateOf(true) }
    var collapsedCourses by remember { mutableStateOf(setOf<String>()) }

    if (showWipeDialog) {
        AppConfirmationDialog(
            title = "Wipe All Records? / تمام ریکارڈز ختم کریں؟",
            message = "Are you sure you want to permanently clear all student admissions, fee payments, and records? This will leave your academy with 0 records.",
            confirmText = "Wipe All Records",
            isDestructive = true,
            onConfirm = {
                viewModel.clearAllRecords()
                showWipeDialog = false
            },
            onDismiss = { showWipeDialog = false }
        )
    }

    // Active course summary if a specific course is selected
    val activeSummary = courseFeeSummaries.find { it.courseName.equals(filterCourse, ignoreCase = true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Screen.AddEditStudent.createRoute()) },
                containerColor = GoldPrimary,
                contentColor = NavyDark,
                modifier = Modifier.testTag("add_student_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Student")
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AcademyHeaderLogo(
                        academyName = settings.academyName,
                        tagline = "Course-wise Admissions & Fee Directory",
                        compact = true
                    )
                }
                if (allStudents.isNotEmpty()) {
                    IconButton(
                        onClick = { showWipeDialog = true },
                        modifier = Modifier.testTag("wipe_students_header_btn")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Wipe Records", tint = DangerRed)
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("student_search_field"),
                placeholder = { Text("Search by name, roll no, course, mobile...", color = TextSecondaryDark, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldPrimary) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark,
                    focusedContainerColor = NavyCard,
                    unfocusedContainerColor = NavyCard
                ),
                singleLine = true
            )

            // Course Filter Tabs (CIT, Trading, etc.)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    val selected = filterCourse == null
                    SuggestionChip(
                        onClick = { viewModel.setFilterCourse(null) },
                        label = {
                            Text(
                                text = "All Courses (${allStudents.size})",
                                color = if (selected) NavyDark else TextPrimaryDark,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (selected) GoldPrimary else NavyCard
                        ),
                        border = BorderStroke(1.dp, if (selected) GoldBright else NavyBorder)
                    )
                }

                items(courses) { course ->
                    val selected = filterCourse.equals(course.name, ignoreCase = true)
                    val count = allStudents.count { it.courseName.equals(course.name, ignoreCase = true) }
                    SuggestionChip(
                        onClick = {
                            viewModel.setFilterCourse(if (selected) null else course.name)
                        },
                        label = {
                            Text(
                                text = "${course.name} ($count)",
                                color = if (selected) NavyDark else TextPrimaryDark,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (selected) GoldPrimary else NavyCard
                        ),
                        border = BorderStroke(1.dp, if (selected) GoldBright else NavyBorder)
                    )
                }
            }

            // Financial Summary Banner for Selected Course or Overall
            if (activeSummary != null) {
                // Course-Specific Financial Banner (e.g. CIT or Trading)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PurpleAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${activeSummary.courseName} Course Records",
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Enrolled Students: ${activeSummary.studentCount} طالب علم",
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { onNavigate(Screen.AddEditStudent.createRoute()) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("+ Admit in ${activeSummary.courseName}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Course Fee", color = TextSecondaryDark, fontSize = 10.sp)
                                Text(
                                    text = "Rs. ${String.format(Locale.US, "%,.0f", activeSummary.totalFee)}",
                                    color = TextPrimaryDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text("Fee Paid (وصول)", color = SuccessGreen, fontSize = 10.sp)
                                Text(
                                    text = "Rs. ${String.format(Locale.US, "%,.0f", activeSummary.paidFee)}",
                                    color = SuccessGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Remaining Balance (بقایا)", color = DangerRed, fontSize = 10.sp)
                                Text(
                                    text = "Rs. ${String.format(Locale.US, "%,.0f", activeSummary.remainingFee)}",
                                    color = DangerRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else if (allStudents.isNotEmpty()) {
                // All Courses Overall Summary & View Switcher
                val totalAllFees = allStudents.sumOf { if (it.finalFee > 0) it.finalFee else it.courseFee }
                val totalAllPaid = allStudents.sumOf { it.paidFee }
                val totalAllBalance = allStudents.sumOf { it.remainingFee }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "All Courses: ${allStudents.size} Students Enrolled",
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Toggle Grouped by Course vs Single List
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavyLight)
                                    .clickable { isGroupedView = !isGroupedView }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isGroupedView) Icons.Default.ViewAgenda else Icons.Default.ViewList,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isGroupedView) "Grouped by Course" else "Single List",
                                    color = TextPrimaryDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total: Rs. ${String.format(Locale.US, "%,.0f", totalAllFees)}", color = TextSecondaryDark, fontSize = 11.sp)
                            Text("Paid: Rs. ${String.format(Locale.US, "%,.0f", totalAllPaid)}", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Balance: Rs. ${String.format(Locale.US, "%,.0f", totalAllBalance)}", color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Students List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredStudents.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(NavyLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (filterCourse != null)
                                        "No Students in ${filterCourse} Course"
                                    else if (searchQuery.isNotBlank())
                                        "No students matching search criteria"
                                    else
                                        "No Students Registered Yet",
                                    color = TextPrimaryDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (filterCourse != null)
                                        "No students have been admitted into ${filterCourse} yet. Tap button below to enroll a student in this course."
                                    else
                                        "Start by admitting students into courses like CIT, Trading, Spoken English, etc.",
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onNavigate(Screen.AddEditStudent.createRoute()) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (filterCourse != null) "Admit Student in ${filterCourse}" else "Admit New Student (+ نیا طالب علم)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                } else if (filterCourse == null && isGroupedView) {
                    // Grouped by Course View (CIT, Trading, etc. segregated)
                    val grouped = filteredStudents.groupBy { it.courseName }
                    grouped.forEach { (courseName, studentsInCourse) ->
                        val courseTotal = studentsInCourse.sumOf { if (it.finalFee > 0) it.finalFee else it.courseFee }
                        val coursePaid = studentsInCourse.sumOf { it.paidFee }
                        val courseBalance = studentsInCourse.sumOf { it.remainingFee }
                        val isCollapsed = collapsedCourses.contains(courseName)

                        item {
                            CourseGroupHeader(
                                courseName = courseName,
                                studentCount = studentsInCourse.size,
                                totalFee = courseTotal,
                                paidFee = coursePaid,
                                balanceFee = courseBalance,
                                isCollapsed = isCollapsed,
                                onToggleCollapse = {
                                    collapsedCourses = if (isCollapsed) {
                                        collapsedCourses - courseName
                                    } else {
                                        collapsedCourses + courseName
                                    }
                                },
                                onSelectCourse = { viewModel.setFilterCourse(courseName) }
                            )
                        }

                        if (!isCollapsed) {
                            items(studentsInCourse) { student ->
                                StudentCourseItemCard(
                                    student = student,
                                    onNavigate = onNavigate
                                )
                            }
                        } else {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { collapsedCourses = collapsedCourses - courseName }
                                        .border(1.dp, NavyBorder, RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${studentsInCourse.size} students admitted in $courseName (Click to show list)",
                                            color = TextSecondaryDark,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Show ▼",
                                            color = GoldPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    // Filtered or Flat List
                    items(filteredStudents) { student ->
                        StudentCourseItemCard(
                            student = student,
                            onNavigate = onNavigate
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
}

@Composable
fun CourseGroupHeader(
    courseName: String,
    studentCount: Int,
    totalFee: Double,
    paidFee: Double,
    balanceFee: Double,
    isCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
    onSelectCourse: () -> Unit
) {
    val isCit = courseName.contains("CIT", ignoreCase = true)
    val isTrading = courseName.contains("Trading", ignoreCase = true)
    val accentColor = when {
        isCit -> InfoBlue
        isTrading -> GoldBright
        courseName.contains("English", ignoreCase = true) -> SuccessGreen
        else -> PurpleAccent
    }
    val courseIcon = when {
        isCit -> Icons.Default.Computer
        isTrading -> Icons.Default.TrendingUp
        else -> Icons.Default.School
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onToggleCollapse).weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.18f))
                            .border(1.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(courseIcon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$courseName Course",
                                color = accentColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$studentCount Admitted",
                                    color = accentColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = when {
                                isCit -> "سی آئی ٹی - داخلہ اور فیس ریکارڈ"
                                isTrading -> "ٹریڈنگ - داخلہ اور فیس ریکارڈ"
                                else -> "کورس داخلہ و فیس ریکارڈ"
                            },
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Focus Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavyLight)
                            .clickable(onClick = onSelectCourse)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Focus",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onToggleCollapse,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isCollapsed) "Expand" else "Collapse",
                            tint = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = NavyBorder.copy(alpha = 0.4f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // 3-Column Financial Metrics Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyLight, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Fee", color = TextSecondaryDark, fontSize = 9.sp)
                    Text(
                        text = "Rs. ${String.format(Locale.US, "%,.0f", totalFee)}",
                        color = TextPrimaryDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Paid (وصول)", color = SuccessGreen, fontSize = 9.sp)
                    Text(
                        text = "Rs. ${String.format(Locale.US, "%,.0f", paidFee)}",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance (بقایا)", color = if (balanceFee > 0) DangerRed else SuccessGreen, fontSize = 9.sp)
                    Text(
                        text = "Rs. ${String.format(Locale.US, "%,.0f", balanceFee)}",
                        color = if (balanceFee > 0) DangerRed else SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StudentCourseItemCard(
    student: Student,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .clickable { onNavigate(Screen.StudentDetail.createRoute(student.id)) }
            .testTag("student_card_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo / Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NavyLight)
                        .border(1.5.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (student.photoUri.isNotBlank() && File(student.photoUri).exists()) {
                        AsyncImage(
                            model = File(student.photoUri),
                            contentDescription = student.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = student.name.take(2).uppercase(),
                            color = GoldBright,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = student.name,
                            color = TextPrimaryDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "S/D of ${student.fatherName} • ${student.studentId}",
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = student.studentMobile.ifBlank { "No Mobile" },
                            color = GoldSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Course Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                student.courseName.contains("CIT", ignoreCase = true) -> InfoBlue.copy(alpha = 0.2f)
                                student.courseName.contains("Trading", ignoreCase = true) -> GoldPrimary.copy(alpha = 0.2f)
                                else -> PurpleAccent.copy(alpha = 0.2f)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                student.courseName.contains("CIT", ignoreCase = true) -> InfoBlue
                                student.courseName.contains("Trading", ignoreCase = true) -> GoldPrimary
                                else -> PurpleAccent
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = student.courseName,
                        color = when {
                            student.courseName.contains("CIT", ignoreCase = true) -> InfoBlue
                            student.courseName.contains("Trading", ignoreCase = true) -> GoldBright
                            else -> PurpleAccent
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fees Breakdown Row: Total Fee | Paid Fee | Remaining Balance
            val effectiveFee = if (student.finalFee > 0) student.finalFee else student.courseFee
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyLight, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Course Fee", color = TextSecondaryDark, fontSize = 9.sp)
                    Text(
                        text = "Rs. ${String.format(Locale.US, "%,.0f", effectiveFee)}",
                        color = TextPrimaryDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Text("Paid (وصول)", color = SuccessGreen, fontSize = 9.sp)
                    Text(
                        text = "Rs. ${String.format(Locale.US, "%,.0f", student.paidFee)}",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance (بقایا)", color = if (student.remainingFee > 0) DangerRed else SuccessGreen, fontSize = 9.sp)
                    Text(
                        text = if (student.remainingFee <= 0) "Cleared ✓" else "Rs. ${String.format(Locale.US, "%,.0f", student.remainingFee)}",
                        color = if (student.remainingFee > 0) DangerRed else SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Actions: Collect Fee & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onNavigate(Screen.StudentDetail.createRoute(student.id)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details / Profile", fontSize = 11.sp, color = GoldPrimary)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onNavigate(Screen.CollectFee.createRoute(student.id)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (student.remainingFee > 0) SuccessGreen else NavyLight,
                        contentColor = if (student.remainingFee > 0) NavyDark else TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (student.remainingFee > 0) "Collect Fee (فیس لیں)" else "Add Payment",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
