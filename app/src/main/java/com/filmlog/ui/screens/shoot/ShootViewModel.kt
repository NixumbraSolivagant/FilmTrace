package com.filmlog.ui.screens.shoot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmlog.domain.model.ExposureValues
import com.filmlog.domain.model.Film
import com.filmlog.domain.model.FilmStatus
import com.filmlog.domain.model.ShootRecord
import com.filmlog.domain.repository.FilmRepository
import com.filmlog.domain.repository.ShootRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SaveResult {
    data object Success : SaveResult
    data class FilmFinished(val film: Film) : SaveResult
    data object NoFilmLoaded : SaveResult
}

data class ShootUiState(
    val loadedFilm: Film? = null,
    val availableFilms: List<Film> = emptyList(),
    val showFilmPicker: Boolean = false,
    val shutterSpeed: String = "1/125",
    val aperture: String = "f/8",
    val isoOffset: Int = 0,
    val focalLength: Int? = null,
    val focusDistance: String? = null,
    val exposureCompensation: Float = 0f,
    val filter: String = "无",
    val note: String = "",
    val savedRecordsCount: Int = 0,
    val isSaving: Boolean = false
)

@HiltViewModel
class ShootViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val recordRepository: ShootRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShootUiState())
    val uiState: StateFlow<ShootUiState> = _uiState.asStateFlow()

    private val _saveResult = MutableSharedFlow<SaveResult>()
    val saveResult: SharedFlow<SaveResult> = _saveResult.asSharedFlow()

    init {
        viewModelScope.launch {
            filmRepository.getLoadedFilm().collect { film ->
                _uiState.update {
                    it.copy(
                        loadedFilm = film,
                        savedRecordsCount = 0
                    )
                }
                film?.let { loadRecordsCount(it.id) }
            }
        }
        viewModelScope.launch {
            filmRepository.getAllFilms().collect { films ->
                _uiState.update { it.copy(availableFilms = films) }
            }
        }
    }

    private fun loadRecordsCount(filmId: Long) {
        viewModelScope.launch {
            recordRepository.getRecordsByFilm(filmId).collect { records ->
                _uiState.update { it.copy(savedRecordsCount = records.size) }
            }
        }
    }

    fun updateShutterSpeed(value: String) {
        _uiState.update { it.copy(shutterSpeed = value) }
    }

    fun updateAperture(value: String) {
        _uiState.update { it.copy(aperture = value) }
    }

    fun updateIsoOffset(value: Int) {
        _uiState.update { it.copy(isoOffset = value) }
    }

    fun updateFocalLength(value: Int?) {
        _uiState.update { it.copy(focalLength = value) }
    }

    fun updateFocusDistance(value: String?) {
        _uiState.update { it.copy(focusDistance = value) }
    }

    fun updateExposureCompensation(value: Float) {
        _uiState.update { it.copy(exposureCompensation = value) }
    }

    fun updateFilter(value: String) {
        _uiState.update { it.copy(filter = value) }
    }

    fun updateNote(value: String) {
        _uiState.update { it.copy(note = value) }
    }

    fun showFilmPicker() {
        _uiState.update { it.copy(showFilmPicker = true) }
    }

    fun hideFilmPicker() {
        _uiState.update { it.copy(showFilmPicker = false) }
    }

    fun switchFilm(film: Film) {
        viewModelScope.launch {
            if (film.status != FilmStatus.LOADED) {
                filmRepository.loadFilm(film.id)
            }
            _uiState.update { it.copy(showFilmPicker = false) }
        }
    }

    fun unloadCurrentFilm() {
        viewModelScope.launch {
            filmRepository.unloadAllFilms()
            _uiState.update { it.copy(showFilmPicker = false) }
        }
    }

    fun saveRecord() {
        val state = _uiState.value
        val film = state.loadedFilm ?: run {
            viewModelScope.launch { _saveResult.emit(SaveResult.NoFilmLoaded) }
            return
        }

        if (state.savedRecordsCount >= film.totalShots) {
            viewModelScope.launch { _saveResult.emit(SaveResult.FilmFinished(film)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val frameNumber = recordRepository.getNextFrameNumber(film.id)
            val effectiveIso = film.iso + (film.iso * state.isoOffset / 3)

            val record = ShootRecord(
                filmId = film.id,
                frameNumber = frameNumber,
                shutterSpeed = state.shutterSpeed,
                aperture = state.aperture,
                isoOffset = state.isoOffset,
                effectiveIso = effectiveIso,
                focalLength = state.focalLength,
                focusDistance = state.focusDistance,
                exposureCompensation = state.exposureCompensation,
                filter = if (state.filter == "无") null else state.filter,
                note = state.note.ifBlank { null },
                createdAt = System.currentTimeMillis()
            )

            recordRepository.insertRecord(record)
            filmRepository.decrementRemainingShots(film.id)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    note = "",
                    focalLength = null,
                    focusDistance = null,
                    exposureCompensation = 0f
                )
            }
            _saveResult.emit(SaveResult.Success)
        }
    }

    fun acknowledgeFilmFinishedAndLoadNew(film: Film) {
        viewModelScope.launch {
            filmRepository.markFinished(film.id)
            // 待用户在弹窗里选择目标胶卷时由 switchFilm 触发装载
        }
    }
}
