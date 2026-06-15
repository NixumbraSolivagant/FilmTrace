package com.filmtrace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.filmtrace.ui.screens.about.AboutScreen
import com.filmtrace.ui.screens.film.AddEditFilmScreen
import com.filmtrace.ui.screens.film.FilmDetailScreen
import com.filmtrace.ui.screens.film.FilmListScreen
import com.filmtrace.ui.screens.history.HistoryScreen
import com.filmtrace.ui.screens.history.RecordDetailScreen
import com.filmtrace.ui.screens.shoot.ShootScreen

@Composable
fun FilmTraceNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Shoot.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Shoot.route) {
            ShootScreen(
                onNavigateToAddFilm = { navController.navigate(Screen.AddFilm.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Film.route) {
            FilmListScreen(
                onFilmClick = { filmId -> navController.navigate(Screen.FilmDetail.createRoute(filmId)) },
                onAddFilm = { navController.navigate(Screen.AddFilm.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onRecordClick = { recordId -> navController.navigate(Screen.RecordDetail.createRoute(recordId)) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.AddFilm.route) {
            AddEditFilmScreen(
                filmId = null,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditFilm.route,
            arguments = listOf(navArgument("filmId") { type = NavType.LongType })
        ) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getLong("filmId")
            AddEditFilmScreen(
                filmId = filmId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FilmDetail.route,
            arguments = listOf(navArgument("filmId") { type = NavType.LongType })
        ) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getLong("filmId") ?: return@composable
            FilmDetailScreen(
                filmId = filmId,
                onBack = { navController.popBackStack() },
                onEditFilm = { navController.navigate(Screen.EditFilm.createRoute(filmId)) },
                onRecordClick = { recordId -> navController.navigate(Screen.RecordDetail.createRoute(recordId)) }
            )
        }

        composable(
            route = Screen.RecordDetail.route,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: return@composable
            RecordDetailScreen(
                recordId = recordId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
