package com.filmlog.data.local.dao

import androidx.room.*
import com.filmlog.data.local.entity.FilmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    @Query("SELECT * FROM films ORDER BY createdAt DESC")
    fun getAllFilms(): Flow<List<FilmEntity>>

    @Query("SELECT * FROM films WHERE id = :id")
    suspend fun getFilmById(id: Long): FilmEntity?

    @Query("SELECT * FROM films WHERE status = 'LOADED' LIMIT 1")
    fun getLoadedFilm(): Flow<FilmEntity?>

    @Query("UPDATE films SET status = 'STOCK' WHERE status = 'LOADED'")
    suspend fun clearLoadedFilm()

    @Query("UPDATE films SET status = 'LOADED', loadedAt = :now, remainingShots = totalShots WHERE id = :filmId")
    suspend fun setFilmLoaded(filmId: Long, now: Long)

    @Query("UPDATE films SET status = :status, remainingShots = 0 WHERE id = :filmId")
    suspend fun markFilmFinished(filmId: Long, status: String = "FINISHED")

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: FilmEntity): Long

    @Update
    suspend fun updateFilm(film: FilmEntity)

    @Delete
    suspend fun deleteFilm(film: FilmEntity)

    @Query("UPDATE films SET remainingShots = remainingShots - 1 WHERE id = :filmId AND remainingShots > 0")
    suspend fun decrementRemainingShots(filmId: Long)
}
