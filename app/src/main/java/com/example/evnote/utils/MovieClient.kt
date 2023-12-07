package com.example.evnote.utils

import com.example.evnote.config.Config
import com.example.evnote.config.Config.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieClient {
    private val BASE_URL = Config.BASE_URL
    private val API_KEY = Config.TMDB_API_KEY

    val movieService: MovieService
        get() {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(MovieService::class.java)
        }
}
