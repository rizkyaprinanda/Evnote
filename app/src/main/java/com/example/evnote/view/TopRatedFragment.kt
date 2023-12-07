package com.example.evnote.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.evnote.R
import com.example.evnote.adapter.MovieAdapter
import com.example.evnote.databinding.FragmentTopRatedBinding
import com.example.evnote.model.Movie
import com.example.evnote.model.Movies
import com.example.evnote.utils.MovieClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopRatedFragment : Fragment() {
    private lateinit var binding: FragmentTopRatedBinding
    private lateinit var adapter1: MovieAdapter
    private lateinit var adapter2: MovieAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBar2 : ProgressBar

    companion object {
        fun newInstance(): TopRatedFragment {
            return TopRatedFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopRatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }
        })

        adapter1 = MovieAdapter(requireContext(), arrayListOf())
        adapter2 = MovieAdapter(requireContext(), arrayListOf())

        // Inisialisasi ProgressBar
        progressBar = binding.progressBar
        progressBar2 = binding.progressBar2

        // Ganti dengan warna yang diinginkan
        val color = ContextCompat.getColor(requireContext(), R.color.yellow)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
            progressBar2.indeterminateTintList = ColorStateList.valueOf(color)
        } else {
            val mode = PorterDuff.Mode.SRC_IN
            progressBar.indeterminateDrawable.setColorFilter(color, mode)
            progressBar2.indeterminateDrawable.setColorFilter(color, mode)
        }

        binding.rvMovie.adapter = adapter1
        binding.rvMovie.setHasFixedSize(true)

        binding.rvMovie2.adapter = adapter2
        binding.rvMovie2.setHasFixedSize(true)

        // Show progress bar before making network requests
        progressBar.visibility = View.VISIBLE
        progressBar2.visibility = View.VISIBLE

        remoteGetTopRatedMovies(adapter1, binding.progressBar)
        remoteGetHeroMovies(adapter2, binding.progressBar2)
    }

    private fun remoteGetTopRatedMovies(adapter: MovieAdapter, progressBar: ProgressBar) {
        MovieClient.movieService.getTopRatedMovies().enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                // Hide progress bar on response
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val data = response.body()?.results
                    Log.d("MovieResponse", data.toString())
                    data?.let { setDataToAdapter(adapter, it) }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                // Hide progress bar on failure
                progressBar.visibility = View.GONE

                Log.d("Error", "" + t.stackTraceToString())
            }
        })
    }

    private fun remoteGetHeroMovies(adapter: MovieAdapter, progressBar: ProgressBar) {
        MovieClient.movieService.getHeroMovies().enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                // Hide progress bar on response
                progressBar2.visibility = View.GONE

                if (response.isSuccessful) {
                    val data = response.body()?.results
                    Log.d("MovieResponse", data.toString())
                    data?.let { setDataToAdapter(adapter, it) }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                // Hide progress bar on response
                progressBar2.visibility = View.GONE

                Log.d("Error", "" + t.stackTraceToString())
            }
        })
    }

    private fun setDataToAdapter(adapter: MovieAdapter, data: List<Movie>) {
        adapter.setData(data)
    }
}
