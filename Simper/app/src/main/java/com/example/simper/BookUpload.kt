package com.example.simper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BookUpload : AppCompatActivity() {

    private val PICK_FILE_REQUEST_CODE = 42
    private val TAG = "TestActivity"

    private val firestore = FirebaseFirestore.getInstance()
    // Initialize Firebase Storage
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var drawerLayoutHelper: DrawerLayoutHelper

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_upload)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)



        val button: Button = findViewById(R.id.pickFileButton)
        button.setOnClickListener { openFilePicker() }

        // Drawer Layout Code
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Inside MainActivity.onCreate()

        // Initialize the drawerLayoutHelper
        drawerLayoutHelper = DrawerLayoutHelper(this)

        // Call the setupDrawerLayout method of drawerLayoutHelper
        drawerLayoutHelper.setupDrawerLayout(this, drawerLayout, navigationView, "null", 0)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/epub+zip" // Restrict to EPUB files
        }
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                Log.i(TAG, "File Uri: $uri")
                uploadFileToFirebase(uri)
            }
        }
    }

    private fun uploadFileToFirebase(fileUri: Uri) {
        // Extracting original filename from the URI
        val originalFileName = getFileNameFromUri(fileUri)

        // Using original filename for storage
        val fileRef = storageRef.child("books/$originalFileName")
        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.addOnFailureListener { exception ->
            Log.e(TAG, "Upload failed", exception)
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                Log.i(TAG, "File uploaded successfully: $downloadUri")
                Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                saveBookInfoToFirestore(originalFileName)
            }
        }
    }

    // Function to get original filename from URI
    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    return it.getString(displayNameIndex)
                }
            }
        }
        // If the display name cannot be retrieved, use a default filename
        return "file_${System.currentTimeMillis()}"
    }

    private fun saveBookInfoToFirestore(fileName: String) {
        // Retrieve user email from shared preferences
        val userEmail = sharedPreferences.getString("email", null)
        if (userEmail == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a map with the book information
        val bookInfo = hashMapOf(
            "email" to userEmail,
            "fileName" to fileName,
            "uploadTime" to System.currentTimeMillis()
        )

        // Add the information to Firestore under the "UserBooks" collection
        firestore.collection("UserBooks")
            .add(bookInfo)
            .addOnSuccessListener {
                Log.i(TAG, "Book information saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving book information", e)
            }
    }
}
