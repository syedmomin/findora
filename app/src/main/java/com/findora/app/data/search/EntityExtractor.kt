package com.findora.app.data.search

import com.findora.app.data.model.Category

/** Structured fields pulled out of raw OCR text. */
data class ExtractedEntities(
    val emails: List<String>,
    val phones: List<String>,
    val dates: List<String>,
    val amounts: List<String>,
    val invoiceNumbers: List<String>,
) {
    /** Flattened, de-duplicated tokens for the FTS index. */
    fun asIndexString(): String =
        (emails + phones + dates + amounts + invoiceNumbers)
            .distinct()
            .joinToString(" ")
}

/**
 * Extracts the searchable fields the spec calls out (email, phone, invoice #,
 * date, amount) and guesses a document category. Pure/stateless so it's trivial
 * to unit-test.
 */
object EntityExtractor {

    private val EMAIL = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
    private val PHONE = Regex("""(?<!\d)(\+?\d[\d\s().-]{7,}\d)(?!\d)""")
    private val DATE = Regex(
        """\b(\d{1,2}[/-]\d{1,2}[/-]\d{2,4}|\d{4}[/-]\d{1,2}[/-]\d{1,2}|""" +
            """(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\.?\s+\d{1,2},?\s*\d{0,4})\b""",
        RegexOption.IGNORE_CASE,
    )
    private val AMOUNT = Regex("""[$€£₹]\s?\d[\d,]*(?:\.\d{2})?""")
    private val INVOICE = Regex(
        """(?:invoice|inv|bill|receipt|order|ref)\s*#?\s*[:.]?\s*([A-Za-z0-9-]{3,})""",
        RegexOption.IGNORE_CASE,
    )

    fun extract(text: String): ExtractedEntities = ExtractedEntities(
        emails = EMAIL.findAll(text).map { it.value }.toList(),
        phones = PHONE.findAll(text).map { it.value.trim() }.filter { it.count(Char::isDigit) in 7..15 }.toList(),
        dates = DATE.findAll(text).map { it.value.trim() }.toList(),
        amounts = AMOUNT.findAll(text).map { it.value.trim() }.toList(),
        invoiceNumbers = INVOICE.findAll(text).map { it.groupValues[1] }.toList(),
    )

    /** Heuristic category guess from OCR keywords. Falls back to OTHER. */
    fun guessCategory(text: String): Category {
        val t = text.lowercase()
        return when {
            listOf("invoice", "amount due", "bill to").any(t::contains) -> Category.INVOICE
            listOf("receipt", "subtotal", "total", "change due", "cash").any(t::contains) -> Category.RECEIPT
            listOf("statement", "due date", "utility", "electric", "account number").any(t::contains) -> Category.BILL
            listOf("passport", "driver", "license", "identity", "date of birth", "id no").any(t::contains) -> Category.ID
            else -> Category.OTHER
        }
    }

    /** Suggests a title: first non-empty line, trimmed, else a default. */
    fun suggestTitle(text: String): String =
        text.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.length in 2..60 }
            ?.replaceFirstChar { it.uppercase() }
            ?: "Scanned document"
}
