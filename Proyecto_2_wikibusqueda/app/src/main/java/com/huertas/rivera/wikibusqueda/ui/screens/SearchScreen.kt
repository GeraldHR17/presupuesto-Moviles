package com.huertas.rivera.wikibusqueda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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
                    onArticleClick = onArticleClick,
                    onArticleLongClick = { article ->
                        wikiViewModel.showPreview(article)
                    }
                )
            }
        }
    }

    // Ventanilla de Inspección Medieval (Bottom Sheet)
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
                    text = "Inspeccionando Pergamino",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val imageUrl = article.thumbnail?.url?.let {
                        if (it.startsWith("//")) "https:$it" else it
                    }

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = article.description ?: "Crónica antigua",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = cleanExcerpt.ifBlank { "El contenido de este pergamino es un misterio hasta que se abra por completo..." },
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
                    Text("Leer Crónica Completa")
                }
            }
        }
    }
}

// Nota: Importamos KnightIron manualmente para el scrimColor
val KnightIron = androidx.compose.ui.graphics.Color(0xFF3E3C3A)
