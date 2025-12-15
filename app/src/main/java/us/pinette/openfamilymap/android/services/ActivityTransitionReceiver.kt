package us.pinette.openfamilymap.android.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

// ActivityTransitionReceiver.kt
open class ActivityTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        for (event in result.transitionEvents) {
            val activityType = event.activityType
            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    when (activityType) {
                        DetectedActivity.IN_VEHICLE,
                        DetectedActivity.ON_BICYCLE,
                        DetectedActivity.ON_FOOT,
                        DetectedActivity.WALKING,
                        DetectedActivity.RUNNING -> {
                            Log.d("ActivityTransitionReceiver", "Received transition type ${activityType}, starting background location service.")

                            // User started moving → start location tracking
                            LocationTrackingService.start(context, activityType)
                        }
                        DetectedActivity.STILL -> {
                            // Entered STILL (optional, depending how you configure)
                            Log.d("ActivityTransitionReceiver", "Received transition type ${activityType}, stopping background location service.")

                            LocationTrackingService.stop(context)
                        }
                    }
                }
                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                    // e.g. left IN_VEHICLE; you can also stop here or wait for STILL
                }
            }
        }
    }
}
