package com.findora.app.data.search

import com.findora.app.data.model.Category
import com.findora.app.data.model.Document
import org.junit.Assert.assertEquals
import org.junit.Test

class RankByRelevanceTest {

    private fun doc(id: Long, title: String, body: String, createdAt: Long = 0L) =
        Document(
            id = id,
            title = title,
            imagePath = null,
            ocrText = body,
            category = Category.OTHER,
            createdAt = createdAt,
        )

    @Test
    fun titleMatchRanksAboveBodyMatch() {
        val bodyMatch = doc(1, "Something else", "the invoice total is due")
        val titleMatch = doc(2, "Invoice March", "unrelated text")
        val ranked = rankByRelevance(listOf(bodyMatch, titleMatch), "invoice", listOf("invoice"))
        assertEquals(listOf(2L, 1L), ranked.map { it.id })
    }

    @Test
    fun moreOccurrencesRanksHigher() {
        val few = doc(1, "x", "invoice")
        val many = doc(2, "y", "invoice invoice invoice")
        val ranked = rankByRelevance(listOf(few, many), "invoice", listOf("invoice"))
        assertEquals(listOf(2L, 1L), ranked.map { it.id })
    }

    @Test
    fun newerWinsWhenScoresTie() {
        val older = doc(1, "invoice", "invoice", createdAt = 100L)
        val newer = doc(2, "invoice", "invoice", createdAt = 200L)
        val ranked = rankByRelevance(listOf(older, newer), "invoice", listOf("invoice"))
        assertEquals(listOf(2L, 1L), ranked.map { it.id })
    }
}
