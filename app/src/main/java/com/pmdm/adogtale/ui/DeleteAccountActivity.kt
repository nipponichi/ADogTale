package com.pmdm.adogtale.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.pmdm.adogtale.R
import com.pmdm.adogtale.utils.DeleteMethods
import com.pmdm.adogtale.utils.FirebaseUtil

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var deleteBtn: Button
    val firebaseUtil = FirebaseUtil()
    val deleteMethods = DeleteMethods()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val txtEmail: TextView = findViewById(R.id.etUsername)
        val txtPassword: TextView = findViewById(R.id.etPassword)
        deleteBtn = findViewById(R.id.deleteBtn)


        deleteBtn.setOnClickListener {
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()
            deleteMethods.deleteEverythingOfAnUser(email,password, this)
            firebaseUtil.killActivity(this)
        }
    }
}