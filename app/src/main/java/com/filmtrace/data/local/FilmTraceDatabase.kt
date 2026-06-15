package com.filmtrace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.filmtrace.data.local.dao.FilmDao
import com.filmtrace.data.local.dao.PresetDao
import com.filmtrace.data.local.dao.ShootRecordDao
import com.filmtrace.data.local.entity.FilmEntity
import com.filmtrace.data.local.entity.PresetEntity
import com.filmtrace.data.local.entity.ShootRecordEntity

@Database(
    entities = [FilmEntity::class, ShootRecordEntity::class, PresetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FilmTraceDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
    abstract fun shootRecordDao(): ShootRecordDao
    abstract fun presetDao(): PresetDao
}
