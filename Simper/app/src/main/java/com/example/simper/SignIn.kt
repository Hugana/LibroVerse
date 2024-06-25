package com.example.simper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignIn : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        firebaseAuth = FirebaseAuth.getInstance()

        var emailField = findViewById<TextView>(R.id.emailField)

        var passwordField = findViewById<TextView>(R.id.passwordLogin)


        var text = findViewById<TextView>(R.id.registerText)

        text.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        var signInButton = findViewById<Button>(R.id.signInButton)

        signInButton.setOnClickListener{
            var emailText = emailField.text.toString()
            var passwordText = passwordField.text.toString()

            if(emailText.isNotEmpty() && passwordText.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener{
                        if(it.isSuccessful){
                            saveEmailToSharedPreferences(emailText)
                            Toast.makeText(this,"Login successful :)", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this,"Please insert text in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveEmailToSharedPreferences(email: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }

}