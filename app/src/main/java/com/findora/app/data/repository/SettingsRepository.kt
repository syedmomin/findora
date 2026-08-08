package com.findora.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val appContext: Context) {

    private val themeKey = stringPreferencesKey("theme_mode")
    private val recentSearchesKey = stringPreferencesKey("recent_searches")

    val themeMode: Flow<ThemeMode> = appContext.dataStore.data.map { prefs ->
        runCatching { ThemeMode.valueOf(prefs[themeKey] ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        appContext.dataStore.edit { it[themeKey] = mode.name }
    }

    /** Most-recent-first list of past search queries (max [MAX_RECENT]). */
    val recentSearches: Flow<List<String>> = appContext.dataStore.data.map { prefs ->
        prefs[recentSearchesKey]
            ?.split(RECENT_DELIMITER)
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    suspend fun addRecentSearch(query: String) {
        val trimmed = query.trim().replace("\n", " ")
        if (trimmed.isEmpty()) return
        appContext.dataStore.edit { prefs ->
            val existing = prefs[recentSearchesKey]?.split(RECENT_DELIMITER).orEmpty()
            val updated = (listOf(trimmed) + existing.filter { !it.equals(trimmed, ignoreCase = true) })
                .filter { it.isNotBlank() }
                .take(MAX_RECENT)
            prefs[recentSearchesKey] = updated.joinToString(RECENT_DELIMITER)
        }
    }

    suspend fun clearRecentSearches() {
        appContext.dataStore.edit { it.remove(recentSearchesKey) }
    }

    private companion object {
        const val RECENT_DELIMITER = "\n"
        const val MAX_RECENT = 8
    }
}
