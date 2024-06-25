package com.example.simper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()

        var emailField = findViewById<TextView>(R.id.emailField)
        var passwordField = findViewById<TextView>(R.id.passwordField)
        var passwordRepeatField = findViewById<TextView>(R.id.passwordRepeatField)



        var signIntext = findViewById<TextView>(R.id.textLogin)

        signIntext.setOnClickListener{
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        var signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.setOnClickListener{

            var emailText = emailField.text.toString()
            var passwordText = passwordField.text.toString()
            var passwordRepeatText = passwordRepeatField.text.toString()

            if(emailText.isNotEmpty() && passwordText.isNotEmpty() && passwordRepeatText.isNotEmpty()) {
                if (passwordText == passwordRepeatText) {

                    firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(this,"Registration complete :)", Toast.LENGTH_SHORT).show()
                            saveEmailToSharedPreferences(emailText)
                            val intent = Intent(this, SignIn::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please insert text in all fields.",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun saveEmailToSharedPreferences(email: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()
    }
}