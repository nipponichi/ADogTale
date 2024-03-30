package com.pmdm.adogtale.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pmdm.adogtale.R

class SplashScreenActivity : AppCompatActivity() {
    private var btnChatLater: Button? = null
    private var countdownTextView: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    // Duración del splash screen en milisegundos
    private val SPLASH_DISPLAY_LENGTH: Long = 10000 // 10 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

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
