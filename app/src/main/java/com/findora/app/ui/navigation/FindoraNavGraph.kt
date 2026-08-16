package com.findora.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.findora.app.ui.screens.categories.CategoriesScreen
import com.findora.app.ui.screens.categories.CategoryScreen
import com.findora.app.ui.screens.detail.DocumentDetailScreen
import com.findora.app.ui.screens.home.HomeScreen
import com.findora.app.ui.screens.scanner.ScannerScreen
import com.findora.app.ui.screens.search.SearchScreen
import com.findora.app.ui.screens.settings.SettingsScreen
import com.findora.app.ui.screens.splash.SplashScreen

@Composable
fun FindoraNavGraph(
    navController: NavHostController,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier,
        // Spec: fast, subtle motion — a simple cross-fade everywhere.
        enterTransition = { fadeIn(tween(220)) },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { fadeOut(tween(180)) },
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                contentPadding = contentPadding,
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenDocument = { id -> navController.navigate(Routes.detail(id)) },
                onOpenCategory = { cat -> navController.navigate(Routes.category(cat.name)) },
                onSeeAllCategories = { navController.navigate(Routes.CATEGORIES) },
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onOpenDocument = { id, query -> navController.navigate(Routes.detail(id, query)) },
            )
        }

        composable(Routes.SCANNER) {
            ScannerScreen(
                onBack = { navController.popBackStack() },
                onScanned = { id ->
                    navController.navigate(Routes.detail(id)) {
                        popUpTo(Routes.SCANNER) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.CATEGORIES) {
            CategoriesScreen(
                contentPadding = contentPadding,
                onOpenCategory = { cat -> navController.navigate(Routes.category(cat.name)) },
            )
        }

        composable(
            route = Routes.CATEGORY,
            arguments = listOf(navArgument("name") { type = NavType.StringType }),
        ) { entry ->
            CategoryScreen(
                categoryName = entry.arguments?.getString("name").orEmpty(),
                onBack = { navController.popBackStack() },
                onOpenDocument = { id -> navController.navigate(Routes.detail(id)) },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("q") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { entry ->
            DocumentDetailScreen(
                documentId = entry.arguments?.getLong("id") ?: -1L,
                query = entry.arguments?.getString("q").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(contentPadding = contentPadding)
        }
    }
}
