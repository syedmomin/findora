package com.findora.app.data.search

import com.findora.app.data.model.Document
import com.findora.app.data.model.SearchResult

private const val SNIPPET_RADIUS = 60

/**
 * Builds a search snippet centered on the first matched term, plus the ranges
 * of every matched term within that snippet (for highlighting in the UI).
 */
fun Document.toSearchResult(terms: List<String>): SearchResult {
    val text = ocrText.replace(Regex("\\s+"), " ").trim()
    val lower = text.lowercase()
    val loweredTerms = terms.map { it.lowercase() }.filter { it.isNotBlank() }

    val firstMatch = loweredTerms
        .mapNotNull { term -> lower.indexOf(term).takeIf { it >= 0 } }
        .minOrNull() ?: 0

    val start = (firstMatch - SNIPPET_RADIUS).coerceAtLeast(0)
    val end = (firstMatch + SNIPPET_RADIUS).coerceAtMost(text.length)
    val prefix = if (start > 0) "…" else ""
    val suffix = if (end < text.length) "…" else ""
    val snippet = prefix + text.substring(start, end).trim() + suffix

    return SearchResult(
        document = this,
        snippet = snippet.ifBlank { preview },
        highlights = highlightRanges(snippet, terms),
    )
}
