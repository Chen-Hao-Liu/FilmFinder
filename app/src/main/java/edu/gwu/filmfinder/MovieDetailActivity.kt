package edu.gwu.filmfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView

class MovieDetailActivity : AppCompatActivity() {

    var currMovie: Movie? = null
    lateinit var movieTitle: TextView
    lateinit var movieTitleDetail: TextView
    lateinit var movieBGImg : ImageView
    lateinit var movieImg: ImageView
    lateinit var movieRating: TextView
    lateinit var movieDescription:TextView
    lateinit var movieFav:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        currMovie = intent.getParcelableExtra<Movie>("movie")
        movieTitle = findViewById(R.id.app_header_movie_name_detailpage)
        movieTitleDetail = findViewById(R.id.movie_title_detailpage)
        movieBGImg = findViewById(R.id.movie_img_background_detailpage)
        movieImg = findViewById(R.id.movie_img_detailpage)
        movieRating = findViewById(R.id.movie_rating_detailpage)
        movieDescription = findViewById(R.id.movie_detail_description)
        movieFav = findViewById(R.id.movie_liked)

        movieTitle.text = currMovie!!.title
        movieTitleDetail.text = currMovie!!.title
        movieRating.text = currMovie!!.rating
        movieDescription.text = currMovie!!.description
    }
}
