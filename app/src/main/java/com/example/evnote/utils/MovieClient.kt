package com.example.evnote.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "c91b07124c855f7577ce56962639cd93"
        val movieService : MovieService
        get() {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(MovieService::class.java)
        }
}
