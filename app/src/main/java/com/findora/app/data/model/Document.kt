package com.findora.app.data.model

/** Domain model for a scanned document. */
data class Document(
    val id: Long,
    val title: String,
    val imagePath: String?,
    val ocrText: String,
    val category: Category,
    val createdAt: Long,
) {
    /** A short one-line preview of the recognized text for cards/lists. */
    val preview: String
        get() = ocrText.trim().replace(Regex("\\s+"), " ").take(120)
}

/** A search hit: the matched document plus a highlighted snippet. */
data class SearchResult(
    val document: Document,
    val snippet: String,
    /** Character ranges within [snippet] that matched the query, for highlighting. */
    val highlights: List<IntRange>,
)
