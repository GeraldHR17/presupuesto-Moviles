package com.huertas.rivera.wikibusqueda.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertas.rivera.wikibusqueda.data.repository.WikipediaRepository
import com.huertas.rivera.wikibusqueda.viewmodel.states.WikiUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WikiViewModel : ViewModel() {

    private val repository = WikipediaRepository()

    private val _uiState = MutableStateFlow(WikiUiState())

    val uiState: StateFlow<WikiUiState> = _uiState.asStateFlow()

    fun searchArticles(query: String) {

        if (query.isBlank()) {

            _uiState.value = WikiUiState()
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {

                val response = repository.searchArticles(query)

                _uiState.value = _uiState.value.copy(
                    articles = response.pages,
                    isLoading = false
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al obtener artículos"
                )
            }
        }
    }
}