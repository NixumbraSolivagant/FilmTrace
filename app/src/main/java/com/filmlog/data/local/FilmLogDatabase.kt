package com.filmlog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.filmlog.data.local.dao.FilmDao
import com.filmlog.data.local.dao.PresetDao
import com.filmlog.data.local.dao.ShootRecordDao
import com.filmlog.data.local.entity.FilmEntity
import com.filmlog.data.local.entity.PresetEntity
import com.filmlog.data.local.entity.ShootRecordEntity

@Database(
    entities = [FilmEntity::class, ShootRecordEntity::class, PresetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FilmLogDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao
    abstract fun shootRecordDao(): ShootRecordDao
    abstract fun presetDao(): PresetDao
}
