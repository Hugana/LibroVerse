package com.example.simper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = FirebaseAuth.getInstance()

        val imageView = findViewById<ImageView>(R.id.imageView2)
        val textView1 = findViewById<TextView>(R.id.textView3)
        val textView2 = findViewById<TextView>(R.id.textView6)

        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation)

        // Apply animations to views
        imageView.startAnimation(rotateAnimation)
        textView1.startAnimation(fadeInAnimation)
        textView2.startAnimation(fadeInAnimation)

        // Redirect to SignInActivity after 1.2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            redirectToSignIn()
        }, 2000)
    }

    private fun redirectToSignIn() {
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish() // Close current activity to prevent going back to it on pressing back button
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close current activity to prevent going back to it on pressing back button
        }
    }
}
