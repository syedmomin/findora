package com.findora.app.data.repository

import android.net.Uri
import com.findora.app.data.ImageStore
import com.findora.app.data.db.DocumentDao
import com.findora.app.data.db.DocumentEntity
import com.findora.app.data.model.Category
import com.findora.app.data.model.Document
import com.findora.app.data.model.SearchResult
import com.findora.app.data.ocr.OcrService
import com.findora.app.data.search.EntityExtractor
import com.findora.app.data.search.toSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepository(
    private val dao: DocumentDao,
    private val ocr: OcrService,
    private val imageStore: ImageStore,
    private val now: () -> Long = System::currentTimeMillis,
) {

    fun recentDocuments(limit: Int = 10): Flow<List<Document>> =
        dao.observeRecent(limit).map { list -> list.map { it.toModel() } }

    fun allDocuments(): Flow<List<Document>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    fun documentsIn(category: Category): Flow<List<Document>> =
        dao.observeByCategory(category.name).map { list -> list.map { it.toModel() } }

    fun document(id: Long): Flow<Document?> =
        dao.observeById(id).map { it?.toModel() }

    fun categoryCounts(): Flow<Map<Category, Int>> =
        dao.observeCategoryCounts().map { rows ->
            rows.associate { Category.fromName(it.category) to it.count }
        }

    /**
     * Runs OCR on [image], extracts entities, guesses a category, persists the
     * image, and stores everything. Returns the new document id.
     */
    suspend fun scanAndSave(image: Uri): Long {
        val text = ocr.recognize(image)
        val entities = EntityExtractor.extract(text)
        val category = EntityExtractor.guessCategory(text)
        val title = EntityExtractor.suggestTitle(text)
        val storedPath = imageStore.persist(image)
        val entity = DocumentEntity(
            title = title,
            imagePath = storedPath,
            ocrText = text,
            category = category.name,
            createdAt = now(),
        )
        return dao.insert(entity, entities.asIndexString())
    }

    suspend fun rename(document: Document, newTitle: String) {
        val entities = EntityExtractor.extract(document.ocrText).asIndexString()
        dao.rename(document.id, newTitle.trim(), entities)
    }

    suspend fun delete(document: Document) {
        dao.deleteById(document.id)
        imageStore.delete(document.imagePath)
    }

    /** Field-aware full-text search with highlighted snippets. */
    suspend fun search(query: String): List<SearchResult> {
        val terms = query.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (terms.isEmpty()) return emptyList()
        val ftsQuery = terms.joinToString(" ") { sanitizeToken(it) + "*" }
        val hits = dao.search(ftsQuery)
        return hits.map { it.toModel().toSearchResult(terms) }
    }

    // ---- helpers ----

    private fun sanitizeToken(token: String): String =
        // Strip FTS operators; keep alphanumerics so MATCH stays valid.
        token.replace(Regex("[^\\p{L}\\p{N}]"), "").ifBlank { token.take(1) }

    private fun DocumentEntity.toModel() = Document(
        id = id,
        title = title,
        imagePath = imagePath,
        ocrText = ocrText,
        category = Category.fromName(category),
        createdAt = createdAt,
    )
}
