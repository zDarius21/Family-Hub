package Posizione

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.family_hub.MainActivity
import com.example.family_hub.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LocationForegroundService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 50_000L)
        .setMinUpdateIntervalMillis(50_000L)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location == null) {
                Log.d("LocationService", "locationResult senza posizione")
                return
            }
            Log.d("LocationService", "Posizione aggiornata: ${location.latitude}, ${location.longitude}")
            saveLocationToDB(location)
        }
    }


    private var trackingStartTimeMillis: Long = 0L
    private var trackingEndTimeMillis: Long = 0L
    private val trackingDurationMillis = 30 * 60 * 1000L // 30 minuti in ms
    private val handler = Handler(Looper.getMainLooper())
    private val stopTrackingRunnable = Runnable {
        Log.d("LocationService", "Tempo tracking scaduto, fermo location updates")
        stopLocationUpdates()
        aggiornaTestoNotifica("Tracking completato")
        stopSelf()

    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification("In attesa dell'inizio del tracking..."))

        val oraInizio = intent?.getStringExtra("oraInizio") ?: run {
            Log.e("LocationService", "Ora inizio non fornita, stop servizio")
            stopSelf()
            return START_NOT_STICKY
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateOraInizio = try {
            sdf.parse(oraInizio)
        } catch (e: Exception) {
            Log.e("LocationService", "Errore parsing oraInizio", e)
            stopSelf()
            return START_NOT_STICKY
        }

        val calendarStart = Calendar.getInstance().apply {
            val now = Calendar.getInstance()
            time = dateOraInizio ?: Date()
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
        }

        trackingStartTimeMillis = calendarStart.timeInMillis
        trackingEndTimeMillis = trackingStartTimeMillis + trackingDurationMillis

        val nowMillis = System.currentTimeMillis()

        if (nowMillis < trackingStartTimeMillis) {
            val delay = trackingStartTimeMillis - nowMillis
            handler.postDelayed({
                startLocationUpdates()
                aggiornaTestoNotifica("Tracking posizione attivo")
                handler.postDelayed(stopTrackingRunnable, trackingDurationMillis)
            }, delay)
        } else if (nowMillis in trackingStartTimeMillis..trackingEndTimeMillis) {
            val delay = trackingEndTimeMillis - nowMillis
            startLocationUpdates()
            aggiornaTestoNotifica("Tracking posizione attivo")
            handler.postDelayed(stopTrackingRunnable, delay)
        }
        else {
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        handler.removeCallbacks(stopTrackingRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startLocationUpdates() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d("LocationService", "lastLocation: ${location.latitude}, ${location.longitude}")
            } else {
                Log.d("LocationService", "lastLocation è null")
            }
        }.addOnFailureListener { e ->
            Log.e("LocationService", "Errore lastLocation", e)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationService", "Permessi di posizione non concessi")
            stopSelf()
            return
        }
        Log.d("LocationService", "Chiedo aggiornamenti posizione")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createNotification(initialText: String = "In attesa dell'inizio del tracking..."): Notification {
        val channelId = "location_channel"

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Tracking Posizione",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, flags)

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("FamilyHub")
            .setContentText(initialText)
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)

        return notificationBuilder.build()
    }

    private fun aggiornaTestoNotifica(nuovoTesto: String) {
        notificationBuilder.setContentText(nuovoTesto)
        notificationManager.notify(1, notificationBuilder.build())
    }


    private fun saveLocationToDB(location: Location) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.e("LocationService", "Utente non autenticato")
            return
        }
        val db = FirebaseFirestore.getInstance()

        val oggi = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        Log.d("LocationService", "UID: $uid, Data oggi: $oggi, Location: ${location.latitude}, ${location.longitude}")

        db.collection("attivita")
            .whereEqualTo("uid", uid)
            .whereEqualTo("data", oggi)
            .whereEqualTo("tipo", "Portare a spasso il cane")
            .get()
            .addOnSuccessListener { result ->
                Log.d("LocationService", "Documenti trovati: ${result.size()}")
                if (!result.isEmpty) {
                    val attivitaDoc = result.documents[0]
                    Log.d("LocationService", "Attività trovata: ${attivitaDoc.data}")

                    val attivitaId = attivitaDoc.id
                    val newCoordinate = hashMapOf(
                        "lat" to location.latitude,
                        "lon" to location.longitude
                    )

                    db.collection("attivita")
                        .document(attivitaId)
                        .update("coordinate", FieldValue.arrayUnion(newCoordinate))
                        .addOnSuccessListener {
                            Log.d("LocationService", "Coordinate salvate per attività $attivitaId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("LocationService", "Errore aggiornando coordinate", e)
                        }
                } else {
                    Log.d("LocationService", "Nessuna attività trovata per oggi.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("LocationService", "Errore nella query attività", e)
            }
    }


}