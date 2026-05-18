package com.huertas.rivera.wikibusqueda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertas.rivera.wikibusqueda.data.repository.WikipediaRepository
import com.huertas.rivera.wikibusqueda.viewmodel.states.WikiUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WikiViewModel : ViewModel() {

    private val repository = WikipediaRepository()

    private val _uiState = MutableStateFlow(WikiUiState())
    val uiState: StateFlow<WikiUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        
        // Cancelamos el trabajo anterior si el usuario sigue escribiendo
        searchJob?.cancel()
        
        if (newQuery.isBlank()) {
            _uiState.update { it.copy(articles = emptyList(), isLoading = false, error = null) }
            return
        }

        // Implementamos un pequeño delay (debounce) para no llamar a la API por cada letra
        searchJob = viewModelScope.launch {
            delay(500) 
            searchArticles(newQuery)
        }
    }

    private suspend fun searchArticles(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        try {
            val response = repository.searchArticles(query)
            _uiState.update { 
                it.copy(
                    articles = response.pages,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = "Error al obtener artículos: ${e.message}"
                )
            }
        }
    }
}