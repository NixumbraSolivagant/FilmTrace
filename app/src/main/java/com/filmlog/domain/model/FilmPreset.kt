package com.filmlog.domain.model

/**
 * 常用胶卷预设（不分库存 / 装载，只描述型号 + ISO + 画幅 + 推荐张数）
 *
 * 选预设后用户可继续修改任意字段再保存。
 */
data class FilmPreset(
    val brand: String,
    val name: String,
    val iso: Int,
    val format: FilmFormat,
    val totalShots: Int
)

object FilmPresets {
    val ALL: List<FilmPreset> = listOf(
        FilmPreset("福马", "Pan 100", 100, FilmFormat.FORMAT_135, 36),
        FilmPreset("福马", "Pan 400", 400, FilmFormat.FORMAT_135, 36),
        FilmPreset("柯达", "金 200 (Gold 200)", 200, FilmFormat.FORMAT_135, 36),
        FilmPreset("柯达", "5219 (Vision3 500T)", 500, FilmFormat.FORMAT_135, 36),
        FilmPreset("乐凯", "SHD 100", 100, FilmFormat.FORMAT_135, 36),
        FilmPreset("乐凯", "SHD 400", 400, FilmFormat.FORMAT_135, 36),
        FilmPreset("乐凯", "C 200", 200, FilmFormat.FORMAT_135, 36)
    )
}
