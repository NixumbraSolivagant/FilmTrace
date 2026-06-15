package com.filmtrace.data.repository.impl

import com.filmtrace.data.local.dao.PresetDao
import com.filmtrace.data.local.toDomain
import com.filmtrace.data.local.toEntity
import com.filmtrace.domain.model.Preset
import com.filmtrace.domain.repository.PresetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PresetRepositoryImpl @Inject constructor(
    private val presetDao: PresetDao
) : PresetRepository {
    override fun getAllPresets(): Flow<List<Preset>> =
        presetDao.getAllPresets().map { list -> list.map { it.toDomain() } }

    override suspend fun insertPreset(preset: Preset): Long =
        presetDao.insertPreset(preset.toEntity())

    override suspend fun updatePreset(preset: Preset) =
        presetDao.updatePreset(preset.toEntity())

    override suspend fun deletePreset(preset: Preset) =
        presetDao.deletePreset(preset.toEntity())
}
