package us.pinette.openfamilymap.android.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object AppPermissions {
    const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    const val ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION
    const val BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION
}

sealed class PermissionStatus {
    object Granted : PermissionStatus()
    data class Denied(val shouldShowRationale: Boolean) : PermissionStatus()
}

fun Context.getPermissionStatus(permission: String): PermissionStatus {
    val granted = ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED

    if (granted) return PermissionStatus.Granted

    val activity = (this as? Activity)
        ?: return PermissionStatus.Denied(shouldShowRationale = false)

    val shouldShowRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

    return PermissionStatus.Denied(shouldShowRationale)
}
