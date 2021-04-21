package edu.gwu.filmfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    lateinit var userNameEt: EditText
    lateinit var emailEt : EditText
    lateinit var passWordEt: EditText
    lateinit var registerBtn: Button
    lateinit var hasAcountBtn: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {

        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

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

        registerBtn.setOnClickListener {
            performRegister()
        }
    }

    fun performRegister(){
        var username = userNameEt.text.toString().trim()
        var email = emailEt.text.toString().trim()
        var password = passWordEt.text.toString()
        Log.d(TAG, "Email is: " + email)
        Log.d(TAG, "Password is: " + password)
        if (email.isEmpty() || password.isEmpty()|| username.isEmpty()) {
            Toast.makeText(this, "Please enter text in username/email/password", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result!!.user!!.uid}")
                saveUserToFirebaseDatabase(username,email)
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirebaseDatabase(username:String,email:String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")

        val user = User(uid, username, email)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }


}