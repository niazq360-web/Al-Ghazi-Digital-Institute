package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.AcademySettings
import com.example.data.model.Expense
import com.example.data.model.FeePayment
import com.example.data.model.SalaryPayment
import com.example.data.model.Student
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfReportGenerator(private val context: Context) {

    fun generateFinancialReportPdf(
        settings: AcademySettings,
        payments: List<FeePayment>,
        salaries: List<SalaryPayment>,
        expenses: List<Expense>
    ): File? {
        val pageWidth = 595
        val pageHeight = 842
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        canvas.drawColor(Color.WHITE)
        val paint = Paint().apply { isAntiAlias = true }

        // Navy Header
        paint.color = Color.parseColor("#0A192F")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 100f, paint)

        // Gold line
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 100f, pageWidth.toFloat(), 100f, paint)

        // Title
        paint.color = Color.parseColor("#D4AF37")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(settings.academyName.uppercase(), pageWidth / 2f, 40f, paint)

        paint.color = Color.WHITE
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("EXECUTIVE FINANCIAL & ACCOUNTS REPORT", pageWidth / 2f, 65f, paint)
        paint.textSize = 10f
        paint.color = Color.parseColor("#94A3B8")
        val dateStr = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.US).format(Date())
        canvas.drawText("Generated on: $dateStr", pageWidth / 2f, 85f, paint)

        var y = 135f
        val totalIncome = payments.sumOf { it.paidAmount }
        val totalSalaries = salaries.sumOf { it.netPaid }
        val totalExpenses = expenses.sumOf { it.amount }
        val netProfit = totalIncome - totalExpenses

        // Summary Card
        paint.color = Color.parseColor("#F8FAFC")
        canvas.drawRoundRect(40f, y, (pageWidth - 40).toFloat(), y + 110f, 8f, 8f, paint)
        paint.color = Color.parseColor("#CBD5E1")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(40f, y, (pageWidth - 40).toFloat(), y + 110f, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 11f
        paint.color = Color.parseColor("#334155")
        canvas.drawText("TOTAL FEE COLLECTION (INCOME):", 55f, y + 28f, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = Color.parseColor("#15803D")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Rs. ${String.format(Locale.US, "%,.0f", totalIncome)}", (pageWidth - 55).toFloat(), y + 28f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#334155")
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("TOTAL TEACHER SALARIES PAID:", 55f, y + 52f, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = Color.parseColor("#B91C1C")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Rs. ${String.format(Locale.US, "%,.0f", totalSalaries)}", (pageWidth - 55).toFloat(), y + 52f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#334155")
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("TOTAL GENERAL EXPENSES (RENT/BILLS/ADS):", 55f, y + 76f, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = Color.parseColor("#B91C1C")
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Rs. ${String.format(Locale.US, "%,.0f", totalExpenses)}", (pageWidth - 55).toFloat(), y + 76f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("NET BALANCE / PROFIT:", 55f, y + 100f, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = if (netProfit >= 0) Color.parseColor("#15803D") else Color.parseColor("#B91C1C")
        paint.textSize = 14f
        canvas.drawText("Rs. ${String.format(Locale.US, "%,.0f", netProfit)}", (pageWidth - 55).toFloat(), y + 100f, paint)

        y += 140f

        // Recent Payments Table
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Recent Fee Collections", 40f, y, paint)
        y += 12f

        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, paint)
        y += 16f

        paint.color = Color.parseColor("#64748B")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("RECEIPT", 45f, y, paint)
        canvas.drawText("STUDENT", 130f, y, paint)
        canvas.drawText("COURSE", 280f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("AMOUNT", 450f, y, paint)
        canvas.drawText("METHOD", (pageWidth - 45).toFloat(), y, paint)

        y += 8f
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, paint)

        paint.typeface = Typeface.DEFAULT
        payments.take(8).forEach { p ->
            y += 18f
            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.parseColor("#1E293B")
            canvas.drawText(p.receiptNo, 45f, y, paint)
            canvas.drawText(p.studentName, 130f, y, paint)
            canvas.drawText(p.courseName, 280f, y, paint)
            paint.textAlign = Paint.Align.RIGHT
            paint.color = Color.parseColor("#15803D")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Rs. ${String.format(Locale.US, "%.0f", p.paidAmount)}", 450f, y, paint)
            paint.typeface = Typeface.DEFAULT
            paint.color = Color.parseColor("#64748B")
            canvas.drawText(p.paymentMethod, (pageWidth - 45).toFloat(), y, paint)
        }

        y += 30f
        // Expenses Table
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Recent Expenditures & Salaries", 40f, y, paint)
        y += 12f

        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, paint)
        y += 16f

        paint.color = Color.parseColor("#64748B")
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("EXPENSE NO", 45f, y, paint)
        canvas.drawText("TITLE / CATEGORY", 150f, y, paint)
        canvas.drawText("PAID TO", 330f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("AMOUNT", (pageWidth - 45).toFloat(), y, paint)

        y += 8f
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawLine(40f, y, (pageWidth - 40).toFloat(), y, paint)

        paint.typeface = Typeface.DEFAULT
        expenses.take(8).forEach { e ->
            y += 18f
            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.parseColor("#1E293B")
            canvas.drawText(e.expenseNo, 45f, y, paint)
            canvas.drawText("${e.title} (${e.category})", 150f, y, paint)
            canvas.drawText(e.paidTo, 330f, y, paint)
            paint.textAlign = Paint.Align.RIGHT
            paint.color = Color.parseColor("#B91C1C")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Rs. ${String.format(Locale.US, "%.0f", e.amount)}", (pageWidth - 45).toFloat(), y, paint)
            paint.typeface = Typeface.DEFAULT
        }

        // Footer
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#94A3B8")
        paint.textSize = 8f
        canvas.drawText("Official Confidential Financial Document • ${settings.academyName}", pageWidth / 2f, pageHeight - 25f, paint)

        pdfDocument.finishPage(page)

        val fileName = "Financial_Report_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.pdf"
        val dir = File(context.cacheDir, "reports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)

        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun shareReportPdf(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Financial Report - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Report via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
