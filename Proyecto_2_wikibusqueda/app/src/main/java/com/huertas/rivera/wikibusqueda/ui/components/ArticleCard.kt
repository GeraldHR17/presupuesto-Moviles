package com.huertas.rivera.wikibusqueda.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.huertas.rivera.wikibusqueda.data.model.Page

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleCard(
    page: Page,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    // Limpieza de etiquetas HTML para la previsualización
    val cleanExcerpt = page.excerpt?.replace(Regex("<[^>]*>"), "") ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(2.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Pergamino Oscuro
        ),
        shape = RoundedCornerShape(0.dp) // Papel cortado recto
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) // Marco Dorado
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Marco de la Imagen (Estilo Retrato Antiguo)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.Black.copy(alpha = 0.1f))
                        .border(3.dp, MaterialTheme.colorScheme.onSurface) // Borde de Hierro
                ) {
                    val imageUrl = page.thumbnail?.url?.let {
                        if (it.startsWith("//")) "https:$it" else it
                    }

                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = page.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            // Si no hay imagen, mostramos el Castillo del Reino
                            Icon(
                                imageVector = Icons.Default.Castle,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = page.title.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                    
                    Text(
                        text = page.description ?: "Misterio de la Antigüedad",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // SECCIÓN DE VISTA PREVIA (Lo que pediste ver antes de tocar)
            if (cleanExcerpt.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu, // Pluma de escribir
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = cleanExcerpt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}