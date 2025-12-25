package com.example.moodlemobileclient.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moodlemobileclient.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM_SERVICE"
        private const val CHANNEL_ID = "moodle_notifications"
    }

    // ================== MENSAJES ==================
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Mensaje recibido desde FCM")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Moodle"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Tienes una nueva notificación"

        showNotification(title, body)
    }

    // ================== TOKEN ==================
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "Nuevo token FCM recibido")

        val prefs = getSharedPreferences("fcm_prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("pending_token", token)
            .apply()
    }

    // ================== NOTIFICACIÓN ==================
    private fun showNotification(title: String, body: String) {

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Canal (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones Moodle",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones del sistema Moodle"
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}