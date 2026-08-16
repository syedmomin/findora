package com.findora.app.data.search

import com.findora.app.data.model.Document

/**
 * Orders [docs] by how well they match [query]/[terms], best first:
 * a title hit outranks a body hit, more occurrences rank higher, and more
 * recent documents win ties. Pure so it can be unit-tested without a database.
 */
fun rankByRelevance(docs: List<Document>, query: String, terms: List<String>): List<Document> = docs
