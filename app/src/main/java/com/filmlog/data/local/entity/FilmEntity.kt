package com.filmlog.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films")
data class FilmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val name: String,
    val iso: Int,
    val format: String,
    val status: String,
    val totalShots: Int,
    val remainingShots: Int,
    val quantity: Int = 1,
    val createdAt: Long,
    val loadedAt: Long? = null
)
