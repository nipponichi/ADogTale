package com.pmdm.adogtale

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
<<<<<<< Updated upstream
import com.pmdm.adogtale.ui.BuddyProfileActivity2
=======
import com.google.firebase.messaging.FirebaseMessaging
>>>>>>> Stashed changes
import com.pmdm.adogtale.ui.CardSwipeActivity
import com.pmdm.adogtale.ui.ForgotPassActivity
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
        val token = null;
        firebaseAuth = Firebase.auth

        btnLogin.setOnClickListener() {
            signIn(txtEmail.text.toString(), txtPassword.text.toString());
        }
        btnSingup.setOnClickListener() {
            getDeviceToken(
                callback = { token ->
                    val intent = Intent(this, SignUpActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                },
                errorCallback = { exception ->
                    Toast.makeText(
                        baseContext,
                        "Error obtaining token: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
        btnRemember.setOnClickListener() {
            val intent = Intent(this, ForgotPassActivity::class.java)
            startActivity((intent))
        }

        btnDebug.setOnClickListener() {
            val intent = Intent(this, BuddyProfileActivity2::class.java)
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

//    private fun createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is not in the Support Library.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system.
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

    private fun getDeviceToken(callback: (String?) -> Unit, errorCallback: (Exception) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                callback(token)
            } else {
                errorCallback(
                    task.exception ?: Exception("Unknown error occurred while getting token")
                )
            }
        }
    }

}