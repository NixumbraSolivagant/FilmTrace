package com.filmlog.ui.screens.film

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmlog.domain.model.Film
import com.filmlog.domain.model.FilmFormat
import com.filmlog.domain.model.FilmPreset
import com.filmlog.domain.model.FilmPresets
import com.filmlog.domain.model.FilmStatus
import com.filmlog.domain.repository.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditFilmUiState(
    val brand: String = "",
    val name: String = "",
    val iso: String = "400",
    val format: FilmFormat = FilmFormat.FORMAT_135,
    val status: FilmStatus = FilmStatus.STOCK,
    val totalShots: String = "36",
    val isCustomShots: Boolean = false,
    val quantity: Int = 1,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

val CommonShotCounts = listOf(8, 12, 16, 24, 36, 72)

@HiltViewModel
class AddEditFilmViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filmId: Long? = savedStateHandle.get<Long>("filmId")

    private val _uiState = MutableStateFlow(AddEditFilmUiState())
    val uiState: StateFlow<AddEditFilmUiState> = _uiState.asStateFlow()

    val presets: List<FilmPreset> = FilmPresets.ALL

    init {
        filmId?.let { id ->
            viewModelScope.launch {
                filmRepository.getFilmById(id)?.let { film ->
                    val shotsStr = film.totalShots.toString()
                    val isCommon = CommonShotCounts.contains(film.totalShots)
                    _uiState.update {
                        it.copy(
                            brand = film.brand,
                            name = film.name,
                            iso = film.iso.toString(),
                            format = film.format,
                            status = film.status,
                            totalShots = shotsStr,
                            isCustomShots = !isCommon,
                            quantity = film.quantity.coerceAtLeast(1),
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun updateBrand(value: String) { _uiState.update { it.copy(brand = value) } }
    fun updateName(value: String) { _uiState.update { it.copy(name = value) } }
    fun updateIso(value: String) { _uiState.update { it.copy(iso = value) } }
    fun updateFormat(value: FilmFormat) { _uiState.update { it.copy(format = value) } }
    fun updateStatus(value: FilmStatus) { _uiState.update { it.copy(status = value) } }
    fun updateTotalShots(value: String) { _uiState.update { it.copy(totalShots = value) } }
    fun selectCommonShots(value: Int) {
        _uiState.update { it.copy(totalShots = value.toString(), isCustomShots = false) }
    }
    fun enableCustomShots() { _uiState.update { it.copy(isCustomShots = true) } }

    fun updateQuantity(value: Int) {
        _uiState.update { it.copy(quantity = value.coerceIn(1, 99)) }
    }
    fun decrementQuantity() { updateQuantity(_uiState.value.quantity - 1) }
    fun incrementQuantity() { updateQuantity(_uiState.value.quantity + 1) }

    /** 选中预设：填充品牌/型号/ISO/画幅/张数，并清除错误。 */
    fun applyPreset(preset: FilmPreset) {
        _uiState.update {
            it.copy(
                brand = preset.brand,
                name = preset.name,
                iso = preset.iso.toString(),
                format = preset.format,
                totalShots = preset.totalShots.toString(),
                isCustomShots = !CommonShotCounts.contains(preset.totalShots),
                error = null
            )
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.brand.isBlank() || state.name.isBlank()) {
            _uiState.update { it.copy(error = "请填写品牌和名称") }
            return
        }

        val iso = state.iso.toIntOrNull()
        val shots = state.totalShots.toIntOrNull()
        if (iso == null || iso <= 0) {
            _uiState.update { it.copy(error = "请输入有效的 ISO 值") }
            return
        }
        if (shots == null || shots <= 0) {
            _uiState.update { it.copy(error = "请输入有效的张数") }
            return
        }
        val qty = state.quantity.coerceIn(1, 99)
        if (!state.isEditing && state.status == FilmStatus.STOCK && qty < 1) {
            _uiState.update { it.copy(error = "请输入有效的库存卷数") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            val existingFilm = filmId?.let { filmRepository.getFilmById(it) }
            val now = System.currentTimeMillis()

            if (state.isEditing) {
                val film = Film(
                    id = filmId ?: 0,
                    brand = state.brand.trim(),
                    name = state.name.trim(),
                    iso = iso,
                    format = state.format,
                    status = state.status,
                    totalShots = shots,
                    remainingShots = existingFilm?.remainingShots ?: shots,
                    quantity = qty,
                    createdAt = existingFilm?.createdAt ?: now,
                    loadedAt = if (state.status == FilmStatus.LOADED) now else existingFilm?.loadedAt
                )
                filmRepository.updateFilm(film)
            } else {
                val quantity = if (state.status == FilmStatus.STOCK) qty else 1
                repeat(quantity) { idx ->
                    val film = Film(
                        id = 0,
                        brand = state.brand.trim(),
                        name = state.name.trim(),
                        iso = iso,
                        format = state.format,
                        status = state.status,
                        totalShots = shots,
                        remainingShots = shots,
                        quantity = 1,
                        createdAt = now + idx,
                        loadedAt = if (state.status == FilmStatus.LOADED) now else null
                    )
                    filmRepository.insertFilm(film)
                }
            }

            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }
}
