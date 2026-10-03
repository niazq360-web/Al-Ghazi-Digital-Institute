package com.example.ui.screens

import android.bluetooth.BluetoothDevice
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.data.model.AcademySettings
import com.example.data.repository.SyncState
import com.example.service.PrinterStatus
import com.example.service.SimCardInfo
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
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
fun SettingsScreen(
    viewModel: AcademyViewModel,
    onNavigate: (String) -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val printerStatus by viewModel.printerStatus.collectAsState()
    val availablePrinters by viewModel.availablePrinters.collectAsState()
    val availableSims by viewModel.availableSims.collectAsState()
    val selectedSim by viewModel.selectedSim.collectAsState()

    var academyName by remember(settings) { mutableStateOf(settings.academyName) }
    var tagline by remember(settings) { mutableStateOf(settings.tagline) }
    var ownerName by remember(settings) { mutableStateOf(settings.ownerName) }
    var phone by remember(settings) { mutableStateOf(settings.phoneNumber) }
    var whatsapp by remember(settings) { mutableStateOf(settings.whatsappNumber) }
    var email by remember(settings) { mutableStateOf(settings.email) }
    var address by remember(settings) { mutableStateOf(settings.address) }
    var website by remember(settings) { mutableStateOf(settings.website) }
    var paperWidth by remember(settings) { mutableStateOf(settings.printerPaperWidth) }
    var receiptFooter by remember(settings) { mutableStateOf(settings.receiptFooterText) }
    var adminPassword by remember(settings) { mutableStateOf(settings.adminPasswordHash) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AcademyHeaderLogo(
                academyName = academyName,
                tagline = tagline,
                compact = true
            )
        }

        // 12-Month Academic Session Archive
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("12-Month Academic Archive & Cloud Security", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "All student enrollments, fee receipts, salaries, and certificates are permanently archived. If app is uninstalled or updated, data is safe.",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.extend12MonthsSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("extend_12months_btn")
                    ) {
                        Text("Renew / Extend Next 12 Months", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Academy Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Institute Branding & Profile", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    AppTextField(value = academyName, onValueChange = { academyName = it }, label = "Academy Name", testTag = "input_set_academy_name")
                    AppTextField(value = tagline, onValueChange = { tagline = it }, label = "Motto / Tagline", testTag = "input_set_tagline")
                    AppTextField(value = ownerName, onValueChange = { ownerName = it }, label = "Director / Admin Name", testTag = "input_set_owner")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            AppTextField(value = phone, onValueChange = { phone = it }, label = "Official Phone", testTag = "input_set_phone")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AppTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = "WhatsApp Number", testTag = "input_set_whatsapp")
                        }
                    }
                    AppTextField(value = email, onValueChange = { email = it }, label = "Email Address", testTag = "input_set_email")
                    AppTextField(value = address, onValueChange = { address = it }, label = "Campus Address", testTag = "input_set_address")
                    AppTextField(value = website, onValueChange = { website = it }, label = "Official Website", testTag = "input_set_website")
                    AppTextField(value = receiptFooter, onValueChange = { receiptFooter = it }, label = "Receipt Footer Message", testTag = "input_set_footer")
                }
            }
        }

        // Thermal Printer Setup Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thermal Printer Settings", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        IconButton(onClick = { viewModel.loadHardwareInfo() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = GoldPrimary)
                        }
                    }

                    // Paper Width Selector
                    Text("Select Thermal Paper Size:", color = TextSecondaryDark, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { paperWidth = "58mm" }) {
                            RadioButton(
                                selected = paperWidth == "58mm",
                                onClick = { paperWidth = "58mm" },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Text("58mm Roll", color = TextPrimaryDark, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { paperWidth = "80mm" }) {
                            RadioButton(
                                selected = paperWidth == "80mm",
                                onClick = { paperWidth = "80mm" },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Text("80mm Roll", color = TextPrimaryDark, fontSize = 12.sp)
                        }
                    }

                    // Printer Status & Actions
                    Text(
                        text = when (printerStatus) {
                            is PrinterStatus.Connected -> "Status: Connected to ${(printerStatus as PrinterStatus.Connected).deviceName}"
                            is PrinterStatus.Connecting -> "Status: Connecting to ${(printerStatus as PrinterStatus.Connecting).deviceName}..."
                            is PrinterStatus.Error -> "Status: ${(printerStatus as PrinterStatus.Error).message}"
                            else -> "Status: No printer connected"
                        },
                        color = when (printerStatus) {
                            is PrinterStatus.Connected -> SuccessGreen
                            is PrinterStatus.Error -> DangerRed
                            else -> TextSecondaryDark
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Paired Printers List
                    if (availablePrinters.isNotEmpty()) {
                        Text("Paired Bluetooth Printers:", color = TextSecondaryDark, fontSize = 11.sp)
                        availablePrinters.forEach { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLight, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(device.name ?: "Thermal Printer", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(device.address, color = TextSecondaryDark, fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { viewModel.connectPrinter(device) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Connect", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Test Print Button
                    Button(
                        onClick = { viewModel.printTestReceipt() },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyLight, contentColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("test_print_btn")
                    ) {
                        Text("Print Test Receipt", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Real Dual SIM Settings
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SimCard, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SMS SIM Configuration", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(
                        "Real Android SMS is sent directly from your device SIM. Select default SIM slot:",
                        color = TextSecondaryDark,
                        fontSize = 12.sp
                    )

                    availableSims.forEach { sim ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setSelectedSim(sim) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSim?.subscriptionId == sim.subscriptionId,
                                onClick = { viewModel.setSelectedSim(sim) },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${sim.displayName} (${sim.carrierName})", color = TextPrimaryDark, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Multi-Language Selection (English, Urdu, Sindhi)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Application Language", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.values().forEach { lang ->
                            val sel = currentLang == lang.code
                            Button(
                                onClick = { viewModel.setLanguage(lang.code) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (sel) GoldPrimary else NavyLight,
                                    contentColor = if (sel) NavyDark else TextPrimaryDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("lang_btn_${lang.code}")
                            ) {
                                Text(lang.displayName, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Security / Admin Password
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Security Password", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    AppTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = "Admin PIN / Master Password",
                        testTag = "input_admin_password"
                    )
                }
            }
        }

        // Save All Settings Button
        item {
            Button(
                onClick = {
                    viewModel.updateAcademySettings(
                        settings.copy(
                            academyName = academyName.trim(),
                            tagline = tagline.trim(),
                            ownerName = ownerName.trim(),
                            phoneNumber = phone.trim(),
                            whatsappNumber = whatsapp.trim(),
                            email = email.trim(),
                            address = address.trim(),
                            website = website.trim(),
                            printerPaperWidth = paperWidth,
                            receiptFooterText = receiptFooter.trim(),
                            adminPasswordHash = adminPassword.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_settings_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Academy Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
