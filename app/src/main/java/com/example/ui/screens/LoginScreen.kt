package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.viewmodel.OtpDeliveryChannel
import com.example.ui.viewmodel.OtpFlowType

enum class AuthScreenMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@Composable
fun LoginScreen(
    viewModel: AcademyViewModel,
    onLoginSuccess: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    }

    var currentMode by remember { mutableStateOf(AuthScreenMode.LOGIN) }

    // Login Form State
    var loginMobile by remember(settings) {
        mutableStateOf(if (settings.registeredMobile.isNotBlank()) settings.registeredMobile else settings.phoneNumber)
    }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginErrorText by remember { mutableStateOf<String?>(null) }

    // Registration Form State
    var regName by remember(settings) { mutableStateOf(settings.ownerName) }
    var regMobile by remember(settings) {
        mutableStateOf(if (settings.registeredMobile.isNotBlank()) settings.registeredMobile else settings.phoneNumber)
    }
    var regDeliveryChannel by remember { mutableStateOf(OtpDeliveryChannel.SMS) }
    var regOtpSent by remember { mutableStateOf(false) }
    var regEnteredOtp by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regErrorText by remember { mutableStateOf<String?>(null) }
    var regGeneratedOtpCode by remember { mutableStateOf("") }

    // Forgot / Reset Password Form State
    var resetMobile by remember(settings) {
        mutableStateOf(if (settings.registeredMobile.isNotBlank()) settings.registeredMobile else settings.phoneNumber)
    }
    var resetDeliveryChannel by remember { mutableStateOf(OtpDeliveryChannel.SMS) }
    var resetOtpSent by remember { mutableStateOf(false) }
    var resetEnteredOtp by remember { mutableStateOf("") }
    var resetNewPassword by remember { mutableStateOf("") }
    var resetConfirmPassword by remember { mutableStateOf("") }
    var resetPasswordVisible by remember { mutableStateOf(false) }
    var resetErrorText by remember { mutableStateOf<String?>(null) }
    var resetGeneratedOtpCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Emblem & Academy Branding
                item {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(NavyLight)
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentMode) {
                                AuthScreenMode.LOGIN -> Icons.Default.Security
                                AuthScreenMode.REGISTER -> Icons.Default.Person
                                AuthScreenMode.FORGOT_PASSWORD -> Icons.Default.Key
                            },
                            contentDescription = "Security Portal",
                            tint = GoldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = settings.academyName.uppercase(),
                        color = GoldPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = settings.tagline,
                        color = TextSecondaryDark,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // 3 Mode Switcher Tabs (Sign In, Register, Reset Pass)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyLight, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AuthTabButton(
                            title = "Sign In\nلاگ ان",
                            icon = Icons.Default.Lock,
                            isSelected = currentMode == AuthScreenMode.LOGIN,
                            modifier = Modifier.weight(1f),
                            testTag = "tab_mode_login"
                        ) {
                            currentMode = AuthScreenMode.LOGIN
                            loginErrorText = null
                        }
                        AuthTabButton(
                            title = "Register\nنیا اکاؤنٹ",
                            icon = Icons.Default.Person,
                            isSelected = currentMode == AuthScreenMode.REGISTER,
                            modifier = Modifier.weight(1f),
                            testTag = "tab_mode_register"
                        ) {
                            currentMode = AuthScreenMode.REGISTER
                            regOtpSent = false
                            regErrorText = null
                        }
                        AuthTabButton(
                            title = "Reset Pass\nپاسورڈ تبدیلی",
                            icon = Icons.Default.Key,
                            isSelected = currentMode == AuthScreenMode.FORGOT_PASSWORD,
                            modifier = Modifier.weight(1f),
                            testTag = "tab_mode_reset"
                        ) {
                            currentMode = AuthScreenMode.FORGOT_PASSWORD
                            resetOtpSent = false
                            resetErrorText = null
                        }
                    }
                }

                // ==========================================
                // 1. SIGN IN MODE
                // ==========================================
                if (currentMode == AuthScreenMode.LOGIN) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ADMIN SIGN IN / لاگ ان",
                                color = TextPrimaryDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Enter registered mobile & password to access app",
                                color = TextSecondaryDark,
                                fontSize = 11.sp
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = loginMobile,
                            onValueChange = {
                                loginMobile = it
                                loginErrorText = null
                            },
                            label = { Text("Registered Mobile Number (موبائل نمبر)") },
                            placeholder = { Text("e.g. 03001234567") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedContainerColor = NavyLight,
                                unfocusedContainerColor = NavyLight
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("login_mobile_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = {
                                loginPassword = it
                                loginErrorText = null
                            },
                            label = { Text("Password (پاسورڈ)") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                            singleLine = true,
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = GoldPrimary
                                    )
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark,
                                focusedContainerColor = NavyLight,
                                unfocusedContainerColor = NavyLight
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                        )
                    }

                    if (loginErrorText != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = loginErrorText!!,
                                    color = DangerRed,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (loginMobile.isBlank()) {
                                    loginErrorText = "Please enter your mobile number (براہ کرم موبائل نمبر درج کریں)"
                                    return@Button
                                }
                                if (loginPassword.isBlank()) {
                                    loginErrorText = "Please enter your password (براہ کرم پاسورڈ درج کریں)"
                                    return@Button
                                }

                                val success = viewModel.loginWithMobileAndPassword(loginMobile, loginPassword)
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    loginErrorText = "Invalid mobile number or password. Tap 'Reset Pass' if you forgot it, or 'Register' to create an account."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_btn")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In (ایپ میں داخل ہوں)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    // Links for Reset Password & Register
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Forgot Password?\nپاسورڈ بھول گئے؟",
                                color = GoldBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        currentMode = AuthScreenMode.FORGOT_PASSWORD
                                        resetOtpSent = false
                                        resetErrorText = null
                                    }
                                    .padding(vertical = 4.dp)
                                    .testTag("forgot_password_link")
                            )

                            Text(
                                text = "Register New Account\nنیا اکاؤنٹ رجسٹر کریں",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .clickable {
                                        currentMode = AuthScreenMode.REGISTER
                                        regOtpSent = false
                                        regErrorText = null
                                    }
                                    .padding(vertical = 4.dp)
                                    .testTag("register_account_link")
                            )
                        }
                    }

                    // Lock Notice Card
                    item {
                        HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyLight.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "App requires mobile number & password on every open for data privacy.\nایپ کھولنے پر ہمیشہ موبائل اور پاسورڈ درج کرنا لازمی ہے۔",
                                color = TextSecondaryDark,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // ==========================================
                // 2. REGISTRATION MODE WITH OTP
                // ==========================================
                if (currentMode == AuthScreenMode.REGISTER) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { currentMode = AuthScreenMode.LOGIN }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                            }
                            Column {
                                Text(
                                    text = "REGISTER NEW ACCOUNT",
                                    color = TextPrimaryDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "OTP on Number or WhatsApp -> Create Password",
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (!regOtpSent) {
                        // Step 1: Input Name, Mobile, and choose OTP channel
                        item {
                            OutlinedTextField(
                                value = regName,
                                onValueChange = { regName = it },
                                label = { Text("Admin / Director Name (نام)") },
                                placeholder = { Text("e.g. Al Ghazi") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regMobile,
                                onValueChange = {
                                    regMobile = it
                                    regErrorText = null
                                },
                                label = { Text("Mobile Number for OTP (موبائل نمبر)") },
                                placeholder = { Text("03001234567") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_mobile_input")
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLight, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Select OTP Channel (او ٹی پی حاصل کرنے کا طریقہ):",
                                    color = GoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { regDeliveryChannel = OtpDeliveryChannel.SMS }
                                        .padding(vertical = 2.dp)
                                ) {
                                    RadioButton(
                                        selected = regDeliveryChannel == OtpDeliveryChannel.SMS,
                                        onClick = { regDeliveryChannel = OtpDeliveryChannel.SMS },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                    )
                                    Icon(Icons.Default.Message, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("📱 Mobile Number SMS (ایس ایم ایس)", color = TextPrimaryDark, fontSize = 13.sp)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { regDeliveryChannel = OtpDeliveryChannel.WHATSAPP }
                                        .padding(vertical = 2.dp)
                                ) {
                                    RadioButton(
                                        selected = regDeliveryChannel == OtpDeliveryChannel.WHATSAPP,
                                        onClick = { regDeliveryChannel = OtpDeliveryChannel.WHATSAPP },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                    )
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("💬 WhatsApp Message (واٹس ایپ میسج)", color = TextPrimaryDark, fontSize = 13.sp)
                                }
                            }
                        }

                        if (regErrorText != null) {
                            item {
                                Text(regErrorText!!, color = DangerRed, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (regMobile.isBlank() || regMobile.length < 9) {
                                        regErrorText = "Please enter a valid mobile number (درست موبائل نمبر درج کریں)"
                                        return@Button
                                    }
                                    val code = viewModel.sendOtp(regMobile, regDeliveryChannel, OtpFlowType.REGISTRATION, context)
                                    regGeneratedOtpCode = code
                                    regOtpSent = true
                                    regErrorText = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("send_reg_otp_btn")
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send OTP (او ٹی پی حاصل کریں)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    } else {
                        // Step 2: Enter OTP & Create Password
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().border(1.dp, GoldBright, RoundedCornerShape(14.dp)),
                                colors = CardDefaults.cardColors(containerColor = NavyLight),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (regDeliveryChannel == OtpDeliveryChannel.WHATSAPP) "Sent via WhatsApp" else "Sent via Mobile SMS",
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        TextButton(onClick = { regOtpSent = false }) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Change No.", color = GoldPrimary, fontSize = 11.sp)
                                        }
                                    }

                                    Text(
                                        text = "Number: $regMobile",
                                        color = TextPrimaryDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Big OTP Display Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(NavyDark, RoundedCornerShape(10.dp))
                                            .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Your OTP Code:", color = TextSecondaryDark, fontSize = 10.sp)
                                                Text(
                                                    text = regGeneratedOtpCode,
                                                    color = GoldBright,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 20.sp,
                                                    letterSpacing = 2.sp
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                OutlinedButton(
                                                    onClick = {
                                                        clipboardManager?.setPrimaryClip(
                                                            ClipData.newPlainText("OTP Code", regGeneratedOtpCode)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = GoldPrimary)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Copy", fontSize = 11.sp, color = GoldPrimary)
                                                }
                                                Button(
                                                    onClick = { regEnteredOtp = regGeneratedOtpCode },
                                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Auto-Fill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    if (regDeliveryChannel == OtpDeliveryChannel.WHATSAPP) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.openWhatsAppForOtp(regMobile, regGeneratedOtpCode, context)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Open WhatsApp App (واٹس ایپ کھولیں)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = regEnteredOtp,
                                onValueChange = { regEnteredOtp = it },
                                label = { Text("Enter 6-Digit OTP (او ٹی پی کوڈ درج کریں)") },
                                placeholder = { Text("e.g. 123456") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_otp_input")
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it },
                                label = { Text("Create Password (نیا پاسورڈ بنائیں)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = GoldPrimary
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_create_password_input")
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regConfirmPassword,
                                onValueChange = { regConfirmPassword = it },
                                label = { Text("Confirm Password (پاسورڈ کی تصدیق)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_input")
                            )
                        }

                        if (regErrorText != null) {
                            item {
                                Text(regErrorText!!, color = DangerRed, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (!viewModel.verifyOtp(regEnteredOtp)) {
                                        regErrorText = "Incorrect OTP code. Please enter the valid code."
                                        return@Button
                                    }
                                    if (regPassword.length < 4) {
                                        regErrorText = "Password must be at least 4 characters."
                                        return@Button
                                    }
                                    if (regPassword != regConfirmPassword) {
                                        regErrorText = "Passwords do not match (پاسورڈ ایک جیسے نہیں ہیں)."
                                        return@Button
                                    }

                                    // Complete registration & auto-login
                                    viewModel.registerUserAccount(regName, regMobile, regPassword)
                                    onLoginSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("reg_submit_btn")
                            ) {
                                Text("Verify & Create Account (رجسٹریشن مکمل کریں)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                TextButton(
                                    onClick = {
                                        val code = viewModel.sendOtp(regMobile, regDeliveryChannel, OtpFlowType.REGISTRATION, context)
                                        regGeneratedOtpCode = code
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Resend OTP (دوبارہ کوڈ بھیجیں)", color = GoldPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    item {
                        TextButton(onClick = { currentMode = AuthScreenMode.LOGIN }) {
                            Text("Already registered? Sign In (واپس لاگ ان)", color = TextSecondaryDark, fontSize = 12.sp)
                        }
                    }
                }

                // ==========================================
                // 3. FORGOT / RESET PASSWORD MODE WITH OTP
                // ==========================================
                if (currentMode == AuthScreenMode.FORGOT_PASSWORD) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { currentMode = AuthScreenMode.LOGIN }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                            }
                            Column {
                                Text(
                                    text = "RESET PASSWORD / پاسورڈ تبدیل کریں",
                                    color = TextPrimaryDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Get OTP on Mobile or WhatsApp to set new password",
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    if (!resetOtpSent) {
                        item {
                            OutlinedTextField(
                                value = resetMobile,
                                onValueChange = {
                                    resetMobile = it
                                    resetErrorText = null
                                },
                                label = { Text("Registered Mobile Number (رجسٹرڈ نمبر)") },
                                placeholder = { Text("03001234567") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reset_mobile_input")
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLight, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Send Reset OTP via (او ٹی پی کس ذریعے حاصل کریں):",
                                    color = GoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { resetDeliveryChannel = OtpDeliveryChannel.SMS }
                                        .padding(vertical = 2.dp)
                                ) {
                                    RadioButton(
                                        selected = resetDeliveryChannel == OtpDeliveryChannel.SMS,
                                        onClick = { resetDeliveryChannel = OtpDeliveryChannel.SMS },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                    )
                                    Icon(Icons.Default.Message, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("📱 Mobile SMS (ایس ایم ایس)", color = TextPrimaryDark, fontSize = 13.sp)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { resetDeliveryChannel = OtpDeliveryChannel.WHATSAPP }
                                        .padding(vertical = 2.dp)
                                ) {
                                    RadioButton(
                                        selected = resetDeliveryChannel == OtpDeliveryChannel.WHATSAPP,
                                        onClick = { resetDeliveryChannel = OtpDeliveryChannel.WHATSAPP },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                    )
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("💬 WhatsApp (واٹس ایپ)", color = TextPrimaryDark, fontSize = 13.sp)
                                }
                            }
                        }

                        if (resetErrorText != null) {
                            item {
                                Text(resetErrorText!!, color = DangerRed, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (resetMobile.isBlank() || resetMobile.length < 9) {
                                        resetErrorText = "Please enter your registered mobile number"
                                        return@Button
                                    }
                                    val code = viewModel.sendOtp(resetMobile, resetDeliveryChannel, OtpFlowType.FORGOT_PASSWORD, context)
                                    resetGeneratedOtpCode = code
                                    resetOtpSent = true
                                    resetErrorText = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("send_reset_otp_btn")
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Reset OTP (او ٹی پی حاصل کریں)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    } else {
                        // Step 2: Enter Reset OTP & New Password
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().border(1.dp, GoldBright, RoundedCornerShape(14.dp)),
                                colors = CardDefaults.cardColors(containerColor = NavyLight),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (resetDeliveryChannel == OtpDeliveryChannel.WHATSAPP) "OTP sent via WhatsApp" else "OTP sent via SMS",
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        TextButton(onClick = { resetOtpSent = false }) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Change No.", color = GoldPrimary, fontSize = 11.sp)
                                        }
                                    }

                                    Text(
                                        text = "Number: $resetMobile",
                                        color = TextPrimaryDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Big OTP Display Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(NavyDark, RoundedCornerShape(10.dp))
                                            .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Your Reset Code:", color = TextSecondaryDark, fontSize = 10.sp)
                                                Text(
                                                    text = resetGeneratedOtpCode,
                                                    color = GoldBright,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 20.sp,
                                                    letterSpacing = 2.sp
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                OutlinedButton(
                                                    onClick = {
                                                        clipboardManager?.setPrimaryClip(
                                                            ClipData.newPlainText("OTP Code", resetGeneratedOtpCode)
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = GoldPrimary)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Copy", fontSize = 11.sp, color = GoldPrimary)
                                                }
                                                Button(
                                                    onClick = { resetEnteredOtp = resetGeneratedOtpCode },
                                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("Auto-Fill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    if (resetDeliveryChannel == OtpDeliveryChannel.WHATSAPP) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.openWhatsAppForOtp(resetMobile, resetGeneratedOtpCode, context)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Open WhatsApp App (واٹس ایپ کھولیں)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = resetEnteredOtp,
                                onValueChange = { resetEnteredOtp = it },
                                label = { Text("Enter OTP Code (او ٹی پی درج کریں)") },
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reset_otp_input")
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = resetNewPassword,
                                onValueChange = { resetNewPassword = it },
                                label = { Text("Enter New Password (نیا پاسورڈ)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                visualTransformation = if (resetPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { resetPasswordVisible = !resetPasswordVisible }) {
                                        Icon(
                                            imageVector = if (resetPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = GoldPrimary
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reset_new_password_input")
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = resetConfirmPassword,
                                onValueChange = { resetConfirmPassword = it },
                                label = { Text("Confirm New Password (پاسورڈ کی تصدیق)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary) },
                                singleLine = true,
                                visualTransformation = if (resetPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark,
                                    focusedContainerColor = NavyLight,
                                    unfocusedContainerColor = NavyLight
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("reset_confirm_password_input")
                            )
                        }

                        if (resetErrorText != null) {
                            item {
                                Text(resetErrorText!!, color = DangerRed, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (!viewModel.verifyOtp(resetEnteredOtp)) {
                                        resetErrorText = "Invalid OTP code. Please enter the correct code."
                                        return@Button
                                    }
                                    if (resetNewPassword.length < 4) {
                                        resetErrorText = "Password must be at least 4 characters long."
                                        return@Button
                                    }
                                    if (resetNewPassword != resetConfirmPassword) {
                                        resetErrorText = "Passwords do not match (پاسورڈ ایک جیسے نہیں ہیں)."
                                        return@Button
                                    }

                                    viewModel.resetPasswordWithOtp(resetMobile, resetNewPassword)
                                    loginMobile = resetMobile
                                    loginPassword = resetNewPassword
                                    currentMode = AuthScreenMode.LOGIN
                                    loginErrorText = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("reset_password_submit_btn")
                            ) {
                                Text("Verify & Save Password (پاسورڈ تبدیل کریں)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    item {
                        TextButton(onClick = { currentMode = AuthScreenMode.LOGIN }) {
                            Text("Back to Sign In (واپس لاگ ان پر جائیں)", color = TextSecondaryDark, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthTabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GoldPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) NavyDark else GoldPrimary,
                modifier = Modifier.size(18.dp)
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
