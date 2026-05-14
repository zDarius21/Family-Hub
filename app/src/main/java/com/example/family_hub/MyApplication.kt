package com.example.family_hub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID_GENERAL = "channel_general"
        const val CHANNEL_NAME_GENERAL = "Notifiche Generali"
        const val CHANNEL_DESCRIPTION_GENERAL = "Canale per notifiche generali dell'app"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canale Generale
            val channelGeneral = NotificationChannel(
                CHANNEL_ID_GENERAL,
                CHANNEL_NAME_GENERAL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION_GENERAL

            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelGeneral)

        }
    }
}