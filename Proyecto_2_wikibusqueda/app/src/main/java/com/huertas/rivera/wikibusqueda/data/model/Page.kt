package com.huertas.rivera.wikibusqueda.data.model

data class Page(
    val id: Int,
    val key: String,
    val title: String,
    val description: String?,
    val thumbnail: Thumbnail?,
    val excerpt: String? // El fragmento del artículo para la previsualización
)
