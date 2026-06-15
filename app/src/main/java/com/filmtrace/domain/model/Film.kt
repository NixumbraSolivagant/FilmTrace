package com.filmtrace.domain.model

data class Film(
    val id: Long = 0,
    val brand: String,
    val name: String,
    val iso: Int,
    val format: FilmFormat,
    val status: FilmStatus,
    val totalShots: Int,
    val remainingShots: Int,
    val quantity: Int = 1,
    val createdAt: Long,
    val loadedAt: Long? = null
)

enum class FilmFormat(val displayName: String) {
    FORMAT_135("135 (35mm)"),
    FORMAT_120("120 (645/6x7)"),
    FORMAT_220("220"),
    FORMAT_4X5("4x5 大画幅")
}

enum class FilmStatus(val displayName: String) {
    STOCK("库存"),
    LOADED("已装载"),
    FINISHED("已拍完")
}
