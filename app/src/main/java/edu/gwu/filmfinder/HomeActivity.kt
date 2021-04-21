package edu.gwu.filmfinder

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync

class HomeActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var goBackBtn : ImageView


    companion object {
        val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val contentType=getString(R.string.Content_Type)
        val primaryReleaseDateGte = "2020-01-15"
        val primaryReleaseDateLte = "2020-02-22"
        val api_key = getString(R.string.movie_KEY)
        val authorization =  getString(R.string.authorization)
        val language = getString(R.string.language)
        val imaFrontPAth = getString(R.string.img_front_path)
        val n = getString(R.string.page)

        goBackBtn = findViewById(R.id.ic_go_back_homepage)
        recyclerView = findViewById(R.id.recycleview_movie)
        recyclerView.layoutManager = GridLayoutManager(this,3)

        goBackBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        doAsync{
            try{
                val movieManagerComingSoon = MovieManagerComingSoon()
                val moviesList = movieManagerComingSoon.retrieveMovie(
                    contentType,
                    primaryReleaseDateGte,
                    primaryReleaseDateLte,
                    api_key,
                    authorization,
                    language,
                    imaFrontPAth,
                    n)

                this@HomeActivity?.runOnUiThread{
                    val myAdapter:RecyclerViewAdapter = RecyclerViewAdapter(moviesList,this@HomeActivity)
                    recyclerView.adapter = myAdapter
//                    mry.adapter = RecyclerViewAdapter(context!!,moviesList,mCallback)
                }

            }catch (e: Exception){
                this@HomeActivity?.runOnUiThread {
                    e.printStackTrace()
                    // Display some kind of error message
                    Toast.makeText(
                        this@HomeActivity,
                        " No Movies!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}