package com.findora.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.findora.app.R
import com.findora.app.data.repository.ThemeMode

@Composable
fun FindoraApp(
    themeMode: ThemeMode,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBars = currentRoute in TopLevelTab.entries.map { it.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBars,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) {
                FindoraBottomBar(navController, currentRoute)
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentRoute == Routes.HOME,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Routes.SCANNER) },
                    icon = { Icon(Icons.Rounded.DocumentScanner, contentDescription = null) },
                    text = { Text(stringResource(R.string.scan)) },
                )
            }
        },
    ) { padding ->
        FindoraNavGraph(
            navController = navController,
            modifier = Modifier,
            contentPadding = padding,
        )
    }
}

@Composable
private fun FindoraBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        TopLevelTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(),
            )
        }
    }
}
