package edu.gwu.filmfinder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        val preferences: SharedPreferences = getSharedPreferences("android-tweets", Context.MODE_PRIVATE)
        email.setText(preferences.getString("SAVED_USERNAME", ""))
        email.addTextChangedListener(textWatcher)
        passWord.addTextChangedListener(textWatcher)

        goToRegister.setOnClickListener {
            Log.d(TAG,"go to register")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        submitBtn.setOnClickListener {
            performLogin(preferences)
        }

    }

    private fun performLogin(preferences: SharedPreferences) {
        // firebaseAnalytics.logEvent("login_clicked", null)
        Log.d(TAG, "Email is: " + email.text.toString())
        Log.d(TAG, "Password is: " + passWord.text.toString())
        val inputtedUserEmail: String = email.text.toString().trim()
        val inputtedPassword: String = passWord.text.toString()

        // Save the inputted username to file
        preferences
            .edit()
            .putString("SAVED_USEREMAIL", inputtedUserEmail)
            .apply()
                preferences
                    .edit()
                    .putString("SAVED_PASSWORD", inputtedPassword)
                    .apply()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

    }


    private var textWatcher: TextWatcher? = object : TextWatcher {

        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(newString: CharSequence, start: Int, before: Int, count: Int) {
            val inputtedUsername: String = email.text.toString().trim()
            val inputtedPassword: String = passWord.text.toString()
            val enabled: Boolean = inputtedUsername.isNotEmpty() && inputtedPassword.isNotEmpty()

            // Kotlin shorthand for login.setEnabled(enabled)
            submitBtn.isEnabled = enabled
            goToRegister.isEnabled = enabled
        }
    }

}