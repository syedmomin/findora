package com.findora.app.data.search

import org.junit.Assert.assertEquals
import org.junit.Test

class HighlightRangesTest {

    @Test
    fun findsSingleTerm() {
        assertEquals(listOf(8 until 13), highlightRanges("Invoice 12345 total", listOf("12345")))
    }

    @Test
    fun isCaseInsensitive() {
        assertEquals(listOf(6 until 11), highlightRanges("Hello WORLD", listOf("world")))
    }

    @Test
    fun findsAllOccurrences() {
        assertEquals(listOf(0 until 2, 6 until 8), highlightRanges("aa bb aa", listOf("aa")))
    }

    @Test
    fun sortsMultipleTermsByPosition() {
        assertEquals(listOf(0 until 4, 5 until 8), highlightRanges("name 999", listOf("999", "name")))
    }

    @Test
    fun returnsEmptyWhenNoMatch() {
        assertEquals(emptyList<IntRange>(), highlightRanges("abc", listOf("xyz")))
    }

    @Test
    fun ignoresBlankTerms() {
        assertEquals(listOf(0 until 1), highlightRanges("abc", listOf("", "a")))
    }
}
