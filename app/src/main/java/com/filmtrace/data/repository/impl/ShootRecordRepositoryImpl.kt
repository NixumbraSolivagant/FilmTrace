package com.filmtrace.data.repository.impl

import com.filmtrace.data.local.dao.ShootRecordDao
import com.filmtrace.data.local.toDomain
import com.filmtrace.data.local.toEntity
import com.filmtrace.domain.model.ShootRecord
import com.filmtrace.domain.repository.ShootRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShootRecordRepositoryImpl @Inject constructor(
    private val shootRecordDao: ShootRecordDao
) : ShootRecordRepository {
    override fun getAllRecords(): Flow<List<ShootRecord>> =
        shootRecordDao.getAllRecords().map { list -> list.map { it.toDomain() } }

    override fun getRecordsByFilm(filmId: Long): Flow<List<ShootRecord>> =
        shootRecordDao.getRecordsByFilm(filmId).map { list -> list.map { it.toDomain() } }

    override suspend fun getRecordById(id: Long): ShootRecord? =
        shootRecordDao.getRecordById(id)?.toDomain()

    override suspend fun getNextFrameNumber(filmId: Long): Int =
        shootRecordDao.getNextFrameNumber(filmId) + 1

    override suspend fun insertRecord(record: ShootRecord): Long =
        shootRecordDao.insertRecord(record.toEntity())

    override suspend fun updateRecord(record: ShootRecord) =
        shootRecordDao.updateRecord(record.toEntity())

    override suspend fun deleteRecord(record: ShootRecord) =
        shootRecordDao.deleteRecord(record.toEntity())
}
