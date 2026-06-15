package com.filmlog.data.local.dao

import androidx.room.*
import com.filmlog.data.local.entity.ShootRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShootRecordDao {
    @Query("SELECT * FROM shoot_records ORDER BY createdAt DESC")
    fun getAllRecords(): Flow<List<ShootRecordEntity>>

    @Query("SELECT * FROM shoot_records WHERE filmId = :filmId ORDER BY frameNumber ASC")
    fun getRecordsByFilm(filmId: Long): Flow<List<ShootRecordEntity>>

    @Query("SELECT * FROM shoot_records WHERE id = :id")
    suspend fun getRecordById(id: Long): ShootRecordEntity?

    @Query("SELECT COALESCE(MAX(frameNumber), 0) FROM shoot_records WHERE filmId = :filmId")
    suspend fun getNextFrameNumber(filmId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ShootRecordEntity): Long

    @Update
    suspend fun updateRecord(record: ShootRecordEntity)

    @Delete
    suspend fun deleteRecord(record: ShootRecordEntity)
}
