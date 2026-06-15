package com.filmlog.ui.screens.film

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmlog.domain.model.Film
import com.filmlog.domain.repository.FilmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FilmListUiState(
    val films: List<Film> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class FilmListViewModel @Inject constructor(
    private val filmRepository: FilmRepository
) : ViewModel() {

    val uiState: StateFlow<FilmListUiState> = filmRepository.getAllFilms()
        .map { FilmListUiState(films = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FilmListUiState())

    fun deleteFilm(film: Film) {
        viewModelScope.launch {
            filmRepository.deleteFilm(film)
        }
    }

    fun loadFilm(film: Film) {
        viewModelScope.launch {
            filmRepository.loadFilm(film.id)
        }
    }

    fun unloadFilm() {
        viewModelScope.launch {
            filmRepository.unloadAllFilms()
        }
    }
}
