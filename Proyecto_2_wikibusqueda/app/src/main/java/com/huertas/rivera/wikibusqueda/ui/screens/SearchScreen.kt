package com.huertas.rivera.wikibusqueda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertas.rivera.wikibusqueda.ui.components.ArticleList
import com.huertas.rivera.wikibusqueda.ui.components.EmptyState
import com.huertas.rivera.wikibusqueda.ui.components.LoadingIndicator
import com.huertas.rivera.wikibusqueda.ui.components.SearchBar
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit,
    wikiViewModel: WikiViewModel = viewModel()
) {
    val uiState by wikiViewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
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
                    message = uiState.error ?: "Error al cargar datos",
                    isError = true
                )
            }

            uiState.articles.isEmpty() && uiState.searchQuery.isNotBlank() -> {
                EmptyState(message = "No se hallaron resultados para \"${uiState.searchQuery}\"")
            }

            else -> {
                ArticleList(
                    articles = uiState.articles,
                    onArticleClick = onArticleClick,
                    onArticleLongClick = { article ->
                        wikiViewModel.showPreview(article)
                    }
                )
            }
        }
    }

    if (uiState.selectedArticleForPreview != null) {
        val article = uiState.selectedArticleForPreview!!
        val cleanExcerpt = article.excerpt?.replace(Regex("<[^>]*>"), "") ?: ""

        ModalBottomSheet(
            onDismissRequest = { wikiViewModel.dismissPreview() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = KnightIron.copy(alpha = 0.7f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Vista previa del artículo",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = article.description ?: "Información de Wikipedia",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = cleanExcerpt.ifBlank { "No hay un extracto disponible para este artículo." },
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        wikiViewModel.dismissPreview()
                        onArticleClick(article.key)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Leer artículo completo")
                }
            }
        }
    }
}

val KnightIron = androidx.compose.ui.graphics.Color(0xFF3E3C3A)