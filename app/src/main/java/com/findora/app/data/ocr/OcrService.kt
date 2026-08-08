package com.findora.app.data.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * On-device text recognition via Google ML Kit. No network, no data leaves the
 * device — this is what lets Findora make its privacy-first promise.
 */
class OcrService(private val appContext: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /** Recognizes all text in the image at [uri]. Returns the full block text. */
    suspend fun recognize(uri: Uri): String {
        val image = InputImage.fromFilePath(appContext, uri)
        return suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result -> cont.resume(result.text) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
            cont.invokeOnCancellation { /* recognizer manages its own lifecycle */ }
        }
    }
}
