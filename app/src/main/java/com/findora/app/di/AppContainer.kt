package com.findora.app.di

import android.content.Context
import com.findora.app.data.ImageStore
import com.findora.app.data.db.FindoraDatabase
import com.findora.app.data.ocr.OcrService
import com.findora.app.data.ocr.PdfRasterizer
import com.findora.app.data.repository.DocumentRepository
import com.findora.app.data.repository.SettingsRepository

/**
 * Manual dependency container (lightweight service locator). Chosen over Hilt to
 * keep the build free of annotation processors. Single instance lives on
 * [com.findora.app.FindoraApplication].
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext
    private val database by lazy { FindoraDatabase.get(appContext) }
    private val ocrService by lazy { OcrService(appContext) }
    private val imageStore by lazy { ImageStore(appContext) }
    private val pdfRasterizer by lazy { PdfRasterizer(appContext) }

    val documentRepository: DocumentRepository by lazy {
        DocumentRepository(
            dao = database.documentDao(),
            ocr = ocrService,
            imageStore = imageStore,
            pdfRasterizer = pdfRasterizer,
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(appContext)
    }
}
