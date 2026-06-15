package com.filmlog.domain.repository

import com.filmlog.domain.model.Preset
import kotlinx.coroutines.flow.Flow

interface PresetRepository {
    fun getAllPresets(): Flow<List<Preset>>
    suspend fun insertPreset(preset: Preset): Long
    suspend fun updatePreset(preset: Preset)
    suspend fun deletePreset(preset: Preset)
}
