package com.example.simper

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class DrawerLayoutHelper(private val activity: Activity) {

    private lateinit var firebaseAuth: FirebaseAuth

    init {
        // Initialize firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun setupDrawerLayout(context: Context, drawerLayout: DrawerLayout, navigationView: NavigationView, bookSelected: String, currentPageInt: Int) {
        val drawerToggle = ActionBarDrawerToggle(
            context as Activity,
            drawerLayout,
            R.string.open_navigation,
            R.string.close_navigation
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home, R.id.book, R.id.dictionary, R.id.settings, R.id.readBooks,R.id.toreadBooks,R.id.favouriteBooks,R.id.SignOut, R.id.UploadBook,R.id.About  -> {
                    if(item.itemId == R.id.SignOut){
                        firebaseAuth.signOut()
                        val intent = Intent(context, SignIn::class.java)
                        context.startActivity(intent)
                        true
                    } else {
                        startNewActivity(context, item.itemId, bookSelected, currentPageInt)
                        true
                    }
                }
                else -> false
            }
        }
        val emailTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.EmailDrawer)
        emailTextView.text = getEmailFromSharedPreferences(context)

        val openDrawerButton = (context as Activity).findViewById<ImageButton>(R.id.ButtonNavToggle)
        openDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun getEmailFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("email", null)
    }

    private fun startNewActivity(context: Context, itemId: Int, bookSelected: String, currentPageInt: Int) {
        val intent = when (itemId) {
            R.id.home -> Intent(context, MainActivity::class.java)
            R.id.book -> Intent(context, Book::class.java)
            R.id.dictionary -> Intent(context, DictionaryActivity::class.java)
            R.id.settings -> Intent(context, SettingsActivity::class.java)
            R.id.readBooks -> Intent(context, ReadBooks::class.java)
            R.id.toreadBooks -> Intent(context, ToReadBooks::class.java)
            R.id.favouriteBooks -> Intent(context, FavouriteBooks::class.java)
            R.id.SignOut -> Intent(context,SignIn::class.java)
            R.id.UploadBook -> Intent(context, BookUpload::class.java)
            R.id.About -> Intent(context, AboutActivity::class.java)
            else -> return
        }
        intent.putExtra("Book Selected", bookSelected)
        intent.putExtra("currentPageInt", currentPageInt)
        context.startActivity(intent)
    }

}
