package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.FinancialCanvasChart
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
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val totalCollected by viewModel.totalCollectedFee.collectAsState()
    val totalPending by viewModel.totalPendingFee.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val totalSalaries by viewModel.totalSalaryPaid.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val teachers by viewModel.teachers.collectAsState()

    val combinedExpenses = totalExpenses + totalSalaries
    val netProfit = totalCollected - combinedExpenses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AcademyHeaderLogo(
                academyName = settings.academyName,
                tagline = "Executive Analytics & PDF Reports",
                compact = true
            )
        }

        // Financial Canvas Chart
        item {
            FinancialCanvasChart(
                income = totalCollected,
                expenses = combinedExpenses
            )
        }

        // Summary Accounting Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Executive Statement of Accounts", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    ReportStatRow(label = "Total Fee Income:", value = "Rs. ${String.format(Locale.US, "%,.0f", totalCollected)}", color = SuccessGreen)
                    ReportStatRow(label = "Teacher Salaries Paid:", value = "Rs. ${String.format(Locale.US, "%,.0f", totalSalaries)}", color = DangerRed)
                    ReportStatRow(label = "Operational Expenses:", value = "Rs. ${String.format(Locale.US, "%,.0f", totalExpenses)}", color = DangerRed)
                    ReportStatRow(label = "Pending Receivables (Due Fee):", value = "Rs. ${String.format(Locale.US, "%,.0f", totalPending)}", color = WarningAmber)

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NavyBorder))

                    ReportStatRow(
                        label = "Net Balance (Profit / Loss):",
                        value = "Rs. ${String.format(Locale.US, "%,.0f", netProfit)}",
                        color = if (netProfit >= 0) SuccessGreen else DangerRed,
                        isBold = true
                    )
                }
            }
        }

        // Downloadable PDF Reports Section
        item {
            Text("Download & Export Official PDF Reports", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        // Financial Report Card
        item {
            ReportActionCard(
                title = "Executive Financial Accounts Report (PDF)",
                description = "Complete audit statement including fee collections, faculty salaries, vendor expenses, and net profit ledger.",
                icon = Icons.Default.Assessment,
                color = SuccessGreen,
                onDownload = { viewModel.generateAndShareFinancialReportPdf(share = false) },
                onShare = { viewModel.generateAndShareFinancialReportPdf(share = true) },
                testTag = "report_financial"
            )
        }

        // Students & Fee Collection Report
        item {
            ReportActionCard(
                title = "Students Enrollment & Fee Ledger (PDF)",
                description = "All enrolled students, course breakdown, paid amount, and outstanding fee balance.",
                icon = Icons.Default.Groups,
                color = InfoBlue,
                onDownload = { viewModel.generateAndShareFinancialReportPdf(share = false) },
                onShare = { viewModel.generateAndShareFinancialReportPdf(share = true) },
                testTag = "report_students"
            )
        }

        // Faculty & Salary Report
        item {
            ReportActionCard(
                title = "Faculty & Staff Salary Ledger (PDF)",
                description = "Teachers directory, assigned subjects, disbursed salary vouchers and bonuses.",
                icon = Icons.Default.MonetizationOn,
                color = WarningAmber,
                onDownload = { viewModel.generateAndShareFinancialReportPdf(share = false) },
                onShare = { viewModel.generateAndShareFinancialReportPdf(share = true) },
                testTag = "report_salaries"
            )
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun ReportStatRow(
    label: String,
    value: String,
    color: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextPrimaryDark, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReportActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(description, color = TextSecondaryDark, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDownload,
                    colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = NavyDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download PDF", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
