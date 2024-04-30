package com.pmdm.adogtale

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pmdm.adogtale.ui.CardSwipeActivity
import com.pmdm.adogtale.ui.ChatActivity
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val random = Random

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtener el contenido de la notificación
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body

        // Enviar un broadcast
        val intent = Intent("com.pmdm.adogtale.NOTIFICATION_RECEIVED")
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        sendBroadcast(intent)
    }
}
