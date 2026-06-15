package com.filmtrace.data.repository.impl

import com.filmtrace.data.local.dao.FilmDao
import com.filmtrace.data.local.toDomain
import com.filmtrace.data.local.toEntity
import com.filmtrace.domain.model.Film
import com.filmtrace.domain.repository.FilmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilmRepositoryImpl @Inject constructor(
    private val filmDao: FilmDao
) : FilmRepository {
    override fun getAllFilms(): Flow<List<Film>> =
        filmDao.getAllFilms().map { list -> list.map { it.toDomain() } }

    override fun getLoadedFilm(): Flow<Film?> =
        filmDao.getLoadedFilm().map { it?.toDomain() }

    override suspend fun getFilmById(id: Long): Film? =
        filmDao.getFilmById(id)?.toDomain()

    override suspend fun insertFilm(film: Film): Long =
        filmDao.insertFilm(film.toEntity())

    override suspend fun updateFilm(film: Film) =
        filmDao.updateFilm(film.toEntity())

    override suspend fun deleteFilm(film: Film) =
        filmDao.deleteFilm(film.toEntity())

    override suspend fun decrementRemainingShots(filmId: Long) =
        filmDao.decrementRemainingShots(filmId)

    override suspend fun loadFilm(filmId: Long) {
        filmDao.clearLoadedFilm()
        filmDao.setFilmLoaded(filmId, System.currentTimeMillis())
    }

    override suspend fun unloadAllFilms() {
        filmDao.clearLoadedFilm()
    }

    override suspend fun markFinished(filmId: Long) {
        filmDao.markFilmFinished(filmId, "FINISHED")
    }
}
