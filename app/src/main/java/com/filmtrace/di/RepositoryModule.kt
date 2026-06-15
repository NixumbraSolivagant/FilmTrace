package com.filmtrace.di

import com.filmtrace.data.repository.impl.FilmRepositoryImpl
import com.filmtrace.data.repository.impl.PresetRepositoryImpl
import com.filmtrace.data.repository.impl.ShootRecordRepositoryImpl
import com.filmtrace.domain.repository.FilmRepository
import com.filmtrace.domain.repository.PresetRepository
import com.filmtrace.domain.repository.ShootRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFilmRepository(impl: FilmRepositoryImpl): FilmRepository

    @Binds
    @Singleton
    abstract fun bindShootRecordRepository(impl: ShootRecordRepositoryImpl): ShootRecordRepository

    @Binds
    @Singleton
    abstract fun bindPresetRepository(impl: PresetRepositoryImpl): PresetRepository
}
