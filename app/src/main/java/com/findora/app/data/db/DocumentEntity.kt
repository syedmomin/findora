package com.findora.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/** Source-of-truth table for documents. */
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val imagePath: String?,
    val ocrText: String,
    val category: String,
    @ColumnInfo(index = true) val createdAt: Long,
)

/**
 * Full-text search index. Kept in sync with [DocumentEntity] by the DAO
 * transactions ([DocumentDao.insert], [update], [deleteById]).
 * `rowid` maps 1:1 to [DocumentEntity.id].
 */
@Fts4
@Entity(tableName = "documents_fts")
data class DocumentFts(
    @PrimaryKey @ColumnInfo(name = "rowid") val rowid: Int,
    val title: String,
    val ocrText: String,
    /** Extracted structured fields (emails, phones, dates…) flattened for search. */
    val entities: String,
)
