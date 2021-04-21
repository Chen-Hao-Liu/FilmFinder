package edu.gwu.filmfinder

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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

        goBackBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}