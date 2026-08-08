package com.findora.app

import com.findora.app.data.model.Category
import com.findora.app.data.model.Document
import com.findora.app.data.search.toSearchResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SnippetTest {

    private fun doc(text: String) = Document(
        id = 1,
        title = "Doc",
        imagePath = null,
        ocrText = text,
        category = Category.OTHER,
        createdAt = 0,
    )

    @Test
    fun `snippet contains the matched term`() {
        val result = doc("The quick brown fox jumps over the lazy dog").toSearchResult(listOf("fox"))
        assertTrue(result.snippet.contains("fox"))
    }

    @Test
    fun `highlights cover every occurrence of the term`() {
        val result = doc("alpha beta alpha gamma alpha").toSearchResult(listOf("alpha"))
        assertEquals(3, result.highlights.size)
        result.highlights.forEach { range ->
            assertEquals("alpha", result.snippet.substring(range.first, range.last + 1).lowercase())
        }
    }

    @Test
    fun `matching is case-insensitive`() {
        val result = doc("Invoice Number ACME-99").toSearchResult(listOf("acme"))
        assertTrue(result.highlights.isNotEmpty())
    }

    @Test
    fun `long text is truncated with ellipsis around the match`() {
        val long = "x".repeat(200) + " needle " + "y".repeat(200)
        val result = doc(long).toSearchResult(listOf("needle"))
        assertTrue(result.snippet.startsWith("…"))
        assertTrue(result.snippet.endsWith("…"))
        assertTrue(result.snippet.contains("needle"))
    }
}
