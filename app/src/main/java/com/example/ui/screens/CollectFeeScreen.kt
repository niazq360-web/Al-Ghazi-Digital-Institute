package com.example.ui.screens

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.navigation.Screen
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
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectFeeScreen(
    preselectedStudentId: Long?,
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var selectedStudent by remember {
        mutableStateOf(
            if (preselectedStudentId != null) allStudents.find { it.id == preselectedStudentId }
            else allStudents.firstOrNull()
        )
    }

    androidx.compose.runtime.LaunchedEffect(allStudents) {
        if (selectedStudent == null && allStudents.isNotEmpty()) {
            selectedStudent = if (preselectedStudentId != null) {
                allStudents.find { it.id == preselectedStudentId } ?: allStudents.firstOrNull()
            } else {
                allStudents.firstOrNull()
            }
        }
    }

    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var methodDropdownExpanded by remember { mutableStateOf(false) }

    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.US) }
    val currentMonthName = remember { monthFormat.format(Date()) }
    val prevMonthName = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -1)
        monthFormat.format(cal.time)
    }
    val twoMonthsAgoName = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -2)
        monthFormat.format(cal.time)
    }
    val threeMonthsAgoName = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -3)
        monthFormat.format(cal.time)
    }
    val pastMonthsList = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until 12) {
            list.add(monthFormat.format(cal.time))
            cal.add(Calendar.MONTH, -1)
        }
        list
    }

    var selectedFeeMonth by remember { mutableStateOf(currentMonthName) }
    var monthDropdownExpanded by remember { mutableStateOf(false) }

    var paidAmountText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var remarks by remember { mutableStateOf("Fee for $currentMonthName") }
    var receivedBy by remember { mutableStateOf(settings.ownerName) }
    var allowAdvance by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paidAmount = paidAmountText.toDoubleOrNull() ?: 0.0
    val totalFee = selectedStudent?.finalFee ?: 0.0
    val alreadyPaid = selectedStudent?.paidFee ?: 0.0
    val currentRemaining = selectedStudent?.remainingFee ?: 0.0

    val newTotalPaid = alreadyPaid + paidAmount
    val calculatedRemaining = (totalFee - newTotalPaid).coerceAtLeast(0.0)

    val methods = listOf("Cash", "EasyPaisa", "JazzCash", "Bank Transfer", "Online Deposit")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fee Collection Receipt", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("collect_fee_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        },
        containerColor = NavyDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Student Selection Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Select Student", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedStudent?.let { "${it.name} (${it.studentId}) - ${it.courseName}" } ?: "Select Student",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { studentDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("select_student_field")
                            )

                            DropdownMenu(
                                expanded = studentDropdownExpanded,
                                onDismissRequest = { studentDropdownExpanded = false },
                                modifier = Modifier.background(NavyCard)
                            ) {
                                allStudents.forEach { s ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("${s.name} (${s.studentId})", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                                                Text("${s.courseName} • Due: Rs. ${s.remainingFee.toInt()}", color = GoldSecondary, fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            selectedStudent = s
                                            studentDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Auto Computed Student Fee Status Summary
                        if (selectedStudent != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLight, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(NavyCard)
                                            .border(1.5.dp, GoldPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedStudent!!.photoUri.isNotBlank() && File(selectedStudent!!.photoUri).exists()) {
                                            AsyncImage(
                                                model = File(selectedStudent!!.photoUri),
                                                contentDescription = selectedStudent!!.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(
                                                text = selectedStudent!!.name.take(2).uppercase(),
                                                color = GoldBright,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Total Course Fee:", color = TextSecondaryDark, fontSize = 11.sp)
                                            Text("Rs. ${String.format(Locale.US, "%,.0f", totalFee)}", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Already Paid:", color = TextSecondaryDark, fontSize = 11.sp)
                                            Text("Rs. ${String.format(Locale.US, "%,.0f", alreadyPaid)}", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Remaining Balance:", color = TextSecondaryDark, fontSize = 11.sp)
                                            Text("Rs. ${String.format(Locale.US, "%,.0f", currentRemaining)}", color = if (currentRemaining > 0) DangerRed else SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        } else if (allStudents.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLight, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("No students currently registered.", color = TextSecondaryDark, fontSize = 12.sp)
                                    Button(
                                        onClick = { onNavigate(com.example.ui.navigation.Screen.AddEditStudent.createRoute()) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Register Student First", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Payment Details Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Payment Information", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        // ----------------------------------------
                        // Fee Month Selection & Previous Month Indicator
                        // ----------------------------------------
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Fee For Month (فیس برائے ماہ)",
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (selectedFeeMonth != currentMonthName) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(WarningAmber.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "پچھلا مہینہ (Previous Month)",
                                            color = WarningAmber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            // Quick Selection Chips for Easy 1-Tap Choice
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    val isCur = selectedFeeMonth == currentMonthName
                                    FilterChip(
                                        selected = isCur,
                                        onClick = {
                                            selectedFeeMonth = currentMonthName
                                            remarks = "Fee for $currentMonthName"
                                        },
                                        label = { Text("Current: $currentMonthName", fontSize = 11.sp) },
                                        leadingIcon = if (isCur) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldPrimary,
                                            selectedLabelColor = NavyDark,
                                            containerColor = NavyLight,
                                            labelColor = TextPrimaryDark
                                        )
                                    )
                                }

                                item {
                                    val isPrev = selectedFeeMonth == prevMonthName
                                    FilterChip(
                                        selected = isPrev,
                                        onClick = {
                                            selectedFeeMonth = prevMonthName
                                            remarks = "Previous Month Fee for $prevMonthName"
                                        },
                                        label = { Text("Prev: $prevMonthName", fontSize = 11.sp) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isPrev) Icons.Default.Check else Icons.Default.History,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WarningAmber,
                                            selectedLabelColor = NavyDark,
                                            containerColor = NavyLight,
                                            labelColor = WarningAmber
                                        )
                                    )
                                }

                                item {
                                    val isTwoAgo = selectedFeeMonth == twoMonthsAgoName
                                    FilterChip(
                                        selected = isTwoAgo,
                                        onClick = {
                                            selectedFeeMonth = twoMonthsAgoName
                                            remarks = "Fee for $twoMonthsAgoName"
                                        },
                                        label = { Text(twoMonthsAgoName, fontSize = 11.sp) },
                                        leadingIcon = if (isTwoAgo) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldPrimary,
                                            selectedLabelColor = NavyDark,
                                            containerColor = NavyLight,
                                            labelColor = TextPrimaryDark
                                        )
                                    )
                                }

                                item {
                                    val isThreeAgo = selectedFeeMonth == threeMonthsAgoName
                                    FilterChip(
                                        selected = isThreeAgo,
                                        onClick = {
                                            selectedFeeMonth = threeMonthsAgoName
                                            remarks = "Fee for $threeMonthsAgoName"
                                        },
                                        label = { Text(threeMonthsAgoName, fontSize = 11.sp) },
                                        leadingIcon = if (isThreeAgo) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GoldPrimary,
                                            selectedLabelColor = NavyDark,
                                            containerColor = NavyLight,
                                            labelColor = TextPrimaryDark
                                        )
                                    )
                                }
                            }

                            // Month Dropdown / Selection Field
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedFeeMonth,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Selected Fee Month (ماہانہ فیس برائے)") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = if (selectedFeeMonth != currentMonthName) WarningAmber else GoldPrimary
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { monthDropdownExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (selectedFeeMonth != currentMonthName) WarningAmber else GoldPrimary,
                                        unfocusedBorderColor = if (selectedFeeMonth != currentMonthName) WarningAmber.copy(alpha = 0.8f) else NavyBorder,
                                        focusedTextColor = TextPrimaryDark,
                                        unfocusedTextColor = TextPrimaryDark,
                                        focusedContainerColor = NavyLight,
                                        unfocusedContainerColor = NavyLight
                                    ),
                                    modifier = Modifier.fillMaxWidth().testTag("select_fee_month_field")
                                )

                                DropdownMenu(
                                    expanded = monthDropdownExpanded,
                                    onDismissRequest = { monthDropdownExpanded = false },
                                    modifier = Modifier.background(NavyCard)
                                ) {
                                    pastMonthsList.forEachIndexed { index, m ->
                                        val isCurrent = index == 0
                                        val isPrevious = index == 1
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = m,
                                                        color = if (m == selectedFeeMonth) GoldBright else TextPrimaryDark,
                                                        fontWeight = if (m == selectedFeeMonth || isCurrent) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                    if (isCurrent) {
                                                        Text(" (Current)", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    } else if (isPrevious) {
                                                        Text(" (Previous / پچھلا)", color = WarningAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            },
                                            onClick = {
                                                selectedFeeMonth = m
                                                remarks = if (isPrevious) "Previous Month Fee for $m" else "Fee for $m"
                                                monthDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Notice Banner if paying for a Previous Month
                            if (selectedFeeMonth != currentMonthName) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.12f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.History, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Paying for Previous Month: $selectedFeeMonth",
                                                color = WarningAmber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Receipt will explicitly show that fee is paid for $selectedFeeMonth (پچھلے مہینے کی فیس)",
                                                color = TextPrimaryDark,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = paidAmountText,
                            onValueChange = {
                                paidAmountText = it
                                errorMessage = null
                            },
                            label = { Text("Paid Amount (Rs.) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedContainerColor = NavyLight,
                                unfocusedContainerColor = NavyLight
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_paid_amount")
                        )

                        // Payment Method Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = paymentMethod,
                                onValueChange = {},
                                label = { Text("Payment Method") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { methodDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("select_payment_method")
                            )

                            DropdownMenu(
                                expanded = methodDropdownExpanded,
                                onDismissRequest = { methodDropdownExpanded = false },
                                modifier = Modifier.background(NavyCard)
                            ) {
                                methods.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m, color = TextPrimaryDark) },
                                        onClick = {
                                            paymentMethod = m
                                            methodDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = receivedBy,
                            onValueChange = { receivedBy = it },
                            label = { Text("Received By (Admin / Cashier)") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedContainerColor = NavyLight,
                                unfocusedContainerColor = NavyLight
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_received_by")
                        )

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remarks / Description") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedContainerColor = NavyLight,
                                unfocusedContainerColor = NavyLight
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("input_payment_remarks")
                        )

                        // Advance / Overpayment Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Allow Extra / Advance Payment", color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("Allows paying more than the remaining fee", color = TextSecondaryDark, fontSize = 10.sp)
                            }
                            Switch(
                                checked = allowAdvance,
                                onCheckedChange = { allowAdvance = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GoldPrimary,
                                    checkedTrackColor = NavyLight
                                )
                            )
                        }

                        // Auto Computed Final Remaining Fee Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyLight, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("New Remaining Balance:", color = TextSecondaryDark, fontSize = 12.sp)
                                    Text(
                                        "Rs. ${String.format(Locale.US, "%,.0f", calculatedRemaining)}",
                                        color = if (calculatedRemaining > 0) DangerRed else SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        val student = selectedStudent
                        if (student == null) {
                            errorMessage = "Please select a student"
                            return@Button
                        }
                        if (paidAmount <= 0.0) {
                            errorMessage = "Please enter a valid payment amount"
                            return@Button
                        }
                        if (!allowAdvance && paidAmount > student.remainingFee) {
                            errorMessage = "Paid amount (Rs. $paidAmount) exceeds remaining fee (Rs. ${student.remainingFee}). Enable 'Allow Extra / Advance' to proceed."
                            return@Button
                        }

                        viewModel.collectFee(
                            studentId = student.id,
                            amount = paidAmount,
                            method = paymentMethod,
                            remarks = remarks,
                            receivedBy = receivedBy,
                            feeMonth = selectedFeeMonth,
                            onSuccess = { payment ->
                                onNavigate(Screen.ReceiptAction.createRoute(payment.id))
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_fee_collection_btn")
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Receive Payment & Generate Receipt", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
