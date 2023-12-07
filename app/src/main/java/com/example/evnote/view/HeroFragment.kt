package com.example.evnote.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.evnote.MainActivity
import com.example.evnote.R
import com.example.evnote.databinding.FragmentHeroBinding
import com.example.evnote.model.Movie
import com.example.evnote.model.Movies
import com.example.evnote.utils.MovieClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HeroFragment : Fragment() {

    private lateinit var binding: FragmentHeroBinding
    private lateinit var progressBar: ProgressBar

    companion object {
        fun newInstance(): HeroFragment {
            return HeroFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ProgressBar
        progressBar = binding.progressBar

        // Ganti dengan warna yang diinginkan
        val color = ContextCompat.getColor(requireContext(), R.color.yellow)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
        } else {
            val mode = PorterDuff.Mode.SRC_IN
            progressBar.indeterminateDrawable.setColorFilter(color, mode)
        }

        // Show progress bar before making network requests
        progressBar.visibility = View.VISIBLE

        // Implement logic to fetch trending movie and populate UI
        fetchTrendingMovie()
        fetchMovieGenres()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }
        })



        // Handle imgBack onClick
        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun fetchMovieGenres() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val genresResponse = MovieClient.movieService.getMovieGenres()
                val genres = genresResponse.genres
                val formattedGenres = genres.joinToString(", ") { it.name }
                binding.genreTextView.text = formattedGenres
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    private fun fetchTrendingMovie() {
        MovieClient.movieService.getTrendingMovies().enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    if (!movies.isNullOrEmpty()) {
                        val trendingMovie = movies.random()
                        displayMovieDetails(trendingMovie)
                    }
                }
                // Hide progress bar on failure
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun displayMovieDetails(movie: Movie) {
        // Implement logic to display movie details in the UI
        // For example:
        binding.titleMovie.text = movie.title
        binding.tvRating.text = String.format("%.1f", movie.voteAverage)
        binding.descriptionMovie.text = movie.overview
        binding.dateRelease.text = movie.releaseDate

        val image = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

        Glide.with(this)
            .load(image)
            .fitCenter()
            .into(binding.detailImageMovie)

        binding.btnTrailer.setOnClickListener {
            // Implement logic to watch the trailer
            // You can use movie.videos.results[0].key to get the trailer key
        }

        // Load backdrop image using Glide or another image loading library
        // For example:
        // Glide.with(this).load(ENDPOINTS.BACKDROP(movie)).into(binding.backdropImageView)
    }
}
