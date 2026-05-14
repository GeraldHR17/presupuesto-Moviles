package com.huertas.rivera.wikibusqueda.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertas.rivera.wikibusqueda.ui.components.ArticleCard
import com.huertas.rivera.wikibusqueda.ui.components.EmptyState
import com.huertas.rivera.wikibusqueda.ui.components.LoadingIndicator
import com.huertas.rivera.wikibusqueda.ui.components.SearchBar
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel

@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit,
    wikiViewModel: WikiViewModel = viewModel()
) {

    var query by remember {
        mutableStateOf("")
    }

    val uiState by wikiViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        SearchBar(
            query = query,
            onQueryChange = {

                query = it
                wikiViewModel.searchArticles(it)
            }
        )

        when {

            uiState.isLoading -> {

                LoadingIndicator()
            }

            uiState.articles.isEmpty() && query.isNotBlank() -> {

                EmptyState()
            }

            else -> {

                LazyColumn {

                    items(uiState.articles) { article ->

                        ArticleCard(
                            page = article,
                            onClick = {

                                onArticleClick(article.key)
                            }
                        )
                    }
                }
            }
        }
    }
}