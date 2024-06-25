package com.example.simper

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.privacysandbox.tools.core.model.Method
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlin.math.log
import kotlin.properties.Delegates

class DictionaryActivity : AppCompatActivity() {

    private var isWordInfoInflated = false

    private var bookSelected: String = ""
    private var defaultValue = 0
    private var currentPageInt by Delegates.notNull<Int>()

    private lateinit var drawerLayoutHelper: DrawerLayoutHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        bookSelected = intent.getStringExtra("Book Selected").toString()
        currentPageInt = intent.getIntExtra("currentPageInt", defaultValue)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == KeyEvent.ACTION_DOWN && event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform search when the Enter key is pressed
                val wordToSearch = searchEditText.text.toString()
                searchWord(wordToSearch)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }


        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Inside MainActivity.onCreate()

        // Initialize the drawerLayoutHelper
        drawerLayoutHelper = DrawerLayoutHelper(this)

        // Call the setupDrawerLayout method of drawerLayoutHelper
        drawerLayoutHelper.setupDrawerLayout(this,drawerLayout, navigationView, bookSelected, currentPageInt)
    }

    private fun searchWord(word: String) {
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->

                val gson = Gson()
                val wordDefinitions = gson.fromJson(response, Array<WordDefinition>::class.java)

                if (wordDefinitions.isNotEmpty()) {
                    inflateWordInformation(wordDefinitions[0])
                } else {
                    // Handle case when no definitions are found
                }
            },
            { error ->
                // Handle errors
                Log.e("DictionaryActivity", error.message.toString())
            })

        Volley.newRequestQueue(this).add(stringRequest)
    }



    private fun inflateWordInformation(wordDefinition: WordDefinition) {
        val layout = findViewById<LinearLayout>(R.id.wordInformation)

        // Clear existing word information views
        layout.removeAllViews()

        // Reset the flag indicating whether word info is inflated
        isWordInfoInflated = false

        val inflater = layoutInflater
        val wordInfoView = inflater.inflate(R.layout.word_info_layout, null)

        val definitionStringBuilder = StringBuilder()
        val synonymStringBuilder = StringBuilder()
        val antonymStringBuilder = StringBuilder()

        wordDefinition.meanings.forEach { meaning ->
            meaning.definitions.forEach { definition ->
                // Append definition
                definitionStringBuilder.append("${definition.definition}, ")

                // Append synonyms
                definition.synonyms.forEach { synonym ->
                    synonymStringBuilder.append("$synonym, ")
                }

                // Append antonyms
                definition.antonyms.forEach { antonym ->
                    antonymStringBuilder.append("$antonym, ")
                }
            }

            // Append synonyms and antonyms at the meaning level
            meaning.synonyms.forEach { synonym ->
                synonymStringBuilder.append("$synonym, ")
            }

            meaning.antonyms.forEach { antonym ->
                antonymStringBuilder.append("$antonym, ")
            }
        }

        // Remove trailing commas and whitespaces
        val definitions = definitionStringBuilder.toString().removeSuffix(", ")
        val synonyms = synonymStringBuilder.toString().removeSuffix(", ")
        val antonyms = antonymStringBuilder.toString().removeSuffix(", ")

        // Find TextViews in the inflated view
        val definitionTextView = wordInfoView.findViewById<TextView>(R.id.definitions)
        val synonymsTextView = wordInfoView.findViewById<TextView>(R.id.synonyms)
        val antonymsTextView = wordInfoView.findViewById<TextView>(R.id.antonyms)

        // Set text to TextViews
        definitionTextView.text = definitions
        synonymsTextView.text = synonyms
        antonymsTextView.text = antonyms

        // Add the inflated view to the parent layout
        layout.addView(wordInfoView)

        // Mark word info as inflated
        isWordInfoInflated = true
    }



}
