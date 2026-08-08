package com.findora.app

import com.findora.app.data.model.Category
import com.findora.app.data.search.EntityExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntityExtractorTest {

    private val receipt = """
        SuperMart Receipt
        Date: 12/03/2024
        Order #: 4471
        Contact: help@supermart.com
        Phone: +1 (415) 555-0132
        Subtotal: $42.10
        Total: $46.31
    """.trimIndent()

    @Test
    fun `extracts email`() {
        val e = EntityExtractor.extract(receipt)
        assertTrue(e.emails.contains("help@supermart.com"))
    }

    @Test
    fun `extracts phone number`() {
        val e = EntityExtractor.extract(receipt)
        assertTrue(e.phones.any { it.contains("415") && it.contains("555") })
    }

    @Test
    fun `extracts amounts`() {
        val e = EntityExtractor.extract(receipt)
        assertTrue(e.amounts.contains("$46.31"))
    }

    @Test
    fun `extracts invoice number`() {
        val e = EntityExtractor.extract(receipt)
        assertTrue(e.invoiceNumbers.any { it.contains("4471") })
    }

    @Test
    fun `extracts date`() {
        val e = EntityExtractor.extract(receipt)
        assertTrue(e.dates.contains("12/03/2024"))
    }

    @Test
    fun `guesses receipt category`() {
        assertEquals(Category.RECEIPT, EntityExtractor.guessCategory(receipt))
    }

    @Test
    fun `index string is non-empty and deduped`() {
        val index = EntityExtractor.extract(receipt).asIndexString()
        assertTrue(index.isNotBlank())
        // duplicates collapsed
        assertEquals(index.split(" ").size, index.split(" ").distinct().size)
    }

    @Test
    fun `suggests a title from first meaningful line`() {
        assertEquals("SuperMart Receipt", EntityExtractor.suggestTitle(receipt))
    }
}
