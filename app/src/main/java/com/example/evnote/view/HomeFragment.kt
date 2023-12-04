package com.example.evnote.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.evnote.adapter.MovieAdapter
import com.example.evnote.databinding.FragmentHomeBinding
import com.example.evnote.model.Movie
import com.example.evnote.model.Movies
import com.example.evnote.utils.MovieClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter1: MovieAdapter
    private lateinit var adapter2: MovieAdapter

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter1 = MovieAdapter(requireContext(), arrayListOf())
        adapter2 = MovieAdapter(requireContext(), arrayListOf())

        binding.rvMovie.adapter = adapter1
        binding.rvMovie.setHasFixedSize(true)

        binding.rvMovie2.adapter = adapter2
        binding.rvMovie2.setHasFixedSize(true)

        remoteGetPopularMovies(adapter1)
        remoteGetHeroMovies(adapter2)
    }

    private fun remoteGetPopularMovies(adapter: MovieAdapter) {
        MovieClient.movieService.getPopularMovies().enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                if (response.isSuccessful) {
                    val data = response.body()?.results
                    Log.d("MovieResponse", data.toString())
                    data?.let { setDataToAdapter(adapter, it) }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                Log.d("Error", "" + t.stackTraceToString())
            }
        })
    }

    private fun remoteGetHeroMovies(adapter: MovieAdapter) {
        MovieClient.movieService.getHeroMovies().enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                if (response.isSuccessful) {
                    val data = response.body()?.results
                    Log.d("MovieResponse", data.toString())
                    data?.let { setDataToAdapter(adapter, it) }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                Log.d("Error", "" + t.stackTraceToString())
            }
        })
    }

    private fun setDataToAdapter(adapter: MovieAdapter, data: List<Movie>) {
        adapter.setData(data)
    }
}
