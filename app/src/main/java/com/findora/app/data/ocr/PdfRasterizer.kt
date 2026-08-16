package com.findora.app.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Renders the pages of a PDF to bitmaps using the platform [PdfRenderer] (no extra
 * dependency). Pages are processed one at a time and recycled after [transform] so
 * memory stays bounded even for long PDFs.
 */
class PdfRasterizer(private val appContext: Context) {

    /** Target raster width in pixels — large enough for accurate OCR. */
    private val targetWidth = 2000

    suspend fun <T> mapPages(uri: Uri, transform: suspend (index: Int, bitmap: Bitmap) -> T): List<T> =
        withContext(Dispatchers.IO) {
            val results = mutableListOf<T>()
            val pfd = appContext.contentResolver.openFileDescriptor(uri, "r")
                ?: error("Cannot open PDF: $uri")
            pfd.use {
                PdfRenderer(it).use { renderer ->
                    for (i in 0 until renderer.pageCount) {
                        val bitmap = renderer.openPage(i).use { page ->
                            val height = (page.height.toFloat() / page.width * targetWidth)
                                .toInt().coerceAtLeast(1)
                            Bitmap.createBitmap(targetWidth, height, Bitmap.Config.ARGB_8888).also { b ->
                                b.eraseColor(Color.WHITE) // white backing so transparent PDFs OCR cleanly
                                page.render(b, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            }
                        }
                        try {
                            results.add(transform(i, bitmap))
                        } finally {
                            bitmap.recycle()
                        }
                    }
                }
            }
            results
        }
}
