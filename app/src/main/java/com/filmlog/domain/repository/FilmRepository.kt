package com.filmlog.domain.repository

import com.filmlog.domain.model.Film
import kotlinx.coroutines.flow.Flow

interface FilmRepository {
    fun getAllFilms(): Flow<List<Film>>
    fun getLoadedFilm(): Flow<Film?>
    suspend fun getFilmById(id: Long): Film?
    suspend fun insertFilm(film: Film): Long
    suspend fun updateFilm(film: Film)
    suspend fun deleteFilm(film: Film)
    suspend fun decrementRemainingShots(filmId: Long)
    suspend fun loadFilm(filmId: Long)
    suspend fun unloadAllFilms()
    suspend fun markFinished(filmId: Long)
}
