package com.filmtrace.ui.screens.history

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

data class RecordDetailUiState(
    val record: ShootRecord? = null,
    val film: Film? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class RecordDetailViewModel @Inject constructor(
    private val recordRepository: ShootRecordRepository,
    private val filmRepository: FilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recordId: Long = savedStateHandle.get<Long>("recordId") ?: 0

    val uiState: StateFlow<RecordDetailUiState> = flow {
        val record = recordRepository.getRecordById(recordId)
        val film = record?.let { filmRepository.getFilmById(it.filmId) }
        emit(RecordDetailUiState(record = record, film = film, isLoading = false))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordDetailUiState())
}
