package com.example.simper

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates

class SettingsActivity : AppCompatActivity() {

    private var bookSelected: String = ""
    private var currentPageInt by Delegates.notNull<Int>()
    private var defaultValue = 0

    private lateinit var textSizeSeekBar: SeekBar
    private lateinit var backgroundColorEditText: EditText
    private lateinit var colorPreview: TextView
    private lateinit var textColorEditText: EditText
    private lateinit var textColorPreview: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayoutHelper: DrawerLayoutHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        bookSelected = intent.getStringExtra("Book Selected").toString()
        currentPageInt = intent.getIntExtra("currentPageInt", defaultValue)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        textSizeSeekBar = findViewById(R.id.textSizeSeekBar)
        backgroundColorEditText = findViewById(R.id.backgroundColorEditText)
        colorPreview = findViewById(R.id.colorPreview)
        textColorEditText = findViewById(R.id.textColorEditText)
        textColorPreview = findViewById(R.id.textColorPreview)



        loadPreferences()

        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update text size display if necessary
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                savePreferences()
            }
        })

        backgroundColorEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateColorPreview()
            }

            override fun afterTextChanged(s: Editable?) {
                savePreferences()
            }
        })

        textColorEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTextColorPreview()
            }

            override fun afterTextChanged(s: Editable?) {
                savePreferences()
            }
        })

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        drawerLayoutHelper = DrawerLayoutHelper(this)
        drawerLayoutHelper.setupDrawerLayout(this, drawerLayout, navigationView, bookSelected, currentPageInt)
    }

    private fun updateColorPreview() {
        try {
            var colorString = backgroundColorEditText.text.toString()
            if (!colorString.startsWith("#")) {
                colorString = "#$colorString"
            }
            val color = Color.parseColor(colorString)
            colorPreview.setBackgroundColor(color)
        } catch (e: IllegalArgumentException) {
            // Handle invalid color input
        }
    }

    private fun updateTextColorPreview() {
        try {
            var colorString = textColorEditText.text.toString()
            if (!colorString.startsWith("#")) {
                colorString = "#$colorString"
            }
            val color = Color.parseColor(colorString)
            textColorPreview.setBackgroundColor(color)
        } catch (e: IllegalArgumentException) {
            // Handle invalid color input
        }
    }

    private fun savePreferences() {
        with(sharedPreferences.edit()) {
            putInt("textSize", textSizeSeekBar.progress)
            putString("backgroundColor", backgroundColorEditText.text.toString())
            putString("textColor", textColorEditText.text.toString())
            apply()
        }
    }

    private fun loadPreferences() {
        val textSize = sharedPreferences.getInt("textSize", 16)
        val backgroundColor = sharedPreferences.getString("backgroundColor", "#FFFFFF")
        val textColor = sharedPreferences.getString("textColor", "#000000")

        textSizeSeekBar.progress = textSize
        backgroundColorEditText.setText(backgroundColor)
        textColorEditText.setText(textColor)

        updateColorPreview()
        updateTextColorPreview()
    }
}
