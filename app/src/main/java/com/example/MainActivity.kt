package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.AcademyHeaderLogo
import com.example.ui.localization.AppLanguage
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditStudentScreen
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.CertificatesScreen
import com.example.ui.screens.CollectFeeScreen
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.FeeManagementScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ReceiptActionScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudentDetailScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.screens.TeachersScreen
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.AcademyViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val viewModel: AcademyViewModel = viewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val currentLangCode by viewModel.currentLanguage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Permissions launcher for SMS and Bluetooth
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.loadHardwareInfo()
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        permissionsToRequest.add(Manifest.permission.SEND_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        permissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }

    // Toast/Snackbar notifications
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val isRtl = currentLangCode == "ur" || currentLangCode == "sd"
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        if (!isLoggedIn) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
            return@CompositionLocalProvider
        }

        val bottomItems = listOf(
            BottomNavItem(Screen.Dashboard.route, viewModel.getString("dashboard"), Icons.Default.Dashboard, "nav_dashboard"),
            BottomNavItem(Screen.Students.route, viewModel.getString("students"), Icons.Default.Groups, "nav_students"),
            BottomNavItem(Screen.Fees.route, viewModel.getString("fees"), Icons.Default.MonetizationOn, "nav_fees"),
            BottomNavItem(Screen.Attendance.route, viewModel.getString("attendance"), Icons.Default.AssignmentTurnedIn, "nav_attendance"),
            BottomNavItem(Screen.Courses.route, viewModel.getString("courses"), Icons.Default.School, "nav_courses")
        )

        val shouldShowBars = currentRoute != Screen.Login.route &&
                currentRoute?.startsWith("add_edit_student") != true &&
                currentRoute?.startsWith("student_detail") != true &&
                currentRoute?.startsWith("collect_fee") != true &&
                currentRoute?.startsWith("receipt_action") != true

        var showMoreMenu by remember { mutableStateOf(false) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = NavyCard,
                    drawerContentColor = TextPrimaryDark
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AcademyHeaderLogo(
                        academyName = settings.academyName,
                        tagline = settings.tagline
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    DrawerNavRow(
                        title = "Dashboard",
                        icon = Icons.Default.Dashboard,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Dashboard.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Students Management",
                        icon = Icons.Default.Groups,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Students.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Courses & Batches",
                        icon = Icons.Default.School,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Courses.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Fee Management & Receipts",
                        icon = Icons.Default.MonetizationOn,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Fees.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Daily Attendance Register",
                        icon = Icons.Default.AssignmentTurnedIn,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Attendance.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Faculty & Salaries",
                        icon = Icons.Default.Groups,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Teachers.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Operating Expenses",
                        icon = Icons.Default.TrendingDown,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Expenses.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Official Certificates",
                        icon = Icons.Default.CardMembership,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Certificates.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Reports & Analytics (PDF)",
                        icon = Icons.Default.Assessment,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Reports.route)
                        }
                    )
                    DrawerNavRow(
                        title = "Academy Settings",
                        icon = Icons.Default.Settings,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    if (shouldShowBars) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = settings.academyName,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("app_menu_drawer_btn")
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = GoldPrimary)
                                }
                            },
                            actions = {
                                Box {
                                    IconButton(
                                        onClick = { showMoreMenu = true },
                                        modifier = Modifier.testTag("app_more_options_btn")
                                    ) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = GoldPrimary)
                                    }
                                    DropdownMenu(
                                        expanded = showMoreMenu,
                                        onDismissRequest = { showMoreMenu = false },
                                        modifier = Modifier.background(NavyCard)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Faculty & Salaries", color = TextPrimaryDark) },
                                            onClick = {
                                                showMoreMenu = false
                                                navController.navigate(Screen.Teachers.route)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Expenses", color = TextPrimaryDark) },
                                            onClick = {
                                                showMoreMenu = false
                                                navController.navigate(Screen.Expenses.route)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Certificates", color = TextPrimaryDark) },
                                            onClick = {
                                                showMoreMenu = false
                                                navController.navigate(Screen.Certificates.route)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Reports (PDF)", color = TextPrimaryDark) },
                                            onClick = {
                                                showMoreMenu = false
                                                navController.navigate(Screen.Reports.route)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Settings", color = TextPrimaryDark) },
                                            onClick = {
                                                showMoreMenu = false
                                                navController.navigate(Screen.Settings.route)
                                            }
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
                        )
                    }
                },
                bottomBar = {
                    if (shouldShowBars) {
                        NavigationBar(
                            containerColor = NavyCard,
                            contentColor = GoldPrimary,
                            modifier = Modifier.border(1.dp, NavyBorder)
                        ) {
                            bottomItems.forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        if (currentRoute != item.route) {
                                            navController.navigate(item.route) {
                                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldBright,
                                        selectedTextColor = GoldBright,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark,
                                        indicatorColor = NavyLight
                                    ),
                                    modifier = Modifier.testTag(item.tag)
                                )
                            }
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = NavyDark
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Students.route) {
                            StudentsScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(
                            route = Screen.StudentDetail.route,
                            arguments = listOf(navArgument("studentId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val sId = backStackEntry.arguments?.getLong("studentId") ?: 0L
                            StudentDetailScreen(
                                studentId = sId,
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Screen.AddEditStudent.route,
                            arguments = listOf(navArgument("studentId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val sIdStr = backStackEntry.arguments?.getString("studentId")
                            val sId = sIdStr?.toLongOrNull()
                            AddEditStudentScreen(
                                studentId = sId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Courses.route) {
                            CoursesScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Fees.route) {
                            FeeManagementScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(
                            route = Screen.CollectFee.route,
                            arguments = listOf(navArgument("studentId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            })
                        ) { backStackEntry ->
                            val sIdStr = backStackEntry.arguments?.getString("studentId")
                            val sId = sIdStr?.toLongOrNull()
                            CollectFeeScreen(
                                preselectedStudentId = sId,
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Screen.ReceiptAction.route,
                            arguments = listOf(navArgument("paymentId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val pId = backStackEntry.arguments?.getLong("paymentId") ?: 0L
                            ReceiptActionScreen(
                                paymentId = pId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Attendance.route) {
                            AttendanceScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Teachers.route) {
                            TeachersScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Expenses.route) {
                            ExpensesScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Certificates.route) {
                            CertificatesScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Reports.route) {
                            ReportsScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerNavRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(title, color = TextPrimaryDark, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
        icon = { Icon(icon, contentDescription = title, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
