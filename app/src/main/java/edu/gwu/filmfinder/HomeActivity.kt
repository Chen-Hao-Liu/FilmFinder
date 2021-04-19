package edu.gwu.filmfinder

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var goBackBtn : ImageView


    companion object {
        val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        goBackBtn = findViewById(R.id.ic_go_back_homepage)

        recyclerView = findViewById(R.id.recycleview_movie)
        recyclerView.layoutManager = GridLayoutManager(this,3)

        goBackBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var moviesList = getFakeData()

        val myAdapter:RecyclerViewAdapter = RecyclerViewAdapter(moviesList, this)
        recyclerView.adapter = myAdapter
    }

    fun getFakeData(): ArrayList<Movie>{
        var moviesList : ArrayList<Movie> = arrayListOf()
        moviesList.add(Movie("movie name 1","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription1","1"))
        moviesList.add(Movie("movie name 2","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription2","2"))
        moviesList.add(Movie("movie name 3","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription3","3"))
        moviesList.add(Movie("movie name 4","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription4","4"))
        moviesList.add(Movie("movie name 5","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription5","5"))
        moviesList.add(Movie("movie name 1","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription1","1"))
        moviesList.add(Movie("movie name 2","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription2","2"))
        moviesList.add(Movie("movie name 3","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription3","3"))
        moviesList.add(Movie("movie name 4","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription4","4"))
        moviesList.add(Movie("movie name 5","55555","","","descriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescriptiondescription5","5"))
        return moviesList
    }

}