package com.filmlog.di

import android.content.Context
import androidx.room.Room
import com.filmlog.data.local.FilmLogDatabase
import com.filmlog.data.local.dao.FilmDao
import com.filmlog.data.local.dao.PresetDao
import com.filmlog.data.local.dao.ShootRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FilmLogDatabase {
        return Room.databaseBuilder(
            context,
            FilmLogDatabase::class.java,
            "filmlog.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFilmDao(db: FilmLogDatabase): FilmDao = db.filmDao()

    @Provides
    fun provideShootRecordDao(db: FilmLogDatabase): ShootRecordDao = db.shootRecordDao()

    @Provides
    fun providePresetDao(db: FilmLogDatabase): PresetDao = db.presetDao()
}
