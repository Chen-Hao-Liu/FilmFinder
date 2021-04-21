package edu.gwu.filmfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SavedMoviesActivity : AppCompatActivity() {

    companion object {
        val TAG = "SavedMoviesActivity"
        var mapLike = HashSet<String>()
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    lateinit var bottomBar : BottomNavigationView

    private lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_movies)

        bottomBar = findViewById(R.id.bottomBar_saved_page)
        bottomBar.setSelectedItemId(R.id.action_save)
        firebaseDatabase = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.recycleview_saved_movie)
        emptyState = findViewById(R.id.empty_state_saved_page)

        // Set the direction of our list to be vertical
        recyclerView.layoutManager = LinearLayoutManager(this)
        database()

        bottomBar.setOnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.action_find ->{
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_save ->{

                }
                R.id.action_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_profile ->{
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }

            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun database() {
        firebaseDatabase  = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().uid

        val movieList = mutableListOf<Movie>()
        getRef(movieList){
            val operList = mutableListOf<MovieId>()
            val opRef : DatabaseReference = firebaseDatabase.getReference("/Actions/${uid}")
            opRef.addValueEventListener(object: ValueEventListener{

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@SavedMoviesActivity,
                        "Failed to retrieve operation:$error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val showList = mutableListOf<Movie>()
                    mapLike.clear()
                    dataSnapshot.children.forEach { child ->
                        val oper = child.getValue(MovieId::class.java)
                        if (oper != null) {
                            operList.add(oper)
                            mapLike.add(oper.id)
                        }
                    }
                    for (cur in it){
                        if (mapLike.contains(cur.id)){
                            showList.add(cur)
                        }
                    }
                    if (showList.size != 0) {
                        emptyState.visibility = View.GONE
                    }else {
                        emptyState.visibility = View.VISIBLE
                    }

                    recyclerView.adapter=SavedMoviesAdapter(showList, this@SavedMoviesActivity)
                }
            })
        }
    }

    fun getRef (movieList: MutableList<Movie>,getList:(MutableList<Movie>) -> Unit){
        val movieRef : DatabaseReference = firebaseDatabase.getReference("/Movies")
        movieRef.addValueEventListener(object: ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@SavedMoviesActivity,
                    "Failed to retrieve savedMovies:$error",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                movieList.clear()
                dataSnapshot.children.forEach { child ->
                    val movie = child.getValue(Movie::class.java)
                    if (movie != null) {
                        movieList.add(movie)
                        getList(movieList)
                    }
                }

            }
        })
    }
}
