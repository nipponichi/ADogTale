package com.pmdm.adogtale.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.LocalUser
import com.pmdm.adogtale.model.Profile

class BuddyProfileActivity : AppCompatActivity() {

    private lateinit var user: LocalUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile)
        val btnNext: Button = findViewById(R.id.btnNext)
        val name: EditText = findViewById(R.id.etName)
        val age: EditText = findViewById(R.id.etAge)
        val something: EditText = findViewById(R.id.etSomething)
        val shortDescription: EditText = findViewById(R.id.etShortDescription)

        user = intent.getSerializableExtra("user") as LocalUser

        // Gender
        val gender = resources.getStringArray(R.array.gender)
        val arrayGender = ArrayAdapter(this, R.layout.dropdown_menu, gender)
        val acGender: AutoCompleteTextView = findViewById(R.id.acGender)
        acGender.setAdapter(arrayGender)

        // Breed
        val breed = resources.getStringArray(R.array.breed)
        val arrayBreed = ArrayAdapter(this, R.layout.dropdown_menu, breed)
        val acBreed: AutoCompleteTextView = findViewById(R.id.acBreed)
        acBreed.setAdapter(arrayBreed)

        btnNext.setOnClickListener() {
            val nameStr = name.text.toString()
            val ageStr = age.text.toString()
            val selectedGenderStr = acGender.text.toString()
            val selectedBreedStr = acBreed.text.toString()
            val somethingStr = something.text.toString()
            val shortDescriptionStr = shortDescription.text.toString()
            val userEmail = user.email
            val profile =
                Profile(
                    name = nameStr,
                    age = ageStr,
                    gender = selectedGenderStr,
                    breed = selectedBreedStr,
                    something = somethingStr,
                    shortDescription = shortDescriptionStr,
                    userEmail = userEmail
                )
            val intent = Intent(this, BuddyProfileActivity2::class.java)
            intent.putExtra("profile", profile)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }
}

