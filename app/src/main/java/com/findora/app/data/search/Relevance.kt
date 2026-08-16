package com.findora.app.data.search

import com.findora.app.data.model.Document

/**
 * Orders [docs] by how well they match [query]/[terms], best first:
 * a title hit outranks a body hit, more occurrences rank higher, and more
 * recent documents win ties. Pure so it can be unit-tested without a database.
 */
fun rankByRelevance(docs: List<Document>, query: String, terms: List<String>): List<Document> {
    val phrase = query.trim().lowercase()
    val loweredTerms = terms.map { it.lowercase() }.filter { it.isNotBlank() }

    fun score(doc: Document): Int {
        val title = doc.title.lowercase()
        val body = doc.ocrText.lowercase()
        var s = 0
        if (phrase.isNotEmpty()) {
            if (title.contains(phrase)) s += 1000   // whole query in the title
            if (body.contains(phrase)) s += 200      // whole query in the body
        }
        for (t in loweredTerms) {
            if (title.contains(t)) s += 100
            s += minOf(highlightRanges(body, listOf(t)).size, 20) * 5  // term frequency, capped
        }
        return s
    }

    return docs.sortedWith(
        compareByDescending<Document> { score(it) }.thenByDescending { it.createdAt },
    )
}
