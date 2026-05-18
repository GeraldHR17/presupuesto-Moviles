package com.huertas.rivera.wikibusqueda.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertas.rivera.wikibusqueda.ui.components.ArticleList
import com.huertas.rivera.wikibusqueda.ui.components.EmptyState
import com.huertas.rivera.wikibusqueda.ui.components.LoadingIndicator
import com.huertas.rivera.wikibusqueda.ui.components.SearchBar
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel

@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit,
    wikiViewModel: WikiViewModel = viewModel()
) {
    val uiState by wikiViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = {
                wikiViewModel.onQueryChange(it)
            }
        )

        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }

            uiState.error != null -> {
                EmptyState(
                    message = uiState.error ?: "Ocurrió un error inesperado",
                    isError = true
                )
            }

            uiState.articles.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                EmptyState(message = "No se encontraron resultados para \"${uiState.searchQuery}\"")
            }

            else -> {
                ArticleList(
                    articles = uiState.articles,
                    onArticleClick = onArticleClick
                )
            }
        }
    }
}