package com.pmdm.adogtale.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.LocalUser

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val txtEmail: TextView = findViewById(R.id.etEmail)
        val txtPassword1: TextView = findViewById(R.id.etPassword)
        val txtPassword2: TextView = findViewById(R.id.etConfirmPassword)
        val username: TextView = findViewById(R.id.etUsername)
        val btnNext: Button = findViewById(R.id.btnNext)

        btnNext.setOnClickListener() {
            var pass1 = txtPassword1.text.toString()
            var pass2 = txtPassword2.text.toString()
            var email = txtEmail.text.toString()
            var username = username.text.toString()

            if (pass1.equals(pass2)) {
                if (email != null && pass1 != null && pass2 != null && username != null) {
                    val user = LocalUser(username = username, email = email,password = pass1)
                    val intent = Intent(this, SignUpActivity2::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "The password does not match", Toast.LENGTH_SHORT).show()
                txtPassword2.requestFocus()
            }
        }
    }
}