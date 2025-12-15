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
        Log.d("ActivityTransitionReceiver", "Activity transition receive: $intent")

        if (!ActivityTransitionResult.hasResult(intent)) return

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        for (event in result.transitionEvents) {
            val activityType = event.activityType
            val transitionType = event.transitionType

            when (transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    when (activityType) {
                        DetectedActivity.IN_VEHICLE,
                        DetectedActivity.ON_BICYCLE,
                        DetectedActivity.ON_FOOT,
                        DetectedActivity.WALKING,
                        DetectedActivity.RUNNING -> {
                            // User started moving → start location tracking
                            LocationTrackingService.start(context, activityType)
                        }
                        DetectedActivity.STILL -> {
                            // Entered STILL (optional, depending how you configure)
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
