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
import com.example.data.model.FeePayment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfReceiptGenerator(private val context: Context) {

    fun generateReceiptPdf(
        payment: FeePayment,
        settings: AcademySettings
    ): File? {
        val pageWidth = 400
        val pageHeight = 650
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Background
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Header Background Banner
        paint.color = Color.parseColor("#0A192F")
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 110f, paint)

        // Gold Accent Line
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 110f, pageWidth.toFloat(), 110f, paint)

        // Academy Name
        paint.color = Color.parseColor("#D4AF37")
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(settings.academyName.uppercase(), pageWidth / 2f, 40f, paint)

        // Tagline
        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(settings.tagline, pageWidth / 2f, 58f, paint)

        // Address & Phone
        paint.color = Color.parseColor("#CBD5E1")
        paint.textSize = 9f
        canvas.drawText(settings.address, pageWidth / 2f, 75f, paint)
        canvas.drawText("Tel: ${settings.phoneNumber} | Web: ${settings.website}", pageWidth / 2f, 92f, paint)

        var y = 140f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("OFFICIAL FEE RECEIPT", pageWidth / 2f, y, paint)

        y += 20f
        paint.color = Color.parseColor("#E2E8F0")
        paint.strokeWidth = 1f
        canvas.drawLine(30f, y, (pageWidth - 30).toFloat(), y, paint)

        fun drawRow(label: String, value: String, isBold: Boolean = false, isHighlight: Boolean = false) {
            y += 20f
            paint.textAlign = Paint.Align.LEFT
            paint.textSize = 11f
            paint.color = if (isHighlight) Color.parseColor("#0A192F") else Color.parseColor("#475569")
            paint.typeface = if (isBold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
            canvas.drawText(label, 35f, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = if (isHighlight) Color.parseColor("#0A192F") else Color.BLACK
            canvas.drawText(value, (pageWidth - 35).toFloat(), y, paint)
        }

        drawRow("Receipt No:", payment.receiptNo, isBold = true)
        val dateStr = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.US).format(Date(payment.paymentDate))
        drawRow("Payment Date:", dateStr)
        drawRow("Student Name:", payment.studentName, isBold = true)
        drawRow("Father Name:", payment.fatherName)
        drawRow("Roll No / ID:", payment.studentRollNo)
        drawRow("Course:", payment.courseName, isBold = true)
        drawRow("Batch:", payment.batchName)

        y += 10f
        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(30f, y, (pageWidth - 30).toFloat(), y, paint)

        drawRow("Total Course Fee:", "Rs. ${String.format(Locale.US, "%,.0f", payment.totalFee)}")
        drawRow("Previous Paid:", "Rs. ${String.format(Locale.US, "%,.0f", payment.previousPaid)}")

        // Highlight box for Current Paid
        y += 8f
        paint.color = Color.parseColor("#FEF3C7")
        canvas.drawRoundRect(30f, y, (pageWidth - 30).toFloat(), y + 30f, 6f, 6f, paint)
        paint.color = Color.parseColor("#B45309")
        paint.strokeWidth = 1.5f
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(30f, y, (pageWidth - 30).toFloat(), y + 30f, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#92400E")
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("AMOUNT PAID NOW:", 40f, y + 20f, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 14f
        canvas.drawText("Rs. ${String.format(Locale.US, "%,.0f", payment.paidAmount)}", (pageWidth - 40).toFloat(), y + 20f, paint)
        y += 30f

        drawRow("New Total Paid:", "Rs. ${String.format(Locale.US, "%,.0f", payment.newTotalPaid)}")
        drawRow("Remaining Balance:", "Rs. ${String.format(Locale.US, "%,.0f", payment.remainingFee)}", isBold = true, isHighlight = true)
        drawRow("Payment Method:", payment.paymentMethod)
        drawRow("Received By:", payment.receivedBy)

        y += 15f
        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(30f, y, (pageWidth - 30).toFloat(), y, paint)

        y += 25f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(settings.receiptFooterText, pageWidth / 2f, y, paint)

        y += 15f
        paint.color = Color.parseColor("#94A3B8")
        paint.textSize = 8f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Computer generated receipt. No signature required.", pageWidth / 2f, y, paint)

        pdfDocument.finishPage(page)

        val cleanStudentName = payment.studentName.replace(Regex("[^a-zA-Z0-9]"), "")
        val fileName = "Receipt_${payment.receiptNo}_$cleanStudentName.pdf"
        val dir = File(context.cacheDir, "receipts")
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

    fun sharePdf(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Fee Receipt - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Fee Receipt via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
