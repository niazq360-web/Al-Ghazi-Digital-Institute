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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SyncState
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.FinancialCanvasChart
import com.example.ui.components.StatsCard
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
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val activeCourses by viewModel.activeCourses.collectAsState()
    val allPayments by viewModel.allPayments.collectAsState()
    val totalCollected by viewModel.totalCollectedFee.collectAsState()
    val totalPending by viewModel.totalPendingFee.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val totalSalaries by viewModel.totalSalaryPaid.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val combinedExpenses = totalExpenses + totalSalaries
    val netBalance = totalCollected - combinedExpenses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Institute Brand Header with Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AcademyHeaderLogo(
                    academyName = settings.academyName,
                    tagline = settings.tagline,
                    modifier = Modifier.weight(1f)
                )

                // Cloud Sync Quick Indicator Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
                        .clickable { viewModel.triggerCloudSync() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (syncState) {
                                is SyncState.Syncing -> Icons.Default.CloudSync
                                is SyncState.Success -> Icons.Default.CloudDone
                                else -> Icons.Default.CloudSync
                            },
                            contentDescription = "Sync",
                            tint = when (syncState) {
                                is SyncState.Success -> SuccessGreen
                                is SyncState.Syncing -> WarningAmber
                                else -> TextSecondaryDark
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (syncState) {
                                is SyncState.Syncing -> "Syncing..."
                                is SyncState.Success -> "Cloud Synced"
                                else -> "Cloud Backup"
                            },
                            color = TextSecondaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 12-Month Academic Archive & Retention Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
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
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Retention",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "12-Month Academic Archive Guaranteed",
                            color = GoldBright,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Data stays preserved safely even if reinstalled.",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.extend12MonthsSession() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = NavyDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("extend_session_button")
                    ) {
                        Text("Extend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stats Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatsCard(
                        title = viewModel.getString("total_students"),
                        value = "${allStudents.size}",
                        icon = Icons.Default.Groups,
                        accentColor = InfoBlue,
                        modifier = Modifier.weight(1f),
                        testTag = "stats_students"
                    )
                    StatsCard(
                        title = viewModel.getString("total_courses"),
                        value = "${activeCourses.size}",
                        icon = Icons.Default.School,
                        accentColor = PurpleAccent,
                        modifier = Modifier.weight(1f),
                        testTag = "stats_courses"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatsCard(
                        title = viewModel.getString("fee_collected"),
                        value = "Rs. ${String.format(Locale.US, "%,.0f", totalCollected)}",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = SuccessGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "stats_collected"
                    )
                    StatsCard(
                        title = viewModel.getString("pending_fee"),
                        value = "Rs. ${String.format(Locale.US, "%,.0f", totalPending)}",
                        icon = Icons.Default.Payment,
                        accentColor = DangerRed,
                        modifier = Modifier.weight(1f),
                        testTag = "stats_pending"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatsCard(
                        title = viewModel.getString("total_expenses"),
                        value = "Rs. ${String.format(Locale.US, "%,.0f", combinedExpenses)}",
                        icon = Icons.Default.TrendingDown,
                        accentColor = WarningAmber,
                        subText = "Salaries + Expenses",
                        modifier = Modifier.weight(1f),
                        testTag = "stats_expenses"
                    )
                    StatsCard(
                        title = viewModel.getString("net_balance"),
                        value = "Rs. ${String.format(Locale.US, "%,.0f", netBalance)}",
                        icon = Icons.Default.AccountBalance,
                        accentColor = if (netBalance >= 0) SuccessGreen else DangerRed,
                        subText = if (netBalance >= 0) "Net Profit" else "Deficit",
                        modifier = Modifier.weight(1f),
                        testTag = "stats_net"
                    )
                }
            }
        }

        // Quick Action Buttons Grid
        item {
            Text(
                text = "Quick Management Actions",
                color = GoldPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    title = "Add Student",
                    icon = Icons.Default.PersonAdd,
                    color = InfoBlue,
                    onClick = { onNavigate(Screen.AddEditStudent.createRoute()) },
                    tag = "quick_add_student"
                )
                QuickActionItem(
                    title = "Collect Fee",
                    icon = Icons.Default.MonetizationOn,
                    color = SuccessGreen,
                    onClick = { onNavigate(Screen.CollectFee.createRoute()) },
                    tag = "quick_collect_fee"
                )
                QuickActionItem(
                    title = "Attendance",
                    icon = Icons.Default.AssignmentTurnedIn,
                    color = GoldBright,
                    onClick = { onNavigate(Screen.Attendance.route) },
                    tag = "quick_attendance"
                )
                QuickActionItem(
                    title = "Courses",
                    icon = Icons.Default.School,
                    color = PurpleAccent,
                    onClick = { onNavigate(Screen.Courses.route) },
                    tag = "quick_courses"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionItem(
                    title = "Teachers",
                    icon = Icons.Default.Groups,
                    color = InfoBlue,
                    onClick = { onNavigate(Screen.Teachers.route) },
                    tag = "quick_teachers"
                )
                QuickActionItem(
                    title = "Expenses",
                    icon = Icons.Default.TrendingDown,
                    color = WarningAmber,
                    onClick = { onNavigate(Screen.Expenses.route) },
                    tag = "quick_expenses"
                )
                QuickActionItem(
                    title = "Certificates",
                    icon = Icons.Default.CardMembership,
                    color = GoldPrimary,
                    onClick = { onNavigate(Screen.Certificates.route) },
                    tag = "quick_certificates"
                )
                QuickActionItem(
                    title = "Reports",
                    icon = Icons.Default.Assessment,
                    color = SuccessGreen,
                    onClick = { onNavigate(Screen.Reports.route) },
                    tag = "quick_reports"
                )
            }
        }

        // Financial Chart: Income vs Expenses
        item {
            FinancialCanvasChart(
                income = totalCollected,
                expenses = combinedExpenses
            )
        }

        // Recent Fee Collections
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Fee Receipts",
                    color = GoldPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All (${allPayments.size})",
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onNavigate(Screen.Fees.route) }
                )
            }
        }

        items(allPayments.take(5)) { payment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                    .clickable { onNavigate(Screen.ReceiptAction.createRoute(payment.id)) }
                    .testTag("payment_item_${payment.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Receipt",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = payment.studentName,
                            color = TextPrimaryDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${payment.courseName} • ${payment.receiptNo}",
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                        val dateFormatted = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(payment.paymentDate))
                        Text(
                            text = "$dateFormatted • ${payment.paymentMethod}",
                            color = GoldSecondary,
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
                            text = "Rem: Rs. ${String.format(Locale.US, "%,.0f", payment.remainingFee)}",
                            color = if (payment.remainingFee > 0) DangerRed else TextSecondaryDark,
                            fontSize = 11.sp
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

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(NavyCard)
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = TextPrimaryDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
