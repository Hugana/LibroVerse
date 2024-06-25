package com.example.simper

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class AboutActivity : AppCompatActivity() {

    private lateinit var drawerLayoutHelper: DrawerLayoutHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        // Drawer Layout Code
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Inside MainActivity.onCreate()

        // Initialize the drawerLayoutHelper
        drawerLayoutHelper = DrawerLayoutHelper(this)

        // Call the setupDrawerLayout method of drawerLayoutHelper
        drawerLayoutHelper.setupDrawerLayout(this, drawerLayout, navigationView, "null", 0)
    }
}