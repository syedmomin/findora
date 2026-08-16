package com.findora.app.data.search

/**
 * Every character range in [text] that matches any of [terms], case-insensitively.
 *
 * Ranges use the `start until end` convention (upper bound exclusive when the
 * consumer applies `+1`, matching [com.findora.app.ui.components.HighlightedText]).
 * Blank terms are ignored; the result is sorted by start offset.
 */
fun highlightRanges(text: String, terms: List<String>): List<IntRange> {
    if (text.isEmpty()) return emptyList()
    val haystack = text.lowercase()
    return terms.asSequence()
        .map { it.lowercase() }
        .filter { it.isNotBlank() }
        .flatMap { term ->
            sequence {
                var idx = haystack.indexOf(term)
                while (idx >= 0) {
                    yield(idx until (idx + term.length))
                    idx = haystack.indexOf(term, idx + term.length)
                }
            }
        }
        .sortedBy { it.first }
        .toList()
}
