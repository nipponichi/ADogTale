package com.pmdm.adogtale.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pmdm.adogtale.R

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var btnChatNow: Button
    private lateinit var btnChatLater: Button
    private lateinit var countdownTextView: TextView
    private var countDownTimer: CountDownTimer? = null
    private lateinit var iv1: ImageView
    private lateinit var iv2: ImageView

    // Duración del splash screen en milisegundos
    private val SPLASH_DISPLAY_LENGTH: Long = 1000000 // 1000 segundos

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        btnChatNow = findViewById(R.id.btnChatNow)
        btnChatLater = findViewById(R.id.btnChatLater)
        countdownTextView = findViewById(R.id.countdownTextView)
        iv1 = findViewById(R.id.iv1)
        iv2 = findViewById(R.id.iv2)

        // Cargar las imágenes en los ImageView
//        iv1.setImageResource();
//        iv2.setImageResource(R.drawable.imagen2);

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
            // Cambia a la actividad principal y cierra esta
            val mainIntent = Intent(this@SplashScreenActivity, CardSwipeActivity::class.java)
            startActivity(mainIntent)
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
