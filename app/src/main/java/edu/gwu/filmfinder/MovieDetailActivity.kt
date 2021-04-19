package edu.gwu.filmfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MovieDetailActivity : AppCompatActivity() {

    var currMovie: Movie? = null
    lateinit var movieTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        currMovie = intent.getParcelableExtra<Movie>("movie")
        movieTitle = findViewById(R.id.app_header_movie_name_detailpage)
        movieTitle.text = currMovie!!.title

    }
}
