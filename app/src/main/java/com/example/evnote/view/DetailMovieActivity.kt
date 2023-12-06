package com.example.evnote.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.evnote.R
import com.example.evnote.databinding.ActivityDetailMovieBinding
import com.example.evnote.model.Movie
import com.example.evnote.model.Movies


@Suppress("DEPRECATION")
class DetailMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMovieBinding

    companion object {
        fun defaultMovie(): Movie {
            return Movie()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedMovie: Movie? = intent.getParcelableExtra("movie")
        if (selectedMovie != null) {
            // Tampilkan data ke dalam TextView melalui binding
            val formattedRating = String.format("%.1f", selectedMovie.voteAverage)

            binding.titleMovie.text = selectedMovie.title
            binding.tvTrailer.text = selectedMovie.posterPath
            binding.dateRelease.text = selectedMovie.releaseDate
            binding.descriptionMovie.text = selectedMovie.overview
            binding.tvRating.text = "\t$formattedRating"

            val image = "https://image.tmdb.org/t/p/w500${selectedMovie.posterPath}"

            Glide.with(this)
                .load(image)
                .fitCenter()
                .into(binding.detailImageMovie)



            binding.imgBack.setOnClickListener {
                onBackPressed()
            }
        } else {
            // Tampilkan data ke dalam TextView melalui binding
            binding.titleMovie.text = "Title"
            binding.tvTrailer.text = "Trailer"
            binding.dateRelease.text = "Date Release"
            binding.descriptionMovie.text = "Description"
            binding.tvRating.text = "Rating"
        }



    }
}
