package com.filmtrace.ui.screens.film

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmtrace.domain.model.Film
import com.filmtrace.domain.model.ShootRecord
import com.filmtrace.domain.repository.FilmRepository
import com.filmtrace.domain.repository.ShootRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FilmDetailUiState(
    val film: Film? = null,
    val records: List<ShootRecord> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FilmDetailViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val recordRepository: ShootRecordRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filmId: Long = savedStateHandle.get<Long>("filmId") ?: 0

    val uiState: StateFlow<FilmDetailUiState> = combine(
        flow { emit(filmRepository.getFilmById(filmId)) },
        recordRepository.getRecordsByFilm(filmId)
    ) { film, records ->
        FilmDetailUiState(film = film, records = records, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FilmDetailUiState())
}
