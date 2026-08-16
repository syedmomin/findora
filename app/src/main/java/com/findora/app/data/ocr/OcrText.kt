package com.findora.app.data.ocr

/**
 * Joins per-page OCR text into one clean document body: trims each page,
 * collapses runs of 3+ newlines into a single blank line, drops empty pages,
 * and separates the remaining pages with one blank line.
 */
fun combinePages(pages: List<String>): String = ""
