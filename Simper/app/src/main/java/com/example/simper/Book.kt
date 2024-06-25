package com.example.simper

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import nl.siegmann.epublib.domain.MediaType
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.properties.Delegates

class Book : AppCompatActivity() {


    private var bookSelected: String = ""
    private var currentPageInt by Delegates.notNull<Int>()
    private var defaultValue = 0
    private var maxPageInt = 0

    private lateinit var drawerLayoutHelper: DrawerLayoutHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        bookSelected = intent.getStringExtra("Book Selected").toString()
        currentPageInt = intent.getIntExtra("currentPageInt", defaultValue)

        val webView = findViewById<WebView>(R.id.webView)
        val TAG = "BookActivity"

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("books/$bookSelected")

        val localFile = File.createTempFile("temp", "epub")

        storageRef.getFile(localFile).addOnSuccessListener {
            try {
                val epubInputStream = FileInputStream(localFile)
                epubInputStream.use { stream ->
                    val epubReader = EpubReader()
                    val book = epubReader.readEpub(stream)
                    val bookSpine = book.spine

                    maxPageInt = bookSpine.size()

                    val (backgroundColor, textColor, textSizePercentage) = loadPreferences()

                    if (!bookSpine.isEmpty) {
                        if (currentPageInt == 0) {
                            val coverImageData: ByteArray = book.coverImage.data
                            val imageMimeType: MediaType? = book.coverImage.mediaType
                            val base64Image: String = Base64.encodeToString(coverImageData, Base64.DEFAULT)
                            val htmlContentWithImage = "<html><body><img src='data:image/$imageMimeType;base64,$base64Image' width='100%' height='auto'/></body></html>"
                            webView.loadDataWithBaseURL(null, htmlContentWithImage, "text/html", "UTF-8", null)
                        } else {
                            val currentPageData: ByteArray = bookSpine.getResource(currentPageInt).data
                            val htmlTextContent = currentPageData.toString(Charsets.UTF_8)
                            val styledHtmlContent = """
                                <html>
                                    <head>
                                        <style>
                                            body {
                                                background-color: $backgroundColor; 
                                                color: $textColor; 
                                                font-size: ${textSizePercentage}%; 
                                                font-weight: 400; 
                                                text-rendering: optimizeLegibility; 
                                                text-rendering: optimizeSpeed;
                                            }
                                        </style>
                                    </head>
                                    <body>
                                        $htmlTextContent
                                    </body>
                                </html>
                            """.trimIndent()
                            webView.loadDataWithBaseURL(null, styledHtmlContent, "text/html", "UTF-8", null)
                        }
                    } else {
                        val contents = book.contents
                        val resources = book.resources
                        val resourcesMap = resources.resourceMap
                        val resourcesMapSorted = resources.resourceMap.toSortedMap()
                        val keys = resourcesMapSorted.keys

                        var element = keys.elementAt(2)

                        var resourceElement = resourcesMap[element]?.data
                        var mediaType = resourcesMap[element]?.mediaType

                        val htmlTextContent = resourceElement?.toString(Charsets.UTF_8)

                        if (htmlTextContent != null) {
                            webView.loadDataWithBaseURL(null, htmlTextContent, "text/html", "UTF-8", null)
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error reading book", e)
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error downloading file", exception)
        }

        val buttonPreviousPage = findViewById<Button>(R.id.buttonPreviousPage)
        val buttonNextPage = findViewById<Button>(R.id.buttonNextPage)

        buttonPreviousPage.setOnClickListener {
            if (currentPageInt <= 0) {
                val intent = Intent(this@Book, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@Book, Book::class.java)
                intent.putExtra("Book Selected", bookSelected)
                intent.putExtra("currentPageInt", currentPageInt - 1)
                startActivity(intent)
            }
        }

        buttonNextPage.setOnClickListener {
            if (currentPageInt == maxPageInt - 1) {
                val intent = Intent(this@Book, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@Book, Book::class.java)
                intent.putExtra("Book Selected", bookSelected)
                intent.putExtra("currentPageInt", currentPageInt + 1)
                startActivity(intent)
            }
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        drawerLayoutHelper = DrawerLayoutHelper(this)
        drawerLayoutHelper.setupDrawerLayout(this, drawerLayout, navigationView, bookSelected, currentPageInt)
    }

    private fun loadPreferences(): Triple<String, String, Int> {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val backgroundColor = sharedPreferences.getString("backgroundColor", "#FFFFFF") ?: "#FFFFFF"
        val textColor = sharedPreferences.getString("textColor", "#000000") ?: "#000000"
        val textSize = sharedPreferences.getInt("textSize", 16)
        val textSizePercentage = (textSize.toFloat() / 16 * 100).toInt()
        return Triple(backgroundColor, textColor, textSizePercentage)
    }
}
