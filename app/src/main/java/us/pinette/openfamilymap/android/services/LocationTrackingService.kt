package us.pinette.openfamilymap.android.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// LocationTrackingService.kt
@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isTracking = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // If you want, inject a repo here and write to Room/API etc
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val activityType = intent?.getIntExtra(EXTRA_ACTIVITY_TYPE, DetectedActivity.UNKNOWN)

        if (!isTracking) {
            startForeground(NOTIFICATION_ID, buildNotification(activityType))
            startLocationUpdates(activityType)
            isTracking = true
        } else {
            // Optionally update notification if activityType changed
        }

        return START_STICKY
    }

    @SuppressLint("MissingPermission") // make sure you checked permissions
    private fun startLocationUpdates(activityType: Int?) {
        val request = createLocationRequestForActivity(activityType)

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            for (location in result.locations) {
                // Persist or send to server
                // e.g. repository.saveLocation(location, activityType)
                Log.d("Location update", "Location update $location")
            }
        }
    }

    override fun onDestroy() {
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun buildNotification(activityType: Int?): Notification {
        val channelId = "location_channel"
        createChannelIfNeeded(channelId)

        val title = "Tracking your location"
        val text = when (activityType) {
            DetectedActivity.IN_VEHICLE -> "Detected driving…"
            DetectedActivity.ON_BICYCLE -> "Detected biking…"
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING -> "Detected walking/running…"
            else -> "Detecting movement…"
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .build()
    }

    private fun createChannelIfNeeded(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun createLocationRequestForActivity(activityType: Int?): LocationRequest {
        val base = LocationRequest.Builder(
            when (activityType) {
                DetectedActivity.IN_VEHICLE,
                DetectedActivity.ON_BICYCLE -> 5_000L  // 5 seconds
                DetectedActivity.ON_FOOT,
                DetectedActivity.WALKING,
                DetectedActivity.RUNNING -> 10_000L // 10 seconds
                else -> 15_000L
            }
        )
            .setMinUpdateIntervalMillis(3_000L)
            .setMaxUpdateDelayMillis(30_000L)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()

        return base
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val EXTRA_ACTIVITY_TYPE = "extra_activity_type"

        fun start(context: Context, activityType: Int?) {
            val intent = Intent(context, LocationTrackingService::class.java).apply {
                putExtra(EXTRA_ACTIVITY_TYPE, activityType)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, LocationTrackingService::class.java))
        }
    }
}
