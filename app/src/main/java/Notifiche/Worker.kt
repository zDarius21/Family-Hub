package Notifiche

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.family_hub.MainActivity
import com.example.family_hub.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class CheckActivityWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val CHANNEL_ID = "activity_reminder_channel"
    private val NOTIFICATION_ID = 1001

    override suspend fun doWork(): Result {
        val prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val currentUid = prefs.getString("uid", null) ?: return Result.failure()

        createNotificationChannel()

        val db = FirebaseFirestore.getInstance()
        val now = System.currentTimeMillis()

        return try {
            val snapshot = db.collection("attivita")
                .whereEqualTo("uid", currentUid)
                .get()
                .await()

            for (doc in snapshot.documents) {
                val dataStr = doc.getString("data")
                val oraStr = doc.getString("ora")

                if (dataStr == null || oraStr == null) continue

                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val dataOra = try {
                    formatter.parse("$dataStr $oraStr")
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }

                val eventTime = dataOra.time
                val diff = eventTime - now

                if (diff in 0..TimeUnit.HOURS.toMillis(1)) {

                    val descrizione = doc.getString("tipo") ?: "Attività imminente"

                    showNotification(descrizione)
                    break
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Promemoria Attività"
            val descriptionText = "Notifiche per attività imminenti"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(tipo: String) {
        val message = when (tipo) {
            "Portare a spasso il cane" -> "Ricordati di portare a spasso il cane! 🐕"
            "Visita Medica" -> "Hai una visita medica programmata. 🏥"
            "Scadenza" -> "Attenzione! Hai una scadenza imminente. ⏳"
            "Evento" -> "Hai un evento in programma. 📅"
            else -> "Hai un'attività imminente da completare!"
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo2)
            .setContentTitle("Promemoria attività")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }


}
