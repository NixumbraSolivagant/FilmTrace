package com.filmlog.domain.repository

import com.filmlog.domain.model.ShootRecord
import kotlinx.coroutines.flow.Flow

interface ShootRecordRepository {
    fun getAllRecords(): Flow<List<ShootRecord>>
    fun getRecordsByFilm(filmId: Long): Flow<List<ShootRecord>>
    suspend fun getRecordById(id: Long): ShootRecord?
    suspend fun getNextFrameNumber(filmId: Long): Int
    suspend fun insertRecord(record: ShootRecord): Long
    suspend fun updateRecord(record: ShootRecord)
    suspend fun deleteRecord(record: ShootRecord)
}
