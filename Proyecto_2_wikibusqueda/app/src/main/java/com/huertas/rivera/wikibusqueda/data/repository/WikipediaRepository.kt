package com.huertas.rivera.wikibusqueda.data.repository

import com.huertas.rivera.wikibusqueda.data.remote.RetrofitInstance

class WikipediaRepository {

    suspend fun searchArticles(query: String) =
        RetrofitInstance.api.searchArticles(query)
}