package com.filmlog.di

import com.filmlog.data.repository.impl.FilmRepositoryImpl
import com.filmlog.data.repository.impl.PresetRepositoryImpl
import com.filmlog.data.repository.impl.ShootRecordRepositoryImpl
import com.filmlog.domain.repository.FilmRepository
import com.filmlog.domain.repository.PresetRepository
import com.filmlog.domain.repository.ShootRecordRepository
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
