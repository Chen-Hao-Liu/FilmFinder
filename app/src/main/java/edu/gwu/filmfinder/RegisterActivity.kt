package edu.gwu.filmfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    lateinit var userNameEt: EditText
    lateinit var emailEt : EditText
    lateinit var passWordEt: EditText
    lateinit var registerBtn: Button
    lateinit var hasAcountBtn: TextView

    companion object {

        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userNameEt = findViewById(R.id.username_edittext_register)
        emailEt = findViewById(R.id.email_edittext_register)
        passWordEt = findViewById(R.id.password_edittext_register)
        hasAcountBtn = findViewById(R.id.already_have_account_text_view)
        registerBtn = findViewById(R.id.register_button_register)
        hasAcountBtn.setOnClickListener{
            Log.d(TAG,"go to login")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}