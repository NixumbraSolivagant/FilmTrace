package com.filmlog.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shoot_records",
    foreignKeys = [
        ForeignKey(
            entity = FilmEntity::class,
            parentColumns = ["id"],
            childColumns = ["filmId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("filmId")]
)
data class ShootRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filmId: Long,
    val frameNumber: Int,
    val shutterSpeed: String,
    val aperture: String,
    val isoOffset: Int,
    val effectiveIso: Int,
    val focalLength: Int?,
    val focusDistance: String?,
    val exposureCompensation: Float,
    val filter: String?,
    val note: String?,
    val createdAt: Long
)
