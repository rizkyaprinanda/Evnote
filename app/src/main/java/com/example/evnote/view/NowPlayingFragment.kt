package com.example.evnote.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.evnote.R
import com.example.evnote.adapter.MovieAdapter
import com.example.evnote.databinding.FragmentNowPlayingBinding
import com.example.evnote.model.Movie
import com.example.evnote.model.Movies
import com.example.evnote.utils.MovieClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Fungsi ekstensi untuk melakukan pencarian pada List<Movie>
private fun List<Movie>.performSearch(query: String): List<Movie> {
    return this.filter { it.title?.contains(query, ignoreCase = true) == true ||
            it.overview?.contains(query, ignoreCase = true) == true }
}

class NowPlayingFragment : Fragment() {
    private lateinit var binding: FragmentNowPlayingBinding
    private lateinit var adapter1: MovieAdapter
    private lateinit var adapter2: MovieAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBar2: ProgressBar
    private var doubleBackPressedOnce = false
    private var originalData: List<Movie> = emptyList()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        fun newInstance(): NowPlayingFragment {
            return NowPlayingFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Aksi refresh
            // Taruh logika refresh data di sini
            refreshData()
        }

        binding.svMovies.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            // Mencegah scroll ke bawah jika sedang di-refresh
            if (swipeRefreshLayout.isRefreshing) {
                // Jika sedang di-refresh, maka stop scroll
                binding.svMovies.scrollTo(0, 0)
            } else {
                // Jika tidak sedang di-refresh, cek posisi paling atas
                val isAtTop = scrollY == 0

                // Jika sedang di paling atas, izinkan swipe refresh
                swipeRefreshLayout.isEnabled = isAtTop
            }
        }





        adapter1 = MovieAdapter(requireContext(), arrayListOf())
        adapter2 = MovieAdapter(requireContext(), arrayListOf())

        progressBar = binding.progressBar
        progressBar2 = binding.progressBar2

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

        progressBar.visibility = View.VISIBLE
        progressBar2.visibility = View.VISIBLE

        remoteGetPopularMovies(adapter1, progressBar)
        remoteGetHeroMovies(adapter2, progressBar2)

        val search = binding.edtSearch
        val noResultTextView = binding.tvNoResult
        noResultTextView.visibility = View.GONE

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()
                performSearch(query)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // Simpan data asli untuk pencarian
        originalData = ArrayList()
    }

    private fun refreshData() {

        remoteGetPopularMovies(adapter1, progressBar)
        remoteGetHeroMovies(adapter2, progressBar2)

        // Setelah selesai refresh, hentikan ikon loading
        swipeRefreshLayout.isRefreshing = false
    }

    private fun performSearch(query: String) {
        val filteredData = originalData.performSearch(query)
        val searchText = binding.edtSearch


        if (filteredData.isEmpty()) {
            binding.tvNoResult.visibility = View.VISIBLE
            adapter1.setData(emptyList())
            binding.rvMovie.visibility = View.GONE
            binding.rvMovie2.visibility = View.GONE

            if(searchText.text.isEmpty()){
                binding.tvNow.visibility = View.VISIBLE
                binding.tvOnTrend.visibility = View.VISIBLE
                adapter1.setData(emptyList())
            }

        } else {
            binding.tvNoResult.visibility = View.GONE
            binding.tvNow.visibility = View.GONE
            binding.tvOnTrend.visibility = View.GONE
            binding.rvMovie.visibility = View.VISIBLE
            binding.rvMovie2.visibility = View.GONE
            adapter1.setData(filteredData)

            if(searchText.text.isEmpty()){
                binding.tvNow.visibility = View.VISIBLE
                binding.tvOnTrend.visibility = View.VISIBLE
                binding.rvMovie2.visibility = View.VISIBLE
                adapter1.setData(filteredData)
            }
        }
    }


    private fun remoteGetMovies(call: Call<Movies>, adapter: MovieAdapter, progressBar: ProgressBar) {
        call.enqueue(object : Callback<Movies> {
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val data = response.body()?.results
                    Log.d("MovieResponse", data.toString())

                    // Shuffle data sebelum menetapkan ke adapter
                    data?.let {
                        val shuffledData = it.shuffled()
                        setDataToAdapter(adapter, shuffledData)
                    }
                }
            }

            override fun onFailure(call: Call<Movies>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.d("Error", "" + t.stackTraceToString())
            }
        })
    }

    private fun remoteGetPopularMovies(adapter: MovieAdapter, progressBar: ProgressBar) {
        val call = MovieClient.movieService.getNowPlayingMovies()
        remoteGetMovies(call, adapter, progressBar)
    }

    private fun remoteGetHeroMovies(adapter: MovieAdapter, progressBar: ProgressBar) {
        val call = MovieClient.movieService.getHeroMovies()
        remoteGetMovies(call, adapter, progressBar)
    }

    private fun setDataToAdapter(adapter: MovieAdapter, data: List<Movie>) {
        originalData = data
        adapter.setData(data)
    }
}
