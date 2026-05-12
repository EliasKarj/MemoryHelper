package com.example.muistikaveri

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class SafetyService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Kodin koordinaatit (Varmista, että nämä vastaavat emulaattorin "Home" -sijaintia)
    private val homeLat = 60.3188
    private val homeLng = 24.8528
    private val safetyRadiusMeters = 200.0 // Turva-alueen säde

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    processNewLocation(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        if (intent?.action == "ACTION_SOS") {
            updateStatusInFirebase("sos_active")
        }

        showForegroundNotification()
        startLocationUpdates()

        return START_STICKY
    }

    // TÄSSÄ ON MUUTETTU FUNKTIO:
    private fun processNewLocation(location: Location) {
        val db = FirebaseFirestore.getInstance()
        val geoPoint = GeoPoint(location.latitude, location.longitude)

        // 1. Päivitetään nykyinen sijainti aina pilveen
        db.collection("Patients").document("testi_potilas_1")
            .update("currentLocation", geoPoint)

        // 2. Lasketaan etäisyys kotiin
        val results = FloatArray(1)
        Location.distanceBetween(location.latitude, location.longitude, homeLat, homeLng, results)
        val distanceInMeters = results[0]

        // 3. Automaattinen tilan päivitys
        if (distanceInMeters > safetyRadiusMeters) {
            // Potilas on poistunut alueelta
            updateStatusInFirebase("wandering")
            println("HÄLYTYS: Potilas on alueen ulkopuolella ($distanceInMeters m)")
        } else {
            // Potilas on palannut tai pysynyt alueella
            // Tämä koodi muuttaa tilan takaisin "safe" -tilaan automaattisesti
            updateStatusInFirebase("safe")
            println("Potilas on turva-alueella ($distanceInMeters m)")
        }
    }

    private fun updateStatusInFirebase(newStatus: String) {
        val db = FirebaseFirestore.getInstance()

        // Haetaan ensin nykyinen status, ettei turhaan ylikirjoiteta samaa arvoa sekunnin välein
        db.collection("Patients").document("testi_potilas_1").get()
            .addOnSuccessListener { document ->
                val currentStatus = document.getString("status")

                // Päivitetään vain, jos status on oikeasti muuttumassa
                // Huom: Emme muuta automaattisesti SOS-tilaa pois, jos se on aktivoitu käsin
                if (currentStatus != newStatus && currentStatus != "sos_active") {
                    db.collection("Patients").document("testi_potilas_1")
                        .update("status", newStatus)
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30000)
            .setMinUpdateIntervalMillis(15000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun showForegroundNotification() {
        createNotificationChannel()
        val sosIntent = Intent(this, SafetyService::class.java).apply { action = "ACTION_SOS" }
        val sosPendingIntent = PendingIntent.getService(this, 0, sosIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "SAFETY_CHANNEL_ID")
            .setContentTitle("Muistikaveri on turvanasi")
            .setContentText("Sijaintia ja turva-aluetta valvotaan automaattisesti.")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_help, "SOS HÄTÄTILANNE", sosPendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("SAFETY_CHANNEL_ID", "Safety Service Channel", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}