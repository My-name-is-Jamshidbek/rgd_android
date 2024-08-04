package com.example.waterfilter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.locationapp.LocationData
import com.example.waterfilter.activities.MainActivity
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class LocationService : Service() {
    private val CHANNEL_ID = "LocationServiceChannel"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onLocationCreate: com.example.waterfilter.LocationService created")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        apiService = ApiClient.getApiService(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d(TAG, "onLocationResult: Received location result")
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: Location received: ${location.latitude}, ${location.longitude}")
                    sendLocationToServer(location.latitude, location.longitude)
                }
            }
        }
        startForegroundService()
    }

    private fun startForegroundService() {
        Log.d(TAG, "startForegroundService: Starting foreground service")
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Tracking location in the background")
            .setSmallIcon(R.drawable.ic_baseline_add_24)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: Starting location updates")
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "startLocationUpdates: Missing location permissions")
            // Consider requesting the missing permissions here
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service destroyed")
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendLocationToServer(latitude: Double, longitude: Double) {
        val locationData = LocationData(latitude, longitude)
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return

        // Check if the user is logged in
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            Log.d(TAG, "sendLocationToServer: User is not logged in. Stopping location updates.")
            stopSelf()
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "sendLocationToServer: Sending location to server: $locationData")
                val response: Response<Void> = apiService.updateLocation("Bearer $token", locationData)
                if (response.isSuccessful) {
                    Log.d(TAG, "sendLocationToServer: Location sent successfully: ${response.message()}")
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@LocationService, "Location sent successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "sendLocationToServer: Failed to send location: ${response.code()} - ${response.message()}")
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@LocationService, "Failed to send location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendLocationToServer: Error sending location", e)
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@LocationService, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
