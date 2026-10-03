package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.AcademyRepository
import com.example.data.repository.FirebaseSyncRepository
import com.example.service.PdfCertificateGenerator
import com.example.service.PdfReceiptGenerator
import com.example.service.PdfReportGenerator
import com.example.service.SmsService
import com.example.service.ThermalPrinterService

class AcademyApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: AcademyRepository
        private set

    lateinit var firebaseSyncRepository: FirebaseSyncRepository
        private set

    lateinit var smsService: SmsService
        private set

    lateinit var thermalPrinterService: ThermalPrinterService
        private set

    lateinit var pdfReceiptGenerator: PdfReceiptGenerator
        private set

    lateinit var pdfCertificateGenerator: PdfCertificateGenerator
        private set

    lateinit var pdfReportGenerator: PdfReportGenerator
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = AcademyRepository(this)
        firebaseSyncRepository = FirebaseSyncRepository(this, repository)
        smsService = SmsService(this)
        thermalPrinterService = ThermalPrinterService(this)
        pdfReceiptGenerator = PdfReceiptGenerator(this)
        pdfCertificateGenerator = PdfCertificateGenerator(this)
        pdfReportGenerator = PdfReportGenerator(this)
    }
}
