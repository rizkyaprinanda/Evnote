package com.example.evnote.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.evnote.R
import com.example.evnote.model.Movie
import com.example.evnote.view.HomeFragment

class MovieAdapter(
    private val context: Context,
    private var dataList: List<Movie>,
    private var itemClickListener: ((Movie) -> Unit)? = null
) : RecyclerView.Adapter<MovieAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.tvMovieTitle)
        val tvRating = view.findViewById<TextView>(R.id.tvRating)
        val tvDate = view.findViewById<TextView>(R.id.tvMovieDate)
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val layoutCard = view.findViewById<LinearLayout>(R.id.layoutCard)
        val cvImageView = view.findViewById<CardView>(R.id.cvMovieImage)
        val ivMovieImage = view.findViewById<ImageView>(R.id.ivMovieImage)
        val upVote = view.findViewById<LinearLayout>(R.id.llUpvote)
        val movieTrailer = view.findViewById<TextView>(R.id.tvMovieTrailer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.items_movies, parent, false)
        return MyViewHolder(itemView)
    }

    val MAX_OVERVIEW_LENGTH = 100
    val MAX_TITLE_LENGTH = 20

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val selectedMovie = dataList[position]
        // Batasi overview menjadi maksimal 100 karakter
        val overviewText = if (selectedMovie.overview!!.length > MAX_OVERVIEW_LENGTH) {
            selectedMovie.overview!!.substring(0, MAX_OVERVIEW_LENGTH) + "..."
        } else {
            selectedMovie.overview
        }
        val titleText = if (selectedMovie.title!!.length > MAX_TITLE_LENGTH) {
            selectedMovie.title!!.substring(0, MAX_TITLE_LENGTH) + "..."
        } else {
            selectedMovie.title
        }

        with(holder) {
            val formattedRating = String.format("%.1f", selectedMovie.voteAverage)

            holder.tvRating.text = "\t$formattedRating"
            holder.tvTitle.text = titleText
            holder.tvDate.text = selectedMovie.releaseDate

            val image = "https://image.tmdb.org/t/p/w500${selectedMovie.posterPath}"

            Glide.with(view)
                .asBitmap()
                .load(image)
                .fitCenter()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // Set the image to ImageView
                        ivMovieImage.setImageBitmap(resource)

                        // Get the dominant color from the image
                        val dominantColor = getDominantColor(resource)

                        // Set the background of cvImageView with the dominant color
                        cvImageView.setBackgroundColor(dominantColor)



                    }
                })

            layoutCard.setOnClickListener {
                Toast.makeText(context, selectedMovie.title, Toast.LENGTH_SHORT).show()
            }


            itemView.setOnClickListener {
                // Memastikan itemClickListener tidak null sebelum dipanggil
                itemClickListener?.invoke(selectedMovie)
            }


        }
    }

    private fun getDominantColor(bitmap: Bitmap?): Int {
        val newBitmap = bitmap?.let { Bitmap.createScaledBitmap(it, 1, 1, true) }
        return newBitmap?.getPixel(0, 0) ?: 0

    }

    override fun getItemCount(): Int = dataList.size

    fun setData(data: List<Movie>) {
        dataList = data
        notifyDataSetChanged()
    }
}
