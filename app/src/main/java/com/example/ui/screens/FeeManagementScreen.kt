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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.StatsCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.InfoBlue
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.LinearProgressIndicator
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FeeScreenTab {
    COURSE_BREAKDOWN,
    ANALYTICS_GRAPHS,
    RECEIPTS_HISTORY,
    BALANCE_DUES
}

@Composable
fun FeeManagementScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val allPayments by viewModel.allPayments.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val courses by viewModel.activeCourses.collectAsState()
    val courseFeeSummaries by viewModel.courseFeeSummaries.collectAsState()
    val totalCollected by viewModel.totalCollectedFee.collectAsState()
    val totalPending by viewModel.totalPendingFee.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var activeTab by remember { mutableStateOf(FeeScreenTab.COURSE_BREAKDOWN) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCourseFilter by remember { mutableStateOf<String?>(null) }
    var expandedCourseName by remember { mutableStateOf<String?>(null) }

    val filteredPayments = allPayments.filter { p ->
        val matchesCourse = selectedCourseFilter == null || p.courseName.equals(selectedCourseFilter, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                p.studentName.contains(searchQuery, ignoreCase = true) ||
                p.receiptNo.contains(searchQuery, ignoreCase = true) ||
                p.studentRollNo.contains(searchQuery, ignoreCase = true) ||
                p.courseName.contains(searchQuery, ignoreCase = true)
        matchesCourse && matchesQuery
    }

    val defaulterStudents = allStudents.filter { s ->
        val hasDues = s.remainingFee > 0
        val matchesCourse = selectedCourseFilter == null || s.courseName.equals(selectedCourseFilter, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                s.name.contains(searchQuery, ignoreCase = true) ||
                s.studentId.contains(searchQuery, ignoreCase = true) ||
                s.courseName.contains(searchQuery, ignoreCase = true)
        hasDues && matchesCourse && matchesQuery
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Screen.CollectFee.createRoute()) },
                containerColor = SuccessGreen,
                contentColor = NavyDark,
                modifier = Modifier.testTag("collect_fee_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Collect Fee")
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
                tagline = "Course-wise Fee Records, Receipts & Balances",
                compact = true
            )

            // Summary Totals
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsCard(
                    title = "Total Collected (وصول)",
                    value = "Rs. ${String.format(Locale.US, "%,.0f", totalCollected)}",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "stats_fee_collected"
                )
                StatsCard(
                    title = "Pending Dues (بقایا جات)",
                    value = "Rs. ${String.format(Locale.US, "%,.0f", totalPending)}",
                    icon = Icons.Default.Receipt,
                    accentColor = DangerRed,
                    modifier = Modifier.weight(1f),
                    testTag = "stats_fee_pending"
                )
            }

            // 4 Mode Switcher Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyCard, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FeeTabButton(
                    title = "Courses\nکورس فیس",
                    icon = Icons.Default.School,
                    isSelected = activeTab == FeeScreenTab.COURSE_BREAKDOWN,
                    modifier = Modifier.weight(1f)
                ) {
                    activeTab = FeeScreenTab.COURSE_BREAKDOWN
                }
                FeeTabButton(
                    title = "Smart Charts\nاسمارٹ گراف %",
                    icon = Icons.Default.Assessment,
                    isSelected = activeTab == FeeScreenTab.ANALYTICS_GRAPHS,
                    modifier = Modifier.weight(1f)
                ) {
                    activeTab = FeeScreenTab.ANALYTICS_GRAPHS
                }
                FeeTabButton(
                    title = "Receipts\nرسیدیں",
                    icon = Icons.Default.Receipt,
                    isSelected = activeTab == FeeScreenTab.RECEIPTS_HISTORY,
                    modifier = Modifier.weight(1f)
                ) {
                    activeTab = FeeScreenTab.RECEIPTS_HISTORY
                }
                FeeTabButton(
                    title = "Defaulters\nبقایا جات",
                    icon = Icons.Default.Warning,
                    isSelected = activeTab == FeeScreenTab.BALANCE_DUES,
                    modifier = Modifier.weight(1f)
                ) {
                    activeTab = FeeScreenTab.BALANCE_DUES
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // TAB 1: COURSE-WISE FEE BREAKDOWN (CIT, Trading, etc.)
            // ==========================================
            if (activeTab == FeeScreenTab.COURSE_BREAKDOWN) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Course-wise Fee Records & Balances / کورس کے لحاظ سے فیس ریکارڈ",
                            color = GoldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "CIT, Trading اور دیگر تمام کورسز کی فیسیں اور بقایا جات الگ الگ دیکھیں",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }

                    if (courseFeeSummaries.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                colors = CardDefaults.cardColors(containerColor = NavyCard),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Courses Available", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    items(courseFeeSummaries) { summary ->
                        val isExpanded = expandedCourseName == summary.courseName

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isExpanded) GoldPrimary else NavyBorder,
                                    RoundedCornerShape(14.dp)
                                ),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Course Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        summary.courseName.contains("CIT", ignoreCase = true) -> InfoBlue.copy(alpha = 0.2f)
                                                        summary.courseName.contains("Trading", ignoreCase = true) -> GoldPrimary.copy(alpha = 0.2f)
                                                        else -> PurpleAccent.copy(alpha = 0.2f)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = when {
                                                    summary.courseName.contains("CIT", ignoreCase = true) -> InfoBlue
                                                    summary.courseName.contains("Trading", ignoreCase = true) -> GoldBright
                                                    else -> PurpleAccent
                                                },
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = summary.courseName,
                                                color = TextPrimaryDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "Admitted Students: ${summary.studentCount}",
                                                color = TextSecondaryDark,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    // Expand / Collapse
                                    IconButton(
                                        onClick = {
                                            expandedCourseName = if (isExpanded) null else summary.courseName
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Expand",
                                            tint = GoldPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Financial Breakdown Row (Total, Paid, Balance)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(NavyLight, RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Course Fee", color = TextSecondaryDark, fontSize = 10.sp)
                                        Text(
                                            text = "Rs. ${String.format(Locale.US, "%,.0f", summary.totalFee)}",
                                            color = TextPrimaryDark,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column {
                                        Text("Paid Fee (وصول)", color = SuccessGreen, fontSize = 10.sp)
                                        Text(
                                            text = "Rs. ${String.format(Locale.US, "%,.0f", summary.paidFee)}",
                                            color = SuccessGreen,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Balance Due (بقایا)", color = DangerRed, fontSize = 10.sp)
                                        Text(
                                            text = "Rs. ${String.format(Locale.US, "%,.0f", summary.remainingFee)}",
                                            color = if (summary.remainingFee > 0) DangerRed else SuccessGreen,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Quick button to toggle list of students
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            expandedCourseName = if (isExpanded) null else summary.courseName
                                        }
                                    ) {
                                        Text(
                                            text = if (isExpanded) "Hide Student Records ▲" else "View ${summary.courseName} Students (${summary.studentCount}) ▼",
                                            color = GoldPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.setFilterCourse(summary.courseName)
                                            onNavigate(Screen.Students.route)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Students Directory", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Expanded Students List for this Course
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (summary.students.isEmpty()) {
                                        Text(
                                            text = "No students admitted in ${summary.courseName} yet.",
                                            color = TextSecondaryDark,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Enrolled Students in ${summary.courseName}:",
                                            color = GoldSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        summary.students.forEach { st ->
                                            val effectiveFee = if (st.finalFee > 0) st.finalFee else st.courseFee
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp),
                                                colors = CardDefaults.cardColors(containerColor = NavyDark),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = st.name,
                                                            color = TextPrimaryDark,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Roll: ${st.studentId} • Total: Rs. ${effectiveFee.toInt()}",
                                                            color = TextSecondaryDark,
                                                            fontSize = 10.sp
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text(
                                                                text = "Paid: Rs. ${st.paidFee.toInt()}",
                                                                color = SuccessGreen,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Text(
                                                                text = if (st.remainingFee <= 0) "Cleared" else "Bal: Rs. ${st.remainingFee.toInt()}",
                                                                color = if (st.remainingFee > 0) DangerRed else SuccessGreen,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.width(8.dp))

                                                        Button(
                                                            onClick = { onNavigate(Screen.CollectFee.createRoute(st.id)) },
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = if (st.remainingFee > 0) SuccessGreen else NavyLight,
                                                                contentColor = if (st.remainingFee > 0) NavyDark else TextPrimaryDark
                                                            ),
                                                            shape = RoundedCornerShape(6.dp),
                                                            modifier = Modifier.height(28.dp)
                                                        ) {
                                                            Text("Fee", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
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

            // ==========================================
            // TAB 2: SMART ANALYTICS & GRAPH CHARTS WITH PERCENTAGES
            // ==========================================
            if (activeTab == FeeScreenTab.ANALYTICS_GRAPHS) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "Smart Fee Analytics & Percentage Graphs / اسمارٹ فیس گراف",
                                color = GoldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "فیس وصولی، بقایا جات، کورس کے لحاظ سے تناسب اور ماہانہ تجزیہ",
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // 1. Overall Fee Recovery Rate Card with Dual Animated Bar & Exact Percentages
                    item {
                        val totalTarget = (totalCollected + totalPending).coerceAtLeast(1.0)
                        val collectedPct = (totalCollected / totalTarget) * 100
                        val pendingPct = (totalPending / totalTarget) * 100

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PieChart, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Overall Fee Recovery Rate", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (collectedPct >= 70) SuccessGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${String.format(Locale.US, "%.1f", collectedPct)}% Recovered",
                                            color = if (collectedPct >= 70) SuccessGreen else WarningAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Custom Dual-Segment Visual Bar
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    val barWidth = size.width
                                    val barHeight = size.height

                                    // Draw background
                                    drawRoundRect(
                                        color = NavyLight,
                                        size = size,
                                        cornerRadius = CornerRadius(12f, 12f)
                                    )

                                    val colWidth = (barWidth * (collectedPct / 100f).toFloat()).coerceIn(0f, barWidth)
                                    if (colWidth > 0f) {
                                        drawRoundRect(
                                            brush = Brush.horizontalGradient(listOf(SuccessGreen, GoldPrimary)),
                                            size = Size(colWidth, barHeight),
                                            cornerRadius = CornerRadius(12f, 12f)
                                        )
                                    }

                                    val pendWidth = (barWidth * (pendingPct / 100f).toFloat()).coerceIn(0f, barWidth - colWidth)
                                    if (pendWidth > 0f) {
                                        drawRoundRect(
                                            color = DangerRed,
                                            topLeft = Offset(barWidth - pendWidth, 0f),
                                            size = Size(pendWidth, barHeight),
                                            cornerRadius = CornerRadius(12f, 12f)
                                        )
                                    }
                                }

                                // Legend & Metrics with Exact Percentages
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SuccessGreen))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Paid / وصول (${String.format(Locale.US, "%.1f", collectedPct)}%)", color = TextSecondaryDark, fontSize = 11.sp)
                                        }
                                        Text(
                                            "Rs. ${String.format(Locale.US, "%,.0f", totalCollected)}",
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(DangerRed))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Pending / بقایا (${String.format(Locale.US, "%.1f", pendingPct)}%)", color = TextSecondaryDark, fontSize = 11.sp)
                                        }
                                        Text(
                                            "Rs. ${String.format(Locale.US, "%,.0f", totalPending)}",
                                            color = DangerRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Course-wise Fee Breakdown & Share % Chart
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Course-wise Fee Collection & Share %",
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "کورس کی بنیاد پر فیصد",
                                        color = TextSecondaryDark,
                                        fontSize = 10.sp
                                    )
                                }

                                if (courseFeeSummaries.isEmpty()) {
                                    Text("No course data available.", color = TextSecondaryDark, fontSize = 12.sp)
                                } else {
                                    courseFeeSummaries.forEach { summary ->
                                        val isCit = summary.courseName.contains("CIT", ignoreCase = true)
                                        val isTrading = summary.courseName.contains("Trading", ignoreCase = true)
                                        val courseColor = if (isCit) InfoBlue else if (isTrading) GoldBright else PurpleAccent

                                        val recoveryPct = if (summary.totalFee > 0) {
                                            ((summary.paidFee / summary.totalFee) * 100).coerceIn(0.0, 100.0)
                                        } else 100.0

                                        val shareOfTotalPct = if (totalCollected > 0) {
                                            ((summary.paidFee / totalCollected) * 100).coerceIn(0.0, 100.0)
                                        } else 0.0

                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(courseColor))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = summary.courseName,
                                                        color = TextPrimaryDark,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "(${summary.studentCount} students)",
                                                        color = TextSecondaryDark,
                                                        fontSize = 10.sp
                                                    )
                                                }

                                                Text(
                                                    text = "${String.format(Locale.US, "%.1f", recoveryPct)}% Recovered",
                                                    color = if (recoveryPct >= 70) SuccessGreen else GoldBright,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }

                                            // Progress Bar
                                            LinearProgressIndicator(
                                                progress = { (recoveryPct / 100f).toFloat() },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = courseColor,
                                                trackColor = NavyLight
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Paid: Rs. ${String.format(Locale.US, "%,.0f", summary.paidFee)} • Bal: Rs. ${String.format(Locale.US, "%,.0f", summary.remainingFee)}",
                                                    color = TextSecondaryDark,
                                                    fontSize = 10.sp
                                                )
                                                Text(
                                                    text = "Academy Share: ${String.format(Locale.US, "%.1f", shareOfTotalPct)}%",
                                                    color = GoldSecondary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Monthly Fee Collections Breakdown & Graph
                    item {
                        val monthlyGroups = allPayments
                            .groupBy { it.feeMonth.ifBlank { "Current Month" } }
                            .map { (month, payments) ->
                                val monthSum = payments.sumOf { it.paidAmount }
                                val pct = if (totalCollected > 0) (monthSum / totalCollected) * 100 else 0.0
                                Triple(month, monthSum, pct)
                            }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Monthly Fee Collections & % Share", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Text("ماہانہ فیس تناسب", color = TextSecondaryDark, fontSize = 10.sp)
                                }

                                if (monthlyGroups.isEmpty()) {
                                    Text("No monthly payments recorded yet.", color = TextSecondaryDark, fontSize = 12.sp)
                                } else {
                                    monthlyGroups.forEach { (month, amount, pct) ->
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(month, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                Text(
                                                    "Rs. ${String.format(Locale.US, "%,.0f", amount)} (${String.format(Locale.US, "%.1f", pct)}%)",
                                                    color = SuccessGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            LinearProgressIndicator(
                                                progress = { (pct / 100f).toFloat().coerceIn(0f, 1f) },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = GoldPrimary,
                                                trackColor = NavyLight
                                            )
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

            // ==========================================
            // TAB 3: RECEIPTS & HISTORY
            // ==========================================
            if (activeTab == FeeScreenTab.RECEIPTS_HISTORY) {
                // Course Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val selected = selectedCourseFilter == null
                        SuggestionChip(
                            onClick = { selectedCourseFilter = null },
                            label = { Text("All Courses (${allPayments.size})", color = if (selected) NavyDark else TextPrimaryDark) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (selected) GoldPrimary else NavyCard
                            ),
                            border = BorderStroke(1.dp, if (selected) GoldBright else NavyBorder)
                        )
                    }
                    items(courses) { course ->
                        val selected = selectedCourseFilter.equals(course.name, ignoreCase = true)
                        val count = allPayments.count { it.courseName.equals(course.name, ignoreCase = true) }
                        SuggestionChip(
                            onClick = {
                                selectedCourseFilter = if (selected) null else course.name
                            },
                            label = { Text("${course.name} ($count)", color = if (selected) NavyDark else TextPrimaryDark) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (selected) GoldPrimary else NavyCard
                            ),
                            border = BorderStroke(1.dp, if (selected) GoldBright else NavyBorder)
                        )
                    }
                }

                // Search Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    placeholder = { Text("Search receipts by student, roll or receipt no...", color = TextSecondaryDark, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                    shape = RoundedCornerShape(12.dp),
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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredPayments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No fee receipts found.", color = TextSecondaryDark, fontSize = 13.sp)
                            }
                        }
                    }

                    items(filteredPayments) { payment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                                .clickable { onNavigate(Screen.ReceiptAction.createRoute(payment.id)) }
                                .testTag("fee_payment_card_${payment.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = payment.studentName,
                                        color = TextPrimaryDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${payment.receiptNo} • ${payment.courseName}",
                                        color = GoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (payment.feeMonth.isNotBlank()) {
                                        Text(
                                            text = "فیس برائے ماہ: ${payment.feeMonth}",
                                            color = GoldBright,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(payment.paymentDate))
                                    Text(
                                        text = "$dateStr • ${payment.paymentMethod}",
                                        color = TextSecondaryDark,
                                        fontSize = 10.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rs. ${String.format(Locale.US, "%,.0f", payment.paidAmount)}",
                                        color = SuccessGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Bal: Rs. ${String.format(Locale.US, "%,.0f", payment.remainingFee)}",
                                        color = if (payment.remainingFee > 0) DangerRed else TextSecondaryDark,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }

            // ==========================================
            // TAB 3: BALANCE DUES / DEFAULTERS
            // ==========================================
            if (activeTab == FeeScreenTab.BALANCE_DUES) {
                // Course Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val selected = selectedCourseFilter == null
                        SuggestionChip(
                            onClick = { selectedCourseFilter = null },
                            label = { Text("All Courses (${defaulterStudents.size})", color = if (selected) NavyDark else TextPrimaryDark) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (selected) GoldPrimary else NavyCard
                            ),
                            border = BorderStroke(1.dp, if (selected) GoldBright else NavyBorder)
                        )
                    }
                    items(courses) { course ->
                        val selected = selectedCourseFilter.equals(course.name, ignoreCase = true)
                        val count = allStudents.count { it.courseName.equals(course.name, ignoreCase = true) && it.remainingFee > 0 }
                        SuggestionChip(
                            onClick = {
                                selectedCourseFilter = if (selected) null else course.name
                            },
                            label = { Text("${course.name} ($count)", color = if (selected) NavyDark else TextPrimaryDark) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (selected) GoldPrimary else NavyCard
                            ),
                            border = BorderStroke(1.dp, if (selected) GoldBright else NavyBorder)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Students with Pending Balance Dues",
                                color = DangerRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val totalDuesInFilter = defaulterStudents.sumOf { it.remainingFee }
                            Text(
                                text = "Total Dues: Rs. ${String.format(Locale.US, "%,.0f", totalDuesInFilter)}",
                                color = DangerRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (defaulterStudents.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                colors = CardDefaults.cardColors(containerColor = NavyCard),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Pending Balance!", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("All students in this course have fully cleared their fees.", color = TextSecondaryDark, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    items(defaulterStudents) { student ->
                        val effectiveFee = if (student.finalFee > 0) student.finalFee else student.courseFee
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DangerRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student.name,
                                            color = TextPrimaryDark,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${student.courseName} • Roll: ${student.studentId} • Mob: ${student.studentMobile}",
                                            color = TextSecondaryDark,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DangerRed.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Due: Rs. ${String.format(Locale.US, "%,.0f", student.remainingFee)}",
                                            color = DangerRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(NavyLight, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Package: Rs. ${effectiveFee.toInt()}", color = TextSecondaryDark, fontSize = 11.sp)
                                    Text("Paid: Rs. ${student.paidFee.toInt()}", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                    Button(
                                        onClick = { onNavigate(Screen.CollectFee.createRoute(student.id)) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = NavyDark),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Collect Fee", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
}

@Composable
private fun FeeTabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GoldPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) NavyDark else GoldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = if (isSelected) NavyDark else TextPrimaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}
