package com.pmdm.adogtale.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pmdm.adogtale.R

class ForgotPassActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)
        val txtEmail : TextView = findViewById(R.id.etEmail)
        val btnRemember : Button = findViewById(R.id.btnRemember)

        btnRemember.setOnClickListener(){
            sendPasswordReset(txtEmail.text.toString())
        }

        firebaseAuth = Firebase.auth
    }

    private fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener() {task->
            if (task.isSuccessful) {
                Toast.makeText(this, "Mail sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Mail not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}