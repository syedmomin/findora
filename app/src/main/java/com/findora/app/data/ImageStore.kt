package com.findora.app.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Persists scanned images into app-private storage (never leaves the device). */
class ImageStore(private val appContext: Context) {

    private val dir: File by lazy {
        File(appContext.filesDir, "documents").apply { mkdirs() }
    }

    /** Copies [source] into private storage and returns the stored file path. */
    suspend fun persist(source: Uri): String = withContext(Dispatchers.IO) {
        val dest = File(dir, "${UUID.randomUUID()}.jpg")
        appContext.contentResolver.openInputStream(source).use { input ->
            requireNotNull(input) { "Cannot open source image: $source" }
            dest.outputStream().use { output -> input.copyTo(output) }
        }
        dest.absolutePath
    }

    /** Saves a [bitmap] (e.g. a rendered PDF page) as a JPEG and returns its path. */
    suspend fun persistBitmap(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val dest = File(dir, "${UUID.randomUUID()}.jpg")
        dest.outputStream().use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
        dest.absolutePath
    }

    suspend fun delete(path: String?) = withContext(Dispatchers.IO) {
        if (path != null) runCatching { File(path).delete() }
        Unit
    }

    fun uriFor(path: String?): Uri? = path?.let { File(it).toUri() }
}
