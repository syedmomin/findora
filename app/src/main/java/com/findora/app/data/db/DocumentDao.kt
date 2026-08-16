package com.findora.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/** A category with its document count, for the Categories screen. */
data class CategoryCount(val category: String, val count: Int)

@Dao
abstract class DocumentDao {

    // ---- Reads (reactive) ----

    @Query("SELECT * FROM documents ORDER BY createdAt DESC LIMIT :limit")
    abstract fun observeRecent(limit: Int): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    abstract fun observeAll(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE category = :category ORDER BY createdAt DESC")
    abstract fun observeByCategory(category: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    abstract fun observeById(id: Long): Flow<DocumentEntity?>

    @Query("SELECT category, COUNT(*) AS count FROM documents GROUP BY category")
    abstract fun observeCategoryCounts(): Flow<List<CategoryCount>>

    /**
     * Full-text search across title, OCR text and extracted entities.
     * Results are ordered most-recent first.
     */
    @Query(
        """
        SELECT documents.* FROM documents
        JOIN documents_fts ON documents.id = documents_fts.rowid
        WHERE documents_fts MATCH :ftsQuery
        ORDER BY documents.createdAt DESC
        """
    )
    abstract suspend fun search(ftsQuery: String): List<DocumentEntity>

    /**
     * Substring fallback for what FTS (token-prefix) misses — e.g. digits in the
     * middle of an invoice number, or a partial name. [q] must already have its
     * LIKE wildcards (`%` `_` `\`) escaped by the caller.
     */
    @Query(
        """
        SELECT * FROM documents
        WHERE title LIKE '%' || :q || '%' ESCAPE '\'
           OR ocrText LIKE '%' || :q || '%' ESCAPE '\'
        ORDER BY createdAt DESC
        """
    )
    abstract suspend fun searchLike(q: String): List<DocumentEntity>

    // ---- Writes (kept in sync with the FTS index) ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertDocument(entity: DocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertFts(fts: DocumentFts)

    @Query("UPDATE documents SET title = :title WHERE id = :id")
    protected abstract suspend fun updateTitle(id: Long, title: String)

    @Query("DELETE FROM documents WHERE id = :id")
    protected abstract suspend fun deleteDocument(id: Long)

    @Query("DELETE FROM documents_fts WHERE rowid = :id")
    protected abstract suspend fun deleteFts(id: Long)

    @Query("SELECT title, ocrText FROM documents WHERE id = :id")
    protected abstract suspend fun ftsSourceFor(id: Long): FtsSource?

    protected data class FtsSource(val title: String, val ocrText: String)

    @Transaction
    open suspend fun insert(entity: DocumentEntity, entities: String): Long {
        val id = insertDocument(entity)
        insertFts(DocumentFts(id.toInt(), entity.title, entity.ocrText, entities))
        return id
    }

    @Transaction
    open suspend fun rename(id: Long, title: String, entities: String) {
        updateTitle(id, title)
        val src = ftsSourceFor(id) ?: return
        insertFts(DocumentFts(id.toInt(), title, src.ocrText, entities))
    }

    @Transaction
    open suspend fun deleteById(id: Long) {
        deleteDocument(id)
        deleteFts(id)
    }
}
