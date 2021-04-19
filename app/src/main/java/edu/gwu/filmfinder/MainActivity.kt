package edu.gwu.filmfinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var email : EditText
    lateinit var passWord: EditText
    lateinit var submitBtn: Button
    lateinit var goToRegister: TextView

    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.email_edittext_login)
        passWord = findViewById(R.id.password_edittext_login)
        submitBtn = findViewById(R.id.login_button_login)
        goToRegister = findViewById(R.id.back_to_register_textview)

//        val preferences: SharedPreferences = getSharedPreferences("android-filmfinder", Context.MODE_PRIVATE)
//        email.setText(preferences.getString("SAVED_USEREMAIL", ""))
//        passWord.setText(preferences.getString("SAVED_PASSWORD", ""))
//        email.addTextChangedListener(textWatcher)
//        passWord.addTextChangedListener(textWatcher)

        goToRegister.setOnClickListener {
            Log.d(TAG,"go to register")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}