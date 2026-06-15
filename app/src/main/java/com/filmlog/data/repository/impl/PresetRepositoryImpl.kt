package com.filmlog.data.repository.impl

import com.filmlog.data.local.dao.PresetDao
import com.filmlog.data.local.toDomain
import com.filmlog.data.local.toEntity
import com.filmlog.domain.model.Preset
import com.filmlog.domain.repository.PresetRepository
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
