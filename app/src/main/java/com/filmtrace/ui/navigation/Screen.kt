package com.filmtrace.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Shoot : Screen("shoot", "拍摄", Icons.Filled.PhotoCamera)
    data object Film : Screen("film", "胶卷", Icons.Filled.CameraRoll)
    data object History : Screen("history", "历史", Icons.Filled.History)
    data object FilmDetail : Screen("film/{filmId}", "胶卷详情", Icons.Filled.CameraRoll) {
        fun createRoute(filmId: Long) = "film/$filmId"
    }
    data object RecordDetail : Screen("record/{recordId}", "拍摄详情", Icons.Filled.History) {
        fun createRoute(recordId: Long) = "record/$recordId"
    }
    data object AddFilm : Screen("add_film", "添加胶卷", Icons.Filled.CameraRoll)
    data object EditFilm : Screen("edit_film/{filmId}", "编辑胶卷", Icons.Filled.CameraRoll) {
        fun createRoute(filmId: Long) = "edit_film/$filmId"
    }
    data object About : Screen("about", "关于", Icons.Filled.CameraRoll)
}

val bottomNavItems = listOf(Screen.Shoot, Screen.Film, Screen.History)
