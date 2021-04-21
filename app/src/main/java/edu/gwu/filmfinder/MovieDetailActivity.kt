package edu.gwu.filmfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync

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

        val contentType=getString(R.string.Content_Type)
        val primaryReleaseDateGte = "2020-01-15"
        val primaryReleaseDateLte = "2020-02-22"
        val api_key = getString(R.string.movie_KEY)
        val authorization =  getString(R.string.authorization)
        val language = getString(R.string.language)
        val imaFrontPAth = getString(R.string.img_front_path)

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

        if (currMovie!!.imageUrl != ""){
            Picasso
                .get()
                .load(currMovie!!.imageUrl)
                .placeholder(R.drawable.no_images_available)
                .into(movieBGImg)

            Picasso
                .get()
                .load(currMovie!!.imageUrl)
                .placeholder(R.drawable.no_images_available)
                .into(movieImg)
        }

        doAsync{
            try{
                val movieManagerDetail = MovieManagerDetail(currMovie!!)
                currMovie = movieManagerDetail.retrieveMovie(
                    contentType,
                    api_key,
                    authorization,
                    language
                )

                movieDescription.text = currMovie!!.description
                movieRating.text = currMovie!!.rating


            }catch (e: Exception){
            }
        }

    }
}
