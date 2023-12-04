package com.example.evnote.utils

import com.example.evnote.model.Movie
import com.example.evnote.model.Movies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {

    @GET("movie/popular?api_key=c91b07124c855f7577ce56962639cd93")
    fun getPopularMovies(): Call<Movies>

    @GET("movie/now_playing?api_key=c91b07124c855f7577ce56962639cd93")
    fun getNowPlayingMovies(): Call<Movies>

    @GET("movie/top_rated?api_key=c91b07124c855f7577ce56962639cd93")
    fun getTopRatedMovies(): Call<Movies>

    @GET("trending/movie/day?api_key=c91b07124c855f7577ce56962639cd93")
    fun getHeroMovies(): Call<Movies>

    @GET("movie/{movieId}?api_key=c91b07124c855f7577ce56962639cd93&append_to_response=videos")
    fun getMovieDetail(@Path("movieId") movieId: Int): Call<Movie>

    @GET("movie/{movieId}/recommendations")
    fun getRecommendedMovies(@Path("movieId") movieId: Int): Call<Movies>

    @GET("https://www.youtube.com/watch?vqZ40Z62tcXM")
    fun getMovieVideos(@Path("movieId") movieId: Int): Call<List<Movies>>

    // Anda mungkin perlu menambahkan endpoint-endpoint lain sesuai kebutuhan
}
