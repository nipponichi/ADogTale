package com.pmdm.adogtale.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.User

class SignUpActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up2)
        val name: TextView = findViewById(R.id.etName)
        val surname: TextView = findViewById(R.id.etSurname)
        val town: TextView = findViewById(R.id.etTown)
        val phone: TextView = findViewById(R.id.etPhone)
        val btnNext: Button = findViewById(R.id.btnNext)
        val user = intent.getSerializableExtra("user") as User

        btnNext.setOnClickListener() {
            val nameStr = name.text.toString()
            val surnameStr = surname.text.toString()
            val townStr = town.text.toString()
            val phoneStr = phone.text.toString()
            user.name = nameStr
            user.surname = surnameStr
            user.town = townStr
            user.phone = phoneStr
            val intent = Intent(this, BuddyProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }
}