package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentScreen(
    studentId: Long?,
    viewModel: AcademyViewModel,
    onBack: () -> Unit
) {
    val allStudents by viewModel.allStudents.collectAsState()
    val courses by viewModel.activeCourses.collectAsState()

    val existing = if (studentId != null) allStudents.find { it.id == studentId } else null
    val context = LocalContext.current

    var photoUri by remember { mutableStateOf(existing?.photoUri ?: "") }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val dir = File(context.filesDir, "student_photos")
                    if (!dir.exists()) dir.mkdirs()
                    val destFile = File(dir, "student_${System.currentTimeMillis()}.jpg")
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    photoUri = destFile.absolutePath
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val dir = File(context.filesDir, "student_photos")
                if (!dir.exists()) dir.mkdirs()
                val destFile = File(dir, "student_cam_${System.currentTimeMillis()}.jpg")
                destFile.outputStream().use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                }
                photoUri = destFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val dir = File(context.filesDir, "student_photos")
                    if (!dir.exists()) dir.mkdirs()
                    val destFile = File(dir, "student_file_${System.currentTimeMillis()}.jpg")
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                    photoUri = destFile.absolutePath
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            title = {
                Text(
                    "Upload Student Photograph",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Choose photo source (کیمرہ یا گیلری سے تصویر منتخب کریں):",
                        color = TextSecondaryDark,
                        fontSize = 13.sp
                    )

                    // Option 1: Camera
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPhotoSourceDialog = false
                                cameraLauncher.launch(null)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Take Photo with Camera", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("کیمرہ سے نئی تصویر لیں", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                        }
                    }

                    // Option 2: Gallery
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPhotoSourceDialog = false
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Collections, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Choose from Gallery / Photos", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("گیلری سے تصویر منتخب کریں", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                        }
                    }

                    // Option 3: Files
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showPhotoSourceDialog = false
                                filePickerLauncher.launch("image/*")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Browse Device Files", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("فائل مینیجر سے تصویر چنیں", color = TextSecondaryDark, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoSourceDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = NavyCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    var name by remember { mutableStateOf(existing?.name ?: "") }
    var fatherName by remember { mutableStateOf(existing?.fatherName ?: "") }
    var rollNo by remember { mutableStateOf(existing?.studentId ?: "") }
    var gender by remember { mutableStateOf(existing?.gender ?: "Male") }
    var dob by remember { mutableStateOf(existing?.dob ?: "") }
    var cnic by remember { mutableStateOf(existing?.cnicOrBForm ?: "") }
    var studentMobile by remember { mutableStateOf(existing?.studentMobile ?: "") }
    var parentMobile by remember { mutableStateOf(existing?.parentMobile ?: "") }
    var whatsapp by remember { mutableStateOf(existing?.whatsappNumber ?: "") }
    var address by remember { mutableStateOf(existing?.address ?: "") }
    var city by remember { mutableStateOf(existing?.city ?: "Karachi") }

    var selectedCourse by remember {
        mutableStateOf(
            if (existing != null) courses.find { it.name == existing.courseName } ?: courses.firstOrNull()
            else courses.firstOrNull()
        )
    }
    var batchName by remember { mutableStateOf(existing?.batchName ?: (selectedCourse?.batchName ?: "Batch-A")) }
    var timing by remember { mutableStateOf(existing?.timing ?: (selectedCourse?.classTiming ?: "10:00 AM - 12:00 PM")) }
    var courseFee by remember { mutableDoubleStateOf(existing?.courseFee ?: (selectedCourse?.totalFee ?: 15000.0)) }
    var discount by remember { mutableDoubleStateOf(existing?.discount ?: 0.0) }
    var initialPaid by remember { mutableDoubleStateOf(existing?.paidFee ?: 0.0) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    var courseDropdownExpanded by remember { mutableStateOf(false) }
    var genderDropdownExpanded by remember { mutableStateOf(false) }

    val finalFee = (courseFee - discount).coerceAtLeast(0.0)
    val remaining = (finalFee - initialPaid).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existing == null) "New Student Admission" else "Edit Student Profile",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("add_student_back")) {
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
            // Student Photo Upload Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Student Photograph (طالب علم کی تصویر)", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .size(108.dp)
                                .clip(CircleShape)
                                .background(NavyLight)
                                .border(2.5.dp, if (photoUri.isNotBlank()) GoldBright else GoldPrimary.copy(alpha = 0.6f), CircleShape)
                                .clickable { showPhotoSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUri.isNotBlank() && File(photoUri).exists()) {
                                AsyncImage(
                                    model = File(photoUri),
                                    contentDescription = "Student Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Upload Photo",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Add Photo", color = TextSecondaryDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        if (photoUri.isNotBlank() && File(photoUri).exists()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Photo Attached Successfully", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showPhotoSourceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("upload_student_photo_btn")
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (photoUri.isBlank()) "Upload Photo" else "Change Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (photoUri.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { photoUri = "" },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", color = DangerRed, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Used on Student Profile, ID Card, Fee Receipts & Certificates",
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Basic Info Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Personal Information", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        AppTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Student Full Name *",
                            testTag = "input_student_name"
                        )

                        AppTextField(
                            value = fatherName,
                            onValueChange = { fatherName = it },
                            label = "Father / Guardian Name *",
                            testTag = "input_father_name"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Gender Dropdown
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = gender,
                                    onValueChange = {},
                                    label = "Gender",
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { genderDropdownExpanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                        }
                                    },
                                    testTag = "input_gender"
                                )
                                DropdownMenu(
                                    expanded = genderDropdownExpanded,
                                    onDismissRequest = { genderDropdownExpanded = false },
                                    modifier = Modifier.background(NavyCard)
                                ) {
                                    listOf("Male", "Female", "Other").forEach { g ->
                                        DropdownMenuItem(
                                            text = { Text(g, color = TextPrimaryDark) },
                                            onClick = {
                                                gender = g
                                                genderDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = dob,
                                    onValueChange = { dob = it },
                                    label = "Date of Birth",
                                    testTag = "input_dob"
                                )
                            }
                        }

                        AppTextField(
                            value = cnic,
                            onValueChange = { cnic = it },
                            label = "CNIC / B-Form Number",
                            testTag = "input_cnic"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = studentMobile,
                                    onValueChange = { studentMobile = it },
                                    label = "Student Mobile *",
                                    keyboardType = KeyboardType.Phone,
                                    testTag = "input_student_mobile"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = parentMobile,
                                    onValueChange = { parentMobile = it },
                                    label = "Parent Mobile",
                                    keyboardType = KeyboardType.Phone,
                                    testTag = "input_parent_mobile"
                                )
                            }
                        }

                        AppTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Residential Address",
                            testTag = "input_address"
                        )

                        AppTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = "City",
                            testTag = "input_city"
                        )
                    }
                }
            }

            // Course & Fee Enrollment Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Course & Fee Package", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        // Course Selector Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            AppTextField(
                                value = selectedCourse?.name ?: "Select Course",
                                onValueChange = {},
                                label = "Select Enrolling Course *",
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { courseDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = GoldPrimary)
                                    }
                                },
                                testTag = "select_course_dropdown"
                            )
                            DropdownMenu(
                                expanded = courseDropdownExpanded,
                                onDismissRequest = { courseDropdownExpanded = false },
                                modifier = Modifier.background(NavyCard)
                            ) {
                                courses.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text("${c.name} (Rs. ${c.totalFee.toInt()})", color = TextPrimaryDark) },
                                        onClick = {
                                            selectedCourse = c
                                            courseFee = c.totalFee
                                            batchName = c.batchName
                                            timing = c.classTiming
                                            courseDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = batchName,
                                    onValueChange = { batchName = it },
                                    label = "Batch Name",
                                    testTag = "input_batch"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = timing,
                                    onValueChange = { timing = it },
                                    label = "Class Timing",
                                    testTag = "input_timing"
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = if (courseFee == 0.0) "" else courseFee.toInt().toString(),
                                    onValueChange = { courseFee = it.toDoubleOrNull() ?: 0.0 },
                                    label = "Course Fee (Rs.)",
                                    keyboardType = KeyboardType.Number,
                                    testTag = "input_course_fee"
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = if (discount == 0.0) "" else discount.toInt().toString(),
                                    onValueChange = { discount = it.toDoubleOrNull() ?: 0.0 },
                                    label = "Discount (Rs.)",
                                    keyboardType = KeyboardType.Number,
                                    testTag = "input_discount"
                                )
                            }
                        }

                        if (existing == null) {
                            AppTextField(
                                value = if (initialPaid == 0.0) "" else initialPaid.toInt().toString(),
                                onValueChange = { initialPaid = it.toDoubleOrNull() ?: 0.0 },
                                label = "Initial Paid Amount on Admission (Rs.)",
                                keyboardType = KeyboardType.Number,
                                testTag = "input_initial_paid"
                            )
                        }

                        // Summary Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyLight, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Final Net Fee:", color = TextSecondaryDark, fontSize = 12.sp)
                                    Text("Rs. ${String.format(Locale.US, "%,.0f", finalFee)}", color = GoldBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Remaining Balance:", color = TextSecondaryDark, fontSize = 12.sp)
                                    Text("Rs. ${String.format(Locale.US, "%,.0f", remaining)}", color = if (remaining > 0) GoldPrimary else SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        AppTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "Notes / Admission Remarks",
                            singleLine = false,
                            testTag = "input_notes"
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        if (name.isBlank()) return@Button
                        val admissionDateStr = existing?.admissionDate
                            ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

                        val studentToSave = Student(
                            id = existing?.id ?: 0L,
                            studentId = rollNo,
                            name = name.trim(),
                            fatherName = fatherName.trim(),
                            gender = gender,
                            dob = dob,
                            cnicOrBForm = cnic,
                            studentMobile = studentMobile.trim(),
                            parentMobile = parentMobile.trim(),
                            whatsappNumber = whatsapp.ifBlank { studentMobile.trim() },
                            address = address.trim(),
                            city = city.trim(),
                            photoUri = photoUri.trim(),
                            courseId = selectedCourse?.id ?: 1L,
                            courseName = selectedCourse?.name ?: "General",
                            batchName = batchName.trim(),
                            timing = timing.trim(),
                            admissionDate = admissionDateStr,
                            courseFee = courseFee,
                            discount = discount,
                            finalFee = finalFee,
                            paidFee = initialPaid,
                            remainingFee = remaining,
                            status = "Active",
                            notes = notes.trim()
                        )

                        if (existing == null) {
                            viewModel.addStudent(studentToSave) {
                                onBack()
                            }
                        } else {
                            viewModel.updateStudent(studentToSave) {
                                onBack()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = NavyDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_student_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (existing == null) "Complete Admission & Enroll" else "Save Changes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    testTag: String = "app_text_field"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        readOnly = readOnly,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = NavyBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedContainerColor = NavyLight,
            unfocusedContainerColor = NavyLight,
            focusedLabelColor = GoldPrimary,
            unfocusedLabelColor = TextSecondaryDark
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
