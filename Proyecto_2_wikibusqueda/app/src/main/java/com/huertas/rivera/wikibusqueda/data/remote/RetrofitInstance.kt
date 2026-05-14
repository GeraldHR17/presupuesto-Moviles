package com.huertas.rivera.wikibusqueda.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.huertas.rivera.wikibusqueda.util.Constants

object RetrofitInstance {

    val api: WikipediaApiService by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WikipediaApiService::class.java)
    }
}