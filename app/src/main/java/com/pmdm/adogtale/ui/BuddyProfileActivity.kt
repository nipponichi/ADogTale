package com.pmdm.adogtale.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.Profile
import com.pmdm.adogtale.model.User

class BuddyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile)
        val btnNext: Button = findViewById(R.id.btnNext)
        val name: EditText = findViewById(R.id.etName)
        val age: EditText = findViewById(R.id.etAge)
        /*val btnPic1: Button = findViewById(R.id.ibPic1)
        val btnPic2: Button = findViewById(R.id.ibPic2)
        val btnPic3: Button = findViewById(R.id.ibPic3)
        val btnPic4: Button = findViewById(R.id.ibPic4)*/
        val user = intent.getSerializableExtra("user") as User

        btnNext.setOnClickListener() {
            val nameStr = name.text.toString()
            val ageStr = age.text.toString()
            if (nameStr != null && ageStr != null) {
                val profile = Profile (user,nameStr,ageStr)
                val intent = Intent(this, BuddyProfileActivity2::class.java)
                intent.putExtra("profile",profile)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
            }

        }
    }
}