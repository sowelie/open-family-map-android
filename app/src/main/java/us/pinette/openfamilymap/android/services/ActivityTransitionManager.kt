package us.pinette.openfamilymap.android.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.os.Build
import com.google.android.gms.location.ActivityRecognition
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ActivityTransitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val activityRecognitionClient =
        ActivityRecognition.getClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityTransitionReceiver::class.java)

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_MUTABLE
                } else {
                    0
                }

        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flags
        )
    }

    fun startMonitoring() {
        val request = ActivityTransitionHelper.buildRequest()

        activityRecognitionClient
            .requestActivityTransitionUpdates(request, pendingIntent)
            .addOnSuccessListener { Log.d("ActivityTransitionManager", "Successfully registered transition listener.") }
            .addOnFailureListener { e -> Log.e("ActivityTransitionManager", "An exception occurred registering a transition listener $e") }
    }

    fun stopMonitoring() {
        activityRecognitionClient
            .removeActivityTransitionUpdates(pendingIntent)
    }
}
