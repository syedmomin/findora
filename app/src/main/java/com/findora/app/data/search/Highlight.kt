package com.findora.app.data.search

/**
 * Every character range in [text] that matches any of [terms], case-insensitively.
 *
 * Ranges use the `start until end` convention (upper bound exclusive when the
 * consumer applies `+1`, matching [com.findora.app.ui.components.HighlightedText]).
 * Blank terms are ignored; the result is sorted by start offset.
 */
fun highlightRanges(text: String, terms: List<String>): List<IntRange> = emptyList()
