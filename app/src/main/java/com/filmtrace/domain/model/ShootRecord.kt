package com.filmtrace.domain.model

data class ShootRecord(
    val id: Long = 0,
    val filmId: Long,
    val frameNumber: Int,
    val shutterSpeed: String,
    val aperture: String,
    val isoOffset: Int,
    val effectiveIso: Int,
    val focalLength: Int? = null,
    val focusDistance: String? = null,
    val exposureCompensation: Float,
    val filter: String? = null,
    val note: String? = null,
    val createdAt: Long
)

object ExposureValues {
    val SHUTTER_SPEEDS = listOf(
        "B", "1s", "1/2", "1/4", "1/8", "1/15", "1/30",
        "1/60", "1/125", "1/250", "1/500", "1/1000", "1/2000", "1/4000"
    )

    val APERTURES = listOf(
        "f/1.0", "f/1.4", "f/2", "f/2.8", "f/4", "f/5.6",
        "f/8", "f/11", "f/16", "f/22"
    )

    val FILTERS = listOf(
        "无", "UV", "偏振 CPL", "渐变灰 GND", "中性密度 ND", "彩色滤镜"
    )

    val ISO_OFFSETS = listOf(-3, -2, -1, 0, 1, 2, 3)

    val FOCUS_DISTANCES = listOf("∞", "10m", "5m", "3m", "2m", "1.5m", "1m", "0.8m", "0.5m")

    val FOCAL_LENGTHS = listOf(24, 28, 35, 50, 85, 100, 135, 200)
}
