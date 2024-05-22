package com.pmdm.adogtale.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.pmdm.adogtale.R
import com.pmdm.adogtale.utils.FirebaseUtil

class OptionsActivity : AppCompatActivity() {
    private lateinit var editProfileBtn: Button
    private lateinit var deleteAccountBtn: Button
    private lateinit var logoutBtn: Button
    val firebaseUtil = FirebaseUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        editProfileBtn = findViewById(R.id.editProfileBtn)
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn)
        logoutBtn = findViewById(R.id.logoutBtn)

        editProfileBtn.setOnClickListener {
            val intent = Intent(this, EditUserActivity::class.java)
            startActivity(intent)
        }

        deleteAccountBtn.setOnClickListener {
            val intent = Intent(this, DeleteAccountActivity::class.java)
            startActivity(intent)
        }

        logoutBtn.setOnClickListener {
            Log.i("logoutBTN", "aqui")
            firebaseUtil.logout(this)
        }
    }

}