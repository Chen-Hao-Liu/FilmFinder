package edu.gwu.filmfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync

class MovieDetailActivity : AppCompatActivity() {

    var uid :String? = ""

    companion object {

        val TAG = "MovieDetailActivity"
    }

    var currMovie: Movie? = null
    lateinit var movieTitle: TextView
    lateinit var movieTitleDetail: TextView
    lateinit var movieBGImg : ImageView
    lateinit var movieImg: ImageView
    lateinit var movieRating: TextView
    lateinit var movieDescription:TextView
    lateinit var movieFav:ImageView
    lateinit var goBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val contentType=getString(R.string.Content_Type)
        val api_key = getString(R.string.movie_KEY)
        val authorization =  getString(R.string.authorization)
        val language = getString(R.string.language)

        currMovie = intent.getParcelableExtra<Movie>("movie")
        movieTitle = findViewById(R.id.app_header_movie_name_detailpage)
        movieTitleDetail = findViewById(R.id.movie_title_detailpage)
        movieBGImg = findViewById(R.id.movie_img_background_detailpage)
        movieImg = findViewById(R.id.movie_img_detailpage)
        movieRating = findViewById(R.id.movie_rating_detailpage)
        movieDescription = findViewById(R.id.movie_detail_description)
        movieFav = findViewById(R.id.movie_liked)
        goBack = findViewById(R.id.ic_go_back_detailpage)

        movieTitle.text = currMovie!!.title
        movieTitleDetail.text = currMovie!!.title
        val from = intent.getStringExtra("from")

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

        if (FirebaseAuth.getInstance().uid == ""){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        uid = FirebaseAuth.getInstance().uid

        movieFav.tag = false

        getMovieFavStatus(currMovie!!)
        movieFav.setOnClickListener {
            Log.d(TAG,"fav click")
            if (movieFav.getTag().toString() == "false"){
                updateLikedMovie(currMovie!!)
            } else {
                updateDislikedMovie(currMovie!!)
            }
        }

        goBack.setOnClickListener {
            if (from.equals(SavedMoviesActivity.TAG)){
                val intent = Intent(this, SavedMoviesActivity::class.java)
                startActivity(intent)
            } else if (from.equals(HomeActivity.TAG)){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun getMovieFavStatus(currMovie: Movie) {
        val movieRef = FirebaseDatabase.getInstance().reference.child("Actions/${uid}").orderByChild("id").equalTo("${currMovie.id}")

        movieRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (movieSnapshot in dataSnapshot.children) {
                    movieFav.tag = true
                    movieFav.setImageDrawable(getDrawable(R.drawable.ic_favorite_selected))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG,"Unlike Movie Failed:${databaseError.toException()}")

            }
        })
    }

    private fun updateLikedMovie(currMovie: Movie) {
        val ref = FirebaseDatabase.getInstance().getReference("/Movies/${currMovie.id}")

        ref.setValue(currMovie)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the movie to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }

        val actionRef = FirebaseDatabase.getInstance().getReference("/Actions/${uid}").push()

        val currMovieId = MovieId(currMovie.id)
        actionRef.setValue(currMovieId)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the movie to Firebase Database in Actions")
                movieFav.tag = true
                movieFav.setImageDrawable(getDrawable(R.drawable.ic_favorite_selected))
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

    private fun updateDislikedMovie(currMovie: Movie) {
        val movieRef = FirebaseDatabase.getInstance().reference.child("Actions/${uid}").orderByChild("id").equalTo("${currMovie.id}")

        movieRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (movieSnapshot in dataSnapshot.children) {
                    movieSnapshot.ref.removeValue().addOnSuccessListener {
                        movieFav.tag = false
                        movieFav.setImageDrawable(getDrawable(R.drawable.ic_favorite_unselected))
                        Log.d(TAG,"Unlike Movie Successfully")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG,"Unlike Movie Failed:${databaseError.toException()}")

            }
        })
    }
}
