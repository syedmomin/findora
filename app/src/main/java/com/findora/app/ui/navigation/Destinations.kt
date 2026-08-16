package com.findora.app.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val SEARCH = "search"
    const val SCANNER = "scanner"
    const val CATEGORIES = "categories"
    const val SETTINGS = "settings"

    // Optional `q` carries the search query so the detail screen can highlight it.
    const val DETAIL = "detail/{id}?q={q}"
    fun detail(id: Long, query: String = "") =
        if (query.isBlank()) "detail/$id" else "detail/$id?q=${Uri.encode(query)}"

    const val CATEGORY = "category/{name}"
    fun category(name: String) = "category/$name"
}

/** Top-level tabs shown in the bottom navigation bar. */
enum class TopLevelTab(val route: String, val label: String, val icon: ImageVector) {
    HOME(Routes.HOME, "Home", Icons.Rounded.Home),
    CATEGORIES(Routes.CATEGORIES, "Categories", Icons.Rounded.GridView),
    SETTINGS(Routes.SETTINGS, "Settings", Icons.Rounded.Settings),
}
