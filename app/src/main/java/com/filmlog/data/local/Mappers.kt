package com.filmlog.data.local

import com.filmlog.data.local.entity.FilmEntity
import com.filmlog.data.local.entity.PresetEntity
import com.filmlog.data.local.entity.ShootRecordEntity
import com.filmlog.domain.model.*

fun FilmEntity.toDomain(): Film = Film(
    id = id,
    brand = brand,
    name = name,
    iso = iso,
    format = FilmFormat.entries.find { it.name == format } ?: FilmFormat.FORMAT_135,
    status = FilmStatus.entries.find { it.name == status } ?: FilmStatus.STOCK,
    totalShots = totalShots,
    remainingShots = remainingShots,
    quantity = if (quantity < 1) 1 else quantity,
    createdAt = createdAt,
    loadedAt = loadedAt
)

fun Film.toEntity(): FilmEntity = FilmEntity(
    id = id,
    brand = brand,
    name = name,
    iso = iso,
    format = format.name,
    status = status.name,
    totalShots = totalShots,
    remainingShots = remainingShots,
    quantity = if (quantity < 1) 1 else quantity,
    createdAt = createdAt,
    loadedAt = loadedAt
)

fun ShootRecordEntity.toDomain(): ShootRecord = ShootRecord(
    id = id,
    filmId = filmId,
    frameNumber = frameNumber,
    shutterSpeed = shutterSpeed,
    aperture = aperture,
    isoOffset = isoOffset,
    effectiveIso = effectiveIso,
    focalLength = focalLength,
    focusDistance = focusDistance,
    exposureCompensation = exposureCompensation,
    filter = filter,
    note = note,
    createdAt = createdAt
)

fun ShootRecord.toEntity(): ShootRecordEntity = ShootRecordEntity(
    id = id,
    filmId = filmId,
    frameNumber = frameNumber,
    shutterSpeed = shutterSpeed,
    aperture = aperture,
    isoOffset = isoOffset,
    effectiveIso = effectiveIso,
    focalLength = focalLength,
    focusDistance = focusDistance,
    exposureCompensation = exposureCompensation,
    filter = filter,
    note = note,
    createdAt = createdAt
)

fun PresetEntity.toDomain(): Preset = Preset(
    id = id,
    name = name,
    shutterSpeed = shutterSpeed,
    aperture = aperture,
    description = description
)

fun Preset.toEntity(): PresetEntity = PresetEntity(
    id = id,
    name = name,
    shutterSpeed = shutterSpeed,
    aperture = aperture,
    description = description
)
