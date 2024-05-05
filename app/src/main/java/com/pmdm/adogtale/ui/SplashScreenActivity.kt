package com.pmdm.adogtale.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.LocalUser

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var btnChatNow: Button
    private lateinit var btnChatLater: Button
    private lateinit var countdownTextView: TextView
    private var countDownTimer: CountDownTimer? = null
    // Duración del splash screen en milisegundos
    private val SPLASH_DISPLAY_LENGTH: Long = 1000000 // 1000 segundos

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        btnChatNow = findViewById(R.id.btnChatNow)
        btnChatLater = findViewById(R.id.btnChatLater)
        countdownTextView = findViewById(R.id.countdownTextView)

        var targetEmail = intent.getStringExtra("targetEmail") as String
        Log.i("splash", "me he ejecutado")

        // Agregar listener de clic al botón ChatNow
        btnChatNow.setOnClickListener {
            // Abrir la nueva ventana de chat
            finish()
            val intent = Intent(this@SplashScreenActivity, ChatActivity::class.java)
            intent.putExtra("targetEmail", targetEmail)
            startActivity(intent)

        }

        // Agregar listener de clic al botón ChatLater
        btnChatLater.setOnClickListener {
            //Se marc el like como chequeado
            // Cierra esta actividad
            finish()
        }

        // Handler para pasar a la actividad principal después de SPLASH_DISPLAY_LENGTH
        Handler().postDelayed({
            // Crea un intent para iniciar la actividad principal
            val mainIntent = Intent(this@SplashScreenActivity, CardSwipeActivity::class.java)
            startActivity(mainIntent)
            // Cierra esta actividad
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}
