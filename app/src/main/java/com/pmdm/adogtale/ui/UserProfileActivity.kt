package com.pmdm.adogtale.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.R

class UserProfileActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val email = intent.getStringExtra("email")
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
        db = FirebaseFirestore.getInstance()

        val txtEmail: TextView = findViewById(R.id.etEmail)
        val txtPassword1: TextView = findViewById(R.id.etPassword)
        val txtPassword2: TextView = findViewById(R.id.etConfirmPassword)
        val username: TextView = findViewById(R.id.etUsername)
        val name: TextView = findViewById(R.id.etName)
        val surname: TextView = findViewById(R.id.etSurname)
        val town: TextView = findViewById(R.id.etTown)
        val phone: TextView = findViewById(R.id.etPhone)
        val btnRecover: Button = findViewById(R.id.btnRecover)
        val btnDelete: Button = findViewById(R.id.btnDelete)
        btnRecover.setOnClickListener() {
            if (email != null) {
                db.collection("user").document(email).get()
                    .addOnSuccessListener {
                        username.setText(it.get("username") as String?)
                        name.setText(it.get("name") as String?)
                        surname.setText(it.get("surname") as String?)
                        town.setText(it.get("town") as String?)
                        phone.setText(it.get("phone") as String?)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Firestore", "get failed with ", exception)
                    }
            }
        }
        btnDelete.setOnClickListener() {
            if (email != null) {
                db.collection("user").document(email).delete()
            }
        }


    }

}