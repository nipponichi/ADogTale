package com.pmdm.adogtale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pmdm.adogtale.ui.CardSwipeActivity
import com.pmdm.adogtale.ui.ForgotPassActivity
import com.pmdm.adogtale.ui.ListTestActivity
import com.pmdm.adogtale.ui.SignUpActivity

class AuthActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val btnDebug: Button = findViewById(R.id.btnDebug)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val txtEmail: TextView = findViewById(R.id.etUsername)
        val txtPassword: TextView = findViewById(R.id.etPassword)
        val btnSingup: Button = findViewById(R.id.btnSignUp)
        val btnRemember: Button = findViewById(R.id.btnRemember)
        firebaseAuth = Firebase.auth

        btnLogin.setOnClickListener() {
            signIn(txtEmail.text.toString(), txtPassword.text.toString());
        }
        btnSingup.setOnClickListener() {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        btnRemember.setOnClickListener() {
            val intent = Intent(this, ForgotPassActivity::class.java)
            startActivity((intent))
        }

        btnDebug.setOnClickListener() {
            val intent = Intent(this, ListTestActivity::class.java)
            startActivity(intent)
        }

    }

    // Login function
    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Logged in as: " + user?.email.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, CardSwipeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }
}