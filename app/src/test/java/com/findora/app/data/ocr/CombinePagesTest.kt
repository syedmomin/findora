package com.findora.app.data.ocr

import org.junit.Assert.assertEquals
import org.junit.Test

class CombinePagesTest {

    @Test
    fun joinsPagesWithBlankLine() {
        assertEquals(
            "Page one text\n\nPage two text",
            combinePages(listOf("Page one text", "Page two text")),
        )
    }

    @Test
    fun skipsBlankPages() {
        assertEquals("hello\n\nworld", combinePages(listOf("hello", "   ", "world")))
    }

    @Test
    fun collapsesExtraBlankLines() {
        assertEquals("a\n\nb", combinePages(listOf("a\n\n\n\nb")))
    }

    @Test
    fun trimsEachPage() {
        assertEquals("hi", combinePages(listOf("  hi  ")))
    }

    @Test
    fun emptyListIsEmpty() {
        assertEquals("", combinePages(emptyList()))
    }
}
