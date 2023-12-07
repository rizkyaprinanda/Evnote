package com.example.evnote.utils

import com.example.evnote.config.Config
import com.example.evnote.model.GenresResponse
import com.example.evnote.model.Movie
import com.example.evnote.model.Movies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String = Config.TMDB_API_KEY): Call<Movies>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(@Query("api_key") apiKey: String = Config.TMDB_API_KEY): Call<Movies>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String = Config.TMDB_API_KEY): Call<Movies>

    @GET("trending/movie/week")
    fun getHeroMovies(@Query("api_key") apiKey: String = Config.TMDB_API_KEY): Call<Movies>

    @GET("genre/movie/list")
    suspend fun getMovieGenres(@Query("api_key") apiKey: String = Config.TMDB_API_KEY): GenresResponse

    @GET("trending/movie/day")
    fun getTrendingMovies(@Query("api_key") apiKey: String = Config.TMDB_API_KEY): Call<Movies>

    @GET("movie/{movieId}")
    fun getMovieDetail(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = Config.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: String = "videos"
    ): Call<Movie>

    @GET("movie/{movieId}/recommendations")
    fun getRecommendedMovies(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = Config.TMDB_API_KEY
    ): Call<Movies>

    @GET("https://www.youtube.com/watch?vqZ40Z62tcXM")
    fun getMovieVideos(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String = Config.TMDB_API_KEY
    ): Call<List<Movies>>

    // Anda mungkin perlu menambahkan endpoint-endpoint lain sesuai kebutuhan
}
