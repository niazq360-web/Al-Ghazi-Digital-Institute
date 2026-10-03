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
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.StatsCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeeManagementScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val allPayments by viewModel.allPayments.collectAsState()
    val totalCollected by viewModel.totalCollectedFee.collectAsState()
    val totalPending by viewModel.totalPendingFee.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val filteredPayments = allPayments.filter { p ->
        searchQuery.isBlank() ||
                p.studentName.contains(searchQuery, ignoreCase = true) ||
                p.receiptNo.contains(searchQuery, ignoreCase = true) ||
                p.studentRollNo.contains(searchQuery, ignoreCase = true) ||
                p.courseName.contains(searchQuery, ignoreCase = true)
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
                tagline = "Fee Management & Payment Receipts",
                compact = true
            )

            // Summary Totals
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsCard(
                    title = "Total Collected",
                    value = "Rs. ${String.format(Locale.US, "%,.0f", totalCollected)}",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "stats_fee_collected"
                )
                StatsCard(
                    title = "Pending Dues",
                    value = "Rs. ${String.format(Locale.US, "%,.0f", totalPending)}",
                    icon = Icons.Default.Receipt,
                    accentColor = DangerRed,
                    modifier = Modifier.weight(1f),
                    testTag = "stats_fee_pending"
                )
            }

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("receipt_search_input"),
                placeholder = { Text("Search by receipt no, student or roll...", color = TextSecondaryDark, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
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

            // Payments List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredPayments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No fee receipts found.", color = TextSecondaryDark, fontSize = 14.sp)
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
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = payment.studentName,
                                    color = TextPrimaryDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${payment.receiptNo} • ${payment.courseName}",
                                    color = GoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(payment.paymentDate))
                                Text(
                                    text = "$dateStr • ${payment.paymentMethod} • By: ${payment.receivedBy}",
                                    color = TextSecondaryDark,
                                    fontSize = 10.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Rs. ${String.format(Locale.US, "%,.0f", payment.paidAmount)}",
                                    color = SuccessGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Bal: Rs. ${String.format(Locale.US, "%,.0f", payment.remainingFee)}",
                                    color = if (payment.remainingFee > 0) DangerRed else TextSecondaryDark,
                                    fontSize = 11.sp
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
    }
}
