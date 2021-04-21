package edu.gwu.filmfinder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SavedMoviesAdapter(val movies: List<Movie>,val mContext: Context) : RecyclerView.Adapter<SavedMoviesAdapter.SavedMoviesViewHolder>() {

    // onCreateViewHolder is called when the RecyclerView needs a new XML layout to be loaded for a row
    // Open & parse our XML file for our row and return the ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedMoviesViewHolder {
        // Open & parse our XML file
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_movie_list_row, parent, false)

        // Create a new ViewHolder
        return SavedMoviesViewHolder(view)
    }

    // Returns the number of rows to render
    override fun getItemCount(): Int {
        return movies.size
    }

    // onBindViewHolder is called when the RecyclerView is ready to display a new row at [position]
    // and needs you to fill that row with the necessary data.
    //
    // It passes you a ViewHolder, either from what you returned from onCreateViewHolder *or*
    // it's passing you an existing ViewHolder as a part of the "recycling" mechanism.
    override fun onBindViewHolder(holder: SavedMoviesViewHolder, position: Int) {
        val currentMovie = movies[position]
        holder.movieName.text = currentMovie.title
        holder.movieGrade.text = currentMovie.rating
        holder.movieDescription.text = currentMovie.description

        if (currentMovie.imageUrl != ""){
            Picasso
                .get()
                .load(currentMovie.imageUrl)
                .placeholder(R.drawable.no_images_available)
                .into(holder.movieImg)
        } else {
            Picasso
                .get()
                .load(currentMovie.imageUrl)
                .into(holder.movieImg)
        }
        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(mContext, MovieDetailActivity::class.java)
            intent.putExtra("movie", movies.get(position))
            intent.putExtra("from", SavedMoviesActivity.TAG)
            mContext.startActivity(intent)
        }
    }

    // A ViewHolder is a class which *holds* references to *views* that we care about in each
    // individual row. The findViewById function is somewhat inefficient, so the idea is to the lookup
    // for each view once and then reuse the object.
    class SavedMoviesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val movieImg: ImageView = view.findViewById(R.id.imageview_saved_movie_list_row)
        val movieName: TextView = view.findViewById(R.id.movie_name_saved_movie_list_row)
        val movieGrade: TextView = view.findViewById(R.id.movie_grade_saved_movie_list_row)
        val movieDescription: TextView = view.findViewById(R.id.movie_description_saved_movie_list_row)
    }
}
