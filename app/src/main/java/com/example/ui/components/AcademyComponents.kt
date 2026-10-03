package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Locale

@Composable
fun AcademyHeaderLogo(
    academyName: String = "Al Ghazi Digital Institute",
    tagline: String = "LEARN • PRACTICE • GROW",
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // High-end Crest Badge
        Box(
            modifier = Modifier
                .size(if (compact) 42.dp else 52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NavyLight, NavyDark)
                    )
                )
                .border(2.dp, GoldPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize(0.7f)) {
                // Gold dome motif
                val w = size.width
                val h = size.height
                drawCircle(
                    color = GoldBright,
                    radius = w * 0.12f,
                    center = Offset(w / 2f, h * 0.35f)
                )
                drawRoundRect(
                    color = GoldPrimary,
                    topLeft = Offset(w * 0.25f, h * 0.45f),
                    size = Size(w * 0.5f, h * 0.4f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = academyName.uppercase(),
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 14.sp else 16.sp,
                letterSpacing = 0.8.sp,
                maxLines = 1
            )
            Text(
                text = tagline,
                color = TextSecondaryDark,
                fontSize = if (compact) 10.sp else 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = GoldPrimary,
    subText: String? = null,
    modifier: Modifier = Modifier,
    testTag: String = "stats_card"
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = TextPrimaryDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (subText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subText,
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun FinancialCanvasChart(
    income: Double,
    expenses: Double,
    modifier: Modifier = Modifier
) {
    val total = (income + expenses).coerceAtLeast(1.0)
    val incomePercent = (income / total).toFloat()
    val expensePercent = (expenses / total).toFloat()

    val animIncome by animateFloatAsState(targetValue = incomePercent, label = "income_bar")
    val animExpense by animateFloatAsState(targetValue = expensePercent, label = "expense_bar")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Overview (Income vs Expense)",
                    color = GoldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (income >= expenses) "Profitable" else "Deficit",
                    color = if (income >= expenses) SuccessGreen else DangerRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom High-Res Visual Bar Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                val barWidth = size.width
                val barHeight = size.height

                // Draw background
                drawRoundRect(
                    color = NavyLight,
                    size = size,
                    cornerRadius = CornerRadius(14f, 14f)
                )

                // Draw Income portion (Green/Gold)
                val incomeWidth = barWidth * animIncome
                if (incomeWidth > 0f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(listOf(SuccessGreen, GoldPrimary)),
                        size = Size(incomeWidth, barHeight),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                }

                // Draw Expense portion (Red)
                val expenseWidth = barWidth * animExpense
                if (expenseWidth > 0f) {
                    drawRoundRect(
                        color = DangerRed,
                        topLeft = Offset(barWidth - expenseWidth, 0f),
                        size = Size(expenseWidth, barHeight),
                        cornerRadius = CornerRadius(14f, 14f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend & Figures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Fee Income", color = TextSecondaryDark, fontSize = 11.sp)
                        Text(
                            "Rs. ${String.format(Locale.US, "%,.0f", income)} (${(incomePercent * 100).toInt()}%)",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Expense Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DangerRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Expenses & Salaries", color = TextSecondaryDark, fontSize = 11.sp)
                        Text(
                            "Rs. ${String.format(Locale.US, "%,.0f", expenses)} (${(expensePercent * 100).toInt()}%)",
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = if (isDestructive) DangerRed else GoldPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(text = message, color = TextPrimaryDark)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) DangerRed else GoldPrimary,
                    contentColor = if (isDestructive) Color.White else NavyDark
                ),
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text(confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_dismiss_button")
            ) {
                Text(dismissText, color = TextSecondaryDark)
            }
        },
        containerColor = NavyCard
    )
}
