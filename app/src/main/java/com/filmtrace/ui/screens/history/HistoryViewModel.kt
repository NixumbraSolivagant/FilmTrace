package com.filmtrace.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filmtrace.domain.model.Film
import com.filmtrace.domain.model.ShootRecord
import com.filmtrace.domain.repository.FilmRepository
import com.filmtrace.domain.repository.ShootRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HistoryFolder(
    val film: Film,
    val records: List<ShootRecord>
)

data class HistoryUiState(
    val folders: List<HistoryFolder> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val recordRepository: ShootRecordRepository,
    private val filmRepository: FilmRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        recordRepository.getAllRecords(),
        filmRepository.getAllFilms(),
        _searchQuery
    ) { records, films, query ->
        val filmMap = films.associateBy { it.id }

        val filteredRecords = if (query.isBlank()) records else records.filter { record ->
            val film = filmMap[record.filmId]
            film?.name?.contains(query, ignoreCase = true) == true ||
                film?.brand?.contains(query, ignoreCase = true) == true ||
                record.note?.contains(query, ignoreCase = true) == true ||
                record.shutterSpeed.contains(query, ignoreCase = true) ||
                record.aperture.contains(query, ignoreCase = true)
        }

        // 按胶卷分组：保留"有胶卷存在的分组"
        val grouped = filteredRecords
            .groupBy { it.filmId }
            .mapNotNull { (filmId, recs) ->
                val film = filmMap[filmId] ?: return@mapNotNull null
                HistoryFolder(
                    film = film,
                    records = recs.sortedBy { it.frameNumber }
                )
            }
            .sortedByDescending { it.film.createdAt }

        HistoryUiState(
            folders = grouped,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
