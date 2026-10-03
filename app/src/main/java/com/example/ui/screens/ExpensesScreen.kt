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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingDown
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.components.AppConfirmationDialog
import com.example.ui.components.StatsCard
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val categories = listOf(
        "Rent",
        "Electricity / Utilities",
        "Staff Salary",
        "Marketing / Ads",
        "Equipment & Maintenance",
        "Internet & Tech",
        "Refreshments / Tea",
        "Miscellaneous"
    )

    if (expenseToDelete != null) {
        AppConfirmationDialog(
            title = "Delete Expense?",
            message = "Are you sure you want to delete ${expenseToDelete?.title} (${expenseToDelete?.expenseNo})?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                expenseToDelete?.let { viewModel.deleteExpense(it) }
                expenseToDelete = null
            },
            onDismiss = { expenseToDelete = null }
        )
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(categories.first()) }
        var amountText by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var paidTo by remember { mutableStateOf("") }
        var method by remember { mutableStateOf("Cash") }
        var categoryDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Record New Expenditure", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Expense Title *") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_expense_title")
                        )
                    }
                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                label = { Text("Category") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { categoryDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("select_expense_category")
                            )
                            DropdownMenu(
                                expanded = categoryDropdownExpanded,
                                onDismissRequest = { categoryDropdownExpanded = false },
                                modifier = Modifier.background(NavyCard)
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = TextPrimaryDark) },
                                        onClick = {
                                            category = cat
                                            categoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            label = { Text("Amount (Rs.) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_expense_amount")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = paidTo,
                            onValueChange = { paidTo = it },
                            label = { Text("Paid To / Vendor") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = method,
                            onValueChange = { method = it },
                            label = { Text("Payment Method (Cash / Online)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description / Bill Details") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (title.isBlank() || amount <= 0.0) return@Button
                        viewModel.addExpense(
                            title = title.trim(),
                            category = category,
                            amount = amount,
                            description = description.trim(),
                            paidTo = paidTo.trim(),
                            method = method.trim()
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed, contentColor = Color.White),
                    modifier = Modifier.testTag("save_expense_btn")
                ) {
                    Text("Record Expense", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard
        )
    }

    val filtered = expenses.filter {
        selectedCategoryFilter == null || it.category == selectedCategoryFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DangerRed,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
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
                tagline = "Expenditure & Accounts Ledger",
                compact = true
            )

            // Total Expenses Card
            StatsCard(
                title = "Total Operating Expenditures",
                value = "Rs. ${String.format(Locale.US, "%,.0f", totalExpenses)}",
                icon = Icons.Default.TrendingDown,
                accentColor = DangerRed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                testTag = "stats_total_expenses"
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val sel = selectedCategoryFilter == null
                    SuggestionChip(
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All (${expenses.size})", color = if (sel) NavyDark else TextPrimaryDark) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (sel) GoldPrimary else NavyCard
                        )
                    )
                }
                items(categories) { cat ->
                    val sel = selectedCategoryFilter == cat
                    SuggestionChip(
                        onClick = { selectedCategoryFilter = if (sel) null else cat },
                        label = { Text(cat, color = if (sel) NavyDark else TextPrimaryDark) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (sel) GoldPrimary else NavyCard
                        )
                    )
                }
            }

            // Expense List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No expenses found.", color = TextSecondaryDark, fontSize = 14.sp)
                        }
                    }
                }

                items(filtered) { exp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                            .testTag("expense_card_${exp.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DangerRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = DangerRed, modifier = Modifier.size(22.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(exp.title, color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${exp.expenseNo} • ${exp.category}", color = GoldPrimary, fontSize = 11.sp)
                                val dateStr = SimpleDateFormat("dd-MMM-yyyy", Locale.US).format(Date(exp.expenseDate))
                                Text("$dateStr • Paid To: ${exp.paidTo.ifBlank { "N/A" }}", color = TextSecondaryDark, fontSize = 10.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Rs. ${String.format(Locale.US, "%,.0f", exp.amount)}", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                IconButton(onClick = { expenseToDelete = exp }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
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
