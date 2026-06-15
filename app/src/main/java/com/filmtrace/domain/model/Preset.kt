package com.filmtrace.domain.model

data class Preset(
    val id: Long = 0,
    val name: String,
    val shutterSpeed: String,
    val aperture: String,
    val description: String? = null
)
