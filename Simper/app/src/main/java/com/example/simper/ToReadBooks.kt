package com.example.simper

import ListManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import nl.siegmann.epublib.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.properties.Delegates

class ToReadBooks : AppCompatActivity() {
    private var bookSelected: String = ""
    private var currentPageInt by Delegates.notNull<Int>()
    private var defaultValue = 0
    private val TAG = "ya mano"

    private lateinit var listManager: ListManager

    private lateinit var firestoreHelper: FirestoreHelper

    private val firestore = FirebaseFirestore.getInstance()

    // Initialize Firebase Storage
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var drawerLayoutHelper: DrawerLayoutHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Load lists from SharedPreferences
        listManager = ListManager(this)

        drawerLayoutHelper = DrawerLayoutHelper(this)

        firestoreHelper = FirestoreHelper(firestore, storageRef)


        bookSelected = intent.getStringExtra("Book Selected").toString()
        currentPageInt = intent.getIntExtra("currentPageInt", defaultValue)

        loadBooksFromFirestore()


        //Drawer Layout Code

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Inside MainActivity.onCreate()

        // Initialize the drawerLayoutHelper
        drawerLayoutHelper = DrawerLayoutHelper(this)

        // Call the setupDrawerLayout method of drawerLayoutHelper
        drawerLayoutHelper.setupDrawerLayout(
            this,
            drawerLayout,
            navigationView,
            bookSelected,
            currentPageInt
        )
    }

    private fun loadBooksFromFirestore() {
        val parentLayout = findViewById<LinearLayout>(R.id.buttonContainer)
        val inflater = layoutInflater

        // Get current user's email
        val userEmail = sharedPreferences.getString("email", null)
        if (userEmail == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            return
        }


        firestore.collection("ToReadBooks")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val fileName = document.getString("fileName")
                    if (fileName != null) {
                        // Download the file from Firebase Storage
                        val fileRef = storageRef.child("books/$fileName")
                        val localFile = File.createTempFile("temp", "epub")
                        fileRef.getFile(localFile).addOnSuccessListener {
                            try {
                                val inputStream = FileInputStream(localFile)
                                inputStream?.use { stream ->
                                    val epubReader = EpubReader()
                                    val book = epubReader.readEpub(stream)
                                    val bookSpine = book.spine

                                    // Inflate the XML layout file to create a view
                                    val view = inflater.inflate(R.layout.book_button_with_image, parentLayout, false)

                                    // Find the ImageView and TextView within the inflated view
                                    val image = view.findViewById<ImageView>(R.id.ImageCoverBook)
                                    val textView = view.findViewById<TextView>(R.id.bookText)
                                    val clickableLayout = view.findViewById<ConstraintLayout>(R.id.buttonGroupLayout)
                                    val buttonCheck = view.findViewById<ImageButton>(R.id.imageButtonCheck)
                                    val buttonToRead = view.findViewById<ImageButton>(R.id.imageButtonToRead)
                                    val buttonFavourite = view.findViewById<ImageButton>(R.id.imageButtonFavourite)

                                    // Set the text for the book title
                                    textView.text = book.title

                                    // Set the cover image
                                    if (!bookSpine.isEmpty) {
                                        val coverImageData: ByteArray = book.coverImage.data
                                        val bitmap = BitmapFactory.decodeByteArray(coverImageData, 0, coverImageData.size)
                                        image.setImageBitmap(bitmap)
                                    } else {
                                        image.setImageResource(R.drawable.no_cover_image_01)
                                    }

                                    // Set a click listener for the clickable layout
                                    clickableLayout.setOnClickListener {
                                        val bookSelection = fileName
                                        val intent = Intent(this, Book::class.java)
                                        if (fileName != bookSelected) {
                                            intent.putExtra("currentPageInt", 0)
                                            intent.putExtra("Book Selected", bookSelection)
                                        } else {
                                            intent.putExtra("Book Selected", bookSelection)
                                            intent.putExtra("currentPageInt", currentPageInt)
                                        }
                                        startActivity(intent)
                                    }

                                    lifecycleScope.launch {
                                        val checkBooks = firestoreHelper.checkIfBookExists("ReadBooks", fileName, userEmail)
                                        if (checkBooks) {
                                            buttonCheck.setImageResource(R.drawable.baseline_check_24_green)
                                        }
                                        else{
                                            buttonCheck.setImageResource(R.drawable.baseline_check_24)
                                        }

                                        val toReadBooks = firestoreHelper.checkIfBookExists("ToReadBooks", fileName, userEmail)
                                        if (toReadBooks) {
                                            buttonToRead.setImageResource(R.drawable.baseline_access_time_24_green)
                                        }
                                        else{
                                            buttonToRead.setImageResource(R.drawable.baseline_access_time_24)
                                        }

                                        val favourite_exists = firestoreHelper.checkIfBookExists("FavouriteBooks", fileName, userEmail)
                                        if (favourite_exists) {
                                            buttonFavourite.setImageResource(R.drawable.baseline_favorite_24_green)
                                        }
                                        else{
                                            buttonFavourite.setImageResource(R.drawable.baseline_favorite_border_24)
                                        }
                                    }

                                    buttonCheck.setOnClickListener {
                                        lifecycleScope.launch {
                                            val exists = firestoreHelper.checkIfBookExists("ReadBooks", fileName, userEmail)
                                            if (exists) {
                                                buttonCheck.setImageResource(R.drawable.baseline_check_24)
                                                firestoreHelper.removeBookFromCollection("ReadBooks", fileName, userEmail)
                                                Log.i(TAG, "The book already exists in the collection.")
                                            } else {
                                                buttonCheck.setImageResource(R.drawable.baseline_check_24_green)
                                                Log.i(TAG, "The book does not exist in the collection.")
                                                // Add the book information since it doesn't exist
                                                firestoreHelper.addBookInfoToCollection("ReadBooks", fileName, userEmail)
                                            }
                                        }
                                    }

                                    buttonToRead.setOnClickListener {
                                        lifecycleScope.launch {
                                            val exists = firestoreHelper.checkIfBookExists("ToReadBooks", fileName, userEmail)
                                            if (exists) {
                                                buttonToRead.setImageResource(R.drawable.baseline_access_time_24)
                                                firestoreHelper.removeBookFromCollection("ToReadBooks", fileName, userEmail)
                                                Log.i(TAG, "The book already exists in the collection.")
                                            } else {
                                                buttonToRead.setImageResource(R.drawable.baseline_access_time_24_green)
                                                Log.i(TAG, "The book does not exist in the collection.")
                                                // Add the book information since it doesn't exist
                                                firestoreHelper.addBookInfoToCollection("ToReadBooks", fileName, userEmail)
                                            }
                                        }
                                    }

                                    buttonFavourite.setOnClickListener {
                                        lifecycleScope.launch {
                                            val exists = firestoreHelper.checkIfBookExists("FavouriteBooks", fileName, userEmail)
                                            if (exists) {
                                                buttonFavourite.setImageResource(R.drawable.baseline_favorite_border_24)
                                                firestoreHelper.removeBookFromCollection("FavouriteBooks", fileName, userEmail)
                                                Log.i(TAG, "The book already exists in the collection.")
                                            } else {
                                                Log.i(TAG, "The book does not exist in the collection.")
                                                // Add the book information since it doesn't exist
                                                buttonFavourite.setImageResource(R.drawable.baseline_favorite_24_green)
                                                firestoreHelper.addBookInfoToCollection("FavouriteBooks", fileName, userEmail)
                                            }
                                        }
                                    }

                                    // Add the inflated view to the parent layout
                                    parentLayout.addView(view)
                                }
                            } catch (e: IOException) {
                                Log.e(TAG, "Error reading book", e)
                            }
                        }.addOnFailureListener { exception ->
                            Log.e(TAG, "Error downloading file", exception)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents", exception)
            }
    }
}