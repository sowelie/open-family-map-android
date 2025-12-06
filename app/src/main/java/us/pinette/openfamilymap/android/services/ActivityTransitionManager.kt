package us.pinette.openfamilymap.android.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun startMonitoring() {
        val request = ActivityTransitionHelper.buildRequest()

        activityRecognitionClient
            .requestActivityTransitionUpdates(request, pendingIntent)
            .addOnSuccessListener { /* log/emit success */ }
            .addOnFailureListener { e -> /* handle error */ }
    }

    fun stopMonitoring() {
        activityRecognitionClient
            .removeActivityTransitionUpdates(pendingIntent)
    }
}
