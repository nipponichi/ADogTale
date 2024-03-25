package com.pmdm.adogtale.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.Profile

class BuddyProfileActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile2)
        val btnSave: Button = findViewById(R.id.btnSave)
        val profile = intent.getSerializableExtra("profile") as Profile
        btnSave.setOnClickListener() {
            createProfileAccount(profile)
        }
    }

    // Create user profile on Firebase
    private fun createProfileAccount(profile: Profile) {
        var db = FirebaseFirestore.getInstance()
        db.collection("profile").document(profile.userEmail).set(
            hashMapOf(
                "user" to profile.userEmail,
                "name" to profile.name,
                "age" to profile.age,
            )
        )
        Toast.makeText(this, "Profile created", Toast.LENGTH_SHORT).show()
    }

}
