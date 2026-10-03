package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.AcademySettings
import com.example.data.model.Certificate
import java.io.File
import java.io.FileOutputStream

class PdfCertificateGenerator(private val context: Context) {

    fun generateCertificatePdf(
        certificate: Certificate,
        settings: AcademySettings
    ): File? {
        // Landscape A4 size in standard 72 DPI points: 842 x 595
        val pageWidth = 842
        val pageHeight = 595
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Background: Soft cream / ivory
        canvas.drawColor(Color.parseColor("#FCFBF7"))

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // 1. Luxury Outer Border (Deep Navy #0A192F)
        paint.color = Color.parseColor("#0A192F")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 14f
        canvas.drawRect(20f, 20f, (pageWidth - 20).toFloat(), (pageHeight - 20).toFloat(), paint)

        // 2. Middle Ornamental Border (Metallic Gold #D4AF37)
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 3f
        canvas.drawRect(34f, 34f, (pageWidth - 34).toFloat(), (pageHeight - 34).toFloat(), paint)

        // 3. Inner Fine Navy Line
        paint.color = Color.parseColor("#1B355A")
        paint.strokeWidth = 1f
        canvas.drawRect(40f, 40f, (pageWidth - 40).toFloat(), (pageHeight - 40).toFloat(), paint)

        // 4. Gold Corner Decorative Accents
        val cornerSize = 40f
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 4f
        // Top-Left
        canvas.drawLine(40f, 40f, 40f + cornerSize, 40f, paint)
        canvas.drawLine(40f, 40f, 40f, 40f + cornerSize, paint)
        // Top-Right
        canvas.drawLine((pageWidth - 40).toFloat(), 40f, (pageWidth - 40 - cornerSize), 40f, paint)
        canvas.drawLine((pageWidth - 40).toFloat(), 40f, (pageWidth - 40).toFloat(), 40f + cornerSize, paint)
        // Bottom-Left
        canvas.drawLine(40f, (pageHeight - 40).toFloat(), 40f + cornerSize, (pageHeight - 40).toFloat(), paint)
        canvas.drawLine(40f, (pageHeight - 40).toFloat(), 40f, (pageHeight - 40 - cornerSize).toFloat(), paint)
        // Bottom-Right
        canvas.drawLine((pageWidth - 40).toFloat(), (pageHeight - 40).toFloat(), (pageWidth - 40 - cornerSize), (pageHeight - 40).toFloat(), paint)
        canvas.drawLine((pageWidth - 40).toFloat(), (pageHeight - 40).toFloat(), (pageWidth - 40).toFloat(), (pageHeight - 40 - cornerSize).toFloat(), paint)

        paint.style = Paint.Style.FILL

        // Center Emblem / Crescent Gold Motif
        paint.color = Color.parseColor("#D4AF37")
        paint.textSize = 28f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("✦ 🏛 ✦", pageWidth / 2f, 75f, paint)

        // Academy Name
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText(settings.academyName.uppercase(), pageWidth / 2f, 110f, paint)

        // Tagline
        paint.color = Color.parseColor("#B45309")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText(settings.tagline, pageWidth / 2f, 128f, paint)

        // Certificate Title Banner
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText("CERTIFICATE OF COMPLETION", pageWidth / 2f, 175f, paint)

        // Thin decorative gold line under title
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 2f
        canvas.drawLine(pageWidth / 2f - 180f, 186f, pageWidth / 2f + 180f, 186f, paint)

        // "PROUDLY PRESENTED TO"
        paint.color = Color.parseColor("#64748B")
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("THIS IS PROUDLY PRESENTED TO", pageWidth / 2f, 215f, paint)

        // Student Name in Large Serif Bold
        paint.color = Color.parseColor("#0A192F")
        paint.textSize = 30f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText(certificate.studentName, pageWidth / 2f, 255f, paint)

        // Underline under Student Name
        paint.color = Color.parseColor("#D4AF37")
        paint.strokeWidth = 1.5f
        val nameWidth = paint.measureText(certificate.studentName)
        canvas.drawLine(pageWidth / 2f - (nameWidth / 2f + 30f), 265f, pageWidth / 2f + (nameWidth / 2f + 30f), 265f, paint)

        // S/O or D/O
        paint.color = Color.parseColor("#475569")
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("S/D of ${certificate.fatherName} (Roll No: ${certificate.studentRollNo})", pageWidth / 2f, 285f, paint)

        // Course completion text
        paint.color = Color.parseColor("#1E293B")
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        canvas.drawText("for successfully completing the specialized professional program in", pageWidth / 2f, 315f, paint)

        // Course Name
        paint.color = Color.parseColor("#B45309")
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText(certificate.courseName, pageWidth / 2f, 345f, paint)

        // Duration & Grade
        paint.color = Color.parseColor("#334155")
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("Duration: ${certificate.duration} | Grade Achieved: ${certificate.grade}", pageWidth / 2f, 370f, paint)

        // Custom User Description
        paint.color = Color.parseColor("#475569")
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        val descriptionLines = breakTextIntoLines(certificate.customDescription, 620f, paint)
        var descY = 398f
        descriptionLines.forEach { line ->
            canvas.drawText(line, pageWidth / 2f, descY, paint)
            descY += 16f
        }

        // Bottom Details & Signatures Section
        val footerY = 515f

        // Left Signature: Teacher / Instructor
        paint.color = Color.parseColor("#0A192F")
        paint.strokeWidth = 1.5f
        canvas.drawLine(100f, footerY, 280f, footerY, paint)

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText(certificate.teacherName, 190f, footerY + 18f, paint)
        paint.textSize = 10f
        paint.color = Color.parseColor("#64748B")
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("Course Instructor / Teacher", 190f, footerY + 32f, paint)

        // Center Seal: Official Institute Seal
        paint.color = Color.parseColor("#FEF3C7")
        paint.style = Paint.Style.FILL
        canvas.drawCircle(pageWidth / 2f, footerY - 5f, 36f, paint)
        paint.color = Color.parseColor("#D4AF37")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        canvas.drawCircle(pageWidth / 2f, footerY - 5f, 36f, paint)
        paint.strokeWidth = 1f
        canvas.drawCircle(pageWidth / 2f, footerY - 5f, 30f, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#92400E")
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("OFFICIAL SEAL", pageWidth / 2f, footerY - 12f, paint)
        paint.textSize = 14f
        canvas.drawText("★", pageWidth / 2f, footerY + 3f, paint)
        paint.textSize = 8f
        canvas.drawText("VERIFIED", pageWidth / 2f, footerY + 16f, paint)

        // Right Signature: Director / Principal
        paint.color = Color.parseColor("#0A192F")
        paint.strokeWidth = 1.5f
        canvas.drawLine(pageWidth - 280f, footerY, pageWidth - 100f, footerY, paint)

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        canvas.drawText(certificate.directorName, (pageWidth - 190).toFloat(), footerY + 18f, paint)
        paint.textSize = 10f
        paint.color = Color.parseColor("#64748B")
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("Director / Principal", (pageWidth - 190).toFloat(), footerY + 32f, paint)

        // Metadata footer: Certificate No & Issue Date
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 9f
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("Certificate No: ${certificate.certificateNo}", 55f, 565f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Date of Issue: ${certificate.issueDate}", (pageWidth - 55).toFloat(), 565f, paint)

        pdfDocument.finishPage(page)

        val cleanName = certificate.studentName.replace(Regex("[^a-zA-Z0-9]"), "")
        val fileName = "Certificate_${certificate.certificateNo}_$cleanName.pdf"
        val dir = File(context.cacheDir, "certificates")
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

    private fun breakTextIntoLines(text: String, maxWidth: Float, paint: Paint): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        for (w in words) {
            val testLine = if (currentLine.isEmpty()) w else "$currentLine $w"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = w
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    fun shareCertificatePdf(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Course Certificate - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Certificate via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
