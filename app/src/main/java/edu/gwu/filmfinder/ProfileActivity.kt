package edu.gwu.filmfinder

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    lateinit var bottomBar : BottomNavigationView
    lateinit var signOutBtn: TextView
    companion object {

        val TAG = "ProfileActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        signOutBtn = findViewById(R.id.sign_out_profile)
        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        bottomBar = findViewById(R.id.bottomBar_profile)
        bottomBar.setSelectedItemId(R.id.action_profile)

        bottomBar.setOnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.action_find ->{
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_save ->{
                    val intent = Intent(this, SavedMoviesActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_profile ->{

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
