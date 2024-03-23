package com.pmdm.adogtale.ui

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

class SignupActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val txtEmail: TextView = findViewById(R.id.etEmail)
        val txtPassword1: TextView = findViewById(R.id.etPassword)
        val txtPassword2: TextView = findViewById(R.id.etConfirmPassword)
        val username: TextView = findViewById(R.id.etUsername)
        val name: TextView = findViewById(R.id.etName)
        val surname: TextView = findViewById(R.id.etSurname)
        val town: TextView = findViewById(R.id.etTown)
        val phone: TextView = findViewById(R.id.etPhone)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnModify: Button = findViewById(R.id.btnModify)

        btnSave.setOnClickListener() {
            var pass1 = txtPassword1.text.toString()
            var pass2 = txtPassword2.text.toString()
            var email = txtEmail.text.toString()
            if (pass1.equals(pass2)) {
                createUserAccount(txtEmail.text.toString(), txtPassword1.text.toString())
            } else {
                Toast.makeText(this, "The password does not match", Toast.LENGTH_SHORT).show()
                txtPassword2.requestFocus()
            }
        }

        btnModify.setOnClickListener() {
            var email = txtEmail.text.toString()
            var pass1 = txtPassword1.text.toString()
            db.collection("user").document(email).set(
                hashMapOf(
                    "username" to username.text.toString(),
                    "name" to name.text.toString(),
                    "surname" to surname.text.toString(),
                    "town" to town.text.toString(),
                    "phone" to phone.text.toString()
                )
            )
        }

        firebaseAuth = Firebase.auth

    }

    private fun createUserAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Created account", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }

}