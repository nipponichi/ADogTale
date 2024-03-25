package com.pmdm.adogtale.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.Profile
import android.Manifest

class BuddyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile)
        val btnNext: Button = findViewById(R.id.btnNext)
        val name: EditText = findViewById(R.id.etName)
        val age: EditText = findViewById(R.id.etAge)
        val btnPic1: ImageButton = findViewById(R.id.ibPic1)
        val btnPic2: ImageButton = findViewById(R.id.ibPic2)
        val btnPic3: ImageButton = findViewById(R.id.ibPic3)
        val btnPic4: ImageButton = findViewById(R.id.ibPic4)
        val user = Firebase.auth.currentUser
        btnNext.setOnClickListener() {
            val nameStr = name.text.toString()
            val ageStr = age.text.toString()
            if (nameStr != null && ageStr != null) {
                val profile = Profile (userEmail = user?.email.toString(), name = nameStr,age = ageStr)
                val intent = Intent(this, BuddyProfileActivity2::class.java)
                intent.putExtra("profile",profile)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
        btnPic1.setOnClickListener() {
            openGallery()
        }
        btnPic2.setOnClickListener() {

        }
        btnPic3.setOnClickListener() {

        }
        btnPic4.setOnClickListener() {

        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }
    // Open device gallery to choose pictures
    fun openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }
    // Set picture on image holder
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val btnPic1: ImageButton = findViewById(R.id.ibPic1)
            btnPic1.setImageURI(data?.data)
        }
    }
    // Check for external storage permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}