package com.filmtrace.di

import android.content.Context
import androidx.room.Room
import com.filmtrace.data.local.FilmTraceDatabase
import com.filmtrace.data.local.dao.FilmDao
import com.filmtrace.data.local.dao.PresetDao
import com.filmtrace.data.local.dao.ShootRecordDao
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
    fun provideDatabase(@ApplicationContext context: Context): FilmTraceDatabase {
        return Room.databaseBuilder(
            context,
            FilmTraceDatabase::class.java,
            "filmtrace.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideFilmDao(db: FilmTraceDatabase): FilmDao = db.filmDao()

    @Provides
    fun provideShootRecordDao(db: FilmTraceDatabase): ShootRecordDao = db.shootRecordDao()

    @Provides
    fun providePresetDao(db: FilmTraceDatabase): PresetDao = db.presetDao()
}
